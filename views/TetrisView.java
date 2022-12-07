package views;

import model.TetrisBoard;
import model.TetrisModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TetrisPiece;
import model.TetrisPoint;
import java.util.HashMap;
import javafx.scene.input.KeyCode;
import sounds.AudioManager;

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
    protected Canvas holdPieceVisual;
    protected GraphicsContext holdgc;
    protected Canvas opBoard1 = new Canvas();
    protected Canvas opBoard2 = new Canvas();
    protected Canvas opBoard3 = new Canvas();
    protected Canvas opBoard4 = new Canvas();

    // Board Variables
    int pieceWidth = 20; //width of block on display
    public double width; //height and width of canvas
    public double height;

    // Controls
    public HashMap<Integer, KeyCode> controlMap = new HashMap<>();

    // Scene References;
    public ConnectView connectView;
    public SettingsView settingsView;
    public GameView gameView;

    // Instance reference for singleton
    private static TetrisView instance;
    public AudioManager audioManager;

    /**
     * Constructor
     *
     * @param model reference to tetris model
     * @param stage application stage
     */

    private TetrisView(TetrisModel model, Stage stage) {
        this.model = model;
        this.stage = stage;
        audioManager = AudioManager.getInstance();
        // Initializing control map. These are the default controls
        controlMap.put(0, KeyCode.W); //0 represents drop
        controlMap.put(1, KeyCode.A); //1 represents left
        controlMap.put(2, KeyCode.D); //2 represents right
        controlMap.put(3, KeyCode.S); //3 represents down
        controlMap.put(4, KeyCode.SPACE); //4 represents rotate
        controlMap.put(5, KeyCode.E); //5 represents hold

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

        Label titleLabel = new Label("TETRIS");
        titleLabel.setFont(new Font(50));
        titleLabel.setTextFill(Color.WHITE);

        VBox controls = new VBox(20, titleLabel, singleplayerButton, multiplayerButton, settingsButton);
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

    /**
<<<<<<< HEAD
     * Draw the hold piece visualizer
     */
    public void paintHoldPiece() {

        holdgc.setStroke(Color.BLACK);
        holdgc.setFill(Color.BLACK);
        holdgc.fillRect(0, 0, 4 * pieceWidth + 2, 6 * pieceWidth + 2);

        float dX = (float) (4 * pieceWidth + 2 - 2) / 4;
        float dY = (float) (4 * pieceWidth + 2 - 2) / 4;
        final int dx = Math.round(dX - 2);
        final int dy = Math.round(dY - 2);

        if (!this.model.holdPiece.isEmpty()) {
            TetrisPiece holdPiece = this.model.holdPiece.get(0);
            for (TetrisPoint point : holdPiece.getBody()) {
                holdgc.setFill(holdPiece.getColor());
                holdgc.fillRect((int) Math.round((point.x) * dX) + 1, (int) Math.round(4 * pieceWidth + 2 - 1 - (point.y + 1) * dY) + 1, dx, dy);
                holdgc.setFill(Color.BLACK);
            }
        }
    }

    /**
     * Given a board position and an opponent board, draws the
     * Tetris board in the given position.
     *
     * @param boardPos an integer representing the board position
     * @param opponentBoard a TetrisBoard object representing the opponents board
     */
    public void paintOpponentBoards(int boardPos, TetrisBoard opponentBoard) {
        //System.out.println("Board Position: " + boardPos + ", Opponent Board: " + opponentBoard);
        switch (boardPos) {
            case 1:
                drawOpponentBoard(opBoard1, opponentBoard);
                break;
            case 2:
                drawOpponentBoard(opBoard2, opponentBoard);
                break;
            case 3:
                drawOpponentBoard(opBoard3, opponentBoard);
                break;
            case 4:
                drawOpponentBoard(opBoard4, opponentBoard);
                break;
            default:
                break;
        }
    }

    /**
     * Given a canvas and a board. Draws the board using the 2D graphics
     * context of the canvas.
     *
     * @param canvas the canvas that the board will be drawn on
     * @param opBoard a TetrisBoard object the board that will be drawn
     */
    private void drawOpponentBoard(Canvas canvas, TetrisBoard opBoard) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int ratio = 2; // ratio from regular board size to opponent board size

        double opHeight = this.height / ratio;
        double opWidth = this.width / ratio;
        final float dX = ((float)(opWidth-2)) / opBoard.getWidth();
        final float dY = ((float)(opHeight-2)) / opBoard.getHeight();
        final int dx = Math.round(dX-2);
        final int dy = Math.round(dY-2);
        final int bWidth = opBoard.getWidth();

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0,  opWidth-1, opHeight-1);

        // Draw the line separating the top area on the screen
        gc.setStroke(Color.GRAY);
        int spacerY = (int) Math.round(opHeight -1 - ( (opBoard.getHeight() - this.model.BUFFERZONE - 1) +1)*dY);
        gc.strokeLine(0, spacerY, opWidth-1, spacerY);

        int x, y;
        // Loop through and draw all the blocks; sizes of blocks are calibrated relative to screen size
        for (x=0; x<bWidth; x++) {
            int left = (int)Math.round((x)*dX) ;	// the left pixel
            // draw from 0 up to the col height
            final int yHeight = opBoard.getColumnHeight(x);
            for (y=0; y<yHeight; y++) {
                if (opBoard.getGrid(x, y)) {
                    gc.setFill(opBoard.getGridColor(x, y));
                    gc.fillRect(left+1, (int) Math.round(opHeight -1 - (y+1)*dY) + 1, dx, dy);
                    gc.setFill(Color.BLACK);
                }
            }
        }
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

        holdPieceVisual = new Canvas(4*pieceWidth + 2, 4*pieceWidth + 2);
        holdPieceVisual.setId("HoldPieceVisual");
        holdgc = holdPieceVisual.getGraphicsContext2D();

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

        holdPieceVisual = new Canvas(4*pieceWidth + 2, 4*pieceWidth + 2);
        holdPieceVisual.setId("HoldPieceVisual");
        holdgc = holdPieceVisual.getGraphicsContext2D();

        gameView = new MultiplayerView();
    }

    /**
     * Create the view to connect to a game lobby or start a game lobby
     */
    private void createConnectView() {
        connectView = new ConnectView(this, model);
    }
}