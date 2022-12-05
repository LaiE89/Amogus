package commands;

import model.TetrisModel;

public class RotateMove implements Moves{
    private TetrisModel model; // Receiver which knows how to perform the actions

    public RotateMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.ROTATE);
    }
}
