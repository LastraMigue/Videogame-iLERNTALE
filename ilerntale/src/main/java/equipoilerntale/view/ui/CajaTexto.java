package equipoilerntale.view.ui;

import javax.swing.*;
import java.awt.*;

/**
 * COMPONENTE REUTILIZABLE PARA PANTALLAS DE DIÁLOGO Y ANUNCIOS.
 */
public class CajaTexto {

    /**
     * PUNTO DE ENTRADA PRINCIPAL DE LA APLICACIÓN.
     * INICIA EL MARCO PRINCIPAL EN EL HILO DE EVENTOS DE SWING.
     */

    /**
     * Crea un globo de diálogo estilizado (estilo Undertale/iLERNTALE).
     * 
     * @param padre JFrame principal que contiene los diálogos.
     * @param texto El texto que debe mostrar el globo.
     * @param x     Posición X en pantalla.
     * @param y     Posición Y en pantalla.
     * @return El cuadro de diálogo flotante configurado.
     */
    public static JDialog crearDialogo(JFrame padre, String texto, int x, int y) {
        JDialog dialogo = new JDialog(padre);
        dialogo.setUndecorated(true);
        dialogo.setSize(500, 120);
        dialogo.setLocation(x, y);

        // Contenido con fondo negro y borde blanco
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Área de texto estilizada
        JTextArea area = new JTextArea(texto);
        area.setBounds(20, 20, 460, 80);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        panel.add(area);
        dialogo.setContentPane(panel);

        return dialogo;
    }
}
