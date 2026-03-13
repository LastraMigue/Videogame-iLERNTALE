package equipoilerntale.model.map;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import equipoilerntale.model.entity.WorldItem;

/**
 * CLASE BASE ABSTRACTA PARA TODAS LAS HABITACIONES DEL JUEGO.
 * CONTIENE LOS DATOS ESTRUCTURALES Y RECURSOS VISUALES COMUNES PARA EL
 * CONTROLADOR Y LA VISTA.
 */
public abstract class AbstractRoom {

    protected String name;
    protected String backgroundPath;
    protected List<Rectangle> walls;
    protected List<DoorModel> doors;
    protected int zombiesToSpawn;

    // ÁREA MÁXIMA EN LA QUE SE PUEDEN GENERAR ZOMBIES EN ESTA SALA
    protected Rectangle zombieSpawnArea;
    // ÁREA DONDE APARECERÁ EL BOSS EN CASO DE HABER UNO
    protected Rectangle bossSpawnArea;
    protected List<WorldItem> items;

    /**
     * CONSTRUCTOR BASE.
     * INSTANCIA LA LISTA DE MUROS Y LLAMA A LA INICIALIZACIÓN PROPIA DE CADA SALA.
     */
    public AbstractRoom() {
        this.walls = new CopyOnWriteArrayList<>();
        this.doors = new CopyOnWriteArrayList<>();
        this.items = new CopyOnWriteArrayList<>();
        initializeRoom();
    }

    /**
     * MÉTODO ABSTRACTO QUE CADA HABITACIÓN DEBE IMPLEMENTAR.
     * AQUÍ SE DEBEN DEFINIR LOS VALORES DE NOMBRE, FONDO, MUROS, PUERTAS Y ZOMBIES.
     */
    protected abstract void initializeRoom();

    // ================= GETTERS ================= //

    /**
     * OBTIENE EL NOMBRE DE LA HABITACIÓN.
     */
    public String getName() {
        return name;
    }

    /**
     * OBTIENE LA RUTA RELATIVA DEL FONDO DE LA HABITACIÓN.
     */
    public String getBackgroundPath() {
        return backgroundPath;
    }

    /**
     * OBTIENE LA LISTA DE RECTÁNGULOS QUE FORMAN LOS MUROS O LÍMITES DE ESTA SALA.
     */
    public List<Rectangle> getWalls() {
        return walls;
    }

    /**
     * OBTIENE LA LISTA DE PUERTAS DE ESTA SALA.
     */
    public List<DoorModel> getDoors() {
        return doors;
    }

    /**
     * OBTIENE LA CANTIDAD DE ZOMBIES A GENERAR INICIALMENTE EN ESTA SALA.
     */
    public int getZombiesToSpawn() {
        return zombiesToSpawn;
    }

    /**
     * OBTIENE EL ÁREA DELIMITADA DONDE ESTÁ PERMITIDO GENERAR ZOMBIES.
     */
    public Rectangle getZombieSpawnArea() {
        return zombieSpawnArea;
    }

    /**
     * OBTIENE EL ÁREA DE GENERACIÓN DEL BOSS SI LO HAY.
     */
    public Rectangle getBossSpawnArea() {
        return bossSpawnArea;
    }

    public List<WorldItem> getItems() {
        return items;
    }
}
