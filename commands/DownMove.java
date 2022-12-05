package commands;

import javafx.animation.Animation;
import model.TetrisModel;

public class DownMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public DownMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.isDownPressed = true;
        model.controlsTimer.start();
        model.canPlace = false;
    }

    @Override
    public void stop() {
        model.isDownPressed = false;
        if (!model.isLeftPressed && !model.isRightPressed) {
            model.canPlace = true;
            model.controlsTimer.stop();
        }
        if (model.downTimeline.getStatus() == Animation.Status.RUNNING) {
            model.downTimeline.setRate(1);
        }
    }
}
