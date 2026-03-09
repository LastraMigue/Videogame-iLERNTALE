package equipoilerntale.view.render;

import java.awt.*;
import equipoilerntale.model.entity.Player;

/**
 * ESPECIALISTA EN RENDERIZAR AL JUGADOR.
 * SI EL SPRITE ES NULL (FALLO DE CARGA), DIBUJA UN INDICADOR DE DEBUG VISIBLE.
 */
public class PlayerRenderer {
    /**
     * DIBUJA AL JUGADOR EN EL CONTEXTO ESPECIFICADO.
     * USA SPRITE O FALLBACK DE DEBUG SI NO HAY SPRITE.
     */
    public void drawPlayer(RenderContext ctx, Image sprite, Player player) {
        Graphics2D g = ctx.getGraphics();
        int x = player.getX();
        int y = player.getY();
        int size = player.getSize();

        if (sprite == null) {
            // FALLBACK DE DEBUG: rectángulo magenta con borde y letra "P"
            g.setColor(new Color(180, 0, 180)); // Magenta oscuro
            g.fillRect(x, y, size, size);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, size - 1, size - 1);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("P?", x + size / 2 - 12, y + size / 2 + 6);
            return;
        }

        // Renderizado normal del sprite del jugador
        g.drawImage(sprite, x, y, size, size, null);
    }
}
