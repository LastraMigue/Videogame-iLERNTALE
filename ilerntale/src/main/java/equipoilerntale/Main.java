package equipoilerntale;

import javax.swing.SwingUtilities;
import equipoilerntale.view.MainFrame;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
