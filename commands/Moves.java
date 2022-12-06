package commands;

public interface Moves {

    // Interface for the command pattern. This pattern is used to organize controls,
    // provide flexibility for how controls are run (i.e. changing the keybindings of controls),
    // and allows easy access for creating new controls.
    // GameView is the invoker class and TetrisModel is the receiver class

    /**
     * This method is used when the controls are pressed.
     */
    void execute();

    /**
     * This method is used when the controls are released.
     */
    void stop();
}
