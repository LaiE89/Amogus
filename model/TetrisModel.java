package model;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import multiplayer.Client;
import views.ConnectView;
import views.GameView;
import views.MultiplayerView;
import views.TetrisView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/** Represents a Tetris Model for Tetris.  
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisModel implements Serializable {

    public static final int WIDTH = 10; //size of the board in blocks
    public static final int HEIGHT = 20; //height of the board in blocks
    public static final int BUFFERZONE = 4; //space at the top

    protected TetrisBoard board;  // Board data structure
    protected TetrisPiece[] pieces; // Pieces to be places on the board
    public TetrisPiece currentPiece; //Piece we are currently placing
    protected TetrisPiece newPiece; //next piece to be placed
    protected int count;		 // how many pieces played so far
    protected int score; //the player's score
    public int currentX;
    protected int newX;
    public int currentY;
    protected int newY;
    public int floorY; // y-value that the piece will fall to
    public boolean canPlace = true;
    public LinkedList<TetrisPiece> holdPiece = new LinkedList<>();

    // State of the game
    public boolean gameOn;	// true if we are playing
    protected Random random;	 // the random generator for new pieces

    // Multiplayer variables
    boolean isMultiplayer = false;
    Client client;

    // Controls
    public AnimationTimer controlsTimer;
    public Timeline downTimeline = new Timeline();
    public boolean isLeftPressed = false;
    public boolean isRightPressed = false;
    public boolean isDownPressed = false;
    private long lastUpdate = 0;

    public enum MoveType {
        ROTATE,
        LEFT,
        RIGHT,
        DROP,
        DOWN,
        HOLD
    }

    /**
     * Constructor for a tetris model
     */
    public TetrisModel() {
        board = new TetrisBoard(WIDTH, HEIGHT + BUFFERZONE);
        pieces = TetrisPiece.getPieces(); //initialize board and pieces
        gameOn = false;

        // Creating new animation timer for controls
        controlsTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            if (now - lastUpdate >= 80_000_000) { // Prevent this loop from occurring more than once every 80 milliseconds
                if (isDownPressed) {
                    if (downTimeline.getStatus() != Animation.Status.RUNNING) {
                        downTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                            modelTick(TetrisModel.MoveType.DOWN);
                            TetrisApp.view.paintBoard();
                        }));
                        downTimeline.setCycleCount(Timeline.INDEFINITE);
                        downTimeline.play();
                    }else {
                        downTimeline.setRate(10);
                    }
                }
                if (isRightPressed) {
                    modelTick(TetrisModel.MoveType.RIGHT);
                    TetrisApp.view.paintBoard();
                }
                if (isLeftPressed) {
                    modelTick(TetrisModel.MoveType.LEFT);
                    TetrisApp.view.paintBoard();
                }
                lastUpdate = now;
            }
            }
        };
    }


    /**
     * Start new game
     */
    public void startGame() { //start game
        random = new Random();
        addNewPiece();
        gameOn = true;
        score = 0;
        count = 0;
        holdPiece.clear();
        if (TetrisApp.view != null) {
            TetrisApp.view.paintBoard();
            TetrisApp.view.paintHoldPiece();
        }

        // Check if current game is multiplayer
        if (ConnectView.client != null && ConnectView.client.isGameStarted) {
            System.out.println("Game is multiplayer.");
            this.isMultiplayer = true;
            this.client = ConnectView.client;
        }else {
            this.isMultiplayer = false;
        }
    }

    /**
     * Board getter
     *
     * @return  board
     */
    public TetrisBoard getBoard() {
        return this.board;
    }

    /**
     * Compute New Position of piece in play based on move type
     * 
     * @param verb type of move to account for
     */
    public void computeNewPosition(MoveType verb) {

        // As a starting point, the new position is the same as the old
        newPiece = currentPiece;
        newX = currentX;
        newY = currentY;

        // Make changes based on the verb
        switch (verb) {
            case LEFT: newX--; break; //move left

            case RIGHT: newX++; break; //move right

            case ROTATE: //rotate
                newPiece = newPiece.fastRotation();
                newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
                newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
                break;
            case DOWN:
                newY--;
                break;
            case DROP: //drop
                newY = board.placementHeight(newPiece, newX, newY);
                if (newY > currentY) { //piece can't move up!
                    newY = currentY;
                }
                break;
            case HOLD:
                // If hold piece queue is empty, just get next piece and push current piece into the queue.
                // Otherwise, switch piece from queue with current piece
                if (holdPiece.isEmpty()) {
                    holdPiece.add(newPiece);
                    newPiece = pickNextPiece();
                }else {
                    TetrisPiece oldPiece = holdPiece.pop();
                    holdPiece.add(newPiece);
                    newPiece = oldPiece;
                }
                if (TetrisApp.view != null) TetrisApp.view.paintHoldPiece();
                break;
            default: //doh!
                throw new RuntimeException("Bad movement!");
        }
    }

    /**
     * Put new piece in play on board 
     */
    public void addNewPiece() {
        count++;
        score++;

        // commit things the way they are
        board.commit();
        currentPiece = null;

        TetrisPiece piece = pickNextPiece();

        // Center it up at the top
        int px = (board.getWidth() - piece.getWidth())/2;
        int py = board.getHeight() - 4;
        int result = setCurrent(piece, px, py);

        if (result > TetrisBoard.ADD_ROW_FILLED) {
            stopGame(); //oops, we lost.
        }

    }

    /**
     * Pick next piece to put in play on board 
     */
    private TetrisPiece pickNextPiece() {
        int pieceNum;
        pieceNum = (int) (pieces.length * random.nextDouble());
        TetrisPiece piece	 = pieces[pieceNum];
        return(piece);
    }

    /**
     * Attempt to set the piece at a given board position
     * 
     * @param piece piece to place
     * @param x placement position, x
     * @param y placement position, y
     * 
     * @return integer defining if placement is OK or not (see Board.java)
     */
    public int setCurrent(TetrisPiece piece, int x, int y) {
        int result = board.placePiece(piece, x, y);

        if (result <= TetrisBoard.ADD_ROW_FILLED) { // SUCCESS
            this.currentPiece = piece;
            this.currentX = x;
            this.currentY = y;
        } else {
            board.undo();
        }
        this.floorY = board.placementHeight(this.currentPiece, this.currentX, this.currentY);
        return(result);
    }

    /**
     * Get width
     * 
     * @return width 
     */
    public double getWidth() {
        return WIDTH;
    }

    /**
     * Get width
     * 
     * @return height (with buffer at top accounted for) 
     */
    public double getHeight() {
        return HEIGHT + BUFFERZONE;
    }

    /**
     * Advance the game one tick forward
     * Each tick is associated with a move of some kind!
     * Put the move in play by executing this.
     */
    public void modelTick(MoveType verb) {
        if (!gameOn) return;
        executeMove(verb);
    }

    /**
     * Execute a given move.  This will compute the new position of the active piece, 
     * set the piece to this location if possible.  If lines are completed
     * as a result of the move, the lines will be cleared from the board,
     * and the board will be updated.  Scores will be added to the player's
     * total based on the number of rows cleared.
     * 
     * @param verb the type of move to execute
     */
    private void executeMove(MoveType verb) {
        if (currentPiece != null) {
            board.undo();	// remove the piece from its old position
        }

        computeNewPosition(verb);

        // try out the new position (and roll it back if it doesn't work)
        int result = setCurrent(newPiece, newX, newY);

        boolean failed = (result >= TetrisBoard.ADD_OUT_BOUNDS);

        // if it didn't work, put it back the way it was
        if (failed) {
            if (currentPiece != null) board.placePiece(currentPiece, currentX, currentY);
        }

        // If move is drop, instantly place piece and add new piece
        if ((canPlace && failed && verb==MoveType.DOWN) || verb==MoveType.DROP) {    // if it's out of bounds due to falling
            this.downTimeline.stop();
            int cleared = board.clearRows();
            if (cleared > 0) {
                    // scores go up by 5, 10, 20, 40 as more rows are cleared
                switch (cleared) {
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                }
            }
            if (board.getMaxHeight() > board.getHeight() - BUFFERZONE) {
                stopGame();
            }else {
                addNewPiece();
            }
        }
    }

    /**
     * Start a new game
     */
    public void newGame() {
        this.board.newGame();
        startGame();
    }

    /**
     * pause game
     */
    public void stopGame() {
        // If the game is a multiplayer game, just exit the game and go to main menu
        if (!isMultiplayer) {
            TetrisApp.view.initUI();
            this.controlsTimer.stop();
            this.downTimeline.stop();
        }else { // If the game is a multiplayer game, tell the server that this player lost
            this.client.sendPacket(this.client.numConnections, true,true);
            this.isMultiplayer = false;
        }
        gameOn = false;
    }
    public int getScore(){
        return this.score;
    }
}



