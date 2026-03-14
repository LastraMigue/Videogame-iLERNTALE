package equipoilerntale.model.combat.minigames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.ProjectileModel;
import equipoilerntale.model.combat.projectiles.WallProjectile;

/**
 * Implementación de las reglas para el minijuego de laberinto.
 * El jugador debe navegar hasta una meta verde evitando el contacto prolongado con las paredes.
 * Las paredes bloquean el movimiento pero infligen daño si el ratón se mantiene pegado a ellas.
 */
public class MazeRules implements MinigameRules {

    /** Área que representa la meta o salida del laberinto. */
    private Rectangle goal;
    /** Indica si el jugador ha llegado con éxito a la meta. */
    private boolean finished = false;
    /** Temporizador para gestionar el daño por contacto continuo con muros. */
    private int contactTimer = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        arena.clearProjectiles(); 
        finished = false;
        contactTimer = 0;

        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setX(210);
            mouse.setY(450);
        }

        // Crear Laberinto usando WallProjectile (tipo 10)
        arena.addProjectile(new WallProjectile(200, 420, 530, 15));
        arena.addProjectile(new WallProjectile(270, 360, 530, 15));
        arena.addProjectile(new WallProjectile(200, 300, 530, 15));
        
        goal = new Rectangle(740, 245, 50, 45);
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        if (finished) return;

        MouseModel mouse = arena.getMouse();
        if (mouse == null) return;

        int dx = 0;
        int dy = 0;
        if (input.isUpPressed()) dy = -1;
        if (input.isDownPressed()) dy = 1;
        if (input.isLeftPressed()) dx = -1;
        if (input.isRightPressed()) dx = 1;

        boolean touchingWall = false;

        if (dx != 0 || dy != 0) {
            int oldX = mouse.getX();
            int oldY = mouse.getY();

            if (dx != 0) {
                arena.intentarMoverMouse(dx, 0);
                if (isCollidingWithWalls(arena, mouse.getBounds())) {
                    mouse.setX(oldX);
                    touchingWall = true;
                }
            }

            if (dy != 0) {
                arena.intentarMoverMouse(0, dy);
                if (isCollidingWithWalls(arena, mouse.getBounds())) {
                    mouse.setY(oldY);
                    touchingWall = true;
                }
            }
        }

        Rectangle contactBounds = new Rectangle(mouse.getX() - 1, mouse.getY() - 1, mouse.getAncho() + 2, mouse.getAlto() + 2);
        if (isCollidingWithWalls(arena, contactBounds)) {
            touchingWall = true;
        }

        if (touchingWall) {
            if (contactTimer == 0) {
                arena.addBadCollision();
            }
            contactTimer++;
            
            if (contactTimer >= 60) {
                arena.addBadCollision();
                contactTimer = 1; 
            }
        } else {
            contactTimer = 0;
        }

        if (mouse.getBounds().intersects(goal)) {
            for (int i = 0; i < 5; i++) {
                arena.addGoodCollision();
            }
            finished = true;
        }
    }

    /**
     * Verifica si un área específica colisiona con proyectiles de tipo pared.
     * 
     * @param arena Modelo de la arena.
     * @param bounds Área a comprobar.
     * @return true si hay colisión con un muro.
     */
    private boolean isCollidingWithWalls(ArenaModel arena, Rectangle bounds) {
        if (arena.getProjectiles() == null) return false;
        for (ProjectileModel proj : arena.getProjectiles()) {
            if (proj.getType() == 10 && bounds.intersects(proj.getBounds())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        g2d.setColor(new Color(50, 255, 50, 150));
        g2d.fillRect(goal.x, goal.y, goal.width, goal.height);
        g2d.setColor(Color.GREEN);
        g2d.drawRect(goal.x, goal.y, goal.width, goal.height);
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return finished;
    }

    @Override
    public int getDurationInSeconds() {
        return 15;
    }
}
