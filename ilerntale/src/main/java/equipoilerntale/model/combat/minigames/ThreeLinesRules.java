package equipoilerntale.model.combat.minigames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.ProjectileModel;
import equipoilerntale.model.combat.projectiles.StraightProjectile;

public class ThreeLinesRules implements MinigameRules {

    private int tickCounter = 0;
    private int nextType = 0;
    private Random rand = new Random();

    // Las posiciones Y centrales de las 3 líneas
    private final int[] lineHeights = { 280, 365, 450 };
    private int currentLineIndex = 1; // Empieza en la línea del medio

    // Cooldown para evitar que el ratón salte múltiples líneas con un solo toque
    private int verticalCooldown = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        nextType = 0;
        currentLineIndex = 1;

        // Forzar al ratón a la línea inicial
        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setY(lineHeights[currentLineIndex] - (mouse.getAlto() / 2));
        }
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        if (verticalCooldown > 0) {
            verticalCooldown--;
        }

        // Movimiento Horizontal Normal
        int dx = 0;
        if (input.leftPressed)
            dx = -1;
        if (input.rightPressed)
            dx = 1;

        if (dx != 0) {
            arena.intentarMoverMouse(dx, 0); // No dy
        }

        // Cambio de línea (Vertical)
        if (verticalCooldown == 0) {
            if (input.upPressed && currentLineIndex > 0) {
                currentLineIndex--;
                verticalCooldown = 15; // 15 frames lock
            } else if (input.downPressed && currentLineIndex < 2) {
                currentLineIndex++;
                verticalCooldown = 15; // 15 frames lock
            }
        }

        // Forzar snap Y
        mouse.setY(lineHeights[currentLineIndex] - (mouse.getAlto() / 2));

        // Proyectiles
        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions(); // Chequea ratón contra balas

            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive())
                    continue;
                if (p.getX() > 800 || p.getX() + p.getSize() < 200) {
                    p.setActive(false);
                }
            }

            tickCounter++;
            if (tickCounter >= 40) { // Un poco más frecuente
                spawnLineProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    private void spawnLineProjectile(ArenaModel arena) {
        int size = rand.nextInt(15) + 15;
        
        // Escalado dinámico por ronda (Limitado al 200% de la velocidad base)
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(3) + 4;
        int speed = (int) (baseSpeed * multiplier);
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        // Escoger una de las 3 líneas
        int targetLine = rand.nextInt(3);
        int spawnY = lineHeights[targetLine] - (size / 2);

        int spawnX;
        int dx;

        // Izquierda a Derecha o Derecha a Izquierda
        if (rand.nextBoolean()) {
            spawnX = 200; // Justo en el borde izquierdo (el recuadro empieza en 200)
            dx = speed;
        } else {
            spawnX = 800 - size; // Justo en el borde derecho (el recuadro acaba en 800)
            dx = -speed;
        }

        arena.addProjectile(new StraightProjectile(spawnX, spawnY, size, dx, 0, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        // Pintar las 3 líneas en el recuadro (x=200, w=600)
        g2d.setColor(new Color(255, 255, 255, 100)); // Blanco semi-transparente
        g2d.setStroke(new BasicStroke(2));

        for (int y : lineHeights) {
            g2d.drawLine(200, y, 800, y);
        }
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return false; // Termina por tiempo
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
