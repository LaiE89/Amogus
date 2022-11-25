package multiplayer;

import javafx.application.Platform;
import views.ConnectView;
import views.TetrisView;

import java.io.BufferedReader;
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
    public Object lock = new Object();
    public int numConnections = 0;
    public boolean isGameStarted = false;
    public int numGameOvers = 0;

    public Server(TetrisView tetrisView, String ip, int port) {
        this.tetrisView = tetrisView;
        this.connectView = tetrisView.connectView;
        try {
            this.serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        }catch (Exception e) {
            connectView.addressErrorLabel.setText(e.getMessage());
        }
        new Thread(this).start();
        //ServerHeartbeat heartbeat = new ServerHeartbeat(this);
        //heartbeat.start();
    }

    public void run() {
        //ServerHeartbeat heartbeat = new ServerHeartbeat(this);
        //heartbeat.start();
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
        while (!serverSocket.isClosed()) {
            if (!isGameStarted && numConnections < 4) {
                listenForServerRequest();
            }else if (numConnections >= 4) {
                fullServerRequest();
            }
        }
    }

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

    public void sendPacket(int numConnections, boolean isGameStarted, boolean isGameOver) {
        for (Socket socket : clientSockets) {
            try {
                //serverDos.get(socket).writeInt(number);
                Packet packet = new Packet(numConnections, isGameStarted, isGameOver);
                serverDos.get(socket).writeObject(packet);
                serverDos.get(socket).flush();
            }catch (IOException e) {
                System.out.println(e.getMessage());
                //System.out.println("SOCKET WITH PORT " + socket.getPort() + " HAS DISCONNECTED: " + e.getMessage());
            }
        }
    }

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
                    numGameOvers += 1;
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

    public void removeClient(Socket socket) throws IOException {
        if (clientSockets.contains(socket)) {
            clientSockets.remove(socket);
            serverDis.remove(socket);
            serverDos.remove(socket);
            socket.close();
        }
    }

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

    public void closeServer() {
        try {
            this.removeAllClients();
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
