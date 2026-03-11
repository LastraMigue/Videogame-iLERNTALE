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
     * @param texto El texto que debe mostrar el globo.
     * @return El panel de diálogo configurado.
     */
    public static JPanel crearPanel(String texto) {
        // Panel principal del diálogo (negro con borde blanco fino)
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panel.setSize(500, 120);

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

        return panel;
    }
}
