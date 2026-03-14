package equipoilerntale.model.entity;

import java.awt.Rectangle;

/**
 * Implementación concreta del enemigo final (Jefe).
 * Permanece estático y tiene un tamaño mayor al de un zombie normal.
 */
public class Boss extends Entity {

    /** Salud actual del jefe. */
    private int health;
    /** Indica si el jefe sigue vivo. */
    private boolean isAlive;

    /** Ancho base del jefe. */
    public static final int WIDTH = 80;
    /** Alto base del jefe. */
    public static final int HEIGHT = 140;
    /** Salud máxima base. */
    public static final int MAX_HEALTH = 100;
    /** Daño causado por contacto. */
    public static final int DAMAGE = 30;

    /**
     * Constructor del jefe.
     * 
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param mapWidth Ancho del mapa para límites.
     * @param mapHeight Alto del mapa para límites.
     */
    public Boss(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, WIDTH, "sergio", mapWidth, mapHeight);
        this.health = MAX_HEALTH;
        this.isAlive = true;
    }

    /**
     * Obtiene la salud actual del jefe.
     * 
     * @return Salud actual.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Obtiene el ancho del jefe.
     * 
     * @return Ancho en píxeles.
     */
    public int getBossWidth() {
        return WIDTH;
    }

    /**
     * Obtiene el alto del jefe.
     * 
     * @return Alto en píxeles.
     */
    public int getBossHeight() {
        return HEIGHT;
    }

    /**
     * Indica si el jefe está vivo.
     * 
     * @return true si tiene salud mayor a 0.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Reduce la salud del jefe.
     * 
     * @param amount Cantidad de daño.
     */
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.isAlive = false;
        }
    }

    /**
     * Obtiene el rectángulo de colisión ajustado al tamaño visual del jefe.
     * 
     * @param currentX Posición X actual.
     * @param currentY Posición Y actual.
     * @return Rectángulo de hitbox.
     */
    @Override
    public Rectangle getHitbox(int currentX, int currentY) {
        int hitboxWidth = (int) (WIDTH * 0.8);
        int hitboxHeight = (int) (HEIGHT * 0.6);
        int hitboxX = currentX + (WIDTH - hitboxWidth) / 2;
        int hitboxY = currentY + (HEIGHT - hitboxHeight);
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
