package equipoilerntale;

import equipoilerntale.view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    /**
     * PUNTO DE ENTRADA PRINCIPAL DE LA APLICACIÓN.
     * INICIA EL MARCO PRINCIPAL EN EL HILO DE EVENTOS DE SWING.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}