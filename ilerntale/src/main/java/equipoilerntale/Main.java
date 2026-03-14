package equipoilerntale;

import equipoilerntale.view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    /**
     * PUNTO DE ENTRADA PRINCIPAL DEL JUEGO.
     * INICIA EL MARCO PRINCIPAL EN EL HILO DE EVENTOS DE SWING.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}