package commands;

import model.TetrisModel;

public class LeftMove implements Moves{
    private TetrisModel model; // Receiver which knows how to perform the actions

    public LeftMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.LEFT);
    }
}
