package equipoilerntale.model.combat.minigames;

import java.awt.Graphics2D;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.projectiles.BouncingProjectile;

public class ClassicDodgeRules implements MinigameRules {

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

        // Projectiles
        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions();

            tickCounter++;
            if (tickCounter >= 30) {
                spawnProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    private void spawnProjectile(ArenaModel arena) {
        if (arena.getProjectiles().size() >= 12)
            return;

        int size = rand.nextInt(21) + 15;
        
        // Escalado dinámico por ronda (Limitado al 200% de la velocidad base)
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(2) + 2;
        int speed = (int) (baseSpeed * multiplier);
        
        int borde = rand.nextInt(4);

        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int x = 0, y = 0, dx = 0, dy = 0;

        switch (borde) {
            case 0:
                x = 200 + rand.nextInt(600 - size);
                y = 240;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = speed;
                break;
            case 1:
                x = 200 + rand.nextInt(600 - size);
                y = 490 - size;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = -speed;
                break;
            case 2:
                x = 200;
                y = 240 + rand.nextInt(250 - size);
                dx = speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
            case 3:
                x = 800 - size;
                y = 240 + rand.nextInt(250 - size);
                dx = -speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
        }

        arena.addProjectile(new BouncingProjectile(x, y, size, dx, dy, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        // Nada extra que pintar en el clásico
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return arena.allBulletsHit();
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
