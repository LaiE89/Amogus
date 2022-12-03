// TetrisBoard.java
package model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a Board class for Tetris.  
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisBoard implements Serializable{
    private int width; //board height and width
    private int height;
    protected boolean[][] tetrisGrid; //board grid
    boolean committed; //indicates if the board is in a 'committed' state, meaning can't undo!

    //In your implementation, you'll want to keep counts of filled grid positions in each column.
    //A completely filled column means the game is over!
    private int colCounts[];
    //You will also want to keep counts by row.
    //A completely filled row can be cleared from the board (and points are awarded)!
    private int rowCounts[];

    //In addition, you'll need to allocate some space to back up your grid data.
    //This will be important when you implement "undo".
    private boolean[][] backupGrid; //to back up your grid
    private int backupColCounts[]; //to back up your row counts
    private int backupRowCounts[]; //to back up your column counts

    //error types (to be returned by the place function)
    public static final int ADD_OK = 0;
    public static final int ADD_ROW_FILLED = 1;
    public static final int ADD_OUT_BOUNDS = 2;
    public static final int ADD_BAD = 3;

    /**
     * Constructor for an empty board of the given width and height measured in blocks.
     *
     * @param aWidth    width
     * @param aHeight    height
     */
    public TetrisBoard(int aWidth, int aHeight) {
        width = aWidth;
        height = aHeight;
        tetrisGrid = new boolean[width][height];

        colCounts = new int[width];
        rowCounts = new int[height];

        //init backup storage, for undo
        backupGrid = new boolean[width][height];
        backupColCounts = new int[width];
        backupRowCounts = new int[height];
    }

    /**
     * Helper to fill new game grid with empty values
     */
    public void newGame() {
        for (int x = 0; x < tetrisGrid.length; x++) {
            for (int y = 0; y < tetrisGrid[x].length; y++) {
                tetrisGrid[x][y] = false;
                }
            }
        Arrays.fill(colCounts, 0);
        Arrays.fill(rowCounts, 0);
        committed = true;
    }

    /**
     * Getter for board width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for board height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the max column height present in the board.
     * For an empty board this is 0.
     *
     * @return the y position of the last filled square in the tallest column
     */
    public int getMaxHeight() {
        return Arrays.stream(colCounts).max().getAsInt();
    }

    /**
     * Returns the height of the given column -- i.e. the y value of the highest block + 1.
     * The height is 0 if the column contains no blocks.
     *
     * @param x grid column, x
     *
     * @return the height of the given column, x
     */
    public int getColumnHeight(int x) {
        return colCounts[x];
    }

    /**
     * Returns the number of filled blocks in the given row.
     *
     * @param y grid row, y
     *
     * @return the number of filled blocks in row y
     */
    public int getRowWidth(int y) {
        return rowCounts[y];
    }

    /**
     * Returns true if the given block is filled in the board. Blocks outside of the
     * valid width/height area always return true (as we can't place anything there).
     *
     * @param x grid position, x
     * @param y grid position, y
     *
     * @return true if the given block at x,y is filled, else false
     */
    public boolean getGrid(int x, int y) {
        if (x >= width || x < 0 || y >= height || y < 0 || tetrisGrid[x][y])
            return true;
        return false;
    }

    /**
     * Given a piece and an x, returns the y value where the piece will come to rest
     * if it were dropped straight down at that x.
     *
     * Use getLowestYVals and the col heights (getColumnHeight) to compute this quickly!
     *
     * @param piece piece to place
     * @param x column of grid where the piece will be placed
     * @param y row of grid where the piece will be placed
     *
     * @return the y value where the piece will come to rest
     */
    //FIX THIS
    public int placementHeight(TetrisPiece piece, int x, int y) {
        //throw new UnsupportedOperationException(); //change this!
        int[] pieceLowest = piece.getLowestYVals();
        int[] heightOfCols = new int[pieceLowest.length];

        // Get height of every column the current piece is on. Any blocks above the current piece will be ignored
        for (int i = 0; i < pieceLowest.length; i++) {
            int curSpace = 0;
            for (int j = y-1; j >= 0; j--) {
                if (!this.tetrisGrid[x + i][j]) {
                    curSpace += 1;
                }else {
                    break;
                }
            }
            heightOfCols[i] = y - curSpace;
        }
        HashMap<Integer, ArrayList<Integer>> largestCol = getLargest(heightOfCols);
        int largestColHeight = (int)largestCol.keySet().toArray()[0];
        ArrayList<Integer> largestColIndex = largestCol.get(largestColHeight);

        // Getting the x-position of the current piece with the deepest space for insertion.
        // These x-positions are only considered if they lie in the same x-position as the tallest column.
        int smallestInsertionLength = pieceLowest[largestColIndex.get(0)];
        for (int i = 0; i < largestColIndex.size(); i++) {
            int insertionLength = pieceLowest[largestColIndex.get(i)];
            if (insertionLength < smallestInsertionLength) {
                smallestInsertionLength = insertionLength;
            }
        }
        return largestColHeight - smallestInsertionLength;
    }

    /**
     * Gets the largest number in an integer array and returns a hashmap with the
     * largest number as a key, and the index of every instance of that large number as the
     * value of the key as an arraylist of integers.
     *
     * @param arr array of integers
     *
     * @return a hashmap with the largest integer as the key and the indexes as the value
     */
    private HashMap<Integer, ArrayList<Integer>> getLargest(int[] arr) {
        // Getting largest integer
        int max = arr[0];
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        // Getting indexes of largest integer
        result.put(max, new ArrayList<>());
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == max) {
                result.get(max).add(i);
            }
        }
        return result;
    }

    /**
     * Attempts to add the body of a piece to the board. Copies the piece blocks into the board grid.
     * Returns ADD_OK for a regular placement, or ADD_ROW_FILLED
     * for a regular placement that causes at least one row to be filled. 
     * 
     * Error cases:
     * A placement may fail in two ways. First, if part of the piece may fall out
     * of bounds of the board, ADD_OUT_BOUNDS is returned.
     * Or the placement may collide with existing blocks in the grid
     * in which case ADD_BAD is returned.
     * In both error cases, the board may be left in an invalid
     * state. The client can use undo(), to recover the valid, pre-place state.
     * 
     * @param piece piece to place
     * @param x placement position, x
     * @param y placement position, y
     * 
     * @return static int that defines result of placement
     */
    public int placePiece(TetrisPiece piece, int x, int y) {
        // throw new UnsupportedOperationException(); //replace this!
        if (this.committed) {

            // Backing up grid
            this.backupGrid();
            this.committed = false;

            if (piece != null) {
                // Checking for errors in every part of the body of the new piece
                for (int i = 0; i < piece.getBody().length; i++) {
                    TetrisPoint curPoint = new TetrisPoint(piece.getBody()[i].x + x, piece.getBody()[i].y + y);
                    if (curPoint.x > this.getWidth() - 1 || curPoint.x < 0 || curPoint.y > this.getHeight() - 1 || curPoint.y < 0) { // Out of bounds
                        return ADD_OUT_BOUNDS;
                    } else if (getGrid(curPoint.x, curPoint.y)) { // Collide with existing blocks
                        return ADD_BAD;
                    } else { // Adding point of the body to the new location
                        this.tetrisGrid[curPoint.x][curPoint.y] = true;
                    }
                }
            }
            // Updating colCount and rowCount
            makeHeightAndWidthArrays();

            // Checking if any rows are full
            for (int i = 0; i < this.rowCounts.length; i++) {
                if (this.rowCounts[i] == this.getWidth()) {
                    // System.out.println("Row filled");
                    return ADD_ROW_FILLED;
                }
            }
            return ADD_OK;
        } else {
            throw new RuntimeException("Board must be committed before placing piece");
        }
    }

    /**
     * Deletes rows that are filled all the way across, moving
     * things above down. Returns the number of rows cleared.
     * 
     * @return number of rows cleared (useful for scoring)
     */
    public int clearRows() {
        // throw new UnsupportedOperationException(); //replace this!
        if (!this.committed) {
            int numRowsCleared = 0;
            ArrayList<Integer> rowsToClear = new ArrayList<>();
            // Get number of rows to clear and keeping track of the y-value of the rows that will be cleared
            for (int y = 0; y < this.rowCounts.length; y++) {
                if (this.rowCounts[y] == this.getWidth()) {
                    numRowsCleared += 1;
                    rowsToClear.add(y);
                }
            }

            // Shifting all blocks above every cleared row the y-value of the cleared row
            for (int row = this.height - 1; row >= 0; row--) {
                if (rowsToClear.contains(row)) {
                    for (int y = row; y < this.height - 1; y++) {
                        for (int x = 0; x < this.width; x++) {
                            if (y == this.height - 1) {
                                tetrisGrid[x][y] = false;
                            } else {
                                tetrisGrid[x][y] = tetrisGrid[x][y + 1];
                            }
                        }
                    }
                }
            }

            // Adjusting colCounts and rowCounts after the changes
            makeHeightAndWidthArrays();
            return numRowsCleared;
        }else {
            throw new RuntimeException("Board must not be committed before clearing row");
        }
    }
    /**
     *Takes in a number of rows and adds that many rows to the bottom of the board
     * returns true if the board is completely filled, false otherwise
     * Precondition: rows >= 0
     * Postcondition: Adds garbage rows from 0th row to the n-1th row
     * **/
    public boolean addGarbage(int rows){
        int highest = getMaxHeight();
        if(highest + rows > height){ //if the # of rows added + max height exceeds board height, then board is filled
            return true;
        }
        moveUp(rows);
        fillGrid(rows);
        //update colCounts
        for(int x = 0; x < colCounts.length; x++){
            colCounts[x] += rows;
        }
        //update rowCounts
        for(int y = highest-1; y >= 0; y--){
            rowCounts[y+rows] = rowCounts[y];
        }
        for(int i = 0; i < rows; i++){
            rowCounts[i] = width-1;
        }
        randomHole(rows);
        return false;
    }
    /**
     * Takes in a number of rows and the height of the highest column
     * Precondition: rows + getMaxHeight() < boardHeight
     * Postcondition: shifts the entire board based on the number of rows
    * **/
    public void moveUp(int rows){
            int highest = getMaxHeight()-1;
            for(int i = highest; i >= 0; i--){
                for(int j = 0; j < width; j++){
                    tetrisGrid[j][i+rows] = tetrisGrid[j][i];
                }
            }
    }
    /**
     * Takes in number of rows n and fills board up to nth row
     * Precondition: None
     * Postcondition: Fills the board from the bottom up to the nth row
     * **/
    public void fillGrid(int n){
        int y = 0;
        for(int i = 0; i < n; i++){
            for(int x = 0; x < width; x++){
                tetrisGrid[x][y] = true;
            }
            y++;
        }
    }

    /**
     *Given n, puts a hole from the 0th to the n-1th row
     * Precondition: n > 0
     * Postcondition: Randomly changes one boolean value on each row of tetrisGrid starting from the 1st row
     * up to nth row
     */
    public void randomHole(int n){
        int col = 0;
        int prev = -1;
        for(int i = n-1; i >= 0; i--){
            col = (int)(Math.random()*width);
            tetrisGrid[col][i] = false;
            if(i == n-1 && getColumnHeight(col) == n){
                colCounts[col] -= 1;
                prev = col;
            }
            else if(prev == col){
                colCounts[col] -= 1;
            }
            else{
                prev = -1;
            }
        }
    }
    /**
     * Reverts the board to its state before up to one call to placePiece() and one to clearRows();
     * If the conditions for undo() are not met, such as calling undo() twice in a row, then the second undo() does nothing.
     * See the overview docs.
     */
    public void undo() {
        if (committed == true) return;  //a committed board cannot be undone!

        if (backupGrid == null) throw new RuntimeException("No source for backup!");  //a board with no backup source cannot be undone!

        //make a copy!!
        for (int i = 0; i < backupGrid.length; i++) {
            System.arraycopy(backupGrid[i], 0, tetrisGrid[i], 0, backupGrid[i].length);
        }

        //copy row and column tallies as well.
        System.arraycopy(backupRowCounts, 0, rowCounts, 0, backupRowCounts.length);
        System.arraycopy(backupColCounts, 0, colCounts, 0, backupColCounts.length);

        committed = true; //no going backwards now!
    }

    /**
     * Copy the backup grid into the grid that defines the board (to support undo)
     */
    private void backupGrid() {
        //make a copy!!
        for (int i = 0; i < tetrisGrid.length; i++) {
            System.arraycopy(tetrisGrid[i], 0, backupGrid[i], 0, tetrisGrid[i].length);
        }
        //copy row and column tallies as well.
        System.arraycopy(rowCounts, 0, backupRowCounts, 0, rowCounts.length);
        System.arraycopy(colCounts, 0, backupColCounts, 0, colCounts.length);
    }

    /**
     * Puts the board in the 'committed' state.
     */
    public void commit() {
        committed = true;
    }

    /**
     * Fills heightsOfCols[] and widthOfRows[].  Useful helper to support clearing rows and placing pieces.
     */
    private void makeHeightAndWidthArrays() {

        Arrays.fill(colCounts, 0);
        Arrays.fill(rowCounts, 0);

        for (int x = 0; x < tetrisGrid.length; x++) {
            for (int y = 0; y < tetrisGrid[x].length; y++) {
                if (tetrisGrid[x][y]) { //means is not an empty cell
                    colCounts[x] = y + 1; //these tallies can be useful when clearing rows or placing pieces
                    rowCounts[y]++;
                }
            }
        }
    }

    /**
     * Print the board
     * 
     * @return a string representation of the board (useful for debugging)
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (int y = height-1; y>=0; y--) {
            buff.append('|');
            for (int x=0; x<width; x++) {
                if (getGrid(x,y)) buff.append('+');
                else buff.append(' ');
            }
            buff.append("|\n");
        }
        for (int x=0; x<width+2; x++) buff.append('-');
        return(buff.toString());
    }


}


