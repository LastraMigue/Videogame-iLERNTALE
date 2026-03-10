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
        boolean hitZombie = zombies.stream().anyMatch(z -> {
            if (!z.isAlive())
                return false;
            return playerHitbox.intersects(z.getHitbox(z.getX(), z.getY()));
        });
        boolean hitBoss = bosses.stream().anyMatch(b -> {
            if (!b.isAlive())
                return false;
            return playerHitbox.intersects(b.getHitbox(b.getX(), b.getY()));
        });

        return hitZombie || hitBoss;
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
