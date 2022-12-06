package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;
/**
 * Representation of L shaped Tetrimino using TetrisPiece
 */
public class LBlock extends TetrisPiece {
    public LBlock(){
        super("0 0	0 1	 0 2  1 0");
        this.setColor(Color.DODGERBLUE.brighter());
        this.setId(3);
    }
}
