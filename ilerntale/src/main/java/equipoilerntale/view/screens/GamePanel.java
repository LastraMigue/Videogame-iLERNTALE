package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import equipoilerntale.view.MainFrame;

public class GamePanel extends JPanel {
    private MainFrame mainFrame;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public GamePanel(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLUE);
        setDoubleBuffered(true);
        setFocusable(true);
        setLayout(new BorderLayout());
    }

}