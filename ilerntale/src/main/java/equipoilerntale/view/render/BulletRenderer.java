package equipoilerntale.view.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import equipoilerntale.model.combat.ProjectileModel;

public class BulletRenderer {
    private Image imagenMalo;
    private Image imagenBueno;

    public BulletRenderer() {
        imagenMalo = cargar("/attack/malo.png");
        imagenBueno = cargar("/attack/bueno.png");
    }

    private Image cargar(String ruta) {
        try (InputStream is = getClass().getResourceAsStream(ruta)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("Error cargando proyectil " + ruta + ": " + e.getMessage());
        }
        return null;
    }

    public void render(Graphics2D g2d, List<ProjectileModel> balas) {
        if (balas == null)
            return;

        for (ProjectileModel bala : balas) {
            if (!bala.isActive())
                continue;

            if (bala.getType() == 99) {
                // Bala del jugador (blanca circular)
                g2d.setColor(new java.awt.Color(0, 255, 255));
                g2d.fillOval(bala.getX(), bala.getY(), bala.getWidth(), bala.getHeight());
            } else if (bala.getType() == 10) {
                // PAREDES DEL LABERINTO
                g2d.setColor(new java.awt.Color(255, 50, 50, 180)); // Rojo suave
                g2d.fillRect(bala.getX(), bala.getY(), bala.getWidth(), bala.getHeight());
                g2d.setColor(java.awt.Color.WHITE);
                g2d.drawRect(bala.getX(), bala.getY(), bala.getWidth(), bala.getHeight());
            } else {
                Image img = (bala.getType() == 0) ? imagenMalo : imagenBueno;

                if (img != null) {
                    g2d.drawImage(img, bala.getX(), bala.getY(), bala.getWidth(), bala.getHeight(), null);
                }
            }
        }
    }
}