package views;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.shape.Rectangle;
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

    Button singleplayerButton, chatButton, multiplayerButton; //buttons for functions

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
    private static TetrisView instance;

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
        borderPane.setStyle("-fx-background-color: #121212;");

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

        VBox controls = new VBox(20, singleplayerButton, multiplayerButton);
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
            initGameUI();
            this.model.newGame();
            this.borderPane.requestFocus();
        });


        borderPane.setCenter(controls);

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void initGameUI() {
        this.stage.setTitle("CSC207 Tetris");

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #121212;");

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

        // Creating variables for checking which buttons are pressed
        BooleanProperty rotatePressed = new SimpleBooleanProperty();
        BooleanProperty rightPressed = new SimpleBooleanProperty();
        BooleanProperty leftPressed = new SimpleBooleanProperty();
        BooleanProperty downPressed = new SimpleBooleanProperty();
        BooleanProperty dropPressed = new SimpleBooleanProperty();
        BooleanBinding anyPressed = rotatePressed.or(rightPressed).or(leftPressed).or(downPressed);

        borderPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                if (k.getCode() == KeyCode.SPACE) {
                    rotatePressed.set(true);
                }
                if (k.getCode() == KeyCode.S) {
                    downPressed.set(true);
                }
                if (k.getCode() == KeyCode.A) {
                    leftPressed.set(true);
                }
                if (k.getCode() == KeyCode.D) {
                    rightPressed.set(true);
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

        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                timer.start();
            }else {
                timer.stop();
            }
        });

        borderPane.setTop(controls);
        borderPane.setCenter(canvas);

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
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

        // Painting the area that the current piece is going to land
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
                    gc.setFill(Color.WHITE);
                    gc.fillRect(left+1, yPixel(y)+1, dx, dy);
                    gc.setFill(Color.BLACK);
                }
            }
        }
        // Attempt to make blocks multicolored
        /*
        // Draw the line separating the top area on the screen
        gc.setStroke(Color.BLACK);
        int spacerY = yPixel(this.model.getBoard().getHeight() - this.model.BUFFERZONE - 1);
        gc.strokeLine(0, spacerY, this.width - 1, spacerY);

        // Factor a few things out to help the optimizer
        final int dx = Math.round(dX() - 2);
        final int dy = Math.round(dY() - 2);
        final int bWidth = this.model.getBoard().getWidth();
        final int bHeight = this.model.getBoard().getHeight();

        for (int x = 0; x < bWidth; x++) {
            int left = xPixel(x);
            for (int y = 0; y < bHeight; y++) {
                if (!this.model.getBoard().getGrid(x, y)) {
                    //gc.setStroke(Color.GREEN);
                    gc.setFill(Color.BLACK);
                    gc.setStroke(Color.BLACK);
                    gc.fillRect(left + 1, yPixel(y) + 1, dX(), dY());
                    gc.strokeRect(left + 1, yPixel(y) + 1, dX(), dY());
                }else {
                    gc.setFill(Color.WHITE);
                    gc.setStroke(model.currentPiece.getColor());
                    gc.fillRect(left + 1, yPixel(y) + 1, dx, dy);
                    gc.strokeRect(left + 1, yPixel(y) + 1, dx, dy);
                }
            }
        }

        // The placement indicator. It paints the area that the current piece is going to land
        int floorYHeight = model.floorY;
        TetrisPoint[] currentPieceBody = model.currentPiece.getBody();
        for (int i = 0; i < currentPieceBody.length; i++) {
            gc.setStroke(model.currentPiece.getColor());
            gc.strokeRect(xPixel(model.currentX + currentPieceBody[i].x) + 1, yPixel(floorYHeight + currentPieceBody[i].y) + 1, dx, dy);
            //gc.setFill(model.currentPiece.getColor());
            //gc.fillRect(xPixel(model.currentX + currentPieceBody[i].x) + 1, yPixel(model.currentY + currentPieceBody[i].y) + 1, dx, dy);
        }*/
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
    private void createConnectView(){
        connectView = new ConnectView(this, model);
    }

}