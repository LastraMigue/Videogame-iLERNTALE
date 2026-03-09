package equipoilerntale.controller;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.Direction;
import equipoilerntale.model.entity.Player;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.service.AssetService;

/**
 * ORQUESTA LA FASE DE EXPLORACIÓN DEL JUEGO.
 */
public class ExplorationManager {
    private static final Logger LOG = Logger.getLogger(ExplorationManager.class.getName());

    private static final long ANIMATION_FRAME_DELAY = 100;

    private final Player player;
    private final InputHandler inputHandler;
    private final EnemySystem enemySystem;
    private final String characterName;
    private final Object mainFrame;

    private final List<Rectangle> walls = new ArrayList<>();
    private Rectangle doorArea;

    private int animationFrameIndex = 0;
    private long lastAnimationTime = 0;

    // Flag que protege la lógica de juego: solo true cuando EXPLORACION es la
    // pantalla visible
    private boolean active = false;

    // FLAG PARA VISUALIZAR LOS MUROS EN MODO DEBUG
    private boolean debugMurosVisibles = true;

    /**
     * CONSTRUCTOR DEL GESTOR DE EXPLORACIÓN.
     * INICIALIZA EL JUGADOR, EL SISTEMA DE ENEMIGOS Y LOS MUROS DEL MUNDO.
     */
    public ExplorationManager(Object mainFrame, String characterName) {
        this.mainFrame = mainFrame;
        this.characterName = characterName;
        this.inputHandler = new InputHandler();

        AssetService.getInstance().loadCharacterSprites(characterName, Player.SIZE);
        this.player = new Player(GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
        this.enemySystem = new EnemySystem();

        initializeWorld();
    }

    /**
     * INICIALIZA EL MUNDO DE EXPLORACIÓN.
     * CREA LOS MUROS PERIMETRALES Y EL ÁREA DE LA PUERTA.
     */
    private void initializeWorld() {
        walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 230));
        walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10));
        walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));
        doorArea = new Rectangle(GameSettings.MAP_WIDTH - 250, 230, 100, 120);
        // Los zombies se generan en activate(), NO aquí, para evitar que corran antes
        // de la pantalla
    }

    /**
     * Activa la fase de exploración: genera zombies y permite las actualizaciones.
     * Debe llamarse cuando la pantalla EXPLORACION se vuelve visible.
     */
    public void activate() {
        if (!active) {
            active = true;
            enemySystem.clear(); // Limpiar zombies anteriores
            spawnZombies();
            LOG.info("ExplorationManager activado.");
        }
    }

    /**
     * Pausa la fase de exploración sin destruir los assets.
     */
    public void deactivate() {
        active = false;
        LOG.info("ExplorationManager desactivado.");
    }

    public void spawnZombies() {
        Rectangle spawnArea = new Rectangle(500, 150, GameSettings.MAP_WIDTH - 500, GameSettings.MAP_HEIGHT - 300);
        enemySystem.spawnZombies(GameSettings.ZOMBIE_CANTIDAD_INICIAL, spawnArea, player.getX(), player.getY(), walls);
    }

    public void update() {
        if (!active)
            return; // Protección: no correr lógica si la pantalla no está activa
        handlePlayerMovement();
        enemySystem.update(player.getX(), player.getY());
        updateAnimationFrame();
        checkInteractions();
    }

    private void handlePlayerMovement() {
        int dx = 0;
        int dy = 0;
        if (inputHandler.leftPressed)
            dx -= Player.SPEED;
        else if (inputHandler.rightPressed)
            dx += Player.SPEED;
        if (inputHandler.upPressed)
            dy -= Player.SPEED;
        else if (inputHandler.downPressed)
            dy += Player.SPEED;

        boolean moving = (dx != 0 || dy != 0);
        player.setMoving(moving);

        if (moving) {
            if (dx != 0)
                player.setDirection((dx < 0) ? Direction.LEFT : Direction.RIGHT);
            else if (dy != 0)
                player.setDirection((dy < 0) ? Direction.UP : Direction.DOWN);

            player.moveIfNoCollision(dx, 0, walls);
            player.moveIfNoCollision(0, dy, walls);
        }
    }

    private void updateAnimationFrame() {
        if (!player.isMoving()) {
            animationFrameIndex = 0;
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastAnimationTime >= ANIMATION_FRAME_DELAY) {
            List<Image> frames = AssetService.getInstance().getCharacterFrames(characterName, player.getDirection());
            if (frames != null && !frames.isEmpty()) {
                animationFrameIndex = (animationFrameIndex + 1) % frames.size();
            }
            lastAnimationTime = now;
        }
    }

    private void checkInteractions() {
        // TOGGLE DE VISIBILIDAD DE MUROS CON LA TECLA M
        if (inputHandler.mPressed) {
            debugMurosVisibles = !debugMurosVisibles;
            inputHandler.mPressed = false; // PREVENIR MULTIPLES CAMBIOS
        }

        if (inputHandler.ePressed && player.intersects(doorArea)) {
            triggerScreenChange("COMBATE");
        }
        if (enemySystem.collidesWithPlayer(player.getHitbox(player.getX(), player.getY()))) {
            triggerScreenChange("COMBATE");
        }
    }

    private void triggerScreenChange(String screenName) {
        if (mainFrame instanceof equipoilerntale.view.MainFrame) {
            ((equipoilerntale.view.MainFrame) mainFrame).cambiarPantalla(screenName);
        }
    }

    public Image getPlayerCurrentSprite() {
        return AssetService.getInstance().getCharacterSprite(characterName, player.getDirection(), animationFrameIndex);
    }

    /**
     * OBTIENE LA LISTA DE ZOMBIES ACTIVOS ACTUALMENTE.
     */
    public List<Zombie> getActiveZombies() {
        return enemySystem.getZombies();
    }

    /**
     * OBTIENE EL JUGADOR ACTUAL.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * OBTIENE LA LISTA DE MUROS DEL MAPA.
     */
    public List<Rectangle> getWalls() {
        return walls;
    }

    /**
     * OBTIENE EL ÁREA DE LA PUERTA DE COMBATE.
     */
    public Rectangle getDoorArea() {
        return doorArea;
    }

    /**
     * OBTIENE EL GESTOR DE ENTRADA DEL TECLADO.
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * ESTABLECE SI LOS MUROS SON VISIBLES EN MODO DEBUG.
     */
    public void setDebugMurosVisibles(boolean visible) {
        this.debugMurosVisibles = visible;
    }

    /**
     * INDICA SI LOS MUROS SON VISIBLES ACTUALMENTE.
     */
    public boolean isDebugMurosVisibles() {
        return debugMurosVisibles;
    }

    /**
     * LIMPIA LOS RECURSOS DEL GESTOR DE EXPLORACIÓN.
     * DESACTIVA LA EXPLORACIÓN Y ELIMINA LOS ZOMBIES.
     */
    public void cleanup() {
        // IMPORTANTE: No llamar AssetService.dispose() aquí.
        // Destruir el singleton borraría todos los sprites para futuros usos.
        deactivate();
        enemySystem.clear();
        LOG.info("ExplorationManager limpiado (assets conservados).");
    }
}
