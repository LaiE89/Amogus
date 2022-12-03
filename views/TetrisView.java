package views;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.effect.ColorAdjust;
import model.TetrisModel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.TetrisPoint;

import java.util.ArrayList;


/**
 * Tetris View
 *
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisView {

    public TetrisModel model; //reference to model
    Stage stage;
    Button singleplayerButton, chatButton, multiplayerButton, settingsButton, backButton; //buttons for functions
    public BorderPane borderPane;
    Canvas canvas;
    GraphicsContext gc; //the graphics context will be linked to the canvas
    public ConnectView connectView;
    public AnimationTimer timer;
    private long lastUpdate = 0;
    public Timeline timeline = new Timeline();
    int pieceWidth = 20; //width of block on display
    private double width; //height and width of canvas
    private double height;

    //settings variables
    private ColorAdjust visualSettings;
    private double brightness = 0;
    private double saturation = 0;
    private double contrast = 0;
    private String backgroundColor = "black";

    // Variables for in-game controls. They check which buttons are pressed
    BooleanProperty rotatePressed = new SimpleBooleanProperty();
    BooleanProperty rightPressed = new SimpleBooleanProperty();
    BooleanProperty leftPressed = new SimpleBooleanProperty();
    BooleanProperty downPressed = new SimpleBooleanProperty();
    BooleanProperty dropPressed = new SimpleBooleanProperty();
    BooleanBinding anyPressed = downPressed.or(rightPressed).or(leftPressed).or(rotatePressed);
    
    private static TetrisView instance; // Instance reference for singleton

    /**
     * Constructor
     *
     * @param model reference to tetris model
     * @param stage application stage
     */

    private TetrisView(TetrisModel model, Stage stage) {
        this.model = model;
        this.stage = stage;
        initUI();
    }

    /**
     * Singleton Pattern. Ensures there is only 1 instance of this class.
     *
     * @param model reference to tetris model
     * @param stage application stage
     */
    public static synchronized TetrisView getInstance(TetrisModel model, Stage stage) {
        if (instance == null) {
            instance = new TetrisView(model, stage);
        }
        return instance;
    }

    /**
     * Initialize interface
     */
    public void initUI() {
        this.stage.setTitle("CSC207 Tetris");
        this.width = this.model.getWidth()*pieceWidth + 2;
        this.height = this.model.getHeight()*pieceWidth + 2;

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: " + backgroundColor);

        //add buttons
        singleplayerButton = new Button("Singleplayer");
        singleplayerButton.setId("Singleplayer");
        singleplayerButton.setPrefSize(150, 50);
        singleplayerButton.setFont(new Font(12));
        singleplayerButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        multiplayerButton = new Button("Multiplayer");
        multiplayerButton.setId("Multiplayer");
        multiplayerButton.setPrefSize(150, 50);
        multiplayerButton.setFont(new Font(12));
        multiplayerButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        settingsButton = new Button("Settings");
        settingsButton.setId("Settings");
        settingsButton.setPrefSize(150, 50);
        settingsButton.setFont(new Font(12));
        settingsButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        VBox controls = new VBox(20, singleplayerButton, multiplayerButton, settingsButton);
        controls.setPadding(new Insets(20, 20, 20, 20));
        controls.setAlignment(Pos.CENTER);

        //configure this such that you start a new game when the user hits the multiplayerButton
        //Make sure to return the focus to the borderPane once you're done!
        multiplayerButton.setOnAction(e -> {
            //TO DO!
            this.createConnectView();
            this.borderPane.requestFocus();
        });

        //configure this such that you restart the game when the user hits the singleplayerButton
        //Make sure to return the focus to the borderPane once you're done!
        singleplayerButton.setOnAction(e -> {
            //TO DO!
            initSinglePlayerUI();
            this.model.newGame();
            this.borderPane.requestFocus();
        });

        settingsButton.setOnAction(e -> {
            initSettings();
            this.borderPane.requestFocus();
        });

        visualSettings = new ColorAdjust();
        visualSettings.setBrightness(brightness);

        borderPane.setCenter(controls);
        updateSettings();

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     * Initialize Settings interface
     */
    public void initSettings() {
        backButton = new Button("Back");
        backButton.setId("Settings");
        backButton.setPrefSize(150, 50);
        backButton.setFont(new Font(12));
        backButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        //brightness slider
        Slider brightnessSlider = new Slider(0, 1, 0.5);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setStyle("-fx-control-inner-background: palegreen;");
        //brightness label
        Label brightnessLabel = new Label("Brightness");
        brightnessLabel.setFont(new Font(20));
        brightnessLabel.setTextFill(Color.WHITE);

        //saturation slider
        Slider saturationSlider = new Slider(0, 1, 0.5);
        saturationSlider.setShowTickLabels(true);
        saturationSlider.setStyle("-fx-control-inner-background: palegreen;");
        //saturation label
        Label saturationLabel = new Label("Saturation");
        saturationLabel.setFont(new Font(20));
        saturationLabel.setTextFill(Color.WHITE);

        //contrast slider
        Slider contrastSlider = new Slider(0, 1, 0.5);
        contrastSlider.setShowTickLabels(true);
        contrastSlider.setStyle("-fx-control-inner-background: palegreen;");
        //contrast label
        Label contrastLabel = new Label("Contrast");
        contrastLabel.setFont(new Font(20));
        contrastLabel.setTextFill(Color.WHITE);

        //volume slider
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setStyle("-fx-control-inner-background: palegreen;");
        //volume label
        Label volumeLabel = new Label("Volume");
        volumeLabel.setFont(new Font(20));
        volumeLabel.setTextFill(Color.WHITE);

        //background color selector
        ComboBox backGroundColor = new ComboBox();
        backGroundColor.getItems().addAll("Red", "Blue", "Green", "Yellow", "Black", "White");
        backGroundColor.setValue("Black");
        //background color label
        Label backGroundColorLabel = new Label("Background Color");
        backGroundColorLabel.setFont(new Font(20));
        backGroundColorLabel.setTextFill(Color.WHITE);
        //background color confirm button
        Button changeColorButton = new Button("Confirm Background Change");
        changeColorButton.setId("Settings");
        changeColorButton.setPrefSize(150, 50);
        changeColorButton.setFont(new Font(12));
        changeColorButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        //hbox containing background color changing
        HBox colorChange = new HBox(20, backGroundColorLabel, backGroundColor, changeColorButton);
        colorChange.setPadding(new Insets(20, 20, 20, 20));
        colorChange.setAlignment(Pos.CENTER);

        //vbox containing all visual settings
        VBox visualSettings = new VBox(20, backButton, brightnessLabel,
                brightnessSlider, saturationLabel, saturationSlider, contrastLabel,
                contrastSlider, volumeLabel, volumeSlider, colorChange);
        visualSettings.setPadding(new Insets(20, 20, 20, 20));
        visualSettings.setAlignment(Pos.CENTER);

        //control settings label
        Label controlSettingsLabel = new Label("Controls");
        controlSettingsLabel.setFont(new Font(20));
        controlSettingsLabel.setTextFill(Color.WHITE);

        Button testButton = new Button("Test");
        testButton.setId("Test");
        testButton.setPrefSize(150, 50);
        testButton.setFont(new Font(12));
        testButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        //vbox containing all control settings
        VBox controlSettings = new VBox(20, controlSettingsLabel, testButton);
        controlSettings.setPadding(new Insets(20, 20, 20, 20));
        controlSettings.setAlignment(Pos.CENTER);

        //hbox containing all settings
        HBox settings = new HBox(50);
        settings.setPadding(new Insets(20, 20, 20, 20));
        settings.setAlignment(Pos.CENTER);
        settings.getChildren().addAll(visualSettings, controlSettings);


        backButton.setOnAction(e -> {
            initUI();
        });

        brightnessSlider.setOnMouseReleased(e -> {
            brightness = (brightnessSlider.getValue() - 0.5);
            updateSettings();
        });

        saturationSlider.setOnMouseReleased(e -> {
            saturation = (saturationSlider.getValue() - 0.5);
            updateSettings();
        });

        contrastSlider.setOnMouseReleased(e -> {
            contrast = (contrastSlider.getValue() - 0.5);
            updateSettings();
        });

        volumeLabel.setOnMouseReleased(e -> {

        });

        changeColorButton.setOnAction(e -> {
            backgroundColor = (String) backGroundColor.getValue();
            borderPane.setStyle("-fx-background-color: " + backgroundColor);
        });

        borderPane = new BorderPane();
        borderPane.setCenter(settings);
        borderPane.setStyle("-fx-background-color: " + backgroundColor);
        updateSettings();

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     * Initialize Singleplayer Game Interface
     */
    public void initSinglePlayerUI() {
        this.stage.setTitle("CSC207 Tetris");

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: " + backgroundColor);

        //add canvas
        canvas = new Canvas(this.width, this.height);
        canvas.setId("Canvas");
        gc = canvas.getGraphicsContext2D();

        final ToggleGroup addGarbageGroup = new ToggleGroup();

        RadioButton addGarbageToggle = new RadioButton("Garbage");
        addGarbageToggle.setToggleGroup(addGarbageGroup);
        addGarbageToggle.setSelected(true);
        addGarbageToggle.setUserData(Color.SALMON);
        addGarbageToggle.setFont(new Font(16));
        addGarbageToggle.setStyle("-fx-text-fill: #e8e6e3");

        Slider addGarbageSpeed = new Slider(0, 100, 50);
        addGarbageSpeed.setShowTickLabels(true);
        addGarbageSpeed.setStyle("-fx-control-inner-background: palegreen;");

        addGarbageGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> swapGarbage(newVal));
        addGarbageSpeed.setOnMouseReleased(e -> {
            //TO DO
            //double rateMultiplier = addGarbageSpeed.getValue() * 0.03;
            //this.timeline.setRate(rateMultiplier);
            //this.borderPane.requestFocus();
        });

        VBox botBox = new VBox(20, addGarbageSpeed);
        botBox.setPadding(new Insets(20, 20, 20, 20));
        botBox.setAlignment(Pos.TOP_CENTER);

        VBox rightBox = new VBox(20, addGarbageToggle);
        rightBox.setPadding(new Insets(20, 20, 20, 20));
        botBox.setAlignment(Pos.TOP_CENTER);

        // Detecting controls press
        dropPressed.set(false);
        borderPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                if (k.getCode() == KeyCode.SPACE) {
                    rotatePressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.S) {
                    downPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.A) {
                    leftPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.D) {
                    rightPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.W) {
                    if (!dropPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.DROP);
                        paintBoard();
                        dropPressed.set(true);
                    }
                }
            }
        });

        // Timer for managing the block movement speed
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 80_000_000) { // Prevent this loop from occurring more than once every 80 milliseconds
                    if (rotatePressed.get()) {
                        model.modelTick(TetrisModel.MoveType.ROTATE);
                        paintBoard();
                        rotatePressed.set(false);
                    }
                    if (downPressed.get()) {
                        if (timeline.getStatus() != Animation.Status.RUNNING) {
                            timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), e -> {
                                model.modelTick(TetrisModel.MoveType.DOWN);
                                paintBoard();
                            }));
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            timeline.play();
                        }else {
                            timeline.setRate(timeline.getCurrentRate() + 0.25);
                        }
                    }
                    if (rightPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.RIGHT);
                        paintBoard();
                    }
                    if (leftPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.LEFT);
                        paintBoard();
                    }
                    lastUpdate = now;
                }
            }
        };

        // Checking when the player releases their finger from a button
        borderPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                if (k.getCode() == KeyCode.S) {
                    downPressed.set(false);
                }
                if (k.getCode() == KeyCode.A) {
                    leftPressed.set(false);
                }
                if (k.getCode() == KeyCode.D) {
                    rightPressed.set(false);
                }
                if (k.getCode() == KeyCode.W) {
                    dropPressed.set(false);
                }
            }
        });

        // Checking if the player pressed any button
        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                timer.start();
            }else {
                model.canPlace = true;
                timer.stop();
            }
        });

        borderPane.setCenter(canvas);
        borderPane.setRight(rightBox);
        borderPane.setBottom(botBox);
        updateSettings();

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /*
    Updates settings according to the settings variables
    * */
    private void updateSettings(){
        visualSettings.setBrightness(brightness);
        visualSettings.setSaturation(saturation);
        visualSettings.setContrast(contrast);
        borderPane.setEffect(visualSettings);
    }

    /**
     * Initialize Multiplayer Game Interface
     */
    public void initGameUI() {
        this.stage.setTitle("CSC207 Tetris");

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: " + backgroundColor);

        //add canvas
        canvas = new Canvas(this.width, this.height);
        canvas.setId("Canvas");
        gc = canvas.getGraphicsContext2D();

        //labels
        chatButton = new Button("Chat");
        chatButton.setId("Chat");
        chatButton.setPrefSize(150, 50);
        chatButton.setFont(new Font(12));
        chatButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        HBox controls = new HBox(20, chatButton);
        controls.setPadding(new Insets(20, 20, 20, 20));
        controls.setAlignment(Pos.CENTER);

        chatButton.setOnAction(e -> {
            //TO DO!
            this.createChatView();
            this.borderPane.requestFocus();
        });

        // Detecting controls press
        dropPressed.set(false);
        borderPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                if (k.getCode() == KeyCode.SPACE) {
                    rotatePressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.S) {
                    downPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.A) {
                    leftPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.D) {
                    rightPressed.set(true);
                    model.canPlace = false;
                }
                if (k.getCode() == KeyCode.W) {
                    if (!dropPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.DROP);
                        paintBoard();
                        dropPressed.set(true);
                    }
                }
            }
        });

        // Timer for managing the block movement speed
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 80_000_000) { // Prevent this loop from occurring more than once every 80 milliseconds
                    if (rotatePressed.get()) {
                        model.modelTick(TetrisModel.MoveType.ROTATE);
                        paintBoard();
                        rotatePressed.set(false);
                    }
                    if (downPressed.get()) {
                        if (timeline.getStatus() != Animation.Status.RUNNING) {
                            timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), e -> {
                                model.modelTick(TetrisModel.MoveType.DOWN);
                                paintBoard();
                            }));
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            timeline.play();
                        }else {
                            timeline.setRate(timeline.getCurrentRate() + 0.25);
                        }
                    }
                    if (rightPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.RIGHT);
                        paintBoard();
                    }
                    if (leftPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.LEFT);
                        paintBoard();
                    }
                    lastUpdate = now;
                }
            }
        };

        // Checking when the player releases their finger from a button
        borderPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                if (k.getCode() == KeyCode.S) {
                    downPressed.set(false);
                }
                if (k.getCode() == KeyCode.A) {
                    leftPressed.set(false);
                }
                if (k.getCode() == KeyCode.D) {
                    rightPressed.set(false);
                }
                if (k.getCode() == KeyCode.W) {
                    dropPressed.set(false);
                }
            }
        });

        // Checking if the player pressed any button
        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                timer.start();
            }else {
                model.canPlace = true;
                timer.stop();
            }
        });

        borderPane.setTop(controls);
        borderPane.setCenter(canvas);
        updateSettings();

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    private void swapGarbage(Toggle newVal) {
    }

    /**
     * Methods to calibrate sizes of pixels relative to board size
     */
    private final int yPixel(int y) {
        return (int) Math.round(this.height -1 - (y+1)*dY());
    }
    private final int xPixel(int x) {
        return (int) Math.round((x)*dX());
    }
    private final float dX() {
        return( ((float)(this.width-2)) / this.model.getBoard().getWidth() );
    }
    private final float dY() {
        return( ((float)(this.height-2)) / this.model.getBoard().getHeight() );
    }


    /**
     * Draw the board
     */
    public void paintBoard() {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, this.width-1, this.height-1);

        // Draw the line separating the top area on the screen
        gc.setStroke(Color.GRAY);
        int spacerY = yPixel(this.model.getBoard().getHeight() - this.model.BUFFERZONE - 1);
        gc.strokeLine(0, spacerY, this.width-1, spacerY);

        // Factor a few things out to help the optimizer
        final int dx = Math.round(dX()-2);
        final int dy = Math.round(dY()-2);
        final int bWidth = this.model.getBoard().getWidth();

        // Painting the placement indicator for the current piece
        int floorYHeight = model.floorY;
        TetrisPoint[] currentPieceBody = model.currentPiece.getBody();
        for (int i = 0; i < currentPieceBody.length; i++) {
            gc.setStroke(model.currentPiece.getColor());
            gc.strokeRect(xPixel(model.currentX + currentPieceBody[i].x) + 1, yPixel(floorYHeight + currentPieceBody[i].y)+1, dx, dy);
        }

        int x, y;
        // Loop through and draw all the blocks; sizes of blocks are calibrated relative to screen size
        for (x=0; x<bWidth; x++) {
            int left = xPixel(x);	// the left pixel
            // draw from 0 up to the col height
            final int yHeight = this.model.getBoard().getColumnHeight(x);
            for (y=0; y<yHeight; y++) {
                if (this.model.getBoard().getGrid(x, y)) {
                    gc.setFill(this.model.getBoard().getGridColor(x, y));
                    gc.fillRect(left+1, yPixel(y)+1, dx, dy);
                    gc.setFill(Color.BLACK);
                }
            }
        }
    }

    /**
     * Create the view to chat with players in the lobby
     */
    private void createChatView(){
        // TODO: Change LoadView class into a UI for chatting
    }

    /**
     * Create the view to connect to a game lobby or start a game lobby
     */
    private void createConnectView() {
        connectView = new ConnectView(this, model);
    }

}