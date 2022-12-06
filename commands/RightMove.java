package commands;

import model.TetrisModel;

public class RightMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public RightMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.isRightPressed = true;
        model.controlsTimer.start();
        model.canPlace = false;
    }

    @Override
    public void stop() {
        model.isRightPressed = false;
        if (!model.isDownPressed && !model.isLeftPressed) {
            model.canPlace = true;
            model.controlsTimer.stop();
        }
    }
}
