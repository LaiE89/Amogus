package multiplayer;

import model.TetrisBoard;
import model.TetrisModel;
import views.ConnectView;
import views.TetrisView;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Server extends Thread{

    private TetrisView tetrisView;
    private ConnectView connectView;
    public ServerSocket serverSocket;
    public HashMap<Socket, ObjectInputStream> serverDis = new HashMap<>(); // Receives data from all clients
    public HashMap<Socket, ObjectOutputStream> serverDos = new HashMap<>(); // Sends data to all clients
    HashMap<Socket, Boolean> clientSockets = new HashMap<>();
    private int clientReadTime = 1000;
    public final Object lock = new Object(); // A monitor lock. Ensures synchronization when accessing <clientSockets>
    public int numConnections = 0;
    public boolean isGameStarted = false;
    public int numGameOvers = 0;

    /**
     * Constructor
     *
     * @param tetrisView current client's tetrisView
     * @param ip the local ip that the server is hosted on
     * @param port the port that the server is hosted on
     */
    public Server(TetrisView tetrisView, String ip, int port) {
        this.tetrisView = tetrisView;
        this.connectView = tetrisView.connectView;
        try {
            this.serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        }catch (Exception e) {
            connectView.addressErrorLabel.setText(e.getMessage());
        }
        new Thread(this).start();
    }

    public void run() {

        // Thread that always receives info from all clients. It is non-blocking.
        Thread checkClients = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!serverSocket.isClosed()) {
                    receivePacket();
                    // Close server conditions
                    if (isGameStarted && numConnections < 2) {
                        System.out.println("NOT ENOUGH PEOPLE IN LOBBY");
                        closeServer();
                    }
                    if (isGameStarted && numGameOvers >= numConnections) {
                        System.out.println("EVERYONE LOST, NUM GAME OVERS: " + numGameOvers + ", NUM CONNECTIONS: " + numConnections);
                        closeServer();
                    }
                }
            }
        });
        checkClients.start();

        // Thread for checking lobby connection
        while (!serverSocket.isClosed()) {
            if (!isGameStarted && numConnections < 4) {
                listenForServerRequest();
            }else {
                fullServerRequest();
            }
        }
    }

    /**
     * Checks if any players are connecting to the lobby. If a player
     * is trying to connect, accepts their connection and lets them into the
     * lobby. Also alerts every other client that a player has joined the lobby.
     */
    public void listenForServerRequest() {
        System.out.println("Listening for server request");
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
            // If a lobby is full (i.e. 4 players is the max limit of a lobby) or game already started, checks for any player trying to connect to the lobby and kicks them out.
            if (isGameStarted || numConnections >= 4) {
                System.out.println("CLIENT WITH IP: " + clientSocket.getInetAddress() + ", AND PORT: " + clientSocket.getPort() + " HAS REQUESTED TO JOIN. THE CURRENT SERVER IS FULL, KICKING HIM OUT.");
                clientSocket.close();
            }
            serverDos.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
            serverDis.put(clientSocket, new ObjectInputStream(clientSocket.getInputStream()));
            clientSocket.setSoTimeout(clientReadTime);
            clientSockets.put(clientSocket, true);
            numConnections = clientSockets.size();
            sendPacket(serverSocket.getLocalPort(), numConnections, isGameStarted, false, 0, null);
            System.out.println("CLIENT WITH IP: " + clientSocket.getInetAddress() + ", AND PORT: " + clientSocket.getPort() + ", HAS REQUESTED TO JOIN. THE CURRENT SERVER LIST SIZE: " + numConnections);
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * If a lobby is full (i.e. 4 players is the max limit of a lobby), checks for any
     * player trying to connect to the lobby and kicks them out.
     */
    public void fullServerRequest() {
        System.out.println("Listening for server request when server is full");
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
            System.out.println("CLIENT WITH IP: " + clientSocket.getInetAddress() + ", AND PORT: " + clientSocket.getPort() + " HAS REQUESTED TO JOIN. THE CURRENT SERVER IS FULL, KICKING HIM OUT.");
            clientSocket.close();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sends a packet of data to all clients.
     *
     * @param numConnections the number of current connections
     * @param isGameStarted true if the lobby has started the game
     * @param isGameOver true if the current client has lost (the current client's board is full)
     */
    public void sendPacket(int sender, int numConnections, boolean isGameStarted, boolean isGameOver, int sendGarbageLines, TetrisBoard senderBoard) {
        for (Socket socket : clientSockets.keySet()) {
            try {
                Packet packet = new Packet(sender, numConnections, isGameStarted, isGameOver, sendGarbageLines, senderBoard);
                serverDos.get(socket).writeObject(packet);
                serverDos.get(socket).flush();
            }catch (IOException e) {
                System.out.println("Error in sendPacket(): " + e.getMessage());
            }
        }
    }

    public void sendPacketToRandomClient(Socket sender, int numConnections, boolean isGameStarted, boolean isGameOver, int sendGarbageLines) {
        HashMap<Socket, Boolean> clientsCopy;
        synchronized (lock) {
            clientsCopy = new HashMap<>(clientSockets);
            clientsCopy.remove(sender);
            for (Socket socket : clientSockets.keySet()) {
                if (clientsCopy.containsKey(socket) && !clientsCopy.get(socket)) {
                    clientsCopy.remove(socket);
                }
            }
        }
        if (clientsCopy.size() > 0) {
            int index = (int) (Math.random() * clientsCopy.size());
            try {
                System.out.println("Sending " + sendGarbageLines + " lines of garbage to " + clientsCopy.keySet().toArray()[index]);
                Packet packet = new Packet(sender.getPort(), numConnections, isGameStarted, isGameOver, sendGarbageLines, null);
                serverDos.get(clientsCopy.keySet().toArray()[index]).writeObject(packet);
                serverDos.get(clientsCopy.keySet().toArray()[index]).flush();
            } catch (IOException e) {
                System.out.println("Failed to send packet to random client: " + e.getMessage());
            }
        }
    }

    /**
     * Receives any available packets of data from all clients.
     * If a client cannot be reached, disconnects the player.
     */
    public void receivePacket() {
        List<Socket> clientsCopy;
        synchronized (lock) {
            clientsCopy = new ArrayList<>(clientSockets.keySet());
        }
        for (Socket socket : clientsCopy) {
            try {
                //System.out.println("Receiving packet from: " + socket.getPort());
                Packet value = (Packet) serverDis.get(socket).readObject();
                boolean isGameOver = value.getIsGameOver();
                int sendGarbageLines = value.getSendGarbageLines();
                TetrisBoard thisBoard = value.getSenderBoard();
                if (isGameOver) {
                    System.out.println(socket.getPort() + " has lost.");
                    numGameOvers += 1; // numGameOvers is only accessed here and this method is called once in only one thread so synchronization is not needed
                    clientSockets.put(socket, false);
                }
                if (sendGarbageLines > 0) {
                    sendPacketToRandomClient(socket, numConnections, isGameStarted, false, sendGarbageLines);
                }
                if (thisBoard != null) {
                    sendPacket(value.getSender(), numConnections, isGameStarted, false, 0, thisBoard);
                }
            }catch (SocketTimeoutException e) {
                // Nothing to read in current socket
            }catch (IOException e) {
                System.out.println("Disconnection: " + e.getMessage());
                sendDisconnect(socket);
            }catch (ClassNotFoundException e) {
                System.out.println("Class not found failure in receivePacket(): " + e.getMessage());
            }catch (NullPointerException e) {
                System.out.println("Packet is null found in receivePacket(): " + e.getMessage());
            }
        }
    }

    /**
     * Removes client from the server and force closes the client's socket.
     *
     * @param socket the client socket that will be removed from the server
     */
    public void removeClient(Socket socket) throws IOException {
        if (clientSockets.containsKey(socket)) {
            clientSockets.remove(socket);
            serverDis.remove(socket);
            serverDos.remove(socket);
            socket.close();
        }
    }

    /**
     * Alerts all other clients that a client socket has disconnected.
     * Then removes the client socket from the server.
     *
     * @param socket the client socket that will be removed from the server
     */
    private synchronized void sendDisconnect(Socket socket) {
        try {
            System.out.println("Disconnecting " + socket.getPort());
            removeClient(socket);
            numConnections = clientSockets.size();
            sendPacket(serverSocket.getLocalPort(), numConnections, isGameStarted, false, 0, null);
        }catch (IOException e) {
            System.out.println("Send Disconnect Update did not go through: " + e.getMessage());
        }
    }

    /**
     * Removes all clients from the server.
     */
    public void removeAllClients() {
        List<Socket> clientsCopy;
        synchronized (lock) {
            clientsCopy = new ArrayList<>(clientSockets.keySet());
        }
        for (Socket socket : clientsCopy) {
            try {
                removeClient(socket);
            }catch (Exception e) {
                System.out.println("Error in removeAllClients(): " + e.getMessage());
            }
        }
    }

    /**
     * Clears the object input stream.
     */
    public void clearAllSentPackets() {
        List<Socket> clientsCopy;
        synchronized (lock) {
            clientsCopy = new ArrayList<>(clientSockets.keySet());
        }
        for (Socket socket : clientsCopy) {
            try {
                serverDis.get(socket).reset();
            } catch (IOException e1) {
                System.out.println("Clearing failure in receivePacket(): " + e1.getMessage());
            }
        }
    }

    /**
     * Closes the server.
     */
    public void closeServer() {
        try {
            this.removeAllClients();
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
