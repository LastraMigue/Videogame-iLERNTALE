package equipoilerntale.model.combat.minigames;

import java.awt.Graphics2D;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.projectiles.TargetingProjectile;

public class TargetDodgeRules implements MinigameRules {

    private int tickCounter = 0;
    private int nextType = 0;
    private Random rand = new Random();

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        nextType = 0;
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        // Mover ratón
        int dx = 0;
        int dy = 0;
        if (input.upPressed)
            dy = -1;
        if (input.downPressed)
            dy = 1;
        if (input.leftPressed)
            dx = -1;
        if (input.rightPressed)
            dx = 1;

        if (dx != 0 || dy != 0) {
            arena.intentarMoverMouse(dx, dy);
        }

        // Projectiles
        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions();

            tickCounter++;
            // 60 ticks per second assuming 60 UPS. The user said "cada segundo aparezca un
            // bullet"
            if (tickCounter >= 60) {
                spawnTargetingProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    private void spawnTargetingProjectile(ArenaModel arena) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        int size = rand.nextInt(21) + 15;
        
        // Escalado dinámico por ronda (Limitado al 200% de la velocidad base)
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(2) + 12;
        int speed = (int) (baseSpeed * multiplier);
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        // Generar a una distancia segura fuera de la arena o en los bordes
        int spawnX = 0, spawnY = 0;
        int borde = rand.nextInt(4);

        switch (borde) {
            case 0: // Top
                spawnX = rand.nextInt(1000);
                spawnY = -50;
                break;
            case 1: // Bottom
                spawnX = rand.nextInt(1000);
                spawnY = 650;
                break;
            case 2: // Left
                spawnX = -50;
                spawnY = rand.nextInt(600);
                break;
            case 3: // Right
                spawnX = 1050;
                spawnY = rand.nextInt(600);
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
        return false; // Termina por tiempo, no por balas
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
