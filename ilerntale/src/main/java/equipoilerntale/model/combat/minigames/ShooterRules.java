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
        if (input.leftPressed) dx = -1;
        if (input.rightPressed) dx = 1;

        if (dx != 0) {
            arena.intentarMoverMouse(dx, 0); // dy=0
        }

        // Mantener abajo
        mouse.setY(490 - mouse.getAlto() - 15);

        if (introTicks > 0) {
            introTicks--;
            return;
        }

        // Disparar
        if (input.enterPressed && shootCooldown == 0) {
            // Bala del jugador (tipo 99)
            arena.addProjectile(new PlayerBullet(mouse.getX() + (mouse.getAncho() / 2) - 5, mouse.getY() - 10, 10, 10));
            shootCooldown = 15; 
        }

        // Projectiles
        if (arena.getProjectiles() != null) {
            arena.updateProjectiles();
            
            // Chequeamos colisión entre balas del jugador y proyectiles enemigos
            checkBulletVsBullet(arena);

            // Check boundaries for despawning
            for (ProjectileModel p : arena.getProjectiles()) {
                if (!p.isActive()) continue;
                
                if (p.getType() == 99) {
                    if (p.getY() <= 240) p.setActive(false);
                } else {
                    // Si sale por los lados
                    if (p.getX() < 180 || p.getX() > 820) {
                        p.setActive(false);
                    }
                }
            }

            tickCounter++;
            if (tickCounter >= 35) { // Spawn más frecuente
                spawnHorizontalProjectile(arena);
                tickCounter = 0;
            }
        }
    }

    private void checkBulletVsBullet(ArenaModel arena) {
        List<ProjectileModel> projectiles = arena.getProjectiles();
        for (int i = 0; i < projectiles.size(); i++) {
            ProjectileModel p1 = projectiles.get(i);
            if (!p1.isActive() || p1.getType() != 99) continue;

            Rectangle bounds1 = p1.getBounds();

            for (int j = 0; j < projectiles.size(); j++) {
                ProjectileModel p2 = projectiles.get(j);
                if (!p2.isActive() || p2.getType() == 99) continue;

                if (bounds1.intersects(p2.getBounds())) {
                    p1.setActive(false);
                    p2.setActive(false);
                    
                    // Lógica de impacto según tipo
                    if (p2.getType() == 1) { // PUÑO VERDE -> Daño a enemigo
                        arena.addGoodCollision();
                    } else { // CALAVERA ROJA -> Daño a jugador
                        arena.addBadCollision();
                    }
                    break;
                }
            }
        }
    }

    private void spawnHorizontalProjectile(ArenaModel arena) {
        int size = 30; // Tamaño fijo para mejor visibilidad
        int speed = rand.nextInt(2) + 4; 
        int type = rand.nextInt(2); // 0 malas, 1 buenas
        
        boolean fromLeft = rand.nextBoolean();
        int spawnX, spawnY, dx;
        
        if (fromLeft) {
            spawnX = 185;
            spawnY = 270; // Carril superior
            dx = speed;
        } else {
            spawnX = 815 - size;
            spawnY = 350; // Carril inferior
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
