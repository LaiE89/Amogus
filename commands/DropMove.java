package commands;

import model.TetrisModel;

public class DropMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public DropMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.DROP);
    }
}
