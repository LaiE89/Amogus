package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;
/**
 * Representation of Z shaped Tetrimino using TetrisPiece
 */
public class ZBlock extends TetrisPiece {
    public ZBlock(){
        super("0 1	1 1  1 0  2 0");
        this.setColor(Color.RED.brighter());
        this.setId(6);
    }
}
