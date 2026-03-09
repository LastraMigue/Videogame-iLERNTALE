package equipoilerntale.model.entity;

import java.awt.Rectangle;
import java.util.List;
import equipoilerntale.GameSettings;

/**
 * IMPLEMENTACIÓN CONCRETA DE UN ENEMIGO ZOMBIE.
 */
public class Zombie extends Entity {

    private int health;
    private boolean isAlive;
    private final int type; // 1-8
    private int frameIndex = 1;
    private long lastAnimationTime = 0;

    public static final int SIZE = GameSettings.ZOMBIE_TAMANO;
    public static final int SPEED = GameSettings.ZOMBIE_VELOCIDAD;
    public static final int MAX_HEALTH = GameSettings.ZOMBIE_SALUD;
    public static final int DAMAGE = GameSettings.ZOMBIE_DANO;

    /**
     * CONSTRUCTOR DEL ZOMBIE CON POSICIÓN ESPECIFICADA.
     * ASIGNA UN TIPO ALEATORIO (1-8) Y ESTABLECE SALUD MÁXIMA.
     */
    public Zombie(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, SIZE, "Zombie", mapWidth, mapHeight);
        this.health = MAX_HEALTH;
        this.isAlive = true;
        this.type = (int) (Math.random() * 8) + 1;
        this.direction = Direction.DOWN;
    }

    // ============ ESTADO Y ATRIBUTOS ============

    /**
     * OBTIENE LA SALUD ACTUAL DEL ZOMBIE.
     */
    public int getHealth() {
        return health;
    }

    /**
     * INDICA SI EL ZOMBIE ESTÁ VIVO.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * OBTIENE EL TIPO DE ZOMBIE (1-8).
     */
    public int getType() {
        return type;
    }

    /**
     * OBTIENE EL ÍNDICE DE ANIMACIÓN ACTUAL.
     */
    public int getFrameIndex() {
        return frameIndex;
    }

    /**
     * REDUCE LA SALUD DEL ZOMBIE POR EL MONTO ESPECIFICADO.
     * SI LA SALUD LLEGA A CERO, EL ZOMBIE MUERE.
     */
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.isAlive = false;
        }
    }

    /**
     * ACTUALIZA LA ANIMACIÓN DE LOS FRAMES DEL ZOMBIE.
     */
    public void updateAnimation() {
        long now = System.currentTimeMillis();
        if (now - lastAnimationTime > 200) { // CAMBIO CADA 200MS
            frameIndex = (frameIndex == 1) ? 2 : 1;
            lastAnimationTime = now;
        }
    }

    /**
     * ACTUALIZA EL MOVIMIENTO HACIA EL JUGADOR CON SEPARACIÓN DE VECINOS.
     * La fuerza de separación evita que todos los zombies se agrupen en el mismo
     * punto.
     *
     * @param allZombies Lista completa de zombies para calcular la separación
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls, List<? extends Object> allZombies) {
        if (!isAlive)
            return;

        double diffX = targetX - this.x;
        double diffY = targetY - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > 5) {
            // --- Dirección base: hacia el jugador ---
            double dx = (diffX / distance) * SPEED;
            double dy = (diffY / distance) * SPEED;

            // --- Fuerza de separación de vecinos ---
            double sepX = 0, sepY = 0;
            int neighborCount = 0;
            final double SEPARATION_RADIUS = 120.0;

            for (Object other : allZombies) {
                if (other == this)
                    continue;
                if (other instanceof Zombie) {
                    Zombie z = (Zombie) other;
                    if (!z.isAlive())
                        continue;
                    double distToNeighbor = Math.sqrt(Math.pow(z.x - this.x, 2) + Math.pow(z.y - this.y, 2));
                    if (distToNeighbor > 0 && distToNeighbor < SEPARATION_RADIUS) {
                        // Alejarse del vecino, con fuerza inversamente proporcional a la distancia
                        double weight = (SEPARATION_RADIUS - distToNeighbor) / SEPARATION_RADIUS;
                        sepX += ((this.x - z.x) / distToNeighbor) * weight;
                        sepY += ((this.y - z.y) / distToNeighbor) * weight;
                        neighborCount++;
                    }
                }
            }

            if (neighborCount > 0) {
                // Combinar: 60% dirección al jugador + 40% separación
                dx = dx * 0.6 + sepX * SPEED * 0.4;
                dy = dy * 0.6 + sepY * SPEED * 0.4;
            }

            // Dirección del sprite según vector resultante
            if (Math.abs(dx) > Math.abs(dy)) {
                this.direction = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                this.direction = (dy > 0) ? Direction.DOWN : Direction.UP;
            }

            moveIfNoCollision((int) dx, 0, walls);
            moveIfNoCollision(0, (int) dy, walls);
            updateAnimation();
        }
    }

    /**
     * Sobrecarga sin lista de vecinos (retrocompatibilidad).
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls) {
        updateMovement(targetX, targetY, walls, List.of());
    }
}
