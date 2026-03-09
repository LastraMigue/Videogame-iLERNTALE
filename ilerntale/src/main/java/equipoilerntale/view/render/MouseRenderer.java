package equipoilerntale.view.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import equipoilerntale.model.combat.MouseModel;

public class MouseRenderer {
    private Image imagenMouse;

    public MouseRenderer() {
        URL url = getClass().getResource("/attack/raton.png");
        if (url != null) {
            imagenMouse = new ImageIcon(url).getImage();
        } else {
            System.err.println("¡No se encontró la imagen del ratón!");
        }
    }

    public void render(Graphics2D g2d, MouseModel raton) {
        if (raton != null && imagenMouse != null) {
            g2d.drawImage(imagenMouse, raton.getX(), raton.getY(), raton.getAncho(), raton.getAlto(), null);
        }
    }
}