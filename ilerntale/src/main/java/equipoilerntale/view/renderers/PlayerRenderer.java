package equipoilerntale.view.renderers;

import java.awt.*;
import equipoilerntale.GameSettings;

/**
 * Especialista en dibujar al jugador.
 */
public class PlayerRenderer {

    /**
     * Dibuja el sprite actual o un cuadrado rojo como respaldo.
     */
    public void dibujarJugador(Graphics2D g2d, Image sprite, int x, int y) {
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, GameSettings.PLAYER_TAMANO, GameSettings.PLAYER_TAMANO);
        }
    }
}