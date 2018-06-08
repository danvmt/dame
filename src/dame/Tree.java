package dame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tree {
    private Board node;
    private Move move;
    private int score;
    private ArrayList<Tree> children;

    public Tree(Board node, Move move, int score, Tree ... children) {
        this.node = node;
        this.children = new ArrayList<>(Arrays.asList(children));
        this.score = score;
        this.move = move;
    }

    public Board getBoard() {return node;}

    public Move getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public int getNumChildren() {
        return children.size();
    }

    public void setScore(int newVal) {
        score = newVal;
    }

    public Tree getChild(int index) {
        return children.get(index);
    }

    public void addChild(Tree child) {
        children.add(child);
    }

    public void addChildren(Tree ... children) {
        for (Tree child : children) {
            addChild(child);
        }
    }
}
