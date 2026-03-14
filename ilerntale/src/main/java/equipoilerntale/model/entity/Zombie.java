package equipoilerntale.model.entity;

import java.awt.Rectangle;
import java.util.List;
import equipoilerntale.GameSettings;

/**
 * Implementación concreta de un enemigo Zombie.
 * Gestiona comportamientos de detección, persecución y separación de grupo.
 */
public class Zombie extends Entity {

    /** Salud actual. */
    private int health;
    /** Salud máxima asignada aleatoriamente al aparecer. */
    private int maxHealth;
    /** Indica si el zombie sigue activo. */
    private boolean isAlive;
    /** Identificador de tipo visual (1-8). */
    private final int type;
    /** Índice actual para la animación de caminata. */
    private int frameIndex = 1;
    /** Marca de tiempo del último cambio de frame. */
    private long lastAnimationTime = 0;

    /** Velocidad personalizada para variar entre individuos. */
    private double customSpeed;
    /** Grado de precisión al girar hacia el jugador. */
    private double trackingPrecision;
    /** Distancia a la que el zombie se activa ante el jugador. */
    private int detectionRadius;
    /** Estado que indica si ha detectado al jugador al menos una vez. */
    private boolean hasDetectedPlayer = false;

    /** Tamaño base de los zombies. */
    public static final int SIZE = GameSettings.ZOMBIE_TAMANO;
    /** Velocidad base de los zombies. */
    public static final int SPEED = GameSettings.ZOMBIE_VELOCIDAD;
    /** Salud base de los zombies. */
    public static final int MAX_HEALTH = GameSettings.ZOMBIE_SALUD;
    /** Daño causado por contacto. */
    public static final int DAMAGE = GameSettings.ZOMBIE_DANO;

    /**
     * Constructor del zombie con posición especificada.
     * Genera atributos aleatorios para dar variedad al comportamiento de la horda.
     * 
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param mapWidth Límite horizontal del mapa.
     * @param mapHeight Límite vertical del mapa.
     */
    public Zombie(int x, int y, int mapWidth, int mapHeight) {
        super(x, y, SIZE, "Zombie", mapWidth, mapHeight);
        this.health = 25 + (int) (Math.random() * 26);
        this.maxHealth = this.health;
        this.isAlive = true;
        this.type = (int) (Math.random() * 8) + 1;
        this.direction = Direction.DOWN;

        this.customSpeed = SPEED + (Math.random() * 3.0 - 1.5);
        this.trackingPrecision = 0.7 + (Math.random() * 0.3);
        this.detectionRadius = GameSettings.ZOMBIE_DETECTION_RADIUS + (int) (Math.random() * 300 - 150);
    }

    /**
     * Obtiene la salud actual del zombie.
     * 
     * @return Puntos de salud restantes.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Obtiene la salud máxima con la que apareció el zombie.
     * 
     * @return Salud máxima individual.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Indica si el zombie está vivo.
     * 
     * @return true si tiene salud.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Obtiene el tipo visual del zombie.
     * 
     * @return Entero entre 1 y 8.
     */
    public int getType() {
        return type;
    }

    /**
     * Obtiene el índice de frame para la animación actual.
     * 
     * @return 1 o 2.
     */
    public int getFrameIndex() {
        return frameIndex;
    }

    /**
     * Establece manualmente si el zombie ha detectado al jugador.
     * 
     * @param detected true para forzar detección.
     */
    public void setDetectedPlayer(boolean detected) {
        this.hasDetectedPlayer = detected;
    }

    /**
     * Aplica daño al zombie y actualiza su estado vital.
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
     * Cicla los frames de animación basándose en el tiempo transcurrido.
     */
    public void updateAnimation() {
        long now = System.currentTimeMillis();
        if (now - lastAnimationTime > 200) {
            frameIndex = (frameIndex == 1) ? 2 : 1;
            lastAnimationTime = now;
        }
    }

    /**
     * Gestiona el movimiento del zombie hacia un objetivo, considerando obstáculos y otros zombies.
     * 
     * @param targetX Coordenada X del objetivo (jugador).
     * @param targetY Coordenada Y del objetivo (jugador).
     * @param walls Lista de colisiones del mapa.
     * @param allZombies Lista de todos los zombies para la fuerza de separación.
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls, List<? extends Object> allZombies) {
        if (!isAlive)
            return;

        double diffX = targetX - this.x;
        double diffY = targetY - this.y;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

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

    /**
     * Calcula el vector de movimiento resultante combinando persecución y separación.
     * 
     * @param diffX Diferencia en X al objetivo.
     * @param diffY Diferencia en Y al objetivo.
     * @param distance Distancia al objetivo.
     * @param allZombies Lista de zombies cercanos.
     * @return Array con las componentes [dx, dy] del movimiento.
     */
    private double[] calculateMoveVector(double diffX, double diffY, double distance,
            List<? extends Object> allZombies) {
        double targetDx = (diffX / distance) * trackingPrecision + (Math.random() * 0.4 - 0.2);
        double targetDy = (diffY / distance) * trackingPrecision + (Math.random() * 0.4 - 0.2);

        double finalDist = Math.sqrt(targetDx * targetDx + targetDy * targetDy);
        double dx = (targetDx / finalDist) * customSpeed;
        double dy = (targetDy / finalDist) * customSpeed;

        double[] separation = calculateSeparation(allZombies);
        if (separation[2] > 0) {
            dx = dx * 0.7 + separation[0] * customSpeed * 0.3;
            dy = dy * 0.7 + separation[1] * customSpeed * 0.3;
        }

        return new double[] { dx, dy };
    }

    /**
     * Calcula una fuerza de separación para evitar que los zombies se amontonen excesivamente.
     * 
     * @param allZombies Lista de posibles vecinos.
     * @return Array con [sepX, sepY, neighborCount].
     */
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

    /**
     * Actualiza la dirección visual del sprite basada en el movimiento predominante.
     * 
     * @param dx Movimiento en X.
     * @param dy Movimiento en Y.
     */
    private void updateSpriteDirection(double dx, double dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            this.direction = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            this.direction = (dy > 0) ? Direction.DOWN : Direction.UP;
        }
    }

    /**
     * Variante de actualización de movimiento para compatibilidad o simplificación.
     */
    public void updateMovement(int targetX, int targetY, List<Rectangle> walls) {
        updateMovement(targetX, targetY, walls, List.of());
    }

    /**
     * Obtiene el hitbox ajustado a los pies del zombie.
     * 
     * @param currentX Posición X.
     * @param currentY Posición Y.
     * @return Rectángulo de hitbox.
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
