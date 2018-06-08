package dame;

import javafx.util.Pair;

public class Move {
    public int currRow, currCol, moveRow, moveCol;

    /**
     * Creates a new move
     * @param currRow the current row of the piece
     * @param currCol the current column of the piece
     * @param moveRow the row the piece will be moved to
     * @param moveCol the column the piece will be moved to
     */
    public Move(int currRow, int currCol, int moveRow, int moveCol) {
        this.currRow = currRow;
        this.currCol = currCol;
        this.moveRow = moveRow;
        this.moveCol = moveCol;
    }

    /**
     * Returns the location of the space in between the move
     * @return the row, col location of the space
     */
    public Pair<Integer, Integer> getSpaceInbetween() {
        return new Pair<>((currRow + moveRow) / 2, (currCol + moveCol) / 2);
    }

    /**
     * Creates a string representation of a move
     * @return a string version of a move
     */
    public String toString() {
        return "current: (" + currRow + ", " + currCol + ") + next: (" + moveRow + "," + moveCol + ")";
    }
}
