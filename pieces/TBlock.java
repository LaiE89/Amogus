package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;
/**
 * Representation of T shaped Tetrimino using TetrisPiece
 */
public class TBlock extends TetrisPiece {
    public TBlock(){
        super("0 0  1 0  1 1  2 0");
        this.setColor(Color.PURPLE.brighter());
        this.setId(8);
    }
}
