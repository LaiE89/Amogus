package commands;

import model.TetrisModel;

public class DropMove implements Moves{
    private TetrisModel model; // Receiver which knows how to perform the actions
    boolean isDropPressed = false;

    public DropMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        if (!this.isDropPressed) {
            model.modelTick(TetrisModel.MoveType.DROP);
            this.isDropPressed = true;
        }
    }

    @Override
    public void stop() {
        this.isDropPressed = false;
    }
}
