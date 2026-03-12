package equipoilerntale.model.combat.minigames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.ProjectileModel;
import equipoilerntale.model.combat.projectiles.StraightProjectile;

/**
 * Minijuego de Ritmo: Pulsar ENTER cuando las notas pasen por la zona.
 */
public class RitmoRules implements MinigameRules {

    private int tickCounter = 0;
    private final Random rand = new Random();
    private final int targetY = 440;
    private final int targetHeight = 40;
    private boolean enterWasPressed = false;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        arena.clearProjectiles();
        tickCounter = 0;
        enterWasPressed = false;
        
        // Ratón estático fuera de la vista
        if (arena.getMouse() != null) {
            arena.getMouse().setY(600);
        }
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        tickCounter++;
        
        // Spawn de notas
        if (tickCounter % 50 == 0) {
            int x = 200 + rand.nextInt(600 - 30);
            arena.addProjectile(new StraightProjectile(x, 240, 30, 0, 5, 1)); // Tipo 1 para notar impacto bueno
        }

        // Lógica de pulsación
        if (input.enterPressed && !enterWasPressed) {
            boolean hit = false;
            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive()) continue;
                
                // Si está en la zona de impacto
                if (p.getY() >= targetY && p.getY() <= targetY + targetHeight) {
                    p.setActive(false);
                    arena.addGoodCollision();
                    hit = true;
                }
            }
            if (!hit) {
                arena.addBadCollision(); // Pulsar en falso resta vida
            }
            enterWasPressed = true;
        } else if (!input.enterPressed) {
            enterWasPressed = false;
        }

        // Lógica de fallo por pasarse
        for (ProjectileModel p : arena.getProjectiles()) {
            if (p.isActive() && p.getY() > targetY + targetHeight + 10) {
                p.setActive(false);
                arena.addBadCollision(); // No pulsar la nota resta vida
            }
        }

        arena.updateProjectiles();
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        // Dibujar zona de impacto
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRect(200, targetY, 600, targetHeight);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(200, targetY, 600, targetHeight);
        
        g2d.drawString("PULSA [ENTER] EN EL MOMENTO JUSTO", 350, 430);
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return false; // Por tiempo
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
