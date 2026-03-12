package equipoilerntale.model.combat.minigames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;

/**
 * Minijuego de Laberinto: Navegar hasta la meta sin tocar las paredes.
 */
public class MazeRules implements MinigameRules {

    private List<Rectangle> walls;
    private Rectangle goal;
    private boolean finished = false;
    private int cooldownDamage = 0;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        arena.clearProjectiles(); // No hay proyectiles en este modo
        finished = false;
        cooldownDamage = 0;

        // Configurar posición inicial (abajo izquierda)
        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setX(210);
            mouse.setY(450);
        }

        // Crear Laberinto (Rectángulos relativos al Arena: 200, 240, 600, 250)
        walls = new ArrayList<>();
        
        // Triple Zig-Zag para mayor dificultad y longitud
        walls.add(new Rectangle(200, 420, 530, 15)); // Muro 1 (inferior, hueco derecha)
        walls.add(new Rectangle(270, 360, 530, 15)); // Muro 2 (central, hueco izquierda)
        walls.add(new Rectangle(200, 300, 530, 15)); // Muro 3 (superior, hueco derecha)
        
        // Pequeños obstáculos verticales extra
        walls.add(new Rectangle(600, 240, 15, 60)); 
        walls.add(new Rectangle(400, 315, 15, 45));

        // Meta (Arriba derecha, ahora más inaccesible)
        goal = new Rectangle(740, 245, 50, 45);
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        if (finished) return;

        MouseModel mouse = arena.getMouse();
        if (mouse == null) return;

        // Movimiento libre (pero más lento para precisión)
        int dx = 0, dy = 0;
        if (input.upPressed) dy = -1;
        if (input.downPressed) dy = 1;
        if (input.leftPressed) dx = -1;
        if (input.rightPressed) dx = 1;

        if (dx != 0 || dy != 0) {
            // Guardamos posición previa
            int oldX = mouse.getX();
            int oldY = mouse.getY();
            
            arena.intentarMoverMouse(dx, dy);

            // Comprobar colisión con muros
            Rectangle playerBounds = mouse.getBounds();
            boolean hitWall = false;
            for (Rectangle wall : walls) {
                if (playerBounds.intersects(wall)) {
                    hitWall = true;
                    break;
                }
            }

            if (hitWall) {
                if (cooldownDamage <= 0) {
                    arena.addBadCollision();
                    cooldownDamage = 30; // 0.5s de invulnerabilidad tras choque
                }
                // Rebotar un poco o impedir paso
                mouse.setX(oldX);
                mouse.setY(oldY);
            }
        }

        if (cooldownDamage > 0) cooldownDamage--;

        // Comprobar Meta
        if (mouse.getBounds().intersects(goal)) {
            arena.addGoodCollision();
            arena.addGoodCollision();
            arena.addGoodCollision();
            arena.addGoodCollision();
            arena.addGoodCollision(); // Total x5
            finished = true;
        }
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        // Dibujar Muros
        g2d.setColor(new Color(255, 50, 50, 180)); // Rojo suave
        for (Rectangle wall : walls) {
            g2d.fillRect(wall.x, wall.y, wall.width, wall.height);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(wall.x, wall.y, wall.width, wall.height);
            g2d.setColor(new Color(255, 50, 50, 180));
        }

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
