package views;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;

public class SinglePlayerView extends GameView{
    
    private Timeline garbageTimeline;
    public SinglePlayerView() {
        super();

        garbageTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    Random rand = new Random();
                    int randRow = rand.nextInt(4)+1;
                    this.model.getBoard().addGarbage(randRow);
                }));
        garbageTimeline.setRate(10);
        garbageTimeline.setCycleCount(Timeline.INDEFINITE);
        garbageTimeline.play();

        Slider addGarbageSpeed = new Slider(0, 100, 50);
        addGarbageSpeed.setShowTickLabels(true);
        addGarbageSpeed.setStyle("-fx-control-inner-background: palegreen;");

        Label garbageSpeedLabel = new Label("Adjust Garbage Speed");
        garbageSpeedLabel.setFont(new Font(20));
        garbageSpeedLabel.setTextFill(Color.WHITE);

        addGarbageSpeed.setOnMouseReleased(e -> {
            adjustGarbageSpeed(addGarbageSpeed.getValue());
        });

        VBox botBox = new VBox(20, garbageSpeedLabel, addGarbageSpeed);
        botBox.setPadding(new Insets(20, 20, 20, 20));
        botBox.setAlignment(Pos.TOP_CENTER);

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
