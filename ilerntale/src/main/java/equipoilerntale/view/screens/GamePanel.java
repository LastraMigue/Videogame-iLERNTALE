package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import equipoilerntale.GameSettings;
import equipoilerntale.view.MainFrame;
import equipoilerntale.view.ui.CajaTexto;

/**
 * PANTALLA DE INTRODUCCIÓN (NARRATIVA).
 * Su única tarea es gestionar el orden de los diálogos automáticos entre Soraya
 * y Jesica.
 * La UI real es delegada a la clase CajaTexto.
 */
public class GamePanel extends JPanel {

    private final MainFrame mainFrame;
    private final JLabel labelFondo = new JLabel();
    private final JLabel labelSoraya = new JLabel();
    private final JLabel labelJesica = new JLabel();
    private int indicePasos = 0;
    private Timer timerHistoria;

    // --- Guion de la Historia ---
    private final String[][] script = {
            { "Soraya", "¡Hola! ¡Hola!¡Hola!Soy Soraya. Bienvenido al mundo de iLERNTALE." },
            { "Jesica", "¡Y yo soy Jesica! ¡Qué emoción tenerte aquí!" },
            { "Soraya", "En iLERNTALE exploraremos el instituto juntos." },
            { "Jesica", "¡Vamos a explorar juntos! ¡Usa las flechas!" }
    };

    /**
     * CONSTRUCTOR DEL PANEL DE INTRODUCCIÓN (NARRATIVA).
     */
    public GamePanel(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(GameSettings.INTRO_WIDTH, GameSettings.INTRO_HEIGHT));
        setLayout(null);

        inicializarPersonajes();
        configurarCicloVida();
    }

    private void inicializarPersonajes() {
        // Carga y escalado de imágenes
        ImageIcon iconoFondo = cargarIcono("/mapa/pasillo1.jpg", GameSettings.ANCHO_PANTALLA,
                GameSettings.ALTO_PANTALLA);
        ImageIcon iconoSoraya = cargarIcono("/dialogue/soraya.png", 256, 256);
        ImageIcon iconoJesica = cargarIcono("/dialogue/jesica.png", 256, 256);

        if (iconoFondo != null)
            labelFondo.setIcon(iconoFondo);
        if (iconoSoraya != null)
            labelSoraya.setIcon(iconoSoraya);
        if (iconoJesica != null)
            labelJesica.setIcon(iconoJesica);

        labelFondo.setBounds(0, 0, GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA);
        labelSoraya.setBounds(0, 344, 256, 256);
        labelJesica.setBounds(750, 344, 256, 256);

        labelSoraya.setVisible(false);
        labelJesica.setVisible(false);

        add(labelSoraya);
        add(labelJesica);
        add(labelFondo);
    }

    private void configurarCicloVida() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                indicePasos = 0;

                timerHistoria = new Timer(3000, evt -> avanzarHistoria());
                timerHistoria.setRepeats(false);
                timerHistoria.start();
            }
        });
    }

    /**
     * Lógica que maneja el paso del guion.
     */
    private void avanzarHistoria() {
        if (indicePasos >= script.length) {

            if (timerHistoria != null) {
                timerHistoria.stop();
            }

            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog) {
                    w.dispose();
                }
            }

            mainFrame.cambiarPantalla("EXPLORACION");
            return;
        }

        String personaje = script[indicePasos][0];
        String texto = script[indicePasos][1];

        // Cambiar quién es visible según quién habla
        labelSoraya.setVisible(personaje.equals("Soraya"));
        labelJesica.setVisible(personaje.equals("Jesica"));

        // Dibujar globo de texto centrado usando el componente reutilizable
        Point p = (isShowing()) ? getLocationOnScreen() : new Point(0, 0);
        JDialog caja = CajaTexto.crearDialogo(mainFrame, texto, p.x + 250, p.y + 450);

        // Timer de auto-cierre idéntico a la funcionalidad original
        Timer timerCierre = new Timer(4000, e -> {
            caja.dispose();
            indicePasos++;
            avanzarHistoria();
        });
        timerCierre.setRepeats(false);
        timerCierre.start();

        caja.setVisible(true);
    }

    private ImageIcon cargarIcono(String ruta, int w, int h) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null)
            return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}
