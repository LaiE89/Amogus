package model;

import java.util.Random;
import java.util.ArrayList;

public class TetriminoPool {

    ArrayList<TetrisPiece> tetriminoPool; // Holds all the possible tetrimino pieces
    private Random random;

    /**
     * Constructor
     */
    public TetriminoPool() {
        initializeBlocks();
        random = new Random();
    }

    /**
     * Initializes all the pieces in the pool.
     */
    private void initializeBlocks() {
        tetriminoPool = new ArrayList<>();
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.STICK_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.L1_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.L2_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.S1_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.S2_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.SQUARE_STR)));
        tetriminoPool.add(TetrisPiece.makeFastRotations(new TetrisPiece(TetrisPiece.PYRAMID_STR)));
    }

    /**
     * Pops a random tetrimino piece from the pool.
     *
     * @return a random TetrisPiece object from the pool.
     */
    public TetrisPiece acquireTetrimino() {
        int pieceNum = (int) (tetriminoPool.size() * random.nextDouble());
        TetrisPiece returnPiece = tetriminoPool.remove(pieceNum);
        return returnPiece;
    }

    /**
     * Returns a given tetrimino piece back to the pool.
     *
     * @param piece the TetrisPiece object that will be sent back to the pool.
     */
    public void releaseTetrimino(TetrisPiece piece) {
        if (piece != null) tetriminoPool.add(piece);
    }

    public ArrayList<TetrisPiece> getTetriminoPool() {
        return this.tetriminoPool;
    }
}
