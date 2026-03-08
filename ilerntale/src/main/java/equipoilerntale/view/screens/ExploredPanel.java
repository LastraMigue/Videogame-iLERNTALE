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
    private int cameraX = 0;
    private int cameraY = 0;

    private static final int MAP_WIDTH = GameSettings.MAP_WIDTH;
    private static final int MAP_HEIGHT = GameSettings.MAP_HEIGHT;

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

        cargarFondo("/mapa/Pasillo2.png");
    }

    /**
     * Carga el fondo del mapa (imagen completa sin escalar a pantalla).
     */
    private void cargarFondo(String ruta) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                background = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Error cargando recurso de fondo: " + e.getMessage());
        }
    }

    /**
     * MÉTODO DE DIBUJO de Swing.
     * Sistema de cámara: sigue al jugador manteniéndolo centrado.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (controller == null)
            return;

        // === CÁMARA: calcular posición centrada ===
        int jugadorX = controller.getJugadorX();
        int jugadorY = controller.getJugadorY();
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        // Cámara centrada en el jugador
        cameraX = jugadorX - screenWidth / 2;
        cameraY = jugadorY - screenHeight / 2;

        // Limitar cámara a los bordes del mapa
        cameraX = Math.max(0, Math.min(cameraX, MAP_WIDTH - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, MAP_HEIGHT - screenHeight));

        // === DIBUJAR MUNDO (con transformación de cámara) ===
        g2d.translate(-cameraX, -cameraY);

        // 1. Dibujar Escenario (fondo del mapa)
        mapRenderer.dibujarFondo(g2d, background, MAP_WIDTH, MAP_HEIGHT);
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

        // Restaurar transformación para HUD fijo
        g2d.translate(cameraX, cameraY);

        // === DIBUJAR HUD (fijo, no se mueve con la cámara) ===
        dibujarHUD(g2d, screenWidth, screenHeight);
    }

    /**
     * Dibuja la interfaz de usuario (HUD) en coordenadas de pantalla fijas.
     */
    private void dibujarHUD(Graphics2D g2d, int width, int height) {
        // Barra de vida (ejemplo)
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(10, 10, 200, 25);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(12, 12, 150, 21);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("VIDA", 15, 26);

        // Indicador de posición
        g2d.setColor(Color.WHITE);
        g2d.drawString("X: " + controller.getJugadorX() + " Y: " + controller.getJugadorY(), 10, 50);
    }

    /**
     * Solicita una actualización visual desde el Timer de MainFrame.
     */
    public void requestRender() {
        repaint();
    }
}
