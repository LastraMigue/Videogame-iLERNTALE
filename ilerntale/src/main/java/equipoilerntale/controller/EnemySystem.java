package equipoilerntale.controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.Zombie;

/**
 * SISTEMA QUE GESTIONA TODOS LOS ENEMIGOS ACTIVOS (ZOMBIES).
 */
public class EnemySystem {
    private static final Logger LOG = Logger.getLogger(EnemySystem.class.getName());

    private final List<Zombie> zombies = new ArrayList<>();
    private final Random random = new Random();
    private boolean active = false;

    private int playerX;
    private int playerY;
    private List<Rectangle> walls = new ArrayList<>();

    private static final int MIN_SPAWN_FROM_PLAYER = 900; // ~18 segundos de camino a vel.2
    private static final int MIN_SPAWN_BETWEEN_ZOMBIES = 300; // Separa grupos en el spawn

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

    private Zombie findSafeSpawn(Rectangle area) {
        int maxAttempts = 50;

        for (int i = 0; i < maxAttempts; i++) {
            int rx = area.x + random.nextInt(Math.max(1, area.width - Zombie.SIZE));
            int ry = area.y + random.nextInt(Math.max(1, area.height - Zombie.SIZE));

            Rectangle spawnBounds = new Zombie(rx, ry, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT).getHitbox(rx,
                    ry);

            // Verificar que no colisione con muros
            boolean hitsWall = walls.stream().anyMatch(w -> w.intersects(spawnBounds));
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

    public boolean collidesWithPlayer(Rectangle playerHitbox) {
        return zombies.stream().anyMatch(z -> {
            if (!z.isAlive())
                return false;

            Rectangle zHitbox = z.getHitbox(z.getX(), z.getY());
            return playerHitbox.intersects(zHitbox);
        });
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
     * LIMPIA TODOS LOS ZOMBIES Y DESACTIVA EL SISTEMA.
     */
    public void clear() {
        zombies.clear();
        active = false;
    }
}
