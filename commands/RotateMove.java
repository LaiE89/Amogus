package commands;

import model.TetrisModel;

public class RotateMove implements Moves{
    private TetrisModel model; // Receiver which knows how to perform the actions
    boolean isRotatePressed = false;

    public RotateMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        if (!this.isRotatePressed) {
            model.modelTick(TetrisModel.MoveType.ROTATE);
            this.isRotatePressed = true;
        }
    }

    @Override
    public void stop() {
        this.isRotatePressed = false;
    }
}
