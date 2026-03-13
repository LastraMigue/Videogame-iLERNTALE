package equipoilerntale.view.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.InputStream;
import javax.imageio.ImageIO;
import equipoilerntale.model.combat.MouseModel;

public class MouseRenderer {
    private Image imagenMouse;

    public MouseRenderer() {
        try (InputStream is = getClass().getResourceAsStream("/attack/raton.png")) {
            if (is != null) {
                imagenMouse = ImageIO.read(is);
            } else {
                System.err.println("¡No se encontró la imagen del ratón!");
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen del ratón: " + e.getMessage());
        }
    }

    public void render(Graphics2D g2d, MouseModel raton, boolean standsForDamage) {
        if (raton != null && imagenMouse != null) {
            // Si está en modo daño, parpadea (visible 100ms, invisible 100ms)
            if (standsForDamage && (System.currentTimeMillis() % 200 < 100)) {
                return;
            }
            g2d.drawImage(imagenMouse, raton.getX(), raton.getY(), raton.getAncho(), raton.getAlto(), null);
        }
    }
}