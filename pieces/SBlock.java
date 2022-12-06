package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;
/**
 * Representation of S shaped Tetrimino using TetrisPiece
 */
public class SBlock extends TetrisPiece {
    public SBlock(){
        super("0 0	1 0	 1 1  2 1");
        this.setColor(Color.GREEN.brighter());
        this.setId(5);
    }
}
