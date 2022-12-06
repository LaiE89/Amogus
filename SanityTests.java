import model.TetrisPiece;
import model.TetrisBoard;

import model.TetrisPoint;
import org.junit.jupiter.api.Test;
import pieces.IBlock;
import pieces.TetriminoFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SanityTests {

    //Piece tests
    @Test
    void testPiece() {

        TetrisPiece piece = new TetrisPiece("1 0  1 1  1 2  0 1");
        int [] output = piece.getLowestYVals();
        int [] target = {1,0};
        for (int i =0; i< output.length; i++) {
            assertEquals(output[i], target[i], "Error when testing lowest Y values");
        }
        assertEquals(piece.getHeight(), 3);
        assertEquals(piece.getWidth(), 2);

        TetrisPiece piece2 = new TetrisPiece("0 0  1 0  2 0  1 1  1 2  2 2");
        int [] output2 = piece2.getLowestYVals();
        int [] target2 = {0,0,0};
        for (int i =0; i< output2.length; i++) {
            assertEquals(output2[i], target2[i], "Error when testing lowest Y values");
        }
        assertEquals(piece2.getHeight(), 3);
        assertEquals(piece2.getWidth(), 3);

        TetrisPiece piece3 = new TetrisPiece("0 0 1 0 2 0 1 1 1 2 2 2");
        int [] output3 = piece3.getLowestYVals();
        int [] target3 = {0,0,0};
        for (int i =0; i< output3.length; i++) {
            assertEquals(output3[i], target3[i], "Error when testing lowest Y values");
        }
        assertEquals(piece3.getHeight(), 3);
        assertEquals(piece3.getWidth(), 3);

        TetrisPiece piece4 = new TetrisPiece("0 2 1 2 1 1 2 1 2 0");
        int [] output4 = piece4.getLowestYVals();
        int [] target4 = {2,1,0};
        for (int i =0; i< output4.length; i++) {
            assertEquals(output4[i], target4[i], "Error when testing lowest Y values");
        }
        assertEquals(piece4.getHeight(), 3);
        assertEquals(piece4.getWidth(), 3);

        TetrisPiece piece5 = new TetrisPiece("");
        int [] output5 = piece5.getLowestYVals();
        int [] target5 = {};
        for (int i =0; i< output5.length; i++) {
            assertEquals(output5[i], target5[i], "Error when testing lowest Y values");
        }
        assertEquals(piece5.getHeight(), 0);
        assertEquals(piece5.getWidth(), 0);

        TetrisPiece piece6 = new TetrisPiece("1 0  1 3  2 2  0 1");
        int [] output6 = piece6.getLowestYVals();
        int [] target6 = {1,0,2};
        for (int i =0; i< output6.length; i++) {
            assertEquals(output6[i], target6[i], "Error when testing lowest Y values");
        }
        assertEquals(piece6.getHeight(), 4);
        assertEquals(piece6.getWidth(), 3);

        TetrisPiece piece7 = new TetrisPiece("2 0 2 1 2 2");
        int [] output7 = piece7.getLowestYVals();
        int [] target7 = {0};
        for (int i =0; i< output7.length; i++) {
            assertEquals(output7[i], target7[i], "Error when testing lowest Y values");
        }
        assertEquals(piece7.getHeight(), 3);
        assertEquals(piece7.getWidth(), 1);

        TetrisPiece piece8 = new TetrisPiece("0 0 1 0 1 1 1 2 0 2");
        int [] output8 = piece8.getLowestYVals();
        int [] target8 = {0, 0};
        for (int i =0; i< output8.length; i++) {
            assertEquals(output8[i], target8[i], "Error when testing lowest Y values");
        }
        assertEquals(piece8.getHeight(), 3);
        assertEquals(piece8.getWidth(), 2);

        TetrisPiece piece9 = new TetrisPiece("0 0");
        int [] output9 = piece9.getLowestYVals();
        int [] target9 = {0};
        for (int i =0; i< output9.length; i++) {
            assertEquals(output9[i], target9[i], "Error when testing lowest Y values");
        }
        assertEquals(piece9.getHeight(), 1);
        assertEquals(piece9.getWidth(), 1);

        TetrisPiece piece10 = new TetrisPiece("1 1  1 2  0 1  2 2");
        int [] output10 = piece10.getLowestYVals();
        int [] target10 = {1,1,2};
        for (int i =0; i< output10.length; i++) {
            assertEquals(output10[i], target10[i], "Error when testing lowest Y values");
        }
        assertEquals(piece10.getHeight(), 3);
        assertEquals(piece10.getWidth(), 3);

    }

    @Test
    void testLowestY() {
        TetrisPiece piece = new TetrisPiece("2 1 2 2 1 2 0 2");
        int[] piece1result = new int[]{2, 2, 1};
        // System.out.println(Arrays.toString(piece.getLowestYVals()));
        for (int i = 0; i < piece1result.length; i++) {
            assertEquals(piece.getLowestYVals()[i], piece1result[i]);
        }

        TetrisPiece piece2 = new TetrisPiece("1 2 0 2 0 1 0 0");
        int[] piece2result = new int[]{0, 2};
        // System.out.println(Arrays.toString(piece.getLowestYVals()));
        for (int i = 0; i < piece2result.length; i++) {
            assertEquals(piece2.getLowestYVals()[i], piece2result[i]);
        }

        TetrisPiece piece3 = new TetrisPiece("0 1 1 1 2 1 3 1");
        int[] piece3result = new int[]{1, 1, 1, 1};
        // System.out.println(Arrays.toString(piece.getLowestYVals()));
        for (int i = 0; i < piece3result.length; i++) {
            assertEquals(piece3.getLowestYVals()[i], piece3result[i]);
        }

        TetrisPiece piece4 = new TetrisPiece("2 0 2 1 2 2 2 3");
        int[] piece4result = new int[]{0};
        // System.out.println(Arrays.toString(piece.getLowestYVals()));
        for (int i = 0; i < piece4result.length; i++) {
            assertEquals(piece4.getLowestYVals()[i], piece4result[i]);
        }

        TetrisPiece piece5 = new TetrisPiece("0 0 0 1 1 1 2 1");
        int[] piece5result = new int[]{0, 1, 1};
        // System.out.println(Arrays.toString(piece.getLowestYVals()));
        for (int i = 0; i < piece5result.length; i++) {
            assertEquals(piece5.getLowestYVals()[i], piece5result[i]);
        }
    }

    @Test
    void testComputeRotation() {
        TetrisPiece piece = new TetrisPiece("0 1  1 1  1 0  2 0");
        TetrisPiece rotatedPiece = piece.computeNextRotation();
        TetrisPoint[] resultBody = new TetrisPoint[]{
                new TetrisPoint(0, 0),
                new TetrisPoint(0, 1),
                new TetrisPoint(1, 1),
                new TetrisPoint(1, 2)};
        TetrisPiece resultPiece = new TetrisPiece(resultBody);

        assertEquals(resultPiece.equals(rotatedPiece), true);

        TetrisPiece piece2 = new TetrisPiece(resultBody);
        TetrisPiece rotatedPiece2 = piece2.computeNextRotation();
        TetrisPoint[] resultBody2 = new TetrisPoint[]{
                new TetrisPoint(0, 1),
                new TetrisPoint(1, 1),
                new TetrisPoint(1, 0),
                new TetrisPoint(2, 0)};
        TetrisPiece resultPiece2 = new TetrisPiece(resultBody2);

        assertEquals(resultPiece2.equals(rotatedPiece2), true);
    }

    @Test
    void testMakeFastRotations() {
        TetrisPiece piece = new TetrisPiece(TetrisPiece.S2_STR);
        piece = TetrisPiece.makeFastRotations(piece);
        String[] target = {"0 0 0 1 1 1 1 2", "0 1 1 0 1 1 2 0", "0 0 0 1 1 1 1 2", "0 1 1 0 1 1 2 0"};
        int counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece2 = new TetrisPiece(TetrisPiece.STICK_STR);
        piece = TetrisPiece.makeFastRotations(piece2);
        String[] target2 = {"0 0 1 0 2 0 3 0", "0 0 0 1 0 2 0 3", "0 0 1 0 2 0 3 0", "0 0 0 1 0 2 0 3"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target2[counter]);
            piece = piece.fastRotation();
            //piece.sortTetrisPoints(piece.getBody());
            //System.out.println(piece);
            //System.out.println("expected: " + np);
            //System.out.println("actual: " + piece + "\n");
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece3 = new TetrisPiece(TetrisPiece.PYRAMID_STR);
        piece = TetrisPiece.makeFastRotations(piece3);
        String[] target3 = {"0 1 1 0 1 1 1 2", "1 0 0 1 1 1 2 1", "0 0 0 1 0 2 1 1", "0 0 1 1 1 0 2 0"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target3[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece4 = new TetrisPiece(TetrisPiece.L1_STR);
        piece = TetrisPiece.makeFastRotations(piece4);
        String[] target4 = {"0 0 1 0 2 0 2 1", "1 0 1 1 1 2 0 2", "0 0 0 1 1 1 2 1", "0 2 0 1 0 0 1 0"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target4[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece5 = new TetrisPiece(TetrisPiece.SQUARE_STR);
        piece = TetrisPiece.makeFastRotations(piece5);
        String[] target5 = {"0 0 0 1 1 0 1 1", "0 0 0 1 1 0 1 1", "0 0 0 1 1 0 1 1", "0 0 0 1 1 0 1 1"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target5[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece6 = new TetrisPiece("0 0 0 1 1 1 1 2 2 2");
        piece = TetrisPiece.makeFastRotations(piece6);
        String[] target6 = {"0 2 0 1 1 1 1 0 2 0", "0 0 1 0 1 1 2 1 2 2", "0 2 1 2 1 1 2 1 2 0", "0 0 0 1 1 1 1 2 2 2"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target6[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece7 = new TetrisPiece("0 1 0 0 1 0");
        piece = TetrisPiece.makeFastRotations(piece7);
        String[] target7 = {"0 0 1 0 1 1", " 0 1 1 1 1 0", "0 0 0 1 1 1", "0 1 0 0 1 0"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target7[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece8 = new TetrisPiece("0 1 0 0 1 0");
        piece = TetrisPiece.makeFastRotations(piece8);
        String[] target8 = {"0 0 1 0 1 1", " 0 1 1 1 1 0", "0 0 0 1 1 1", "0 1 0 0 1 0"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target8[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }

        TetrisPiece piece9 = new TetrisPiece("0 0 1 0 1 1 1 2 2 0 2 1");
        piece = TetrisPiece.makeFastRotations(piece9);
        String[] target9 = {"0 1  1 1  1 2  2 0  2 1  2 2", "0 2  1 2  2 2  0 1  1 1  1 0", "0 2  0 1  0 0  1 0  1 1  2 1", "0 0 1 0 1 1 1 2 2 0 2 1"};
        counter = 0;
        while(counter < 4){
            TetrisPiece np = new TetrisPiece(target9[counter]);
            piece = piece.fastRotation();
            assertTrue(np.equals(piece), "Error when testing piece equality");
            counter++;
        }
    }

    @Test
    void testEquals() {

        TetrisPiece pieceA = new TetrisPiece("1 0  1 1  1 2  0 1");
        TetrisPiece pieceB = new TetrisPiece("1 0  1 1  1 2  0 1");
        assertTrue(pieceB.equals(pieceA), "Error when testing piece equality");
        assertTrue(pieceA.equals(pieceB), "Error when testing piece equality");

        pieceA = new TetrisPiece("0 0 1 0 2 0 1 1 0 1");
        pieceB = new TetrisPiece("1 0 0 0 2 0 0 1 1 1");
        assertTrue(pieceB.equals(pieceA), "Error when testing piece equality");
        assertTrue(pieceA.equals(pieceB), "Error when testing piece equality");

    }

    @Test
    void testPlacePiece() {

        TetrisBoard board = new TetrisBoard(10,24);
        TetrisPiece pieceA = new TetrisPiece(TetrisPiece.SQUARE_STR);

        board.commit();
        int retval = board.placePiece(pieceA, 0,0);
        assertEquals(TetrisBoard.ADD_OK,retval);

        board.commit();
        retval = board.placePiece(pieceA, 12,12); //out of bounds
        assertEquals(TetrisBoard.ADD_OUT_BOUNDS,retval);

        board.commit();
        //retval = board.placePiece(pieceA, 0,0);
        retval = board.placePiece(pieceA, 0,0);
        board.undo();
        assertEquals(TetrisBoard.ADD_BAD, retval);
        //assertEquals(TetrisBoard.ADD_BAD, retval);

        //fill the entire row
        retval = board.placePiece(pieceA, 2,0); board.commit();
        retval = board.placePiece(pieceA, 4,0); board.commit();
        retval = board.placePiece(pieceA, 6,0); board.commit();
        retval = board.placePiece(pieceA, 8,0);
        assertEquals(TetrisBoard.ADD_ROW_FILLED, retval);

        for (int i = 0; i < board.getWidth(); i++) {
            assertEquals(board.getGrid(i,0), true);
            assertEquals(board.getGrid(i,1), true);
            assertEquals(board.getGrid(i,2), false);
        }

        TetrisBoard board2 = new TetrisBoard(10,24);
        TetrisPiece pieceB = new TetrisPiece(TetrisPiece.PYRAMID_STR);
        pieceB = TetrisPiece.makeFastRotations(pieceB);
        board2.commit();

        retval = board2.placePiece(pieceB, 0,0);
        assertEquals(TetrisBoard.ADD_OK,retval);
        board2.commit();

        retval = board2.placePiece(pieceB, 0,24); //out of bounds
        assertEquals(TetrisBoard.ADD_OUT_BOUNDS,retval);
        board2.commit();

        retval = board2.placePiece(pieceB.fastRotation().fastRotation(), 0,1);
        assertEquals(TetrisBoard.ADD_BAD, retval);
        board2.undo();

        board2.placePiece(pieceB.fastRotation().fastRotation(), 0, 2);
        board2.commit();
        assertEquals(board2.getGrid(0,0), true);
        assertEquals(board2.getGrid(1,0), true);
        assertEquals(board2.getGrid(2,0), true);
        assertEquals(board2.getGrid(1,1), true);
        assertEquals(board2.getGrid(1,2), true);
        assertEquals(board2.getGrid(2,1), false);
        assertEquals(board2.getGrid(0,2), false);
        assertEquals(board2.getGrid(0,3), true);
        assertEquals(board2.getGrid(1,3), true);
        assertEquals(board2.getGrid(2,3), true);
    }

    @Test
    void testPlacementHeight() {
        TetrisPiece pieceA = new TetrisPiece(TetrisPiece.SQUARE_STR);
        TetrisBoard board = new TetrisBoard(10,24); board.commit();
        int retval = board.placePiece(pieceA, 0,0); board.commit();
        int x = board.placementHeight(pieceA, 0, 20);
        assertEquals(2,x);
        retval = board.placePiece(pieceA, 0,2); board.commit();
        x = board.placementHeight(pieceA, 0, 20);
        assertEquals(4,x);

        TetrisPiece pieceB = new TetrisPiece(TetrisPiece.L1_STR);
        pieceB = TetrisPiece.makeFastRotations(pieceB);
        TetrisBoard board2 = new TetrisBoard(10,24); board2.commit();
        retval = board2.placePiece(pieceB, 0,0); board2.commit();
        x = board2.placementHeight(pieceB, 0, 20);
        assertEquals(3,x);
        pieceB = pieceB.fastRotation().fastRotation();
        x = board2.placementHeight(pieceB, 0, 20);
        assertEquals(1,x);

        TetrisPiece pieceC = new TetrisPiece(TetrisPiece.L1_STR);
        TetrisPiece pieceD = new TetrisPiece(TetrisPiece.S2_STR);
        pieceC = TetrisPiece.makeFastRotations(pieceC);
        pieceC = pieceC.fastRotation();
        pieceD = TetrisPiece.makeFastRotations(pieceD);
        pieceD = pieceD.fastRotation();
        TetrisBoard board3 = new TetrisBoard(10,24); board3.commit();
        retval = board3.placePiece(pieceC, 2,0); board3.commit();
        x = board3.placementHeight(pieceD, 1, 20);
        assertEquals(0,x);
        x = board3.placementHeight(pieceD, 2, 20);
        assertEquals(1,x);
        x = board3.placementHeight(pieceD, 3, 20);
        assertEquals(1,x);
        x = board3.placementHeight(pieceD, 4, 20);
        assertEquals(2,x);
        x = board3.placementHeight(pieceD, 5, 20);
        assertEquals(0,x);

        TetrisPiece pieceE = new TetrisPiece(TetrisPiece.S1_STR);
        TetrisPiece pieceF = new TetrisPiece(TetrisPiece.STICK_STR);
        pieceF = TetrisPiece.makeFastRotations(pieceF);
        pieceE = TetrisPiece.makeFastRotations(pieceE);
        pieceE = pieceE.fastRotation().fastRotation();
        TetrisBoard board4 = new TetrisBoard(10,24); board4.commit();
        retval = board4.placePiece(pieceF, 7,0); board4.commit();
        pieceF = pieceF.fastRotation();
        int y = board4.placementHeight(pieceF, 4, 20);
        retval = board4.placePiece(pieceF, 4, 4); board4.commit();
        assertEquals(4,y);
        x = board4.placementHeight(pieceE, 7, 20);
        assertEquals(5,x);
        retval = board4.placePiece(pieceE, 7,5); board4.commit();

    }

    @Test
    void testClearRows() {
        TetrisBoard board = new TetrisBoard(10,24); board.commit();
        TetrisPiece pieceA = new TetrisPiece(TetrisPiece.SQUARE_STR);
        TetrisPiece pieceB = new TetrisPiece(TetrisPiece.STICK_STR);
        pieceB = TetrisPiece.makeFastRotations(pieceB);
        pieceB = pieceB.fastRotation();
        TetrisPiece pieceC = new TetrisPiece(TetrisPiece.L1_STR);
        TetrisPiece pieceD = new TetrisPiece(TetrisPiece.S1_STR);
        TetrisPiece pieceE = new TetrisPiece(TetrisPiece.L2_STR);

        //fill two rows completely
        int retval = board.placePiece(pieceA, 0,0); board.commit();
        retval = board.placePiece(pieceA, 2,0); board.commit();
        retval = board.placePiece(pieceA, 4,0); board.commit();
        retval = board.placePiece(pieceA, 6,0); board.commit();
        retval = board.placePiece(pieceA, 8,0); board.commit();
        retval = board.placePiece(pieceA, 3,2);

        int rcleared = board.clearRows();
        board.commit();
        assertEquals(2, rcleared);

        //fill 1 row completely
        retval = board.placePiece(pieceB, 0,2); board.commit();
        retval = board.placePiece(pieceB, 4,2); board.commit();
        retval = board.placePiece(pieceC, 8,2); board.commit();

        //placing square as filler
        retval = board.placePiece(pieceA, 3,3); board.commit();
        //filling 2 rows
        retval = board.placePiece(pieceA, 0,5); board.commit();
        retval = board.placePiece(pieceA, 2,5); board.commit();
        retval = board.placePiece(pieceA, 4,5); board.commit();
        retval = board.placePiece(pieceA, 6,5); board.commit();
        retval = board.placePiece(pieceA, 8,5); board.commit();

        //fill 2 row completely
        retval = board.placePiece(pieceB, 0,7); board.commit();
        retval = board.placePiece(pieceB, 4,7); board.commit();
        retval = board.placePiece(pieceC, 8,7);

        rcleared = board.clearRows();
        board.commit();
        assertEquals(4, rcleared);

        TetrisBoard board2 = new TetrisBoard(10,24); board2.commit();
        board2.placePiece(pieceA, 0,0); board2.commit();
        board2.placePiece(pieceD, 3,0); board2.commit();
        board2.placePiece(pieceE, 5,0); board2.commit();
        board2.placePiece(pieceA, 7,0); board2.commit();
        board2.placePiece(pieceB.fastRotation(), 9,0); board2.commit();
        board2.placePiece(pieceC, 2,1);

        rcleared = board2.clearRows();
        board2.commit();
        assertEquals(1, rcleared);
    }
    @Test
    void addGarbageTest(){
        TetrisBoard board = new TetrisBoard(8, 24); board.commit();
        TetrisPiece piece = new TetrisPiece(TetrisPiece.S1_STR);
        board.placePiece(piece,0, 0); board.commit();
        System.out.println(board.addGarbage(10));
        System.out.println(board);
        for(int i = 0; i < board.getWidth(); i++){
            System.out.print(board.getColumnHeight(i) + " ");
        }
        System.out.println();
        for(int i = 0; i < board.getHeight(); i++){
            System.out.print(board.getRowWidth(i) + " ");
        }
    }
    @Test
    void randomHoleTest(){
        TetrisBoard board = new TetrisBoard(4, 24);board.commit();
        TetrisPiece piece = new TetrisPiece(TetrisPiece.STICK_STR);
        piece = piece.computeNextRotation();
        board.placePiece(piece, 0 ,0 ); board.commit();
        board.addGarbage(3);
        board.randomHole(3);
        System.out.println(board);
        for(int i = 0; i < board.getWidth(); i++){
            System.out.print(board.getColumnHeight(i) + " ");
        }
        System.out.println();
        for(int i = 0; i < board.getHeight(); i++){
            System.out.print(board.getRowWidth(i) + " ");
        }
    }
    @Test
    void randomHoleTest2(){
        TetrisBoard board = new TetrisBoard(2, 24);board.commit();
        board.addGarbage(3);
        board.randomHole(3);
        System.out.println(board);
        for(int i = 0; i < board.getWidth(); i++){
            System.out.print(board.getColumnHeight(i) + " ");
        }
        System.out.println();
        for(int i = 0; i < board.getHeight(); i++){
            System.out.print(board.getRowWidth(i) + " ");
        }
    }
    @Test
    void tetriminoValues(){
        TetrisPiece piece = new TetrisPiece(TetrisPiece.STICK_STR);
        IBlock piece2 = new IBlock();
        assertTrue(piece.getHeight() == piece2.getHeight());
        assertTrue(piece.getWidth() == piece2.getWidth());
    }
    @Test
    void tetriminoComparison(){
        TetrisPiece piece = new TetrisPiece(TetrisPiece.STICK_STR);
        IBlock piece2 = new IBlock();
        piece2 = (IBlock) IBlock.makeFastRotations(piece2);
        System.out.println(piece2.fastRotation().fastRotation());
    }
    @Test
    void tetriminoFactory(){
        TetriminoFactory factory = new TetriminoFactory();
        TetrisPiece piece1 = factory.getTetrimino("IBLOCK");
        piece1 = piece1.fastRotation();

        TetrisBoard board = new TetrisBoard(8,12);board.commit();
        board.placePiece(piece1, 0, 0);board.commit();
        System.out.println(board);
    }
}
