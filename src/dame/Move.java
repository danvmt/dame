package dame;

import javafx.util.Pair;

public class Move {
    public int currRow, currCol, moveRow, moveCol;

    public Move(int currRow, int currCol, int moveRow, int moveCol) {
        this.currRow = currRow;
        this.currCol = currCol;
        this.moveRow = moveRow;
        this.moveCol = moveCol;
    }

    public Pair<Integer, Integer> getSpaceInbetween() {
        return new Pair<>((currRow + moveRow) / 2, (currCol + moveCol) / 2);
    }

    public String toString() {
        return "current: (" + currRow + ", " + currCol + ") + next: (" + moveRow + "," + moveCol + ")";
    }
}
