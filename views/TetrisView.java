package views;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;
import model.TetrisApp;
import model.TetrisBoard;
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

import java.net.Socket;
import java.util.*;


/**
 * Tetris View
 *
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisView {

    // UI Variables
    public TetrisModel model; //reference to model
    public BorderPane borderPane;
    Stage stage;
    Button singleplayerButton, chatButton, multiplayerButton, settingsButton; //buttons for functions
    Canvas canvas;
    GraphicsContext gc; //the graphics context will be linked to the canvas
    protected Group opBoard1;
    protected Group opBoard2;
    protected Group opBoard3;
    protected Group opBoard4;

    // Board Variables
    int pieceWidth = 20; //width of block on display
    public double width; //height and width of canvas
    public double height;

    // Scene References;
    public ConnectView connectView;
    public SettingsView settingsView;
    public GameView gameView;

    // Instance reference for singleton
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
        borderPane.setStyle("-fx-background-color: " + SettingsView.backgroundColor);

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
            createSinglePlayerView();
            this.model.newGame();
            this.borderPane.requestFocus();
        });

        settingsButton.setOnAction(e -> {
            createSettingsView();
            this.borderPane.requestFocus();
        });

        //visualSettings = new ColorAdjust();
        //visualSettings.setBrightness(brightness);

        borderPane.setCenter(controls);
        SettingsView.updateSettings(borderPane);

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

    public void paintOpponentBoards(int boardPos, TetrisBoard opponentBoard) {
        switch (boardPos) {
            case 1:
                //opBoard1 = drawOpponentBoard(0, 0, opponentBoard);
            case 2:
                //opBoard2 = drawOpponentBoard(0, 0, opponentBoard);
            case 3:
                //opBoard3 = drawOpponentBoard(0, 0, opponentBoard);
            case 4:
                //opBoard4 = drawOpponentBoard(0, 0, opponentBoard);
        }
    }

    private Group drawOpponentBoard(int posX, int posY, TetrisBoard opBoard) {
        Group result = new Group();
        int ratio = 4; // ratio from regular board size to opponent board size
        double oppHeight = (this.model.getHeight() - 1) / ratio;
        double oppWidth = (this.model.getWidth() - 1) / ratio;
        double opHeight = this.model.getHeight() / ratio;
        double opWidth = this.model.getWidth() / ratio;
        Rectangle board = new Rectangle(posX, posY, oppWidth, oppHeight);
        board.setFill(Color.BLACK);
        board.setStroke(Color.BLACK);
        result.getChildren().add(board);

        // Draw the line separating the top area on the screen
        int spacerY = yPixel(opBoard.getHeight() - this.model.BUFFERZONE - 1);
        Line line = new Line(posX, spacerY, oppWidth, spacerY);
        line.setStroke(Color.GRAY);
        result.getChildren().add(line);

        // Factor a few things out to help the optimizer
        final float dX = (float)((opWidth-2) / opWidth);
        final float dY = (float)((opHeight-2) / opHeight);
        final int dx = Math.round(dX - 2);
        final int dy = Math.round(dY - 2);
        final int bWidth = (opBoard.getWidth());

        int x, y;
        // Loop through and draw all the blocks; sizes of blocks are calibrated relative to screen size
        for (x = 0; x < bWidth; x++) {
            int left = xPixel(x);    // the left pixel
            // draw from 0 up to the col height
            final int yHeight = opBoard.getColumnHeight(x);
            for (y = 0; y < yHeight; y++) {
                if (opBoard.getGrid(x, y)) {
                    Rectangle piece = new Rectangle(left + 1, yPixel(y), dx, dy);
                    piece.setFill(opBoard.getGridColor(x, y));
                    result.getChildren().add(piece);
                }
            }
        }
        return result;
    }

    /**
     * Create the settings view to modify the client's settings
     */
    private void createSettingsView() {
        settingsView = new SettingsView();
    }

    /**
     * Create a single player game view
     */
    private void createSinglePlayerView() {
        //add canvas
        canvas = new Canvas(this.width, this.height);
        canvas.setId("Canvas");
        gc = canvas.getGraphicsContext2D();
        gameView = new SinglePlayerView();
    }

    /**
     * Create a multiplayer game view
     */
    public void createMultiplayerView() {
        //add canvas
        canvas = new Canvas(this.width, this.height);
        canvas.setId("Canvas");
        gc = canvas.getGraphicsContext2D();
        gameView = new MultiplayerView();
    }

    /**
     * Create the view to connect to a game lobby or start a game lobby
     */
    private void createConnectView() {
        connectView = new ConnectView(this, model);
    }

}