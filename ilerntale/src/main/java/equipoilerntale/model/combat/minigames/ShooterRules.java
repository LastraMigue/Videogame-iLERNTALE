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

public class ShooterRules implements MinigameRules {

    private int tickCounter = 0;
    private int shootCooldown = 0;
    private int nextType = 0;
    private Random rand = new Random();
    private Font customFont;
    private Font giantFont;
    private Image enterBtnImage;
    private int introTicks = 120;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        tickCounter = 0;
        shootCooldown = 0;
        nextType = 0;

        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setY(490 - mouse.getAlto() - 5); // Pegado abajo
        }

        introTicks = 120;

        try {
            URL imgUrl = getClass().getResource("/controls/keyboard_enter.png");
            if (imgUrl != null) {
                enterBtnImage = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cargar fuente
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
            e.printStackTrace();
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

        // Movimiento solo horizontal
        int dx = 0;
        if (input.leftPressed)
            dx = -1;
        if (input.rightPressed)
            dx = 1;

        if (dx != 0) {
            arena.intentarMoverMouse(dx, 0); // dy=0
        }

        // Mantener abajo
        mouse.setY(490 - mouse.getAlto() - 5);

        if (introTicks > 0) {
            introTicks--;
            return;
        }

        // Disparar
        if (input.enterPressed && shootCooldown == 0) {
            // Bala blanca del jugador (tipo 99)
            arena.addProjectile(new PlayerBullet(mouse.getX() + (mouse.getAncho() / 2) - 5, mouse.getY() - 10, 10, 8));
            shootCooldown = 20; // 20 frames cooldown
        }

        // Projectiles
        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            arena.checkCollisions(); // Chequea ratón contra balas malas

            // Check boundaries for despawning
            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive())
                    continue;
                if (p.getType() == 99 && p.getY() <= 240) {
                    p.setActive(false);
                } else if (p.getType() != 99 && p.getY() >= 490 - p.getSize()) {
                    p.setActive(false);
                }
            }

            // Chequeo de colisión entre balas del jugador y balas enemigas
            checkBulletVsBullet(arena.getProjectiles());

            tickCounter++;
            if (tickCounter >= 45) { // Balas enemigas cayendo
                spawnFallingProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    private void checkBulletVsBullet(List<ProjectileModel> projectiles) {
        for (int i = 0; i < projectiles.size(); i++) {
            ProjectileModel p1 = projectiles.get(i);
            if (!p1.isActive() || p1.getType() != 99)
                continue; // Solo nos interesan las balas del jugador como p1

            Rectangle bounds1 = p1.getBounds();

            for (int j = 0; j < projectiles.size(); j++) {
                ProjectileModel p2 = projectiles.get(j);
                if (!p2.isActive() || p2.getType() == 99)
                    continue; // P2 son balas enemigas

                if (bounds1.intersects(p2.getBounds())) {
                    p1.setActive(false);
                    p2.setActive(false);
                    break; // P1 ya destruida
                }
            }
        }
    }

    private void spawnFallingProjectile(ArenaModel arena) {
        int size = rand.nextInt(15) + 15;
        int speed = rand.nextInt(3) + 3; // Más velocidad
        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int spawnX = 200 + rand.nextInt(600 - size);
        int spawnY = 240 + size; // Por dentro de la arena superior

        // Caen hacia abajo (dx = 0, dy = speed)
        arena.addProjectile(new StraightProjectile(spawnX, spawnY, size, 0, speed, type));
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
        } else {
            if (customFont != null) {
                // If you want any text during the minigame, can be placed here
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
