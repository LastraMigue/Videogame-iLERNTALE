package equipoilerntale.model.entity;

import java.awt.Rectangle;
import java.util.List;
import equipoilerntale.GameSettings;

/**
 * IMPLEMENTACIÓN CONCRETA DE UN ENEMIGO ZOMBIE.
 */
public class Zombie extends Entity {

    private int health;
    private int maxHealth;
    private boolean isAlive;
    private final int type; // 1-8
    private int frameIndex = 1;
    private long lastAnimationTime = 0;

    private double customSpeed;
    private double trackingPrecision;
    private int detectionRadius;
    private boolean hasDetectedPlayer = false;

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
        // Salud aleatoria entre 25 y 50 para cada Zombie
        this.health = 25 + (int) (Math.random() * 26);
        this.maxHealth = this.health;
        this.isAlive = true;
        this.type = (int) (Math.random() * 8) + 1;
        this.direction = Direction.DOWN;

        // ALEATORIEDAD DE ATRIBUTOS
        // Velocidad: SPEED +/- 1.5
        this.customSpeed = SPEED + (Math.random() * 3.0 - 1.5);
        // Seguimiento: 0.7 a 1.0 (cuánto de su vector va al jugador vs inercia/error)
        this.trackingPrecision = 0.7 + (Math.random() * 0.3);
        // Radio de detección: GameSettings.ZOMBIE_DETECTION_RADIUS +/- 150px
        this.detectionRadius = GameSettings.ZOMBIE_DETECTION_RADIUS + (int) (Math.random() * 300 - 150);
    }

    // ============ ESTADO Y ATRIBUTOS ============

    /**
     * OBTIENE LA SALUD ACTUAL DEL ZOMBIE.
     */
    public int getHealth() {
        return health;
    }

    /**
     * OBTIENE LA SALUD MÁXIMA ESPECÍFICA DE ESTE ZOMBIE.
     */
    public int getMaxHealth() {
        return maxHealth;
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
     * ESTABLECE SI EL ZOMBIE HA DETECTADO AL JUGADOR.
     */
    public void setDetectedPlayer(boolean detected) {
        this.hasDetectedPlayer = detected;
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
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls, List<? extends Object> allZombies) {
        if (!isAlive)
            return;

        double diffX = targetX - this.x;
        double diffY = targetY - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // Detección del jugador
        if (!hasDetectedPlayer && distance < detectionRadius) {
            hasDetectedPlayer = true;
        }

        if (hasDetectedPlayer && distance > 5) {
            double[] moveVector = calculateMoveVector(diffX, diffY, distance, allZombies);
            double dx = moveVector[0];
            double dy = moveVector[1];

            updateSpriteDirection(dx, dy);
            moveIfNoCollision((int) dx, 0, walls);
            moveIfNoCollision(0, (int) dy, walls);
            updateAnimation();
        }
    }

    private double[] calculateMoveVector(double diffX, double diffY, double distance,
            List<? extends Object> allZombies) {
        // Vector base con wobble (zig-zag)

        double targetDx = (diffX / distance) * trackingPrecision + (Math.random() * 0.4 - 0.2);
        double targetDy = (diffY / distance) * trackingPrecision + (Math.random() * 0.4 - 0.2);

        // Re-normalizar y aplicar velocidad
        double finalDist = Math.sqrt(targetDx * targetDx + targetDy * targetDy);
        double dx = (targetDx / finalDist) * customSpeed;
        double dy = (targetDy / finalDist) * customSpeed;

        // Fuerza de separación
        double[] separation = calculateSeparation(allZombies);
        if (separation[2] > 0) { // neighborCount
            dx = dx * 0.7 + separation[0] * customSpeed * 0.3;
            dy = dy * 0.7 + separation[1] * customSpeed * 0.3;
        }

        return new double[] { dx, dy };
    }

    private double[] calculateSeparation(List<? extends Object> allZombies) {
        double sepX = 0, sepY = 0;
        int neighborCount = 0;
        final double SEPARATION_RADIUS = 80.0;

        for (Object other : allZombies) {
            if (other == this || !(other instanceof Zombie))
                continue;
            Zombie z = (Zombie) other;
            if (!z.isAlive())
                continue;

            double distToNeighbor = Math.sqrt(Math.pow(z.x - this.x, 2) + Math.pow(z.y - this.y, 2));
            if (distToNeighbor < SEPARATION_RADIUS) {
                if (distToNeighbor == 0) {
                    sepX += (Math.random() * 2 - 1);
                    sepY += (Math.random() * 2 - 1);
                } else {
                    double weight = (SEPARATION_RADIUS - distToNeighbor) / SEPARATION_RADIUS;
                    sepX += ((this.x - z.x) / distToNeighbor) * weight;
                    sepY += ((this.y - z.y) / distToNeighbor) * weight;
                }
                neighborCount++;
            }
        }
        return new double[] { sepX, sepY, neighborCount };
    }

    private void updateSpriteDirection(double dx, double dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            this.direction = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            this.direction = (dy > 0) ? Direction.DOWN : Direction.UP;
        }
    }

    /**
     * Sobrecarga sin lista de vecinos (retrocompatibilidad).
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls) {
        updateMovement(targetX, targetY, walls, List.of());
    }

    /**
     * SOBRESCRIBE EL HITBOX PARA REDUCIRLO A LOS "PIES" DEL ZOMBIE.
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
