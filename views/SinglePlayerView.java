package views;

import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SinglePlayerView extends GameView{
    
    private Timeline garbageTimeline;
    public SinglePlayerView() {
        super();

        Slider addGarbageSpeed = new Slider(0, 100, 50);
        addGarbageSpeed.setShowTickLabels(true);
        addGarbageSpeed.setStyle("-fx-control-inner-background: palegreen;");

        Label garbageSpeedLabel = new Label("Adjust Garbage Speed");
        garbageSpeedLabel.setFont(new Font(20));
        garbageSpeedLabel.setTextFill(Color.WHITE);

        addGarbageSpeed.setOnMouseReleased(e -> {
            double rateMultiplier = addGarbageSpeed.getValue() * 0.03;
            adjustGarbageSpeed(rateMultiplier);
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
        // TODO
        //this.garbageTimeline.setRate(newRate);
        //this.borderPane.requestFocus();
    }
}
