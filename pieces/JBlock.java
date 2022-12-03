package pieces;

import javafx.scene.paint.Color;
import model.TetrisPiece;

/**
 * Representation of J shaped Tetrimino using TetrisPiece
 */
public class JBlock extends TetrisPiece {
    public JBlock(){
        super("0 0	1 0 1 1	 1 2");
        this.setColor(Color.ORANGE.brighter());
        this.setId(4);
    }
}
