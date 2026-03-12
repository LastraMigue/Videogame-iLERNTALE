package equipoilerntale.controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.model.entity.Boss;

/**
 * SISTEMA QUE GESTIONA TODOS LOS ENEMIGOS ACTIVOS (ZOMBIES).
 */
public class EnemySystem {
    private static final Logger LOG = Logger.getLogger(EnemySystem.class.getName());

    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Boss> bosses = new ArrayList<>();
    private final Random random = new Random();
    private boolean active = false;

    private int playerX;
    private int playerY;
    private List<Rectangle> walls = new ArrayList<>();

    private static final int MIN_SPAWN_FROM_PLAYER = 300; // Antes 900
    private static final int MIN_SPAWN_BETWEEN_ZOMBIES = 60; // Antes 300 (ZOMBIE_SIZE es 50)

    /**
     * CONSTRUCTOR DEL SISTEMA DE ENEMIGOS.
     */
    public EnemySystem() {
    }

    /**
     * GENERA UNA LISTA DE ZOMBIES EN UBICACIONES SEGURAS.
     */
    /**
     * GENERA ZOMBIES EN EL ÁREA ESPECIFICADA.
     * ASEGURA QUE NO APAREZCAN DEMASIADO CERCA DEL JUGADOR NI ENTRE SÍ.
     */
    public void spawnZombies(int count, Rectangle area, int px, int py, List<Rectangle> currentWalls) {
        this.playerX = px;
        this.playerY = py;
        this.walls = currentWalls;

        for (int i = 0; i < count; i++) {
            Zombie z = findSafeSpawn(area);
            if (z != null) {
                zombies.add(z);
            }
        }
        active = true;
    }

    /**
     * GENERA LOS JEFES FINALES EN LAS ÁREAS ESPECIFICADAS POR CADA SALA.
     */
    public void spawnBoss(Rectangle area) {
        if (area != null) {
            Boss boss = new Boss(area.x, area.y, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
            bosses.add(boss);
        }
        active = true;
    }

    private Zombie findSafeSpawn(Rectangle area) {
        int maxAttempts = 200;

        for (int i = 0; i < maxAttempts; i++) {
            int rx = area.x + random.nextInt(Math.max(1, area.width - Zombie.SIZE));
            int ry = area.y + random.nextInt(Math.max(1, area.height - Zombie.SIZE));

            Rectangle spawnBounds = new Zombie(rx, ry, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT).getHitbox(rx,
                    ry);
            
            // Margen de seguridad: expandir el hitbox de prueba para no nacer pegado a muros
            Rectangle safetyBounds = new Rectangle(
                spawnBounds.x - 10, spawnBounds.y - 10, 
                spawnBounds.width + 20, spawnBounds.height + 20
            );

            // Verificar que no colisione con muros
            boolean hitsWall = walls.stream().anyMatch(w -> w.intersects(safetyBounds));
            if (hitsWall)
                continue;

            // Verificar distancia mínima al jugador
            double distFromPlayer = Math.sqrt(Math.pow(rx - playerX, 2) + Math.pow(ry - playerY, 2));
            if (distFromPlayer < MIN_SPAWN_FROM_PLAYER)
                continue;

            // Verificar distancia mínima entre zombies (evitar agrupaciones en spawn)
            boolean tooCloseToOtherZombie = zombies.stream()
                    .anyMatch(z -> Math
                            .sqrt(Math.pow(z.getX() - rx, 2) + Math.pow(z.getY() - ry, 2)) < MIN_SPAWN_BETWEEN_ZOMBIES);
            if (tooCloseToOtherZombie)
                continue;

            return new Zombie(rx, ry, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
        }
        LOG.warning("findSafeSpawn: no se encontró posición segura tras " + maxAttempts + " intentos.");
        return null;
    }

    public void update(int px, int py) {
        if (!active)
            return;

        this.playerX = px;
        this.playerY = py;

        // Pasar la lista completa de zombies para que cada uno calcule separación de
        // sus vecinos
        for (Zombie z : zombies) {
            z.updateMovement(playerX, playerY, walls, zombies);
        }
    }

    /**
     * ALEJA A LOS ENEMIGOS QUE ESTÉN DEMASIADO CERCA DE UNA POSICIÓN.
     * Se usa para dar un margen al jugador tras salir de un combate.
     */
    public void disperseEnemiesFrom(int px, int py, int minDistance) {
        for (Zombie z : zombies) {
            double dist = Math.sqrt(Math.pow(z.getX() - px, 2) + Math.pow(z.getY() - py, 2));
            if (dist < minDistance) {
                // Vector de alejamiento
                double angle = Math.atan2(z.getY() - py, z.getX() - px);
                if (dist < 1)
                    angle = random.nextDouble() * Math.PI * 2;

                // Intentar alejar al zombie a una posición segura (fuera de muros)
                int pushDist = 600;
                boolean foundSafe = false;

                // Intentamos desde el alejamiento máximo hacia abajo hasta encontrar sitio
                for (int d = pushDist; d >= 100; d -= 20) {
                    int nextX = px + (int) (Math.cos(angle) * d);
                    int nextY = py + (int) (Math.sin(angle) * d);

                    // Límites del mapa (evitar salir por los bordes)
                    nextX = Math.max(20, Math.min(nextX, GameSettings.MAP_WIDTH - Zombie.SIZE - 20));
                    nextY = Math.max(250, Math.min(nextY, GameSettings.MAP_HEIGHT - Zombie.SIZE - 20));

                    Rectangle testHitbox = z.getHitbox(nextX, nextY);
                    boolean hits = walls.stream().anyMatch(w -> w.intersects(testHitbox));

                    if (!hits) {
                        z.setX(nextX);
                        z.setY(nextY);
                        foundSafe = true;
                        break;
                    }
                }

                z.setDetectedPlayer(false); // Olvidar al jugador momentáneamente
                if (!foundSafe) {
                    LOG.info("No se encontró punto de dispersión seguro para zombie en " + z.getX() + "," + z.getY());
                }
            }
        }
        // Los bosses no se dispersan ya que suelen ser estáticos o importantes en su sitio
    }

    public Object getEnemyAt(Rectangle playerHitbox) {
        for (Zombie z : zombies) {
            if (z.isAlive() && playerHitbox.intersects(z.getHitbox(z.getX(), z.getY()))) {
                return z;
            }
        }
        for (Boss b : bosses) {
            if (b.isAlive() && playerHitbox.intersects(b.getHitbox(b.getX(), b.getY()))) {
                return b;
            }
        }
        return null;
    }

    public boolean collidesWithPlayer(Rectangle playerHitbox) {
        return getEnemyAt(playerHitbox) != null;
    }

    /**
     * DETIENE LA ACTIVIDAD DE LOS ENEMIGOS Y LIMPIA LA LISTA.
     */
    public void stop() {
        active = false;
        zombies.clear();
    }

    /**
     * OBTIENE LA LISTA DE ZOMBIES ACTIVOS.
     */
    public List<Zombie> getZombies() {
        return zombies;
    }

    /**
     * OBTIENE LA LISTA DE JEFES ACTIVOS.
     */
    public List<Boss> getBosses() {
        return bosses;
    }

    /**
     * LIMPIA TODOS LOS ZOMBIES Y DESACTIVA EL SISTEMA.
     */
    public void clear() {
        zombies.clear();
        bosses.clear();
        active = false;
    }
}
