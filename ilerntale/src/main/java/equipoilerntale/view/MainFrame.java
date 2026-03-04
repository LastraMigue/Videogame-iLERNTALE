package equipoilerntale.view;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {
        // Set the title of the window
        setTitle("iLERNTALE");

        // Ensure the application exits when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set an initial size
        setSize(800, 600);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Make the window visible
        setVisible(true);
    }
}
