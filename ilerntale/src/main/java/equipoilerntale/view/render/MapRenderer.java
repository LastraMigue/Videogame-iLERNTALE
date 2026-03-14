package equipoilerntale.view.render;

import java.awt.*;
import equipoilerntale.GameSettings;

/**
 * Especialista en renderizar elementos del mapa y etiquetas de zona.
 */
public class MapRenderer {
    /**
     * Dibuja la imagen de fondo del mapa en las dimensiones totales configuradas.
     * 
     * @param ctx        Contexto de renderizado.
     * @param background Imagen de fondo a dibujar.
     */
    public void drawBackground(RenderContext ctx, Image background) {
        if (background == null)
            return;
        ctx.getGraphics().drawImage(background, 0, 0, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT, null);
    }

    /**
     * Dibuja una etiqueta de depuración o información sobre una zona rectangular.
     * 
     * @param ctx   Contexto de renderizado.
     * @param zone  Área rectangular de la zona.
     * @param label Texto descriptivo de la zona.
     */
    public void drawZoneLabel(RenderContext ctx, Rectangle zone, String label) {
        Graphics2D g2d = ctx.getGraphics();
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.draw(zone);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, zone.x + 5, zone.y - 10);
    }
}
