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

/**
 * Implementación de las reglas para el minijuego de tres líneas (vial).
 * El ratón se mueve suavemente entre tres carriles horizontales predefinidos.
 */
public class ThreeLinesRules implements MinigameRules {

    /** Contador de ticks para la generación de proyectiles. */
    private int tickCounter = 0;
    /** Tipo del próximo proyectil (alterna entre 0 y 1). */
    private int nextType = 0;
    /** Generador de números aleatorios. */
    private Random rand = new Random();

    /** Coordenadas Y que representan el centro de cada una de las 3 líneas. */
    private final int[] lineHeights = { 280, 365, 450 };
    /** Índice de la línea en la que se encuentra el jugador (0, 1 o 2). */
    private int currentLineIndex = 1;

    /** Tiempo de espera para evitar cambios de línea demasiado rápidos. */
    private int verticalCooldown = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        nextType = 0;
        currentLineIndex = 1;

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

        int dx = 0;
        if (input.isLeftPressed())
            dx = -1;
        if (input.isRightPressed())
            dx = 1;

        if (dx != 0) {
            arena.intentarMoverMouse(dx, 0);
        }

        if (verticalCooldown == 0) {
            if (input.isUpPressed() && currentLineIndex > 0) {
                currentLineIndex--;
                verticalCooldown = 15;
            } else if (input.isDownPressed() && currentLineIndex < 2) {
                currentLineIndex++;
                verticalCooldown = 15;
            }
        }

        mouse.setY(lineHeights[currentLineIndex] - (mouse.getAlto() / 2));

        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions();

            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive())
                    continue;
                if (p.getX() > 800 || p.getX() + p.getSize() < 200) {
                    p.setActive(false);
                }
            }

            tickCounter++;
            if (tickCounter >= 40) {
                spawnLineProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    /**
     * Genera un proyectil en una de las tres líneas de forma aleatoria, moviéndose horizontalmente.
     * 
     * @param arena Modelo donde añadir el proyectil.
     */
    private void spawnLineProjectile(ArenaModel arena) {
        int size = rand.nextInt(15) + 15;
        
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(3) + 4;
        int speed = (int) (baseSpeed * multiplier);
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int targetLine = rand.nextInt(3);
        int spawnY = lineHeights[targetLine] - (size / 2);

        int spawnX;
        int dx;

        if (rand.nextBoolean()) {
            spawnX = 200;
            dx = speed;
        } else {
            spawnX = 800 - size;
            dx = -speed;
        }

        arena.addProjectile(new StraightProjectile(spawnX, spawnY, size, dx, 0, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        g2d.setColor(new Color(255, 255, 255, 100));
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
        return false;
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
