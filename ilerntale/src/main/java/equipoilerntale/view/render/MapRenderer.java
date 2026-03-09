package equipoilerntale.view.render;

import java.awt.*;
import java.util.List;
import equipoilerntale.GameSettings;

/**
 * ESPECIALISTA EN RENDERIZAR EL MAPA.
 */
public class MapRenderer {
    /**
     * DIBUJA LA IMAGEN DE FONDO DEL MAPA.
     */
    public void drawBackground(RenderContext ctx, Image background) {
        if (background == null)
            return;
        ctx.getGraphics().drawImage(background, 0, 0, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT, null);
    }

    public void drawZoneLabel(RenderContext ctx, Rectangle zone, String label) {
        Graphics2D g2d = ctx.getGraphics();
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.draw(zone);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, zone.x + 5, zone.y - 10);
    }
}
