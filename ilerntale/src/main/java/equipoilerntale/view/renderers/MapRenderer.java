package equipoilerntale.view.renderers;

import java.awt.*;
import java.util.List;

/**
 * Especialista en dibujar el escenario.
 */
public class MapRenderer {

    /**
     * Dibuja el fondo cargado o un color negro por defecto.
     * Si el fondo es más grande que la pantalla, se recorta automáticamente.
     */
    public void dibujarFondo(Graphics2D g2d, Image fondo, int mapWidth, int mapHeight) {
        if (fondo != null) {
            // Dibujar solo la parte visible del mapa
            g2d.drawImage(fondo, 0, 0, mapWidth, mapHeight, null);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, mapWidth, mapHeight);
        }
    }

    /**
     * Dibuja etiquetas sobre zonas interesantes.
     */
    public void dibujarEtiquetaZona(Graphics2D g2d, Rectangle zona, String texto) {
        if (zona == null)
            return;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        int width = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, zona.x + (zona.width / 2) - (width / 2), zona.y - 10);
    }

    /**
     * Dibuja los hitboxes invisibles del juego.
     */
    public void dibujarDebug(Graphics2D g2d, List<Rectangle> paredes, Rectangle hitbox, Rectangle puerta) {
        g2d.setColor(Color.GREEN);
        for (Rectangle r : paredes) {
            g2d.draw(r);
        }
        g2d.setColor(Color.BLUE);
        if (hitbox != null)
            g2d.draw(hitbox);
        if (puerta != null)
            g2d.draw(puerta);
    }
}
