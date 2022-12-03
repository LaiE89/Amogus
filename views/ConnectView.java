package views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TetrisModel;
import multiplayer.Client;
import multiplayer.Server;
import java.io.*;
import java.net.*;

/** 
 * Connect View
 *
 * Allows the user to start a game lobby and connect to a game lobby
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

        addressLabel.setId("Connect"); // DO NOT MODIFY ID
        addressLabel.setStyle("-fx-text-fill: #e8e6e3;");
        addressLabel.setFont(new Font(16));

        addressErrorLabel.setId("AddressErrorLabel");
        addressErrorLabel.setStyle("-fx-text-fill: #e8e6e3;");
        addressErrorLabel.setFont(new Font(16));
        addressErrorLabel.setWrapText(true);

        ipTextField.setId("IPTextField");
        ipTextField.setStyle("-fx-text-fill: black;");
        ipTextField.setFont(new Font(16));
        ipTextField.setText("localhost");

        portTextField.setId("PortTextField");
        portTextField.setStyle("-fx-text-fill: black;");
        portTextField.setFont(new Font(16));
        portTextField.setText("22222");

        connectButton = new Button("Connect");
        connectButton.setId("Connect"); // DO NOT MODIFY ID
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
                            addressErrorLabel.setText("This port is already in use");
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
     * Returns true if there is a server with an ip and port that
     * matches <ip> and <port>. If so, connects to the server.
     * Otherwise, returns false.
     *
     * @param ip the local IP address of the server
     * @param port the port that the server is hosted on
     *
     * @return true if there is a server with an ip and port that matches the parameters
     */
    private boolean connect(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            client = new Client(this.tetrisView, socket);
            new Thread(client).start();
            connectButton.setVisible(false);
            addressErrorLabel.setText("Connected to lobby. Wait for host to start.");
        }catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Starts up a server.
     */
    private void initializeServer() {
        try {
            String hostIp = InetAddress.getLocalHost().getHostAddress();
            addressErrorLabel.setText("Unable to connect to address: " + ip + ": " + port + " | Starting a server with address: " + hostIp + ": " + port);
            ipTextField.setText(hostIp);
            clientHostedServer = new Server(this.tetrisView, hostIp, this.port);

            // Automatically connect this client to the server
            Socket socket = new Socket(hostIp, port);
            client = new Client(this.tetrisView, socket);
            new Thread(client).start();
            connectButton.setVisible(false);
        }catch (Exception e) {
            addressErrorLabel.setText(e.getMessage());
        }
    }

    /**
     * Checks if port is available.
     * THIS WAS COPIED FROM THE INTERNET SO REFACTOR IT
     *
     * @param port the port to check
     *
     * @return true if the port is available.
     */
    private boolean portAvailable(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            //
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                }catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

    /**
     * Checks if a string can be cast an integer.
     * THIS WAS COPIED FROM THE INTERNET SO REFACTOR IT
     *
     * @param str the string to check
     *
     * @return true if string can be cast as an integer
     */
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
