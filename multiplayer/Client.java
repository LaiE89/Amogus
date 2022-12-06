package multiplayer;

import javafx.application.Platform;
import javafx.util.Pair;
import model.TetrisApp;
import model.TetrisBoard;
import model.TetrisModel;
import views.ConnectView;
import views.MultiplayerView;
import views.TetrisView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client extends Thread {
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
    public int receiveGarbageLines = 0;
    public HashMap<Integer, Pair<Integer, TetrisBoard>> opponentBoards = new HashMap<>();

    /**
     * Constructor
     *
     * @param tetrisView current client's tetrisView
     * @param socket current client's socket
     */
    public Client(TetrisView tetrisView, Socket socket) throws IOException {
        this.tetrisView = tetrisView;
        this.connectView = tetrisView.connectView;
        this.model = tetrisView.model;

        this.socket = socket;
        this.dis = new ObjectInputStream(this.socket.getInputStream());
        this.dos = new ObjectOutputStream(this.socket.getOutputStream());
        this.localPort = this.socket.getLocalPort();

        opponentBoards.put(1, null);
        opponentBoards.put(2, null);
        opponentBoards.put(3, null);
        opponentBoards.put(4, null);
        System.out.println("CREATED NEW CLIENT, CLIENT'S CONNECTED TO: " + this.socket.getInetAddress() + ": " + this.socket.getPort() + ", CLIENT'S LOCAL DETAILS: " + this.socket.getLocalSocketAddress());
    }

    public void run() {
        Thread updateBoard = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    // TODO There is a bug involving receiving garbage while the current block is falling. This causes the server to detect the client as a disconnect occasionally
                    if (receiveGarbageLines > 0 && model.currentY >= model.HEIGHT) {
                        model.modelTick(TetrisModel.MoveType.GARBAGE);
                        TetrisApp.view.paintBoard();
                    }
                }
            }
        });
        updateBoard.start();

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
            if (value.getSendGarbageLines() > 0) {
                this.receiveGarbageLines += value.getSendGarbageLines();
                System.out.println(this.socket.getLocalPort() + " got " + value.getSendGarbageLines() + " garbage lines. Current garbage lines: " + this.receiveGarbageLines);
            }
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
                        tetrisView.gameView.borderPane.requestFocus();
                    }
                });
            }
            if (value.getSenderBoard() != null && value.getSender() != this.socket.getLocalPort() && !value.getIsGameOver()) {
                for (int pos : this.opponentBoards.keySet()) {
                    if (this.opponentBoards.get(pos) == null || this.opponentBoards.get(pos).getKey() == value.getSender()) {
                        this.opponentBoards.put(pos, new Pair<>(value.getSender(), value.getSenderBoard()));
                        TetrisApp.view.paintOpponentBoards(pos, value.getSenderBoard());
                        break;
                    }
                }
            }
        } catch (IOException e1) {
            try {
                System.out.println("SERVER GOT SHUTDOWN: " + e1);
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
     * @param sendGarbageLines the amount of garbage lines that this client is sending
     */
    public void sendPacket(int numConnections, boolean isGameStarted, boolean isGameOver, int sendGarbageLines) {
        try {
            Packet packet = new Packet(this.socket.getLocalPort(), numConnections, isGameStarted, isGameOver, sendGarbageLines, this.model.getBoard());
            dos.reset();
            dos.writeObject(packet);
            dos.flush();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
