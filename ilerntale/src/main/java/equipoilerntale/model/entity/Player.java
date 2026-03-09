package equipoilerntale.model.entity;

import java.awt.Rectangle;

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

    /**
     * SOBRESCRIBE EL HITBOX PARA REDUCIRLO A LOS "PIES" DEL PERSONAJE.
     * ESTO DA UNA SENSACIÓN DE PROFUNDIDAD (2.5D) CONTRA LOS MUROS.
     */
    @Override
    public Rectangle getHitbox(int currentX, int currentY) {
        // Reducimos el ancho a aproximadamente el 60%
        int hitboxWidth = (int) (SIZE * 0.6);
        // Reducimos el alto, colocando el hitbox solo en el tercio inferior del sprite
        int hitboxHeight = (int) (SIZE * 0.35);
        // Centramos horizontalmente
        int hitboxX = currentX + (SIZE - hitboxWidth) / 2;
        // Colocamos en la parte inferior
        int hitboxY = currentY + SIZE - hitboxHeight;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }
}
