package multiplayer;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import model.TetrisApp;
import model.TetrisModel;
import views.ConnectView;
import views.TetrisView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
        while (!this.socket.isClosed()) {
            getPacket();
        }
    }

    public void closeClientConnection() {
        //sendPacket(numConnections, isGameStarted, localPort);
        try {
            System.out.println(this.socket.getLocalPort() + " has disconnected.");
            this.socket.close();
        } catch (IOException e) {
            System.out.println("IOException in closeClientConnection(): " + e.getMessage());
        }
    }

    public void getPacket() {
        try {
            //this.isGameStarted = dis.readBoolean();
            Packet value = (Packet) dis.readObject();
            this.numConnections = value.getNumConnections();
            this.isGameStarted = value.getIsGameStarted();
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    connectView.startButton.setText("Number of players: " + numConnections);
                }
            });
            if (this.isGameStarted) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tetrisView.initGameUI();
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
                            tetrisView.timer.stop();
                            tetrisView.timeline.stop();
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
