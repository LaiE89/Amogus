package views;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.TetrisApp;
import model.TetrisModel;

public class GameView {

    // Controls Variables
    public AnimationTimer timer;
    public Timeline timeline = new Timeline();
    private long lastUpdate = 0;
    BooleanProperty rotatePressed = new SimpleBooleanProperty();
    BooleanProperty rightPressed = new SimpleBooleanProperty();
    BooleanProperty leftPressed = new SimpleBooleanProperty();
    BooleanProperty downPressed = new SimpleBooleanProperty();
    BooleanProperty dropPressed = new SimpleBooleanProperty();
    BooleanBinding anyPressed = downPressed.or(rightPressed).or(leftPressed).or(rotatePressed);

    // Reference to TetrisView variables
    protected BorderPane borderPane;
    protected Stage stage;
    protected Canvas canvas;
    protected GraphicsContext gc;
    protected TetrisModel model;

    public GameView () {
        borderPane = new BorderPane();
        model = TetrisApp.view.model;
        stage = TetrisApp.view.stage;
        canvas = TetrisApp.view.canvas;
        gc = TetrisApp.view.gc;
        stage.setTitle("CSC207 Tetris");

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
                        TetrisApp.view.paintBoard();
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
                        TetrisApp.view.paintBoard();
                        rotatePressed.set(false);
                    }
                    if (downPressed.get()) {
                        if (timeline.getStatus() != Animation.Status.RUNNING) {
                            timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), e -> {
                                model.modelTick(TetrisModel.MoveType.DOWN);
                                TetrisApp.view.paintBoard();
                            }));
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            timeline.play();
                        }else {
                            timeline.setRate(timeline.getCurrentRate() + 0.25);
                        }
                    }
                    if (rightPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.RIGHT);
                        TetrisApp.view.paintBoard();
                    }
                    if (leftPressed.get()) {
                        model.modelTick(TetrisModel.MoveType.LEFT);
                        TetrisApp.view.paintBoard();
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
    }
}
