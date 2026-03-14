package equipoilerntale.model.entity;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;

/**
 * Implementación concreta de la entidad del Jugador.
 * Gestiona el hitbox específico de los "pies" para permitir el paso tras muros.
 */
public class Player extends Entity {

    /** Posición X inicial definida en la configuración global. */
    public static final int START_X = GameSettings.PLAYER_INICIO_X;
    /** Posición Y inicial definida en la configuración global. */
    public static final int START_Y = GameSettings.PLAYER_INICIO_Y;
    /** Tamaño base del jugador. */
    public static final int SIZE = GameSettings.PLAYER_TAMANO;
    /** Velocidad de movimiento base. */
    public static final int SPEED = GameSettings.PLAYER_VELOCIDAD;

    /**
     * Constructor del jugador con posición especificada.
     * 
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param mapWidth Límite horizontal del mapa.
     * @param mapHeight Límite vertical del mapa.
     */
    public Player(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, SIZE, "Player", mapWidth, mapHeight);
        this.direction = Direction.RIGHT;
    }

    /**
     * Constructor del jugador en posición inicial por defecto.
     * 
     * @param mapWidth Límite horizontal del mapa.
     * @param mapHeight Límite vertical del mapa.
     */
    public Player(int mapWidth, int mapHeight) {
        this(START_X, START_Y, mapWidth, mapHeight);
    }

    /**
     * Obtiene el hitbox ajustado a la base del personaje.
     * Reduce el área de colisión para permitir que la parte superior del sprite
     * se solape con elementos del mapa, simulando profundidad.
     * 
     * @param currentX Coordenada X a evaluar.
     * @param currentY Coordenada Y a evaluar.
     * @return Rectángulo de hitbox ajustado.
     */
    @Override
    public Rectangle getHitbox(int currentX, int currentY) {
        int hitboxWidth = (int) (SIZE * 0.6);
        int hitboxHeight = (int) (SIZE * 0.35);
        int hitboxX = currentX + (SIZE - hitboxWidth) / 2;
        int hitboxY = currentY + SIZE - hitboxHeight;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }
}
