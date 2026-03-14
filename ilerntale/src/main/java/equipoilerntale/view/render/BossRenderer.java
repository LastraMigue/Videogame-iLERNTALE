package equipoilerntale.view.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import equipoilerntale.model.entity.Boss;
import equipoilerntale.service.AssetService;

/**
 * Clase encargada de renderizar al jefe (Boss) en la pantalla.
 */
public class BossRenderer {

    /**
     * Dibuja al jefe en el contexto de renderizado especificado.
     * Si el jefe no está vivo o no se encuentra su imagen, se dibuja un fallback visual.
     * 
     * @param ctx  El contexto de renderizado que contiene el objeto Graphics2D.
     * @param boss La instancia del jefe a dibujar.
     */
    public void drawBoss(RenderContext ctx, Boss boss) {
        if (!boss.isAlive())
            return;

        Image img = AssetService.getInstance().getBossSprite(boss.getName());

        if (img != null) {
            ctx.getGraphics().drawImage(img, boss.getX(), boss.getY(), boss.getBossWidth(), boss.getBossHeight(), null);
        } else {
            Graphics2D g2d = ctx.getGraphics();
            g2d.setColor(new Color(153, 0, 0));
            g2d.fillOval(boss.getX(), boss.getY(), boss.getBossWidth(), boss.getBossHeight());
            g2d.setColor(Color.WHITE);
            g2d.drawString("BOSS", boss.getX() + boss.getBossWidth() / 4, boss.getY() + boss.getBossHeight() / 2);
        }
    }
}
