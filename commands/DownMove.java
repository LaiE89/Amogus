package commands;

import model.TetrisModel;

public class DownMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public DownMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.DOWN);
    }
}
