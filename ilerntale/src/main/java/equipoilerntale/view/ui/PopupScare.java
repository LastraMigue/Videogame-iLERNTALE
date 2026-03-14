package equipoilerntale.view.ui;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

/**
 * Ventana emergente tipo "jumpscare" o aviso rápido que aparece sobre todas las demás.
 * Utiliza {@link JWindow} para no tener bordes ni controles de ventana típicos.
 */
public class PopupScare extends JWindow {
    /** Fuente personalizada compartida para todos los popups. */
    private static Font customFont;

    /**
     * Constructor del PopupScare.
     * 
     * @param x Coordenada X de aparición.
     * @param y Coordenada Y de aparición.
     * @param width Ancho de la ventana.
     * @param height Alto de la ventana.
     */
    public PopupScare(int x, int y, int width, int height) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        loadFont();

        JLabel label = new JLabel("MUERE!", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        if (customFont != null) {
            label.setFont(customFont.deriveFont(24f));
        } else {
            label.setFont(new Font("Monospaced", Font.BOLD, 24));
        }

        add(label, BorderLayout.CENTER);

        setBounds(x, y, width, height);
        setAlwaysOnTop(true);
    }

    /**
     * Carga la fuente "Deltarune" si no ha sido cargada previamente.
     */
    private void loadFont() {
        if (customFont == null) {
            try (InputStream is = getClass().getResourceAsStream("/font/deltarune.ttf")) {
                if (is != null) {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Muestra el popup durante un tiempo determinado y luego lo cierra automáticamente.
     * 
     * @param millis Tiempo en milisegundos que permanecerá visible.
     */
    public void showFor(int millis) {
        setVisible(true);
        Timer timer = new Timer(millis, e -> {
            setVisible(false);
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
