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
 * Minijuego de Laberinto: Navegar hasta la meta sin tocar las paredes.
 * Las paredes ahora son proyectiles tipo WallProjectile que BLOQUEAN el paso.
 */
public class MazeRules implements MinigameRules {

    private Rectangle goal;
    private boolean finished = false;
    private int contactTimer = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        arena.clearProjectiles(); 
        finished = false;
        contactTimer = 0;

        // Configurar posición inicial (abajo izquierda)
        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setX(210);
            mouse.setY(450);
        }

        // Crear Laberinto usando WallProjectile (tipo 10)
        // Arena: 200, 240, 600, 250
        arena.addProjectile(new WallProjectile(200, 420, 530, 15)); // Muro 1
        arena.addProjectile(new WallProjectile(270, 360, 530, 15)); // Muro 2
        arena.addProjectile(new WallProjectile(200, 300, 530, 15)); // Muro 3
        
        // Meta (Arriba derecha)
        goal = new Rectangle(740, 245, 50, 45);
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        if (finished) return;

        MouseModel mouse = arena.getMouse();
        if (mouse == null) return;

        // 1. Obtener dirección deseada
        int dx = 0, dy = 0;
        if (input.upPressed) dy = -1;
        if (input.downPressed) dy = 1;
        if (input.leftPressed) dx = -1;
        if (input.rightPressed) dx = 1;

        boolean touchingWall = false;

        // 2. Movimiento con bloqueo (Deslizamiento)
        if (dx != 0 || dy != 0) {
            int oldX = mouse.getX();
            int oldY = mouse.getY();

            // Intentar mover en horizontal
            if (dx != 0) {
                arena.intentarMoverMouse(dx, 0);
                if (isCollidingWithWalls(arena, mouse.getBounds())) {
                    mouse.setX(oldX);
                    touchingWall = true;
                }
            }

            // Intentar mover en vertical
            if (dy != 0) {
                arena.intentarMoverMouse(0, dy);
                if (isCollidingWithWalls(arena, mouse.getBounds())) {
                    mouse.setY(oldY);
                    touchingWall = true;
                }
            }
        }

        // 3. Comprobar si está "pegado" a la pared (incluso sin moverse o tras bloquear)
        // Usamos un margen de 1 píxel para detectar contacto visual/físico
        Rectangle contactBounds = new Rectangle(mouse.getX() - 1, mouse.getY() - 1, mouse.getAncho() + 2, mouse.getAlto() + 2);
        if (isCollidingWithWalls(arena, contactBounds)) {
            touchingWall = true;
        }

        // 4. Lógica de daño
        if (touchingWall) {
            if (contactTimer == 0) {
                arena.addBadCollision(); // Daño al chocar
            }
            contactTimer++;
            
            // Daño cada segundo (60 frames aprox) mientras se mantenga el contacto
            if (contactTimer >= 60) {
                arena.addBadCollision();
                contactTimer = 1; 
            }
        } else {
            contactTimer = 0;
        }

        // 5. Comprobar Meta
        if (mouse.getBounds().intersects(goal)) {
            for (int i = 0; i < 5; i++) {
                arena.addGoodCollision();
            }
            finished = true;
        }
    }

    /**
     * Comprueba si los límites proporcionados colisionan con algún proyectil de tipo pared (10).
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
        // Dibujar Meta
        g2d.setColor(new Color(50, 255, 50, 150)); // Verde meta
        g2d.fillRect(goal.x, goal.y, goal.width, goal.height);
        g2d.setColor(Color.GREEN);
        g2d.drawRect(goal.x, goal.y, goal.width, goal.height);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("META", goal.x + 5, goal.y + 30);
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
        return 25;
    }
}
