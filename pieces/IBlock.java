package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;

/**
 * Representation of I shaped Tetrimino using TetrisPiece
 */
public class IBlock extends TetrisPiece {
    public IBlock(){
        super("0 0	0 1	 0 2  0 3");
        this.setColor(Color.CYAN.brighter());
        this.setId(2);
    }
}
