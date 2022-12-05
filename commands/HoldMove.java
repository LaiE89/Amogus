package commands;

import model.TetrisModel;

public class HoldMove implements Moves{

    private TetrisModel model; // Receiver which knows how to perform the actions
    boolean isHoldPressed = false;
    public HoldMove (TetrisModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        if (!this.isHoldPressed) {
            model.modelTick(TetrisModel.MoveType.HOLD);
            this.isHoldPressed = true;
        }
    }

    @Override
    public void stop() {
        this.isHoldPressed = false;
    }
}
