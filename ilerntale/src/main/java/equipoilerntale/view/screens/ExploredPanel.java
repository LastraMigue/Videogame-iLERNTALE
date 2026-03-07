package equipoilerntale.view.screens;

import java.awt.*;
import javax.swing.*;
import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationController;
import equipoilerntale.view.MainFrame;
import equipoilerntale.view.renderers.MapRenderer;
import equipoilerntale.view.renderers.PlayerRenderer;

/**
 * PANEL DE EXPLORACIÓN.
 * Es la pantalla principal donde el jugador se mueve por el mapa.
 * Esta clase es una VISTA: pide datos al controlador y los utiliza
 * para "pintar" la escena mediante renderizadores modulares.
 */
public class ExploredPanel extends JPanel {

    private final ExplorationController controller;
    private final MapRenderer mapRenderer;
    private final PlayerRenderer playerRenderer;
    private Image background;

    /**
     * Constructor del panel de exploración.
     * 
     * @param mainFrame  Referencia al marco principal de la aplicación.
     * @param personaje  Nombre de la carpeta de sprites del héroe elegido.
     * @param controller El controlador lógico que gestionará este nivel.
     */
    public ExploredPanel(MainFrame mainFrame, String personaje, ExplorationController controller) {
        this.controller = controller;
        this.mapRenderer = new MapRenderer();
        this.playerRenderer = new PlayerRenderer();

        // Configuración de la ventana Swing
        setPreferredSize(new Dimension(GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA));
        setFocusable(true);
        addKeyListener(controller.getInputHandler());

        cargarFondo("/mapa/Pasillo1.png");
    }

    /**
     * Carga y escala el fondo del mapa.
     */
    private void cargarFondo(String ruta) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                background = new ImageIcon(url).getImage().getScaledInstance(
                        GameSettings.ANCHO_PANTALLA,
                        GameSettings.ALTO_PANTALLA,
                        Image.SCALE_SMOOTH);
            }
        } catch (Exception e) {
            System.err.println("Error cargando recurso de fondo: " + e.getMessage());
        }
    }

    /**
     * MÉTODO DE DIBUJO de Swing.
     * Delega el pixel art real a los renderizadores especializados.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (controller == null)
            return;

        // 1. Dibujar Escenario
        mapRenderer.dibujarFondo(g2d, background, getWidth(), getHeight());
        mapRenderer.dibujarEtiquetaZona(g2d, controller.getZonaPuerta(), "Aula 124");

        // 2. Dibujar Jugador
        playerRenderer.dibujarJugador(g2d,
                controller.getCurrentSprite(),
                controller.getJugadorX(),
                controller.getJugadorY());

        // 3. Dibujar Capa de Debug
        if (ExplorationController.DEBUG_MODE) {
            mapRenderer.dibujarDebug(g2d,
                    controller.getParedes(),
                    controller.getHitboxJugador(),
                    controller.getZonaPuerta());
        }
    }

    /**
     * Solicita una actualización visual desde el Timer de MainFrame.
     */
    public void requestRender() {
        repaint();
    }
}
