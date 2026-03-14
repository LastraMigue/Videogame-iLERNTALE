package equipoilerntale.view.render;

import java.awt.*;
import equipoilerntale.model.entity.Player;

/**
 * Especialista en renderizar al jugador en el mapa de exploración.
 * Incluye lógica de fallback en caso de que no se carguen correctamente los sprites.
 */
public class PlayerRenderer {
    /**
     * Dibuja al jugador en el contexto especificado utilizando su sprite actual.
     * Si el sprite es nulo, dibuja una representación geométrica de depuración.
     * 
     * @param ctx    Contexto de renderizado.
     * @param sprite Imagen del sprite a dibujar.
     * @param player Instancia del jugador para obtener posición y tamaño.
     */
    public void drawPlayer(RenderContext ctx, Image sprite, Player player) {
        Graphics2D g = ctx.getGraphics();
        int x = player.getX();
        int y = player.getY();
        int size = player.getSize();

        if (sprite == null) {
            g.setColor(new Color(180, 0, 180));
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
