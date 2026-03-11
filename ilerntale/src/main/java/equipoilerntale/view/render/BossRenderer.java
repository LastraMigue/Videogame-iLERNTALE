package equipoilerntale.view.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.logging.Logger;

import equipoilerntale.model.entity.Boss;
import equipoilerntale.service.AssetService;

public class BossRenderer {

    public void drawBoss(RenderContext ctx, Boss boss) {
        if (!boss.isAlive())
            return;

        // "sergio" -> /boss/sergio/sergionormal.png
        Image img = AssetService.getInstance().getBossSprite(boss.getName());

        if (img != null) {
            ctx.getGraphics().drawImage(img, boss.getX(), boss.getY(), boss.getSize(), boss.getSize(), null);
        } else {
            // FALLBACK VISIBLE SI FALLA LA IMAGEN
            Graphics2D g2d = ctx.getGraphics();
            g2d.setColor(new Color(153, 0, 0)); // Rojo oscuro
            g2d.fillOval(boss.getX(), boss.getY(), boss.getSize(), boss.getSize());
            g2d.setColor(Color.WHITE);
            g2d.drawString("BOSS", boss.getX() + boss.getSize() / 4, boss.getY() + boss.getSize() / 2);
        }
    }
}
