package views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.TetrisApp;
import model.TetrisModel;

public class SinglePlayerView extends GameView{
    
    private Timeline garbageTimeline;
    boolean isSendingGarbage = false;
    public SinglePlayerView() {
        super();

        model.gameOn = true;

        // Thread to constantly check conditions of adding garbage
        Thread garbageThread = new Thread(() -> {
            while (model.gameOn) {
                try {
                    Thread.sleep(10); // for 100 FPS
                } catch (InterruptedException ignore) {
                }
                if (isSendingGarbage && model.currentY >= model.HEIGHT) {
                    Platform.runLater(() -> {
                        model.modelTick(TetrisModel.MoveType.GARBAGE);
                        TetrisApp.view.paintBoard();
                    });
                    isSendingGarbage =false;
                }
            }
        });
        garbageThread.setDaemon(true);
        garbageThread.start();

        // Adds garbage every 5 seconds by default
        garbageTimeline = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                if (!isSendingGarbage) isSendingGarbage = true;
            })
        );
        garbageTimeline.setCycleCount(Timeline.INDEFINITE);
        garbageTimeline.play();

        Slider addGarbageSpeed = new Slider(0, 100, 50);
        addGarbageSpeed.setShowTickLabels(true);
        addGarbageSpeed.setValue(50);
        addGarbageSpeed.setStyle("-fx-control-inner-background: palegreen;");
        adjustGarbageSpeed(addGarbageSpeed.getValue());

        Label garbageSpeedLabel = new Label("Adjust Garbage Speed");
        garbageSpeedLabel.setFont(new Font(20));
        garbageSpeedLabel.setTextFill(Color.WHITE);

        Label holdPieceLabel = new Label("Hold");
        holdPieceLabel.setFont(new Font(20));
        holdPieceLabel.setTextFill(Color.WHITE);

        VBox holdPieceBox = new VBox(20, holdPieceLabel, holdPieceCanvas);
        holdPieceBox.setPadding(new Insets(40, 20, 20, 20));
        holdPieceBox.setAlignment(Pos.TOP_CENTER);

        addGarbageSpeed.setOnMouseReleased(e -> {
            adjustGarbageSpeed(addGarbageSpeed.getValue());
        });

        VBox botBox = new VBox(20, garbageSpeedLabel, addGarbageSpeed);
        botBox.setPadding(new Insets(20, 20, 20, 20));
        botBox.setAlignment(Pos.TOP_CENTER);

        borderPane.setTop(holdPieceBox);
        borderPane.setCenter(canvas);
        borderPane.setBottom(botBox);
        SettingsView.updateSettings(borderPane);

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();

    }

    private void adjustGarbageSpeed(double newRate) {
        double rateMulti = newRate * 0.03;
        this.garbageTimeline.setRate(rateMulti);
        this.borderPane.requestFocus();
    }
}
