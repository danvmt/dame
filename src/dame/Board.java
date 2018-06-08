package dame;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Board extends JPanel implements MouseListener{
    private color[][] boardColor = new color[8][8];
    private color player = color.RED;
    private ArrayList<Move> moves;
    private boolean jumped = false;
    private int redCount = 12;
    private int blackCount = 12;
    private int blackKing = 0;
    private int redKing = 0;
    private color winner = color.NULL;
    private boolean initial = true;
    private Ai ai;
    private boolean tie = false;

    public Board() {
        addMouseListener(this);
        ai = new Ai(color.BLACK);
    }

    public Board(color[][] boardColor) {
        this();
        this.boardColor = boardColor;
    }

    public Board(color[][] boardColor, int numRed, int numBlack) {
        this();
        this.boardColor = boardColor;
        this.redCount = numRed;
        this.blackCount = numBlack;
    }

    /**
     * @param boardColor Position der Steine
     * @param numRed Anzahl rote Steine
     * @param numBlack Anzahl schwarze Steine
     * @param numRedKing Anzahl roter Damen
     * @param numBlackKing Anzahl schwarzer Damen
     */
    public Board(color[][] boardColor, int numRed, int numBlack, int numRedKing, int numBlackKing) {
        this(boardColor, numRed, numBlack);
        this.redKing = numRedKing;
        this.blackKing = numBlackKing;
    }

    public int getNumRed() {
        return redCount;
    }

    public int getNumRedKing() {
        return redKing;
    }

    public int getNumBlack() {
        return blackCount;
    }

    public int getNumBlackKing() {
        return blackKing;
    }

    /**
     * Gibt an ob beim letzten Zug ein Gegner geschlagen wurde
     */
    public boolean isJumped() {
    	return jumped;
    }

    /**
     * Stärke (Gewicht) der roten Figuren. Eine Dame zählt dreifach
     */
    public int getRedWeightedScore() {
        return redCount - redKing + (3 * redKing);
    }

    /**
     * Stärke (Gewicht) der schwarzen Figuren. Eine Dame zählt dreifach
     */
    public int getBlackWeightedScore() {
        return blackCount - blackKing + (3 * blackKing);
    }

    public void setPlayer(color color) {
        player = color;
    }

    /**
     * Verarbeitung des Mausklicks/Stein drag
     */
    public void mouseReleased(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

        boolean crowned;

        if (player != ai.getColor()) {
            for (Move move : moves) {
                if (move.moveRow == row && move.moveCol == col) {

                    crowned = movePiece(move);
                    handleJump(move);
                    updateBoard(move, crowned);
                    break;
                }
            }
            repaint();
        } else if (player == ai.getColor()) {
            Move aiMove = ai.getAIMove(this);

            crowned = movePiece(aiMove);
            handleJump(aiMove);
            updateBoard(aiMove, crowned);
        }

    }

    public void updateBoard(Move move, boolean crowned) {
        // Checks for winner
        if (blackCount == 0) {
            winner = color.RED;
            repaint();
            return;
        }
        if (redCount == 0) {
            winner = color.BLACK;
            repaint();
            return;
        }

        // Handle multiple jumps
        if (!crowned && jumped) {
            if (getJumps(move.moveRow, move.moveCol).isEmpty()) {
                jumped = false;
                if (player == color.RED)
                    player = color.BLACK;
                else
                    player = color.RED;
            }
        } else {
            // Changes player
            jumped = false;
            if (player == color.RED) {
                player = color.BLACK;
            } else
                player = color.RED;
        }
        repaint();
    }

    public void mouseClicked(MouseEvent evt) {
        if (initial) {
            initial = false;
            repaint();
        }
    }


    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

    /**
     * Bestimmt alle gültigen Positionen um den Stein zu platzieren
     */
    public void mousePressed(MouseEvent evt) {
        int col = evt.getX() / 75;
        int row = evt.getY() / 75;

        if (getAllLegalMovesForColor(player).size() == 0) {
            if (player == color.RED) {
                player = color.BLACK;
            } else {
                player = color.RED;
            }
            if (getAllLegalMovesForColor(player).size() == 0) {
                tie = true;
                repaint();
            }
        }

        if (col >= 0 && col < 8 && row >=0 && row < 8) {
            if (jumped) {
                moves = getJumps(row, col);
            } else {
                moves = getLegalMovesForPlayer(row, col);
            }
        }
    }

    /**
     * Entfernt einen geschlagenen Stein
     */
    public void handleJump(Move move) {
        Pair<Integer, Integer> spaceSkipped = move.getSpaceInbetween();

        if (spaceSkipped.getKey() != move.currRow && spaceSkipped.getKey() != move.moveRow &&
                spaceSkipped.getValue() != move.moveCol && spaceSkipped.getValue() != move.currCol) {
            if (boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] == color.RED_KING) {
                redKing -= 1;
            }
            if (boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] == color.BLACK_KING) {
                blackKing -= 1;
            }
            boardColor[spaceSkipped.getKey()][spaceSkipped.getValue()] = color.EMPTY;
            jumped = true;
            if (player == color.RED) {
                blackCount -= 1;

            } else {
                redCount -= 1;
            }
        } else {
            jumped = false;
        }
    }

    /**
     * Bestimmt alle möglichkeiten um von der aktuellen Position Gegner zu schlagen
     * Damen können rückwärts schlagen
     */
    public ArrayList<Move> getJumps(int row, int col) {
        ArrayList<Move> jumps = new ArrayList<>();
        color chosenPiece = getInfoAtPosition(row, col);

        if (player == color.RED) {
            if (chosenPiece == color.RED || chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.BLACK ||
                        getInfoAtPosition(row + 1, col + 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == color.BLACK ||
                        getInfoAtPosition(row + 1, col - 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }

            if (chosenPiece == color.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.BLACK ||
                        getInfoAtPosition(row - 1, col + 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                } if (getInfoAtPosition(row - 1, col - 1) == color.BLACK ||
                        getInfoAtPosition(row - 1, col - 1) == color.BLACK_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }
        } else if (player == color.BLACK) {
            if (chosenPiece == color.BLACK || chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == color.RED ||
                        getInfoAtPosition(row - 1, col + 1) == color.RED_KING) {
                    if (getInfoAtPosition(row - 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row - 1, col - 1) == color.RED ||
                        getInfoAtPosition(row - 1, col - 1) == color.RED_KING) {
                    if (getInfoAtPosition(row - 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row - 2, col - 2));
                    }
                }
            }

            if (chosenPiece == color.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == color.RED ||
                        getInfoAtPosition(row + 1, col + 1) == color.RED_KING) {
                    if (getInfoAtPosition(row + 2, col + 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col + 2));
                    }
                }
                if (getInfoAtPosition(row + 1, col - 1) == color.RED ||
                        getInfoAtPosition(row + 1, col - 1) == color.RED_KING) {
                    if (getInfoAtPosition(row + 2, col - 2) == color.EMPTY) {
                        jumps.add(new Move(row, col, row + 2, col - 2));
                    }
                }
            }
        }
        return jumps;
    }

    /**
     * Bestimmt alle möglichen Züge
     */
    public ArrayList<Move> getLegalMovesForPlayer(int row, int col) {
        return getLegalMovesForColorAtPosition(player, row, col);
    }

    public ArrayList<Move> getLegalMovesForColorAtPosition(color color, int row, int col) {
        Board.color chosenPiece = getInfoAtPosition(row, col);
        ArrayList<Move> moves = new ArrayList<>();

        if (color == Board.color.RED) {
            if (chosenPiece == Board.color.RED || chosenPiece == Board.color.RED_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));

            }
            if (chosenPiece == Board.color.RED_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));

            }
        } else if (color == Board.color.BLACK){
            if (chosenPiece == Board.color.BLACK || chosenPiece == Board.color.BLACK_KING) {
                if (getInfoAtPosition(row - 1, col + 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col + 1));
                if (getInfoAtPosition(row - 1, col - 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row - 1, col - 1));
            }
            if (chosenPiece == Board.color.BLACK_KING) {
                if (getInfoAtPosition(row + 1, col + 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col + 1));
                if (getInfoAtPosition(row + 1, col - 1) == Board.color.EMPTY)
                    moves.add(new Move(row, col, row + 1, col - 1));
            }
        }

        ArrayList<Move> jumps = getJumps(row, col);
        moves.addAll(jumps);
        return moves;
    }

    public ArrayList<Move> getAllLegalMovesForColor(color color) {
        ArrayList<Move> moves = new ArrayList<>();
        int count = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Board.color currPosition = getInfoAtPosition(row, col);
                if (currPosition == color) {
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                }

                if (color == Board.color.RED && currPosition == Board.color.RED_KING){
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                } else if (color == Board.color.BLACK && currPosition == Board.color.BLACK_KING) {
                    moves.addAll(getLegalMovesForColorAtPosition(color, row, col));
                    count++;
                }

                if (count == 12) {
                    return moves;
                }
            }
        }
        return moves;
    }

    /**
     * Stein wird bewegt
     * Wandelt in Dame um, falls auf der Stein auf der anderen Seite ankommt
     */
    public boolean movePiece(Move move) {
        color temp = boardColor[move.currRow][move.currCol];
        boardColor[move.currRow][move.currCol] = color.EMPTY;

        if (player == color.RED && move.moveRow == 7) {
            boardColor[move.moveRow][move.moveCol] = color.RED_KING;
            redKing += 1;
            return true;
        } else if (player == color.BLACK && move.moveRow == 0){
            boardColor[move.moveRow][move.moveCol] = color.BLACK_KING;
            blackKing += 1;
            return true;
        } else {
            boardColor[move.moveRow][move.moveCol] = temp;
            return false;
        }
    }

    public void paintComponent(Graphics graphic) {
        // Creates checker board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (col % 2 == row % 2) {
                    graphic.setColor(Color.white);
                } else {
                    graphic.setColor(Color.black);
                }
                graphic.fillRect(row * 75, col * 75, 75, 75);

            }
        }
        
        if (initial == true) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 20));
            graphic.drawString("Clicke um das Spiel zu starten", 85, 300);
        }

        if (winner != color.NULL) {
            graphic.setColor(Color.RED);
            if (winner == color.RED) {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
                graphic.drawString("Rot gewinnt", 50, 300);
            } else {
                graphic.setFont(new Font("Helvetica", Font.PLAIN, 90));
                graphic.drawString("Blau gewinnt", 30, 300);
            }
        } else if (tie) {
            graphic.setColor(Color.RED);
            graphic.setFont(new Font("Helvetica", Font.PLAIN, 100));
            graphic.drawString("Unentschieden", 225, 300);

        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {

                    switch (getInfoAtPosition(row, col)) {

                        case RED:
                            graphic.setColor(Color.RED);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            break;
                        case RED_KING:
                            graphic.setColor(Color.RED);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            graphic.setColor(Color.WHITE);
                            graphic.setFont(new Font("Helvetica", Font.PLAIN, 50));
                            graphic.drawString("K", col * 75 + 23, row * 75 + 55);
                            break;
                        case BLACK:
                            graphic.setColor(Color.BLUE);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            break;
                        case BLACK_KING:
                            graphic.setColor(Color.BLUE);
                            graphic.fillOval(col * 75, row * 75, 75, 75);
                            graphic.setColor(Color.WHITE);
                            graphic.setFont(new Font("Helvetica", Font.PLAIN, 50));
                            graphic.drawString("K", col * 75 + 23, row * 75 + 55);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Startaufstellung
     */
    public void placePieces() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 != col % 2) {
                    if (row < 3) {
                        boardColor[row][col] = color.RED;
                    } else if (row > 4) {
                        boardColor[row][col] = color.BLACK;
                    } else {
                        boardColor[row][col] = color.EMPTY;
                    }
                } else {
                    boardColor[row][col] = color.EMPTY;
                }
            }
        }
    }

    public color getInfoAtPosition(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return color.NULL;
        }
        return boardColor[row][col];
    }

    public String toString() {
        String string = "";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                switch (getInfoAtPosition(row, col)) {
                    case EMPTY:
                        string = string.concat("|  ");
                        break;
                    case RED:
                        string = string.concat("| R");
                        break;
                    case RED_KING:
                        string = string.concat("|RK");

                        break;
                    case BLACK:
                        string = string.concat("| B");

                        break;
                    case BLACK_KING:
                        string = string.concat("|BK");
                        break;
                }
                if (col == 7) {
                    string = string.concat("|\n");
                }
            }
        }
        return string;
    }

    public boolean equals(Board other) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (this.getInfoAtPosition(row, col) != other.getInfoAtPosition(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * mögliche Stati der Felder
     */
    public enum color {
        BLACK,
        BLACK_KING,
        RED,
        RED_KING,
        EMPTY,
        NULL
    }
}