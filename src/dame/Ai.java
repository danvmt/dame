package dame;

import java.util.ArrayList;

public class Ai {

    private Board.color color;
    private Board.color oppColor;
    private Tree descisionTree;
    private Move lastMove;
    
    /**
     * Computer Gegner
     */
    public Ai(Board.color color) {
        this.color = color;
        if (color == Board.color.RED) {
            oppColor = Board.color.BLACK;
        } else {
            oppColor = Board.color.RED;
        }
    }

    /**
     * Entscheidung, welcher Zug ausgeführt wird
     */
    public Move getAIMove(Board board) {
        descisionTree = makeDescisionTree(board);
        lastMove = pickMove();
        return lastMove;
    }

    public Board.color getColor() {
        return color;
    }

    /**
     * Erstellt einen Baum mit allen mögliche Zügen der nächsten drei Runden
     */
    private Tree makeDescisionTree(Board board) {
        Tree mainTree = new Tree(board, null, score(board));
        ArrayList<Move> moves;
        if (board.isJumped()) {
            moves  = board.getJumps(lastMove.moveRow, lastMove.moveCol);
        } else {
            moves = board.getAllLegalMovesForColor(color);
        }

        for (Move move : moves) {
            Board temp = copyBoard(board);
            temp.movePiece(move);
            temp.handleJump(move);
            Tree firstLayer = new Tree(temp, move, score(temp));
            ArrayList<Move> secondMoves = temp.getAllLegalMovesForColor(oppColor);

            for (Move sMove : secondMoves) {
                Board temp2 = copyBoard(temp);
                temp2.movePiece(sMove);
                temp2.handleJump(sMove);
                Tree secondLayer = new Tree(temp2, sMove, score(temp2));
                ArrayList<Move> thirdMoves = temp2.getAllLegalMovesForColor(color);

                for (Move tMove : thirdMoves) {
                    Board temp3 = copyBoard(temp2);
                    temp3.movePiece(tMove);
                    temp3.handleJump(tMove);

                    secondLayer.addChild(new Tree(temp3, tMove, score(temp3)));
                }

                firstLayer.addChild(secondLayer);
            }
            mainTree.addChild(firstLayer);
        }

        return mainTree;
    }

    /**
     * Auswahl des Zugs anhand der minimax Methode
     */
    private Move pickMove() {
        int max = -13;
        int index = 0;
        for (int i = 0; i < descisionTree.getNumChildren(); i++) {
            Tree child = descisionTree.getChild(i);
            int smin = 13;
            for (Tree sChild : child.getChildren()) {
                int tMax = -13;
                for (Tree tchild : sChild.getChildren()) {
                    if (tchild.getScore() >= tMax) {
                        tMax = tchild.getScore();
                    }
                }
                sChild.setScore(tMax);
                if (sChild.getScore() <= smin) {
                    smin = sChild.getScore();
                }
            }
            child.setScore(smin);
            if (child.getScore() >= max) {
                max = child.getScore();
                index = i;
            }
        }
        return descisionTree.getChild(index).getMove();
    }

    private int score(Board board) {
        if (color == dame.Board.color.RED) {
            return board.getRedWeightedScore() - board.getBlackWeightedScore();
        } else {
            return board.getRedWeightedScore() - board.getRedWeightedScore();
        }
    }

    private Board copyBoard(Board board) {
        dame.Board.color[][] color = new Board.color[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                color[row][col] = board.getInfoAtPosition(row, col);
            }
        }
        return new Board(color, board.getNumRed(), board.getNumBlack(), board.getNumRedKing(), board.getNumBlackKing());
    }


}
