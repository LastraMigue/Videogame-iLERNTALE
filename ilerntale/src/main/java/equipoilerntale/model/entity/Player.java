package equipoilerntale.model.entity;

import equipoilerntale.GameSettings;

/**
 * IMPLEMENTACIÓN CONCRETA DE LA ENTIDAD JUGADOR.
 */
public class Player extends Entity {

    // VALORES CONSTANTES PARA LA INICIALIZACIÓN DEL JUGADOR
    public static final int START_X = GameSettings.PLAYER_INICIO_X;
    public static final int START_Y = GameSettings.PLAYER_INICIO_Y;
    public static final int SIZE = GameSettings.PLAYER_TAMANO;
    public static final int SPEED = GameSettings.PLAYER_VELOCIDAD;

    /**
     * CONSTRUCTOR DEL JUGADOR CON POSICIÓN ESPECIFICADA.
     */
    public Player(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, SIZE, "Player", mapWidth, mapHeight);
        this.direction = Direction.RIGHT;
    }

    /**
     * CONSTRUCTOR DEL JUGADOR EN POSICIÓN INICIAL POR DEFECTO.
     */
    public Player(int mapWidth, int mapHeight) {
        this(START_X, START_Y, mapWidth, mapHeight);
    }
}
