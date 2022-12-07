package commands;

import model.TetrisModel;

public class LeftMove implements Moves{
    private TetrisModel model; // Receiver which knows how to perform the actions

    public LeftMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.isLeftPressed = true;
        model.controlsTimer.start();
        model.canPlace = false;
    }

    @Override
    public void stop() {
        model.isLeftPressed = false;
        if (!model.isDownPressed && !model.isRightPressed) {
            model.canPlace = true;
            model.controlsTimer.stop();
        }
    }
}
