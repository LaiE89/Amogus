package views;

import commands.*;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.TetrisApp;
import model.TetrisModel;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class GameView {

    // Key Binds variables
    public ArrayList<Pair<KeyCode, Moves>> movesList = new ArrayList<>(); // This will be updated by settings
    public HashMap<KeyCode, Moves> moveBindings = new HashMap<>();

    // Reference to TetrisView variables
    protected BorderPane borderPane;
    protected Stage stage;
    protected Canvas canvas;
    protected Canvas holdPieceCanvas;
    protected GraphicsContext gc;
    protected GraphicsContext holdgc;
    protected TetrisModel model;

    public GameView () {
        borderPane = new BorderPane();
        model = TetrisApp.view.model;
        stage = TetrisApp.view.stage;
        canvas = TetrisApp.view.canvas;
        gc = TetrisApp.view.gc;
        holdPieceCanvas = TetrisApp.view.holdPieceVisual;
        holdgc = TetrisApp.view.holdgc;
        stage.setTitle("CSC207 Tetris");

        updateMoveBindings(TetrisApp.view.controlMap);

        // Detecting controls press
        borderPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                for (KeyCode i : moveBindings.keySet()) {
                    if (k.getCode() == i) {
                        moveBindings.get(i).execute();
                        TetrisApp.view.paintBoard();
                    }
                }
            }
        });

        // Checking when the player releases their finger from a button
        borderPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                //TO DO
                for (KeyCode i : moveBindings.keySet()) {
                    if (k.getCode() == i) {
                        moveBindings.get(i).stop();
                    }
                }
            }
        });
    }

    /**
     * Takes in a hashmap and updates the moveBindings attribute according to the hashmap.
     *
     * @param newBindings a hashmap containing the move type as an integer and the new keycode binding for the move type
     */
    public void updateMoveBindings(HashMap<Integer, KeyCode> newBindings) {
        moveBindings.clear();
        for (int moveType : newBindings.keySet()) {
            switch (moveType) {
                case 0:
                    moveBindings.put(newBindings.get(moveType), new DropMove(model));
                    break;
                case 1:
                    moveBindings.put(newBindings.get(moveType), new LeftMove(model));
                    break;
                case 2:
                    moveBindings.put(newBindings.get(moveType), new RightMove(model));
                    break;
                case 3:
                    moveBindings.put(newBindings.get(moveType), new DownMove(model));
                    break;
                case 4:
                    moveBindings.put(newBindings.get(moveType), new RotateMove(model));
                    break;
                case 5:
                    moveBindings.put(newBindings.get(moveType), new HoldMove(model));
                    break;
                default:
                    break;
            }
        }
    }
}
