package equipoilerntale.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.model.entity.Boss;
import equipoilerntale.model.entity.WorldItem;
import equipoilerntale.model.map.DoorModel;
import equipoilerntale.service.AssetService;
import equipoilerntale.view.MainFrame;
import equipoilerntale.view.render.*;

/**
 * PANEL PRINCIPAL DEL JUEGO.
 * CONTIENE EXPLORACIÓN: Juego con jugador, zombies y mapa
 */
public class ExplorationPanel extends JPanel {

    private static final Logger LOG = Logger.getLogger(ExplorationPanel.class.getName());

    // ==================== ATRIBUTOS ====================
    private final MainFrame mainFrame;
    private final ExplorationManager manager;
    private final MapRenderer mapRenderer = new MapRenderer();
    private final PlayerRenderer playerRenderer = new PlayerRenderer();
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    private final BossRenderer bossRenderer = new BossRenderer();

    private Image currentBackground;

    private int cameraX, cameraY;

    // ==================== CONSTRUCTOR ====================
    public ExplorationPanel(MainFrame mainFrame, String characterName, ExplorationManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;

        setPreferredSize(new Dimension(GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA));
        setLayout(null);
        setFocusable(true);
        addKeyListener(manager.getInputHandler());

        inicializarRecursosExploracion();
        configurarCicloVida();

        LOG.info("EXPLORATIONPANEL INICIALIZADO");
    }

    // ==================== INICIALIZACIÓN ====================

    private void inicializarRecursosExploracion() {
        // EL FONDO SE CARGA DINÁMICAMENTE POR HABITACIÓN EN dibujarExploracion().
    }

    private void configurarCicloVida() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (manager != null) {
                    manager.activate();
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                dispose();
            }
        });
    }

    // ==================== CARGA DE RECURSOS ====================

    private Image cargarFondo(String ruta, int w, int h) {
        return AssetService.getInstance().loadBackground(ruta);
    }

    private ImageIcon cargarImagen(String ruta, int w, int h) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.warning("NO SE ENCONTRÓ EL RECURSO: " + ruta);
            return null;
        }
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // ==================== RENDERIZADO ====================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        dibujarExploracion(g);
    }

    private void dibujarExploracion(Graphics g) {
        if (manager == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        calculateCamera();
        RenderContext ctx = new RenderContext(g2d, getWidth(), getHeight(), cameraX, cameraY);
        ctx.translateCamera();

        // CARGAR EL FONDO DINÁMICAMENTE SEGÚN LA HABITACIÓN ACTUAL
        if (manager.getCurrentRoom() != null) {
            if (currentBackground == null || !currentBackground
                    .equals(AssetService.getInstance().loadBackground(manager.getCurrentRoom().getBackgroundPath()))) {
                currentBackground = AssetService.getInstance()
                        .loadBackground(manager.getCurrentRoom().getBackgroundPath());
            }
            mapRenderer.drawBackground(ctx, currentBackground);
        }

        if (manager.isDebugMurosVisibles() && manager.getCurrentRoom() != null) {
            for (DoorModel door : manager.getCurrentRoom().getDoors()) {
                mapRenderer.drawZoneLabel(ctx, door.getArea(), door.getTargetRoomName());
            }
        }

        playerRenderer.drawPlayer(ctx, manager.getPlayerCurrentSprite(), manager.getPlayer());

        for (Zombie z : manager.getActiveZombies()) {
            zombieRenderer.drawZombie(ctx, z);
        }

        for (Boss b : manager.getActiveBosses()) {
            bossRenderer.drawBoss(ctx, b);
        }

        // DIBUJAR OBJETOS DEL MAPA
        if (manager.getCurrentRoom() != null) {
            for (WorldItem item : manager.getCurrentRoom().getItems()) {
                if (!item.isCollected() && item.getItem().getSprite() != null) {
                    ctx.getGraphics().drawImage(item.getItem().getSprite(), 
                        item.getX(), item.getY(), 
                        item.getSize(), item.getSize(), null);
                }
            }
        }

        if (manager.isDebugMurosVisibles() && manager.getCurrentRoom() != null) {
            g2d.setStroke(new BasicStroke(2));

            // Dibujar muros y puerta
            g2d.setColor(Color.RED);
            for (Rectangle wall : manager.getCurrentRoom().getWalls()) {
                g2d.draw(wall);
            }
            g2d.setColor(Color.GREEN);
            for (DoorModel door : manager.getCurrentRoom().getDoors()) {
                g2d.draw(door.getArea());
            }

            // Dibujar hitboxes
            g2d.setColor(Color.BLUE);
            g2d.draw(manager.getPlayer().getHitbox(manager.getPlayer().getX(), manager.getPlayer().getY()));
            for (Zombie z : manager.getActiveZombies()) {
                g2d.draw(z.getHitbox(z.getX(), z.getY()));
            }
            for (Boss b : manager.getActiveBosses()) {
                g2d.draw(b.getHitbox(b.getX(), b.getY()));
            }

            // Hitboxes de objetos
            g2d.setColor(Color.YELLOW);
            for (WorldItem item : manager.getCurrentRoom().getItems()) {
                if (!item.isCollected()) {
                    g2d.draw(item.getHitbox());
                }
            }
        }

        ctx.restoreCamera();
    }

    private void calculateCamera() {
        if (manager == null || manager.getPlayer() == null)
            return;

        cameraX = Math.max(0,
                Math.min(manager.getPlayer().getX() - getWidth() / 2, GameSettings.MAP_WIDTH - getWidth()));
        cameraY = Math.max(0,
                Math.min(manager.getPlayer().getY() - getHeight() / 2, GameSettings.MAP_HEIGHT - getHeight()));
    }

    // ==================== EVENTOS PÚBLICOS ====================
    public void requestRender() {
        if (isShowing())
            repaint();
    }

    public void dispose() {
        if (manager != null)
            manager.deactivate();
    }

    public void reset() {
        if (manager != null) {
            manager.activate();
        }
    }

}
