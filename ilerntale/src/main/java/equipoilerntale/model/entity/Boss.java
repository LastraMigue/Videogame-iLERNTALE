package equipoilerntale.model.entity;

import java.awt.Rectangle;

/**
 * IMPLEMENTACIÓN CONCRETA DEL ENEMIGO FINAL (JEFE).
 * PERMANECE ESTÁTICO Y TIENE UN TAMAÑO MAYOR AL DE UN ZOMBIE NORMAL.
 */
public class Boss extends Entity {

    private int health;
    private boolean isAlive;

    // Tamaño base del jefe, modificado para respetar el aspect ratio original
    // (412x720) y ajustado al personaje (80x80)
    public static final int WIDTH = 80;
    public static final int HEIGHT = 140;
    public static final int MAX_HEALTH = 100;
    public static final int DAMAGE = 30;

    /**
     * CONSTRUCTOR DEL BOOS.
     */
    public Boss(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, WIDTH, "sergio", mapWidth, mapHeight);
        this.health = MAX_HEALTH;
        this.isAlive = true;
    }

    public int getHealth() {
        return health;
    }

    public int getBossWidth() {
        return WIDTH;
    }

    public int getBossHeight() {
        return HEIGHT;
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
     * CAJA DE COLISIONES AJUSTADA AL NUEVO TAMAÑO ACHATADO.
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
