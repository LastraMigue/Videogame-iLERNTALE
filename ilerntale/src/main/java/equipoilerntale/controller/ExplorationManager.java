package equipoilerntale.controller;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.Direction;
import equipoilerntale.model.entity.Player;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.model.entity.Boss;
import equipoilerntale.model.map.AbstractRoom;
import equipoilerntale.model.map.DoorModel;
import equipoilerntale.model.map.Room1;
import equipoilerntale.model.map.Room2;
import equipoilerntale.model.map.Room3;
import equipoilerntale.model.map.RoomPasillo;
import equipoilerntale.model.entity.WorldItem;
import equipoilerntale.view.ui.Inventario;
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

    private AbstractRoom currentRoom;
    private final Map<String, AbstractRoom> roomCache = new HashMap<>();
    private String lastRoomName = "";

    private int animationFrameIndex = 0;
    private long lastAnimationTime = 0;

    // Flag que protege la lógica de juego: solo true cuando EXPLORACION es la
    // pantalla visible
    private boolean active = false;


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
        
        // INICIALIZAR CACHÉ CON LA SALA INICIAL
        AbstractRoom startRoom = new RoomPasillo();
        roomCache.put(startRoom.getName(), startRoom);

        // CARGAMOS LA SALA INICIAL POR DEFECTO AL CREAR EL MANAGER
        loadRoom(startRoom, Player.START_X, Player.START_Y);
    }

    /**
     * CARGA UNA NUEVA HABITACIÓN EN EL GESTOR Y EL MOTOR DEL JUEGO.
     * DETIENE ENEMIGOS PREVIOS Y ESTABLECE LOS NUEVOS DATOS DEL MAPA.
     */
    public void loadRoom(AbstractRoom room, int playerStartX, int playerStartY) {
        LOG.info("CARGANDO HABITACIÓN: " + room.getName());
        enemySystem.clear(); // Limpiar zombies
        
        this.currentRoom = room;
        this.lastRoomName = room.getName(); // Registrar sala cargada

        // POSICIONAMOS AL JUGADOR EN LA ENTRADA
        this.player.setX(playerStartX);
        this.player.setY(playerStartY);

        // Si la pantalla ya estaba activa, generar los nuevos zombies.
        // Si no (estamos en intro o pausa), se generarán al hacer activate().
        if (active) {
            spawnZombies();
        }
    }

    /**
     * Activa la fase de exploración: genera zombies y permite las actualizaciones.
     * Debe llamarse cuando la pantalla EXPLORACION se vuelve visible.
     */
    public void activate() {
        if (!active) {
            active = true;
            // Siempre limpiar teclas al volver a EXPLORACION, evita teclas atascadas
            if (inputHandler != null) {
                inputHandler.reset();
            }
            // Solo regeneramos si hemos cambiado de sala o si no hay enemigos
            String roomName = (currentRoom != null) ? currentRoom.getName() : null;
            if (roomName != null && !roomName.equals(lastRoomName)) {
                enemySystem.clear();
                spawnZombies();
                lastRoomName = roomName;
            } else if (enemySystem.getZombies().isEmpty() && enemySystem.getBosses().isEmpty()) {
                // Si la sala es la misma pero está vacía (ej: reload), spawnear
                spawnZombies();
                lastRoomName = (currentRoom != null) ? currentRoom.getName() : "";
            } else {
                // Si la sala es la misma y hay enemigos (volvemos de un combate)
                // Dispersamos a los enemigos cercanos para dar un respiro al jugador
                enemySystem.disperseEnemiesFrom(player.getX(), player.getY(), 450);
            }
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
        if (currentRoom != null && currentRoom.getZombiesToSpawn() > 0) {
            enemySystem.spawnZombies(
                    currentRoom.getZombiesToSpawn(),
                    currentRoom.getZombieSpawnArea(),
                    player.getX(),
                    player.getY(),
                    currentRoom.getWalls());
        }
        if (currentRoom != null && currentRoom.getBossSpawnArea() != null) {
            enemySystem.spawnBoss(currentRoom.getBossSpawnArea());
        }
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

            // COMPROBAR COLISIONES CON LOS MUROS DE LA SALA ACTUAL
            List<Rectangle> roomWalls = currentRoom.getWalls();
            boolean movedX = player.moveIfNoCollision(dx, 0, roomWalls);
            boolean movedY = player.moveIfNoCollision(0, dy, roomWalls);
            
            if (!movedX && dx != 0) LOG.info("Colisión horizontal en x=" + player.getX() + ", y=" + player.getY());
            if (!movedY && dy != 0) LOG.info("Colisión vertical en x=" + player.getX() + ", y=" + player.getY());
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

        // COMPROBAR COLISIÓN CON PUERTAS
        if (inputHandler.ePressed && currentRoom != null) {
            for (DoorModel door : currentRoom.getDoors()) {
                if (player.intersects(door.getArea())) {
                    String targetName = door.getTargetRoomName();

                    // VALIDACIÓN DE LLAVE PARA EL AULA 124
                    if (targetName.equals("Aula 124")) {
                        equipoilerntale.model.entity.ItemModel llave = null;
                        for (equipoilerntale.model.entity.ItemModel im : Inventario.getInstance().getItems()) {
                            if ("Llave".equals(im.getNombre())) {
                                llave = im;
                                break;
                            }
                        }

                        // Lógica de acceso:
                        // 1. Si ya se usó anteriormente, pasa directo.
                        // 2. Si no se ha usado pero se tiene (cantidad > 0), se marca como usada y pasa.
                        // 3. Si no se tiene ni se ha usado, se bloquea.
                        if (llave != null && llave.isUsado()) {
                            // YA USADA: OK
                        } else if (llave != null && llave.getCantidad() > 0) {
                            // PRIMER USO: MARCAR COMO USADA
                            llave.setUsado(true);
                            LOG.info("LLAVE RECOGIDA Y USADA PARA ABRIR AULA 124");
                        } else {
                            // NO TIENE LA LLAVE (o cantidad 0)
                            if (mainFrame instanceof equipoilerntale.view.MainFrame) {
                                ((equipoilerntale.view.MainFrame) mainFrame).showTimedDialogue("Está cerrada. Necesitas una llave.", 2000);
                            }
                            inputHandler.ePressed = false;
                            return; // CANCELAR TRANSICIÓN
                        }
                    }

                    AbstractRoom targetRoom = roomCache.get(targetName);

                    // Si la sala no está en caché, la creamos
                    if (targetRoom == null) {
                        if (targetName.equals("Aula 124")) {
                            targetRoom = new Room1();
                        } else if (targetName.equals("Pasillo Principal")) {
                            targetRoom = new RoomPasillo();
                        } else if (targetName.equals("Aula 123")) {
                            targetRoom = new Room2();
                        } else if (targetName.equals("Aula 125")) {
                            targetRoom = new Room3();
                        }
                        
                        if (targetRoom != null) {
                            roomCache.put(targetName, targetRoom);
                        }
                    }

                    if (targetRoom != null) {
                        loadRoom(targetRoom, door.getTargetPlayerX(), door.getTargetPlayerY());
                    }
                    
                    inputHandler.ePressed = false; // Evitar salto doble por mantener pulsado
                    break;
                }
            }
        }

        // COMPROBAR COMBATE CON ENEMIGOS
        Object enemy = enemySystem.getEnemyAt(player.getHitbox(player.getX(), player.getY()));
        if (enemy != null) {
            if (mainFrame instanceof equipoilerntale.view.MainFrame) {
                ((equipoilerntale.view.MainFrame) mainFrame).entrarCombate(enemy);
            }
        }

        // COMPROBAR COLISIÓN CON OBJETOS DEL MAPA
        if (currentRoom != null) {
            for (WorldItem item : currentRoom.getItems()) {
                if (!item.isCollected() && player.getHitbox(player.getX(), player.getY()).intersects(item.getHitbox())) {
                    item.setCollected(true);
                    Inventario.getInstance().agregarItem(item.getItem());
                    LOG.info("OBJETO RECOGIDO: " + item.getItem().getNombre());
                }
            }
        }
    }

    /**
     * ELIMINA UN ENEMIGO DEL SISTEMA TRAS EL COMBATE.
     */
    public void removeEnemy(Object enemy) {
        if (enemy instanceof Zombie) {
            enemySystem.getZombies().remove(enemy);
        } else if (enemy instanceof Boss) {
            enemySystem.getBosses().remove(enemy);
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
     * OBTIENE LA LISTA DE JEFES ACTIVOS ACTUALMENTE.
     */
    public List<Boss> getActiveBosses() {
        return enemySystem.getBosses();
    }

    /**
     * OBTIENE EL JUGADOR ACTUAL.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * OBTIENE LA HABITACIÓN O SALA ACTUAL.
     */
    public AbstractRoom getCurrentRoom() {
        return currentRoom;
    }

    /**
     * OBTIENE EL GESTOR DE ENTRADA DEL TECLADO.
     */
    public InputHandler getInputHandler() {
        return inputHandler;
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
