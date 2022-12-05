package model;

import java.util.Random;
import java.util.ArrayList;

public class TetriminoPool {
    ArrayList<TetrisPiece> tetriminoPool;
    private Random random;

    public TetriminoPool() {
        initializeBlocks();
        random = new Random();
    }

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

    public TetrisPiece acquireTetrimino() {
        System.out.println(tetriminoPool.toString());
        int pieceNum = (int) (tetriminoPool.size() * random.nextDouble());
        TetrisPiece returnPiece = tetriminoPool.remove(pieceNum);
        return returnPiece;
    }

    public void releaseTetrimino(TetrisPiece piece) {
        if (piece != null) tetriminoPool.add(piece);
    }
}
