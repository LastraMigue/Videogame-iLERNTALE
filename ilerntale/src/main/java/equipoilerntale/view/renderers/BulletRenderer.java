package equipoilerntale.view.renderers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import equipoilerntale.model.combat.ProjectileModel;

public class BulletRenderer {
    private Image imagenMalo;
    private Image imagenBueno;

    public BulletRenderer() {
        imagenMalo = cargar("/attack/malo.png");
        imagenBueno = cargar("/attack/bueno.png");
    }

    private Image cargar(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }
        return null;
    }

    public void render(Graphics2D g2d, List<ProjectileModel> balas) {
        if (balas == null)
            return;

        for (ProjectileModel bala : balas) {
            if (!bala.isActive()) continue;

            Image img = (bala.getType() == 0) ? imagenMalo : imagenBueno;

            if (img != null) {
                g2d.drawImage(img, bala.getX(), bala.getY(), bala.getSize(), bala.getSize(), null);
            }
        }
    }
}