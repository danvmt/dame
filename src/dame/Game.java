package dame;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class Game extends Applet {
    private static Board b = new Board();

    /**
     * Erstelle das Spielbrett und setze die Steine
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(620, 620);
        b.placePieces();

        frame.getContentPane().add(b);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
