package multiplayer;

import javafx.application.Platform;
import model.TetrisModel;
import views.ConnectView;
import views.MultiplayerView;
import views.TetrisView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Thread{
    private TetrisView tetrisView;
    private ConnectView connectView;
    private TetrisModel model;

    //public boolean accepted = false;
    public Socket socket;
    public ObjectOutputStream dos; // Sends data to server
    public ObjectInputStream dis; // Receives data from server
    public int localPort;
    public int numConnections = 0;
    public boolean isGameStarted = false;

    /**
     * Constructor
     *
     * @param tetrisView current client's tetrisView
     * @param socket current client's socket
     */
    public Client(TetrisView tetrisView, Socket socket) throws IOException{
        this.tetrisView = tetrisView;
        this.connectView = tetrisView.connectView;
        this.model = tetrisView.model;

        this.socket = socket;
        this.dis = new ObjectInputStream(this.socket.getInputStream());
        this.dos = new ObjectOutputStream(this.socket.getOutputStream());
        this.localPort = this.socket.getLocalPort();
        System.out.println("CREATED NEW CLIENT, CLIENT'S CONNECTED TO: " + this.socket.getInetAddress() + ": " + this.socket.getPort() + ", CLIENT'S LOCAL DETAILS: " + this.socket.getLocalSocketAddress());
    }

    public void run() {

        // Always receiving packets of data from the server
        while (!this.socket.isClosed()) {
            getPacket();
        }

        // "Deleting" this instance of client
        ConnectView.client = null;
    }

    /**
     * Closes this client's socket.
     */
    public void closeClientConnection() {
        //sendPacket(numConnections, isGameStarted, localPort);
        try {
            System.out.println(this.socket.getLocalPort() + " has disconnected.");
            this.socket.close();
            ConnectView.client = null;
        } catch (IOException e) {
            System.out.println("IOException in closeClientConnection(): " + e.getMessage());
        }
    }

    /**
     * Gets any available packets of data from the server.
     * If it finds any specific information from the packet,
     * does an action correspondingly.
     */
    public void getPacket() {
        try {
            //this.isGameStarted = dis.readBoolean();
            Packet value = (Packet) dis.readObject();
            this.numConnections = value.getNumConnections();
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    connectView.startButton.setText("Number of players: " + numConnections);
                }
            });
            if (!this.isGameStarted && value.getIsGameStarted()) {
                this.isGameStarted = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tetrisView.createMultiplayerView();
                        model.newGame();
                        connectView.dialog.close();
                        tetrisView.borderPane.requestFocus();
                    }
                });
            }
        } catch (IOException e1) {
            try {
                System.out.println("SERVER GOT SHUTDOWN");
                this.socket.close();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (isGameStarted) {
                            tetrisView.initUI();
                            model.controlsTimer.stop();
                            model.downTimeline.stop();
                            //tetrisView.gameView.timer.stop();
                            //tetrisView.gameView.timeline.stop();
                            model.gameOn = false;
                        }
                        else {
                            connectView.dialog.close();
                            tetrisView.borderPane.requestFocus();
                        }
                    }
                });
            }catch (Exception e) {
                System.out.println("Socket Close failure in getPacket(): " + e.getMessage());
            }
            System.out.println("Read Boolean failure in getPacket(): " + e1.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println("Class not found failure in getPacket(): " + e.getMessage());
        }
    }

    /**
     * Sends a packet of data to the server. Usually only used send information that this client has
     * lost.
     *
     * @param numConnections the number of total connections to the lobby
     * @param isGameStarted true if the lobby game has started
     * @param isGameOver true if this client lost
     */
    public void sendPacket(int numConnections, boolean isGameStarted, boolean isGameOver) {
        try {
            Packet packet = new Packet(numConnections, isGameStarted, isGameOver);
            dos.writeObject(packet);
            dos.flush();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
