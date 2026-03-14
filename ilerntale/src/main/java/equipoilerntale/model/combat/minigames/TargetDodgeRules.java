package equipoilerntale.model.combat.minigames;

import java.awt.Graphics2D;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.projectiles.TargetingProjectile;

/**
 * Implementación de las reglas para el minijuego de esquive con apuntado (Target Dodge).
 * Los proyectiles se generan fuera de la arena y apuntan directamente a la posición actual del ratón.
 */
public class TargetDodgeRules implements MinigameRules {

    /** Contador de ticks para gestionar la frecuencia de aparición de proyectiles. */
    private int tickCounter = 0;
    /** Tipo del próximo proyectil (alterna entre 0 y 1). */
    private int nextType = 0;
    /** Generador de números aleatorios. */
    private Random rand = new Random();

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        nextType = 0;
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        int dx = 0;
        int dy = 0;
        if (input.isUpPressed())
            dy = -1;
        if (input.isDownPressed())
            dy = 1;
        if (input.isLeftPressed())
            dx = -1;
        if (input.isRightPressed())
            dx = 1;

        if (dx != 0 || dy != 0) {
            arena.intentarMoverMouse(dx, dy);
        }

        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions();

            tickCounter++;
            if (tickCounter >= 60) {
                spawnTargetingProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    /**
     * Genera un proyectil que apunta a la posición actual del ratón desde un borde aleatorio de la pantalla.
     * 
     * @param arena Modelo de la arena.
     */
    private void spawnTargetingProjectile(ArenaModel arena) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        int size = rand.nextInt(21) + 15;
        
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(2) + 12;
        int speed = (int) (baseSpeed * multiplier);
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int spawnX = 0;
        int spawnY = 0;
        int borde = rand.nextInt(4);

        switch (borde) {
            case 0: // Superior
                spawnX = rand.nextInt(1000);
                spawnY = -50;
                break;
            case 1: // Inferior
                spawnX = rand.nextInt(1000);
                spawnY = 650;
                break;
            case 2: // Izquierdo
                spawnX = -50;
                spawnY = rand.nextInt(600);
                break;
            case 3: // Derecho
                spawnX = 1050;
                spawnY = rand.nextInt(600);
                break;
            default:
                break;
        }

        int targetX = mouse.getX() + (mouse.getAncho() / 2);
        int targetY = mouse.getY() + (mouse.getAlto() / 2);

        arena.addProjectile(new TargetingProjectile(spawnX, spawnY, targetX, targetY, size, speed, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return false;
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
