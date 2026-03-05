package equipoilerntale;

import equipoilerntale.controller.MainController;
import equipoilerntale.view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Instanciamos la vista
            MainFrame mainFrame = new MainFrame();

            // 2. Instanciamos el controlador pasándole la vista
            MainController mainController = new MainController(mainFrame);

            // 3. Arrancamos el hilo del juego
            mainController.startGameThread();
        });
    }
}