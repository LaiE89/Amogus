package views;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.TetrisApp;
import model.TetrisModel;
import multiplayer.Client;
import multiplayer.Server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * Save File View
 * 
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class ConnectView {

    public Label addressErrorLabel = new Label("");
    private Label addressLabel = new Label(String.format("Enter IP address and Port: "));
    private TextField ipTextField = new TextField("localhost");
    private TextField portTextField = new TextField("22222");
    private Button connectButton = new Button("Connect");
    public Button startButton = new Button("Start");
    public Stage dialog = new Stage();
    TetrisView tetrisView;
    TetrisModel tetrisModel;

    private String ip;
    private int port;
    public static Client client;
    public static Server clientHostedServer;


     /**
         * Constructor
         * 
         * @param tetrisView master view
         */
    public ConnectView(TetrisView tetrisView, TetrisModel tetrisModel) {
        this.tetrisView = tetrisView;
        this.tetrisModel = tetrisModel;

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tetrisView.stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #121212;");

        addressLabel.setId("SaveBoard"); // DO NOT MODIFY ID
        addressLabel.setStyle("-fx-text-fill: #e8e6e3;");
        addressLabel.setFont(new Font(16));

        addressErrorLabel.setId("SaveFileErrorLabel");
        addressErrorLabel.setStyle("-fx-text-fill: #e8e6e3;");
        addressErrorLabel.setFont(new Font(16));
        addressErrorLabel.setWrapText(true);

        ipTextField.setId("SaveFileNameTextField");
        ipTextField.setStyle("-fx-text-fill: black;");
        ipTextField.setFont(new Font(16));
        ipTextField.setText("localhost");

        portTextField.setId("PortTextField");
        portTextField.setStyle("-fx-text-fill: black;");
        portTextField.setFont(new Font(16));
        portTextField.setText("22222");

        connectButton = new Button("Connect");
        connectButton.setId("SaveBoard"); // DO NOT MODIFY ID
        connectButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        connectButton.setPrefSize(200, 50);
        connectButton.setFont(new Font(16));
        connectButton.setOnAction(e -> {
            this.ip = ipTextField.getText();
            if (isInteger(portTextField.getText())) {
                if (Integer.parseInt(portTextField.getText()) < 1 || Integer.parseInt(portTextField.getText()) > 65535) { // Ports can only take on the values of a position short (16-bit integer)
                    addressErrorLabel.setText("The port must be between 1 and 65535");
                }else {
                    this.port = Integer.parseInt(portTextField.getText());
                    if (!connect(ip, port)) {
                        if (portAvailable(Integer.parseInt(portTextField.getText()))) {
                            initializeServer();
                        }else {
                            addressErrorLabel.setText("The port is already in use");
                        }
                    }
                }
            }else {
                addressErrorLabel.setText("The port must be a number");
            }
        });

        startButton = new Button("Start");
        startButton.setWrapText(true);
        startButton.setId("StartGame");
        startButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        startButton.setPrefSize(200, 50);
        startButton.setFont(new Font(16));
        startButton.setOnAction(e -> {
            if (client != null && clientHostedServer != null && client.numConnections >= 2 && client.numConnections <= 4) {
                clientHostedServer.isGameStarted = true;
                clientHostedServer.clearAllSentPackets();
                clientHostedServer.sendPacket(clientHostedServer.numConnections, true, false);
                System.out.println("GAME STARTED");
            }
        });

        VBox saveBoardBox = new VBox(10, addressLabel, ipTextField, portTextField, connectButton, startButton, addressErrorLabel);

        dialogVbox.getChildren().add(saveBoardBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
        Platform.setImplicitExit(false);
        dialog.setOnCloseRequest(event -> {
            if (client != null && !client.isGameStarted) {
                if (clientHostedServer != null) {
                    clientHostedServer.closeServer();
                    try {
                        client.socket.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }else {
                    client.closeClientConnection();
                }
            }
        });
    }

    /**
     * Save the board to a file 
     */

    private boolean connect(String ip, int port) {
        try {
            /*this.tetrisModel.socket = new Socket(ip, port);
            this.tetrisModel.dos = new DataOutputStream(this.tetrisModel.socket.getOutputStream());
            this.tetrisModel.dis = new DataInputStream(this.tetrisModel.socket.getInputStream());
            this.tetrisModel.accepted = true;
            this.tetrisModel.isMultiplayer = true;
            addressErrorLabel.setText("Connected with server with address: " + ip + ":" + port); */
            Socket socket = new Socket(ip, port);
            client = new Client(this.tetrisView, socket);
            new Thread(client).start();
            connectButton.setVisible(false);
        }catch (IOException e) {
            addressErrorLabel.setText("Unable to connect to address: " + ip + ":" + port + " | Starting a server");
            return false;
        }
        return true;
    }

    private void initializeServer() {
        try {
            //this.tetrisModel.serverSocket = new ServerSocket(this.port, 8, InetAddress.getByName(this.ip));
            clientHostedServer = new Server(this.tetrisView, this.ip, this.port);
        }catch (Exception e) {
            addressErrorLabel.setText(e.getMessage());
        }
    }

    private boolean portAvailable(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
