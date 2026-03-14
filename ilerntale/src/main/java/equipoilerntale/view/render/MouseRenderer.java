package equipoilerntale.view.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.InputStream;
import javax.imageio.ImageIO;
import equipoilerntale.model.combat.MouseModel;

/**
 * Renderizador del ratón (jugador en modo combate).
 */
public class MouseRenderer {
    /** Imagen visual del ratón. */
    private Image imagenMouse;

    /**
     * Constructor que carga la imagen del ratón desde los recursos.
     */
    public MouseRenderer() {
        try (InputStream is = getClass().getResourceAsStream("/attack/raton.png")) {
            if (is != null) {
                imagenMouse = ImageIO.read(is);
            } else {
                System.err.println("¡No se encontró la imagen del ratón!");
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen del ratón: " + e.getMessage());
        }
    }

    /**
     * Dibuja el ratón en la posición indicada.
     * Implementa un efecto de parpadeo si el ratón ha recibido daño recientemente.
     * 
     * @param g2d              Contexto gráfico 2D.
     * @param raton            Modelo del ratón con su posición y dimensiones.
     * @param standsForDamage Indica si el ratón está en periodo de invulnerabilidad tras recibir daño.
     */
    public void render(Graphics2D g2d, MouseModel raton, boolean standsForDamage) {
        if (raton != null && imagenMouse != null) {
            if (standsForDamage && (System.currentTimeMillis() % 200 < 100)) {
                return;
            }
            g2d.drawImage(imagenMouse, raton.getX(), raton.getY(), raton.getAncho(), raton.getAlto(), null);
        }
    }
}