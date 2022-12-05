package commands;

import model.TetrisModel;

public class HoldMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions

    public HoldMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.modelTick(TetrisModel.MoveType.ROTATE);
    }
}
