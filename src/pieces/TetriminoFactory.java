package pieces;

import model.TetrisPiece;
/**
 * This object produces tetriminos based on the specification given. Uses factory design pattern
* **/

public class TetriminoFactory {
    private int totalTetriminos = 0; //total count of all tetriminos created
    public TetrisPiece getTetrimino(String blockType){
        TetrisPiece piece;
        switch(blockType){
            case "IBLOCK":
                piece = IBlock.makeFastRotations(new IBlock());
                break;
            case "LBlock":
                piece = LBlock.makeFastRotations(new LBlock());
                break;
            case "JBlock":
                piece = JBlock.makeFastRotations(new JBlock());
                break;
            case "SBlock":
                piece = SBlock.makeFastRotations(new SBlock());
                break;
            case "ZBlock":
                piece = ZBlock.makeFastRotations(new ZBlock());
                break;
            case "OBlock":
                piece = OBlock.makeFastRotations(new OBlock());
                break;
            case "TBlock":
                piece = TBlock.makeFastRotations(new TBlock());
                break;
            default:
                piece = new TetrisPiece("0 0	0 1	 0 2  0 3");
                break;
        }
        totalTetriminos += 1;
        return piece;
    }

    /**
     *
     * @return total number of tetriminos created
     */
    public int getTotalTetriminos(){return this.totalTetriminos;}
}
