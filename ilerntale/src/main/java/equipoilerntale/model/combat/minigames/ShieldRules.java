package equipoilerntale.model.combat.minigames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.ProjectileModel;
import equipoilerntale.model.combat.projectiles.StraightProjectile;

/**
 * Implementación de las reglas para el minijuego de escudo.
 * El jugador debe orientar un escudo para bloquear proyectiles que vienen desde los cuatro puntos cardinales hacia el centro.
 */
public class ShieldRules implements MinigameRules {

    /** Contador de ticks para gestionar la generación de proyectiles. */
    private int tickCounter = 0;
    /** Tipo del próximo proyectil (alterna entre 0 y 1). */
    private int nextType = 0;
    /** Generador de números aleatorios. */
    private Random rand = new Random();

    /** Dirección actual del escudo (0: Arriba, 1: Derecha, 2: Abajo, 3: Izquierda). */
    private int shieldDirection = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        nextType = 0;
        shieldDirection = 0;

        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setX(500 - (mouse.getAncho() / 2));
            mouse.setY(365 - (mouse.getAlto() / 2));
        }
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        mouse.setX(500 - (mouse.getAncho() / 2));
        mouse.setY(365 - (mouse.getAlto() / 2));

        if (input.isUpPressed())
            shieldDirection = 0;
        else if (input.isRightPressed())
            shieldDirection = 1;
        else if (input.isDownPressed())
            shieldDirection = 2;
        else if (input.isLeftPressed())
            shieldDirection = 3;

        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();

            Rectangle shieldBounds = getShieldBounds(mouse);

            for (int i = 0; i < arena.getProjectiles().size(); i++) {
                ProjectileModel p = arena.getProjectiles().get(i);
                if (!p.isActive())
                    continue;

                if (shieldBounds.intersects(p.getBounds())) {
                    p.setActive(false);
                }
            }

            arena.checkCollisions();

            tickCounter++;
            if (tickCounter >= 40) {
                spawnShieldProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    /**
     * Calcula los límites de colisión del escudo según la dirección actual y la posición del ratón.
     * 
     * @param mouse Modelo del ratón (jugador).
     * @return Rectángulo que representa el área del escudo.
     */
    private Rectangle getShieldBounds(MouseModel mouse) {
        int x = mouse.getX();
        int y = mouse.getY();
        int w = mouse.getAncho();
        int h = mouse.getAlto();
        int offset = 14;
        int thickness = 6;
        int length = w + 20;

        switch (shieldDirection) {
            case 0: // Arriba
                return new Rectangle(x - 10, y - offset - thickness, length, thickness);
            case 1: // Derecha
                return new Rectangle(x + w + offset, y - 10, thickness, h + 20);
            case 2: // Abajo
                return new Rectangle(x - 10, y + h + offset, length, thickness);
            case 3: // Izquierda
                return new Rectangle(x - offset - thickness, y - 10, thickness, h + 20);
            default:
                return new Rectangle(x, y, 0, 0);
        }
    }

    /**
     * Genera un proyectil recto que se dirige hacia el centro desde un lado aleatorio.
     * 
     * @param arena Modelo donde añadir el proyectil.
     */
    private void spawnShieldProjectile(ArenaModel arena) {
        int size = rand.nextInt(11) + 15;
        
        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(2) + 2;
        int speed = (int) (baseSpeed * multiplier);
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int side = rand.nextInt(4);

        int spawnX = 0;
        int spawnY = 0;
        int dx = 0;
        int dy = 0;

        switch (side) {
            case 0: // Desde arriba hacia abajo
                spawnX = 500 - (size / 2);
                spawnY = 240 - size;
                dy = speed;
                break;
            case 1: // Desde la derecha hacia la izq
                spawnX = 800;
                spawnY = 365 - (size / 2);
                dx = -speed;
                break;
            case 2: // Desde abajo hacia arriba
                spawnX = 500 - (size / 2);
                spawnY = 490;
                dy = -speed;
                break;
            case 3: // Desde la izq hacia la derecha
                spawnX = 200 - size;
                spawnY = 365 - (size / 2);
                dx = speed;
                break;
            default:
                break;
        }

        arena.addProjectile(new StraightProjectile(spawnX, spawnY, size, dx, dy, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        Rectangle sb = getShieldBounds(mouse);

        g2d.setColor(new Color(0, 255, 255));
        if (shieldDirection == 0 || shieldDirection == 2) {
            int arc = sb.height;
            g2d.fillRoundRect(sb.x, sb.y, sb.width, sb.height, arc, arc);
        } else {
            int arc = sb.width;
            g2d.fillRoundRect(sb.x, sb.y, sb.width, sb.height, arc, arc);
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
