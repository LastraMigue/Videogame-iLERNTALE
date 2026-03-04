package equipoilerntale.view;

import javax.swing.JPanel;
import java.awt.Color;

public class GamePanel extends JPanel {

    public GamePanel() {
        // Set an initial background color
        setBackground(Color.BLACK);

        // This makes the panel focusable so it can receive key events
        setFocusable(true);
    }
}
