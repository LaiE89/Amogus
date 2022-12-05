package commands;

import model.TetrisModel;

public class RightMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public RightMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.RIGHT);
    }
}
