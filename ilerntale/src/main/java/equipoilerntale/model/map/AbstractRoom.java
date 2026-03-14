package equipoilerntale.model.map;

import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import equipoilerntale.model.entity.WorldItem;

/**
 * Clase base abstracta para todas las habitaciones del juego.
 * Define la estructura física (muros), lógica (puertas, zombies) y visual (fondo).
 */
public abstract class AbstractRoom {

    /** Nombre único de la habitación. */
    protected String name;
    /** Ruta al recurso de imagen del fondo. */
    protected String backgroundPath;
    /** Lista de rectángulos que representan colisiones estáticas. */
    protected List<Rectangle> walls;
    /** Lista de áreas de transición a otras salas. */
    protected List<DoorModel> doors;
    /** Cantidad de enemigos a generar al entrar. */
    protected int zombiesToSpawn;

    /** Área máxima delimitada para la aparición de zombies. */
    protected Rectangle zombieSpawnArea;
    /** Área específica para la aparición del jefe (si aplica). */
    protected Rectangle bossSpawnArea;
    /** Lista de objetos físicos presentes en el suelo de la sala. */
    protected List<WorldItem> items;

    /**
     * Constructor base de la habitación.
     * Inicializa las colecciones de elementos y llama a la configuración específica.
     */
    public AbstractRoom() {
        this.walls = new CopyOnWriteArrayList<>();
        this.doors = new CopyOnWriteArrayList<>();
        this.items = new CopyOnWriteArrayList<>();
        initializeRoom();
    }

    /**
     * Añade un muro de colisión a la sala.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param w Ancho.
     * @param h Alto.
     */
    protected void addWall(int x, int y, int w, int h) {
        walls.add(new Rectangle(x, y, w, h));
    }

    /**
     * Añade una puerta de transición.
     * 
     * @param x Posición X del área sensible.
     * @param y Posición Y del área sensible.
     * @param w Ancho del área.
     * @param h Alto del área.
     * @param targetRoom Nombre de la sala destino.
     * @param targetX Posición X donde aparecerá el jugador.
     * @param targetY Posición Y donde aparecerá el jugador.
     */
    protected void addDoor(int x, int y, int w, int h, String targetRoom, int targetX, int targetY) {
        doors.add(new DoorModel(x, y, w, h, targetRoom, targetX, targetY));
    }

    /**
     * Registra un objeto recolectable en la sala.
     * 
     * @param item Objeto a añadir.
     */
    protected void addWorldItem(WorldItem item) {
        items.add(item);
    }

    /**
     * Método abstracto para configurar los parámetros específicos de la sala.
     * Debe definir el fondo, muros, puertas y lógica de spawn en las subclases.
     */
    protected abstract void initializeRoom();

    /**
     * Obtiene el nombre de la habitación.
     * 
     * @return Nombre identificador.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la ruta del fondo.
     * 
     * @return Ruta del recurso.
     */
    public String getBackgroundPath() {
        return backgroundPath;
    }

    /**
     * Obtiene la lista de colisiones de la sala.
     * 
     * @return Lista de rectángulos de muros.
     */
    public List<Rectangle> getWalls() {
        return walls;
    }

    /**
     * Obtiene las puertas de salida de la sala.
     * 
     * @return Lista de {@link DoorModel}.
     */
    public List<DoorModel> getDoors() {
        return doors;
    }

    /**
     * Obtiene la cantidad de zombies configurada.
     * 
     * @return Número de zombies.
     */
    public int getZombiesToSpawn() {
        return zombiesToSpawn;
    }

    /**
     * Obtiene el área permitida para generar zombies.
     * 
     * @return Rectángulo de spawn.
     */
    public Rectangle getZombieSpawnArea() {
        return zombieSpawnArea;
    }

    /**
     * Obtiene el área de spawn del jefe.
     * 
     * @return Rectángulo de spawn del boss.
     */
    public Rectangle getBossSpawnArea() {
        return bossSpawnArea;
    }

    /**
     * Obtiene los objetos presentes en la sala.
     * 
     * @return Lista de {@link WorldItem}.
     */
    public List<WorldItem> getItems() {
        return items;
    }
}
