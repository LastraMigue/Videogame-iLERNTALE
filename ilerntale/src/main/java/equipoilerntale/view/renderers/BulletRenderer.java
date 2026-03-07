package equipoilerntale.view.renderers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import equipoilerntale.model.combat.ProjectileModel;

public class BulletRenderer {
    private Image imagenBala;

    public BulletRenderer() {
        URL url = getClass().getResource("/boss/sergio/sergionormal.png");
        if (url != null) {
            imagenBala = new ImageIcon(url).getImage();
        }
    }

    public void render(Graphics2D g2d, List<ProjectileModel> balas) {
        if (balas != null && imagenBala != null) {
            for (ProjectileModel bala : balas) {
                g2d.drawImage(imagenBala, bala.getX(), bala.getY(), bala.getSize(), bala.getSize(), null);
            }
        }
    }
}