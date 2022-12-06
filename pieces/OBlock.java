package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;
/**
 * Representation of O shaped Tetrimino using TetrisPiece
 */
public class OBlock extends TetrisPiece {
    public OBlock(){
        super("0 0  0 1  1 0  1 1");
        this.setColor(Color.YELLOW.brighter());
        this.setId(7);
    }
}
