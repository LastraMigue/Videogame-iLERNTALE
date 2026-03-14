package equipoilerntale.controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.model.entity.Boss;

/**
 * Sistema que gestiona todos los enemigos activos (Zombies y Jefes).
 */
public class EnemySystem {

    /** Lista de zombies presentes en la sala actual. */
    private final List<Zombie> zombies = new CopyOnWriteArrayList<>();
    /** Lista de jefes presentes en el mapa. */
    private final List<Boss> bosses = new CopyOnWriteArrayList<>();
    /** Generador de números aleatorios para lógica de dispersión y spawn. */
    private final Random random = new Random();
    /** Indica si el sistema de enemigos está procesando actualizaciones. */
    private boolean active = false;

    /** Coordenada X actual del jugador mapeada para lógica de seguimiento. */
    private int playerX;
    /** Coordenada Y actual del jugador mapeada para lógica de seguimiento. */
    private int playerY;
    /** Lista de rectángulos que representan los muros del mapa actual. */
    private List<Rectangle> walls = new ArrayList<>();

    /** Distancia mínima en píxeles para generar un enemigo lejos del jugador. */
    private static final int MIN_SPAWN_FROM_PLAYER = 300;
    /** Distancia mínima entre zombies durante el proceso de spawn. */
    private static final int MIN_SPAWN_BETWEEN_ZOMBIES = 60;

    /**
     * Constructor del sistema de enemigos.
     */
    public EnemySystem() {
    }

    /**
     * Genera zombies en un área específica, asegurando que no colisionen con muros
     * ni aparezcan demasiado cerca del jugador o entre sí.
     * 
     * @param count Cantidad de zombies a generar.
     * @param area Área del mapa donde pueden aparecer.
     * @param px Posición X inicial del jugador.
     * @param py Posición Y inicial del jugador.
     * @param currentWalls Lista de colisiones del mapa.
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
     * Genera un jefe final en la posición especificada.
     * 
     * @param area Rectángulo que define la posición del jefe.
     */
    public void spawnBoss(Rectangle area) {
        if (area != null) {
            Boss boss = new Boss(area.x, area.y, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
            bosses.add(boss);
        }
        active = true;
    }

    /**
     * Busca una posición segura y válida para spawnear un zombie.
     * 
     * @param area Área de búsqueda.
     * @return Una instancia de Zombie en una posición segura, o null si falla tras varios intentos.
     */
    private Zombie findSafeSpawn(Rectangle area) {
        int maxAttempts = 200;

        for (int i = 0; i < maxAttempts; i++) {
            int rx = area.x + random.nextInt(Math.max(1, area.width - Zombie.SIZE));
            int ry = area.y + random.nextInt(Math.max(1, area.height - Zombie.SIZE));

            Rectangle spawnBounds = new Zombie(rx, ry, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT).getHitbox(rx,
                    ry);

            Rectangle safetyBounds = new Rectangle(
                    spawnBounds.x - 10, spawnBounds.y - 10,
                    spawnBounds.width + 20, spawnBounds.height + 20);

            boolean hitsWall = walls.stream().anyMatch(w -> w.intersects(safetyBounds));
            if (hitsWall)
                continue;

            double distFromPlayer = Math.sqrt(Math.pow(rx - playerX, 2) + Math.pow(ry - playerY, 2));
            if (distFromPlayer < MIN_SPAWN_FROM_PLAYER)
                continue;

            boolean tooCloseToOtherZombie = zombies.stream()
                    .anyMatch(z -> Math
                            .sqrt(Math.pow(z.getX() - rx, 2) + Math.pow(z.getY() - ry, 2)) < MIN_SPAWN_BETWEEN_ZOMBIES);
            if (tooCloseToOtherZombie)
                continue;

            return new Zombie(rx, ry, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT);
        }
        return null;
    }

    /**
     * Actualiza el movimiento y comportamiento de todos los enemigos activos.
     * 
     * @param px Posición X del jugador.
     * @param py Posición Y del jugador.
     */
    public void update(int px, int py) {
        if (!active)
            return;

        this.playerX = px;
        this.playerY = py;

        for (Zombie z : zombies) {
            z.updateMovement(playerX, playerY, walls, zombies);
        }
    }

    /**
     * Aleja a los enemigos que estén en un radio cercano a la posición del jugador.
     * Se utiliza para dar un margen de seguridad tras el combate.
     * 
     * @param px Posición X del foco.
     * @param py Posición Y del foco.
     * @param minDistance Distancia mínima de alejamiento.
     */
    public void disperseEnemiesFrom(int px, int py, int minDistance) {
        for (Zombie z : zombies) {
            double dist = Math.sqrt(Math.pow(z.getX() - px, 2) + Math.pow(z.getY() - py, 2));
            if (dist < minDistance) {
                double angle = Math.atan2(z.getY() - py, z.getX() - px);
                if (dist < 1)
                    angle = random.nextDouble() * Math.PI * 2;

                int pushDist = 600;

                for (int d = pushDist; d >= 100; d -= 20) {
                    int nextX = px + (int) (Math.cos(angle) * d);
                    int nextY = py + (int) (Math.sin(angle) * d);

                    nextX = Math.max(20, Math.min(nextX, GameSettings.MAP_WIDTH - Zombie.SIZE - 20));
                    nextY = Math.max(250, Math.min(nextY, GameSettings.MAP_HEIGHT - Zombie.SIZE - 20));

                    Rectangle testHitbox = z.getHitbox(nextX, nextY);
                    boolean hits = walls.stream().anyMatch(w -> w.intersects(testHitbox));

                    if (!hits) {
                        z.setX(nextX);
                        z.setY(nextY);
                        break;
                    }
                }

                z.setDetectedPlayer(false);
            }
        }
    }

    /**
     * Busch un enemigo que colisione con el hitbox del jugador.
     * 
     * @param playerHitbox Hitbox del jugador.
     * @return El enemigo encontrado (Zombie o Boss), o null si no hay colisión.
     */
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

    /**
     * Indica si el jugador está colisionando con algún enemigo.
     * 
     * @param playerHitbox Hitbox del jugador.
     * @return true si hay colisión, false en caso contrario.
     */
    public boolean collidesWithPlayer(Rectangle playerHitbox) {
        return getEnemyAt(playerHitbox) != null;
    }

    /**
     * Detiene la actividad del sistema y libera la lista de enemigos.
     */
    public void stop() {
        active = false;
        zombies.clear();
        bosses.clear();
    }

    /**
     * Obtiene la lista de zombies activos en el sistema.
     * 
     * @return Lista de zombies.
     */
    public List<Zombie> getZombies() {
        return zombies;
    }

    /**
     * Obtiene la lista de jefes activos en el sistema.
     * 
     * @return Lista de jefes.
     */
    public List<Boss> getBosses() {
        return bosses;
    }

    /**
     * Limpia todas las listas de enemigos y desactiva el sistema.
     */
    public void clear() {
        zombies.clear();
        bosses.clear();
        active = false;
    }
}
