package model;

import java.io.Serializable;
import java.util.*;
import javafx.scene.paint.Color;

/** An immutable representation of a tetris piece in a particular rotation.
 *  Each piece is defined by the blocks that make up its body.
 */
public class TetrisPiece implements Serializable {

    /*
     Implementation notes:
     -The starter code does a few simple things
     -It stores the piece body as a TetrisPoint[] array
     -The attributes in the TetrisPoint class are x and y coordinates
     -Don't assume there are 4 points in the piece body; there might be less or more!
    */
    private TetrisPoint[] body; // y and x values that make up the body of the piece.
    private int[] lowestYVals; //The lowestYVals array contains the lowest y value for each x in the body.
    private int width;
    private int height;
    private TetrisPiece next; // We'll use this to link each piece to its "next" rotation.
    private Color color;
    private int id;
    static private TetrisPiece[] pieces;	// array of rotations for this piece


    // String constants for the standard 7 tetris pieces
    public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
    public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
    public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
    public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
    public static final String S2_STR		= "0 1	1 1  1 0  2 0";
    public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
    public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";

    /**
     * Defines a new piece given a TetrisPoint[] array that makes up its body.
     * Makes a copy of the input array and the TetrisPoint inside it.
     * Note that this constructor should define the piece's "lowestYVals". For each x value
     * in the body of a piece, the lowestYVals will contain the lowest y value for that x in the body.
     * This will become useful when computing where the piece will land on the board!!
     */
    public TetrisPiece(TetrisPoint[] points) {
        //your code here!
        this.body = points;
        HashMap<Integer, Integer> lowYVal4XVal = new HashMap<>();

        // Finding the lowest y coordinate of every x-value in the body
        for (int i = 0; i < this.body.length; i++) {
            if (!lowYVal4XVal.containsKey(this.body[i].x)) {
                int curX = this.body[i].x;
                int lowestY = this.body[i].y;
                for (int j = 0; j < this.body.length; j++) {
                    if (this.body[j].x == curX && this.body[j].y < lowestY) {
                        lowestY = this.body[j].y;
                    }
                }
                lowYVal4XVal.put(curX, lowestY);
            }
        }
        this.lowestYVals = lowYVal4XVal.values().stream().mapToInt(Integer::intValue).toArray();
        this.width = lowestYVals.length;

        // Calculating the max height of the body
        int maxHeight = 0;
        for (int i = 0; i < this.body.length; i++) {
            if (this.body[i].y + 1 > maxHeight) {
                maxHeight = this.body[i].y + 1;
            }
        }
        this.height = maxHeight;

        // Sets color to white and id to 1 by default.
        // Garbage pieces will be white and have an id of 1.
        if (this.color == null) {
            this.color = Color.WHITE;
            this.id = 1;
        }
    }

    /**
     * Alternate constructor for a piece, takes a String with the x,y body points
     * all separated by spaces, such as "0 0  1 0  2 0  1 1". Depending on the body,
     * assigns a color to the new constructed piece.
     */
    public TetrisPiece(String points) {
        this(parsePoints(points));
        switch (points) {
            case STICK_STR:
                this.color = Color.CYAN;
                this.color = this.color.brighter();
                this.id = 2;
                break;
            case L1_STR:
                this.color = Color.DODGERBLUE;
                this.color = this.color.brighter();
                this.id = 3;
                break;
            case L2_STR:
                this.color = Color.ORANGE;
                this.color = this.color.brighter();
                this.id = 4;
                break;
            case S1_STR:
                this.color = Color.GREEN;
                this.color = this.color.brighter();
                this.id = 5;
                break;
            case S2_STR:
                this.color = Color.RED;
                this.color = this.color.brighter();
                this.id = 6;
                break;
            case SQUARE_STR:
                this.color = Color.YELLOW;
                this.color = this.color.brighter();
                this.id = 7;
                break;
            case PYRAMID_STR:
                this.color = Color.PURPLE;
                this.color = this.color.brighter();
                this.id = 8;
                break;
        }
    }

    /**
     * Returns the width of the piece measured in blocks.
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the piece measured in blocks.
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns a pointer to the piece's body. The caller
     * should not modify this array.
     *
     * @return point array that defines piece body
     */
    public TetrisPoint[] getBody() {
        return body;
    }

    /**
     * Returns the color of this piece. The color is determined by the
     * piece shape.
     *
     * @return the color of this piece
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color of this piece manually.
     *
     * @param newColor the new color of this piece
     */
    public void setColor(Color newColor) {
        this.color = newColor;
    }

    /**
     * Returns the ID of this piece. The ID of a piece is determined by
     * its color.
     *
     * @return the id of this piece
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of this piece manually.
     *
     * @param newId the new id of this piece
     */
    public void setId(int newId) {
        this.id = newId;
    }


    /**
     * Returns a pointer to the piece's lowest Y values. For each x value
     * across the piece, this gives the lowest y value in the body.
     * This is useful for computing where the piece will land (if dropped).
     * The caller should not modify the values that are returned
     *
     * @return array of integers that define the lowest Y value for every X value of the piece.
     */
    public int[] getLowestYVals() {
        return lowestYVals;
    }

    /**
     * Returns true if two pieces are the same --
     * their bodies contain the same points.
     * Interestingly, this is not the same as having exactly the
     * same body arrays, since the points may not be
     * in the same order in the bodies. Used internally to detect
     * if two rotations are effectively the same.
     *
     * @param obj the object to compare to this
     *
     * @return true if objects are the same
     */
    public boolean equals(Object obj) {
        // throw new UnsupportedOperationException(); //replace this!
        if (obj instanceof TetrisPiece) {

            // Sorting the tetris points to ensure order does not matter when comparing
            TetrisPiece tp = (TetrisPiece)obj;
            TetrisPoint[] sortedTp = sortTetrisPoints(tp.getBody());
            TetrisPoint[] sortedBody = sortTetrisPoints(this.getBody());
            for (int i = 0; i < this.body.length; i++) {
                if (!sortedBody[i].equals(sortedTp[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * This is a static method that will return all rotations of
     * each of the 7 standard tetris pieces:
     * STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
     * The next (counterclockwise) rotation can be obtained
     * from each piece with the fastRotation() method.
     * This method will be called by the model to facilitate
     * selection of random pieces to add to the board.
     * The pieces can be easily rotated because the rotations
     * have been precomputed.
     *
     * @return a list of all the rotations for all the given pieces.
     */
    public static TetrisPiece[] getPieces() {
        // lazy evaluation -- create static array only if needed
        if (TetrisPiece.pieces==null) {
            // use makeFastRotations() to compute all the rotations for each piece
            try {
                TetrisPiece.pieces = new TetrisPiece[]{
                        makeFastRotations(new TetrisPiece(STICK_STR)),
                        makeFastRotations(new TetrisPiece(L1_STR)),
                        makeFastRotations(new TetrisPiece(L2_STR)),
                        makeFastRotations(new TetrisPiece(S1_STR)),
                        makeFastRotations(new TetrisPiece(S2_STR)),
                        makeFastRotations(new TetrisPiece(SQUARE_STR)),
                        makeFastRotations(new TetrisPiece(PYRAMID_STR)),
                };
            } catch (UnsupportedOperationException e) {
                System.out.println("You must implement makeFastRotations!");
                System.exit(1);
            }
        }
        return TetrisPiece.pieces;
    }

    /**
     * Returns a pre-computed piece that is 90 degrees counter-clockwise
     * rotated from the receiver. Fast because the piece is pre-computed.
     * This only works on pieces set up by makeFastRotations(), and otherwise
     * just returns null.
     *
     * @return the next rotation of the given piece
     */
    public TetrisPiece fastRotation() {
        return next;
    }

    /**
     * Given the "first" root rotation of a piece, computes all
     * the other rotations and links them all together
     * in a circular list. The list should loop back to the root as soon
     * as possible.
     * Return the root piece. makeFastRotations() will need to relies on the
     * pointer structure setup here. The suggested implementation strategy
     * is to use the subroutine computeNextRotation() to generate 90 degree rotations.
     * Use this to generate a sequence of rotations and link them together.
     * Continue generating rotations until you generate the piece you started with.
     * Use Piece.equals() to detect when the rotations
     * have gotten you back to the first piece.
     *
     * @param root the default rotation for a piece
     *
     * @return a piece that is a linked list containing all rotations for the piece
     */
    public static TetrisPiece makeFastRotations(TetrisPiece root) {
        // throw new UnsupportedOperationException(); //replace this!
        // Initializing the head of the linked list and the next node of the linked list
        TetrisPiece head = root;
        TetrisPiece curRoot = head.computeNextRotation();
        curRoot.setColor(root.getColor());
        curRoot.setId(root.getId());

        head.next = curRoot;
        TetrisPiece nextRotation = curRoot.computeNextRotation();
        nextRotation.setColor(root.getColor());
        nextRotation.setId(root.getId());

        // Continuously creating the next rotation until it reaches a rotation that is equal to its original root
        while (!nextRotation.equals(head)) {
            curRoot.next = nextRotation;
            curRoot = curRoot.next;
            nextRotation = curRoot.computeNextRotation();
            nextRotation.setColor(root.getColor());
            nextRotation.setId(root.getId());
        }

        // Setting the next node to the head to ensure it is a circular linked list
        curRoot.next = head;
        return head;
    }

    /**
     * Returns a new piece that is 90 degrees counter-clockwise
     * rotated from the receiver.
     *
     * @return the next rotation of the given piece
     */
    public TetrisPiece computeNextRotation() {
        // throw new UnsupportedOperationException(); //replace this!
        TetrisPoint[] result = new TetrisPoint[this.body.length];

        // rotating all the points in the body counterclockwise around the origin
        for (int i = 0; i < this.body.length; i++) {
            int rotatedX = (int)Math.round(this.body[i].x * Math.cos(Math.PI/2) - this.body[i].y * Math.sin(Math.PI/2));
            int rotatedY = (int)Math.round(this.body[i].x * Math.sin(Math.PI/2) + this.body[i].y * Math.cos(Math.PI/2));
            result[i] = new TetrisPoint(rotatedX, rotatedY);
        }

        // getting the smallest x-value of every rotated point
        int biggestXDiff = result[0].x;
        for (int i = 0; i < result.length; i++) {
            if (result[i].x < biggestXDiff) {
                biggestXDiff = result[i].x;
            }
        }

        // translating all the rotated points back to positive x and y-axis
        biggestXDiff = Math.abs(biggestXDiff);
        for (int i = 0; i < result.length; i++) {
            result[i].x += biggestXDiff;
        }
        return new TetrisPiece(result);
    }

    /**
     * Sorts an array of tetris points by points closest to point (0, 0)
     * to the point closest to the top-right corner. Returns a new array
     * of the sorted points
     *
     * @param c an array of unsorted TetrisPoints
     * @return the next rotation of the given piece
     */
    public TetrisPoint[] sortTetrisPoints(TetrisPoint[] c) {
        TetrisPoint[] pointList = Arrays.copyOf(c, c.length);

        // using insertion sort to sort the tetris points
        for (int j = 1; j < pointList.length; j++) {
            TetrisPoint current = pointList[j];
            int i = j-1;
            while ((i > -1) && (pointList[i].compareTo(current) == 1)) {
                pointList[i+1] = pointList[i];
                i--;
            }
            pointList[i+1] = current;
        }
        return pointList;
    }

    /**
     * Print the points within the piece
     *
     * @return a string representation of the piece
     */
    public String toString() {
        String str = "";
        for (int i = 0; i < body.length; i++) {
            str += body[i].toString();
        }
        return str;
    }

    /**
     * Given a string of x,y pairs (e.g. "0 0 0 1 0 2 1 0"), parses
     * the points into a TPoint[] array.
     *
     * @param string input of x,y pairs
     *
     * @return an array of parsed points
     */
    private static TetrisPoint[] parsePoints(String string) {
        List<TetrisPoint> points = new ArrayList<TetrisPoint>();
        StringTokenizer tok = new StringTokenizer(string);
        try {
            while(tok.hasMoreTokens()) {
                int x = Integer.parseInt(tok.nextToken());
                int y = Integer.parseInt(tok.nextToken());

                points.add(new TetrisPoint(x, y));
            }
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse x,y string:" + string);
        }
        // Make an array out of the collection
        TetrisPoint[] array = points.toArray(new TetrisPoint[0]);
        return array;
    }
}
