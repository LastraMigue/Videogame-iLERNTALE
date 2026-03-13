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
    private double wobbleOffset;
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
        // Oscilación aleatoria para que no todos caminen en línea recta perfecta
        this.wobbleOffset = Math.random() * Math.PI * 2;
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

        // Si ya detectó al jugador o el jugador entra en su radio, empieza el
        // seguimiento
        if (!hasDetectedPlayer && distance < detectionRadius) {
            hasDetectedPlayer = true;
        }

        if (hasDetectedPlayer && distance > 5) {
            // --- Dirección base: hacia el jugador con "Wobble" (zig-zag aleatorio) ---
            long now = System.currentTimeMillis();
            double wobble = Math.sin((now / 500.0) + wobbleOffset) * 0.3;

            // Vector al jugador
            double baseDx = (diffX / distance);
            double baseDy = (diffY / distance);

            // Aplicar imprecisión (mezclar con wobble)
            double targetDx = baseDx * trackingPrecision + (Math.random() * 0.4 - 0.2);
            double targetDy = baseDy * trackingPrecision + (Math.random() * 0.4 - 0.2);

            // Re-normalizar y aplicar velocidad
            double finalDist = Math.sqrt(targetDx * targetDx + targetDy * targetDy);
            double dx = (targetDx / finalDist) * customSpeed;
            double dy = (targetDy / finalDist) * customSpeed;

            // --- Fuerza de separación de vecinos ---
            double sepX = 0, sepY = 0;
            int neighborCount = 0;
            final double SEPARATION_RADIUS = 80.0; // Reducido para grupos más densos

            for (Object other : allZombies) {
                if (other == this)
                    continue;
                if (other instanceof Zombie) {
                    Zombie z = (Zombie) other;
                    if (!z.isAlive())
                        continue;
                    double distToNeighbor = Math.sqrt(Math.pow(z.x - this.x, 2) + Math.pow(z.y - this.y, 2));
                    if (distToNeighbor < SEPARATION_RADIUS) {
                        if (distToNeighbor == 0) {
                            // Si están exactamente en el mismo sitio, empujar en dirección aleatoria
                            sepX += (Math.random() * 2 - 1);
                            sepY += (Math.random() * 2 - 1);
                        } else {
                            // Alejarse del vecino, con fuerza inversamente proporcional a la distancia
                            double weight = (SEPARATION_RADIUS - distToNeighbor) / SEPARATION_RADIUS;
                            sepX += ((this.x - z.x) / distToNeighbor) * weight;
                            sepY += ((this.y - z.y) / distToNeighbor) * weight;
                        }
                        neighborCount++;
                    }
                }
            }

            if (neighborCount > 0) {
                // Combinar: 70% dirección al jugador + 30% separación
                dx = dx * 0.7 + sepX * customSpeed * 0.3;
                dy = dy * 0.7 + sepY * customSpeed * 0.3;
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
