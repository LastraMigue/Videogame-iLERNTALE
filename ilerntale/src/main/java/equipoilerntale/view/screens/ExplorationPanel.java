package equipoilerntale.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
 * Panel principal del modo exploración del juego.
 * Encargado de coordinar el renderizado del mapa, el jugador y las entidades activas 
 * mediante el uso de renderizadores especializados.
 */
public class ExplorationPanel extends JPanel {
    /** Logger para el registro de eventos y depuración. */
    private static final Logger LOG = Logger.getLogger(ExplorationPanel.class.getName());

    private final ExplorationManager manager;
    /** Renderizador para los elementos del mapa y fondos. */
    private final MapRenderer mapRenderer = new MapRenderer();
    /** Renderizador para el personaje jugador. */
    private final PlayerRenderer playerRenderer = new PlayerRenderer();
    /** Renderizador para los enemigos tipo Zombie. */
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    /** Renderizador para el jefe final. */
    private final BossRenderer bossRenderer = new BossRenderer();

    /** Imagen de fondo cargada para la habitación actual. */
    private Image currentBackground;

    /** Posición X de la cámara en el mundo. */
    private int cameraX;
    /** Posición Y de la cámara en el mundo. */
    private int cameraY;

    /**
     * Constructor del panel de exploración.
     * 
     * @param mainFrame Referencia al marco principal de la aplicación.
     * @param characterName Nombre del personaje seleccionado (para futura expansión).
     * @param manager Gestor de la lógica de exploración.
     */
    public ExplorationPanel(MainFrame mainFrame, String characterName, ExplorationManager manager) {
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

    /**
     * Inicializa los recursos específicos de la exploración.
     */
    private void inicializarRecursosExploracion() {
    }

    /**
     * Configura el ciclo de vida del componente, gestionando su activación y limpieza.
     */
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        dibujarExploracion(g);
    }

    /**
     * Orquestador del dibujo de todos los elementos del modo exploración.
     * 
     * @param g Contexto gráfico.
     */
    private void dibujarExploracion(Graphics g) {
        if (manager == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        calculateCamera();
        RenderContext ctx = new RenderContext(g2d, getWidth(), getHeight(), cameraX, cameraY);
        ctx.translateCamera();

        if (manager.getCurrentRoom() != null) {
            if (currentBackground == null || !currentBackground
                    .equals(AssetService.getInstance().loadBackground(manager.getCurrentRoom().getBackgroundPath()))) {
                currentBackground = AssetService.getInstance()
                        .loadBackground(manager.getCurrentRoom().getBackgroundPath());
            }
            mapRenderer.drawBackground(ctx, currentBackground);
        }

        playerRenderer.drawPlayer(ctx, manager.getPlayerCurrentSprite(), manager.getPlayer());

        synchronized (manager) {
            for (Zombie z : manager.getActiveZombies()) {
                zombieRenderer.drawZombie(ctx, z);
            }

            for (Boss b : manager.getActiveBosses()) {
                bossRenderer.drawBoss(ctx, b);
            }

            if (manager.getCurrentRoom() != null) {
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(Color.WHITE);

                for (WorldItem item : manager.getCurrentRoom().getItems()) {
                    if (!item.isCollected()) {
                        if (item.getItem().getSprite() != null) {
                            ctx.getGraphics().drawImage(item.getItem().getSprite(),
                                    item.getX(), item.getY(),
                                    item.getSize(), item.getSize(), null);
                        }

                    }
                }

                for (DoorModel door : manager.getCurrentRoom().getDoors()) {
                    Rectangle area = door.getArea();
                    double distX = Math.abs(manager.getPlayer().getX() - area.getCenterX());
                    double distY = Math.abs(manager.getPlayer().getY() - area.getCenterY());

                    if (distX < 120 && distY < 120) {
                        g2d.drawString("[E]", (int) area.getCenterX() - 10, (int) area.getY() + 20);
                    }
                }
            }
        }

        ctx.restoreCamera();
    }

    /**
     * Calcula la posición de la cámara para que siga al jugador, 
     * manteniéndose dentro de los límites del mapa.
     */
    private void calculateCamera() {
        if (manager == null || manager.getPlayer() == null)
            return;

        cameraX = Math.max(0,
                Math.min(manager.getPlayer().getX() - getWidth() / 2, GameSettings.MAP_WIDTH - getWidth()));
        cameraY = Math.max(0,
                Math.min(manager.getPlayer().getY() - getHeight() / 2, GameSettings.MAP_HEIGHT - getHeight()));
    }

    // ==================== EVENTOS PÚBLICOS ====================
    /** Solicita un repintado del panel si este es visible. */
    public void requestRender() {
        if (isShowing())
            repaint();
    }

    /** Libera los recursos y desactiva el gestor de exploración. */
    public void dispose() {
        if (manager != null)
            manager.deactivate();
    }

    /** Reinicia el estado del panel activando el gestor. */
    public void reset() {
        if (manager != null) {
            manager.activate();
        }
    }

}
