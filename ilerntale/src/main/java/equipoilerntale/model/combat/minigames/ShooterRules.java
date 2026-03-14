package equipoilerntale.model.combat.minigames;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.awt.Image;
import javax.swing.ImageIcon;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;
import equipoilerntale.model.combat.ProjectileModel;
import equipoilerntale.model.combat.projectiles.PlayerBullet;
import equipoilerntale.model.combat.projectiles.StraightProjectile;

/**
 * Implementación de las reglas para el minijuego de disparos (Shooter).
 * El jugador controla el ratón en la parte inferior y puede disparar balas hacia arriba para destruir proyectiles enemigos.
 */
public class ShooterRules implements MinigameRules {

    /** Contador de ticks para gestionar la generación de proyectiles. */
    private int tickCounter = 0;
    /** Enfriamiento entre disparos del jugador. */
    private int shootCooldown = 0;
    /** Generador de números aleatorios. */
    private Random rand = new Random();
    /** Fuente personalizada para el texto de introducción. */
    private Font customFont;
    /** Fuente de gran tamaño para mensajes destacados. */
    private Font giantFont;
    /** Imagen de la tecla Enter para las instrucciones. */
    private Image enterBtnImage;
    /** Tiempo de vida de la pantalla de introducción en ticks. */
    private int introTicks = 120;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        shootCooldown = 0;

        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setY(490 - mouse.getAlto() - 5);
        }

        introTicks = 120;

        try {
            URL imgUrl = getClass().getResource("/controls/keyboard_enter.png");
            if (imgUrl != null) {
                enterBtnImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
        }

        try {
            URL fontUrl = getClass().getResource("/font/deltarune.ttf");
            if (fontUrl != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                customFont = baseFont.deriveFont(Font.BOLD, 24f);
                giantFont = baseFont.deriveFont(Font.BOLD, 40f);
            } else {
                customFont = new Font("Monospaced", Font.BOLD, 24);
                giantFont = new Font("Monospaced", Font.BOLD, 40);
            }
        } catch (Exception e) {
            customFont = new Font("Monospaced", Font.BOLD, 24);
            giantFont = new Font("Monospaced", Font.BOLD, 40);
        }
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        MouseModel mouse = arena.getMouse();
        if (mouse == null)
            return;

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        int dx = 0;
        if (input.isLeftPressed())
            dx = -1;
        if (input.isRightPressed())
            dx = 1;

        if (dx != 0) {
            arena.intentarMoverMouse(dx, 0);
        }

        mouse.setY(490 - mouse.getAlto() - 15);

        if (introTicks > 0) {
            introTicks--;
            return;
        }

        if (input.isEnterPressed() && shootCooldown == 0) {
            arena.addProjectile(new PlayerBullet(mouse.getX() + (mouse.getAncho() / 2) - 5, mouse.getY() - 10, 10, 10));
            shootCooldown = 15;
        }

        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();

            checkBulletVsBullet(arena);

            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive())
                    continue;

                if (p.getType() == 99) {
                    if (p.getY() <= 240)
                        p.setActive(false);
                } else {
                    if (p.getX() < 180 || p.getX() > 820) {
                        p.setActive(false);
                    }
                }
            }

            tickCounter++;
            if (tickCounter >= 35) {
                spawnHorizontalProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    /**
     * Verifica colisiones entre las balas del jugador (tipo 99) y los proyectiles enemigos.
     * 
     * @param arena Modelo donde se encuentran los proyectiles.
     */
    private void checkBulletVsBullet(ArenaModel arena) {
        List<ProjectileModel> projectiles = arena.getProjectiles();
        for (int i = 0; i < projectiles.size(); i++) {
            ProjectileModel p1 = projectiles.get(i);
            if (!p1.isActive() || p1.getType() != 99)
                continue;

            Rectangle bounds1 = p1.getBounds();

            for (int j = 0; j < projectiles.size(); j++) {
                ProjectileModel p2 = projectiles.get(j);
                if (!p2.isActive() || p2.getType() == 99)
                    continue;

                if (bounds1.intersects(p2.getBounds())) {
                    p1.setActive(false);
                    p2.setActive(false);

                    if (p2.getType() == 1) {
                        arena.addGoodCollision();
                    } else {
                        arena.addBadCollision();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Genera un proyectil horizontal que se mueve por uno de los dos carriles disponibles.
     * 
     * @param arena Modelo donde añadir el proyectil.
     */
    private void spawnHorizontalProjectile(ArenaModel arena) {
        int size = 30;

        int round = arena.getCurrentRound();
        double multiplier = Math.min(2.0, 1.0 + (round - 1) * 0.1);
        int baseSpeed = rand.nextInt(2) + 4;
        int speed = (int) (baseSpeed * multiplier);
        int type = rand.nextInt(2);

        boolean fromLeft = rand.nextBoolean();
        int spawnX;
        int spawnY;
        int dx;

        if (fromLeft) {
            spawnX = 185;
            spawnY = 270;
            dx = speed;
        } else {
            spawnX = 815 - size;
            spawnY = 350;
            dx = -speed;
        }

        arena.addProjectile(new StraightProjectile(spawnX, spawnY, size, dx, 0, type));
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        if (introTicks > 0) {
            if (giantFont != null) {
                g2d.setFont(giantFont);
                g2d.setColor(Color.WHITE);
                String msg1 = "PULSA ";
                String msg2 = " PARA DISPARAR";

                FontMetrics fm = g2d.getFontMetrics();
                int w1 = fm.stringWidth(msg1);
                int w2 = fm.stringWidth(msg2);
                int imgW = 40;
                int imgH = 40;

                int totalW = w1 + imgW + w2;
                int startX = 200 + (600 - totalW) / 2;
                int y = 240 + (250 - fm.getHeight()) / 2 + fm.getAscent();

                g2d.drawString(msg1, startX, y);
                if (enterBtnImage != null) {
                    g2d.drawImage(enterBtnImage, startX + w1, y - 35, imgW, imgH, null);
                }
                g2d.drawString(msg2, startX + w1 + imgW, y);
            }
        }
    }

    @Override
    public boolean isIntroActive() {
        return introTicks > 0;
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
