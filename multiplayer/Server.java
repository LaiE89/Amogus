package multiplayer;

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
    List<Socket> clientSockets = new ArrayList<>();
    private int clientReadTime = 1000;
    public Object lock = new Object(); // A monitor lock. Ensures synchronization when accessing <clientSockets>
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
            }else if (numConnections >= 4) {
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
            serverDos.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
            serverDis.put(clientSocket, new ObjectInputStream(clientSocket.getInputStream()));
            clientSocket.setSoTimeout(clientReadTime);
            clientSockets.add(clientSocket);
            numConnections = clientSockets.size();
            sendPacket(numConnections, isGameStarted, false);
            System.out.println("CLIENT WITH LOCAL ADDRESS: " + clientSocket.getRemoteSocketAddress() + ", HAS REQUESTED TO JOIN. THE CURRENT SERVER LIST OF SIZE " + numConnections + " IS: " + clientSockets.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * If a lobby is full (i.e. 4 players is the max limit of a lobby), checks for any
     * player trying to connect to the lobby and kicks them out.
     */
    public void fullServerRequest() {
        System.out.println("Listening for server request");
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
    public void sendPacket(int numConnections, boolean isGameStarted, boolean isGameOver) {
        for (Socket socket : clientSockets) {
            try {
                Packet packet = new Packet(numConnections, isGameStarted, isGameOver);
                serverDos.get(socket).writeObject(packet);
                serverDos.get(socket).flush();
            }catch (IOException e) {
                System.out.println(e.getMessage());
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
            clientsCopy = new ArrayList<>(clientSockets);
        }
        for (Socket socket : clientsCopy) {
            try {
                System.out.println("Receiving packet from: " + socket.getPort());
                Packet value = (Packet) serverDis.get(socket).readObject();
                boolean isGameOver = value.getIsGameOver();
                if (isGameOver) {
                    numGameOvers += 1; // numGameOvers is only accessed here and this method is called once in only one thread so synchronization is not needed
                }
            }catch (SocketTimeoutException e) {
                // Nothing to read in current socket
            }catch (IOException e) {
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
        if (clientSockets.contains(socket)) {
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
            sendPacket(numConnections, isGameStarted, false);
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
            clientsCopy = new ArrayList<>(clientSockets);
        }
        for (Socket socket : clientsCopy) {
            try {
                removeClient(socket);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Clears the object input stream.
     */
    public void clearAllSentPackets() {
        List<Socket> clientsCopy;
        synchronized (lock) {
            clientsCopy = new ArrayList<>(clientSockets);
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
