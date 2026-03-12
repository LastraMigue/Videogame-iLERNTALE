package equipoilerntale.model.entity;

import java.awt.Rectangle;

/**
 * IMPLEMENTACIÓN CONCRETA DEL ENEMIGO FINAL (JEFE).
 * PERMANECE ESTÁTICO Y TIENE UN TAMAÑO MAYOR AL DE UN ZOMBIE NORMAL.
 */
public class Boss extends Entity {

    private int health;
    private boolean isAlive;

    // Tamaño base del jefe, el hitbox luego se ajustará
    public static final int SIZE = 120;
    public static final int MAX_HEALTH = 100;
    public static final int DAMAGE = 30;

    /**
     * CONSTRUCTOR DEL BOOS.
     */
    public Boss(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, SIZE, "sergio", mapWidth, mapHeight);
        this.health = MAX_HEALTH;
        this.isAlive = true;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.isAlive = false;
        }
    }

    /**
     * CAJA DE COLISIONES.
     * SE HACE MÁS GRANDE QUE LA DE UN ZOMBIE O LA DE UN JUGADOR PARA DIFICULTAR
     * PASAR A SU LADO.
     */
    @Override
    public Rectangle getHitbox(int currentX, int currentY) {
        int hitboxWidth = (int) (SIZE * 0.8);
        int hitboxHeight = (int) (SIZE * 0.6);
        int hitboxX = currentX + (SIZE - hitboxWidth) / 2;
        int hitboxY = currentY + (SIZE - hitboxHeight);
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }
}
