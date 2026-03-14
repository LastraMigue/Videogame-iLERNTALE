package equipoilerntale.model.combat;

import java.awt.Rectangle;

/**
 * Modelo que representa el ratón controlado por el jugador durante los minijuegos de combate.
 */
public class MouseModel {
    /** Posición X del ratón en la arena. */
    private int x;
    /** Posición Y del ratón en la arena. */
    private int y;
    /** Ancho del hitbox del ratón. */
    private int ancho = 20;
    /** Alto del hitbox del ratón. */
    private int alto = 30;
    /** Velocidad base de movimiento. */
    private int velocidad = 3;

    /**
     * Constructor del modelo del ratón.
     * 
     * @param x Posición inicial X.
     * @param y Posición inicial Y.
     */
    public MouseModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene los límites de colisión del ratón.
     * 
     * @return Rectángulo que representa el área del ratón.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    /**
     * Mueve el ratón según un desplazamiento y su velocidad.
     * 
     * @param dx Dirección X (-1, 0, 1).
     * @param dy Dirección Y (-1, 0, 1).
     */
    public void mover(int dx, int dy) {
        x += dx * velocidad;
        y += dy * velocidad;
    }

    /** @return Coordenada X actual. */
    public int getX() {
        return x;
    }

    /** @return Coordenada Y actual. */
    public int getY() {
        return y;
    }

    /** @param x Nueva coordenada X. */
    public void setX(int x) {
        this.x = x;
    }

    /** @param y Nueva coordenada Y. */
    public void setY(int y) {
        this.y = y;
    }

    /** @return Ancho del ratón. */
    public int getAncho() {
        return ancho;
    }

    /** @return Alto del ratón. */
    public int getAlto() {
        return alto;
    }
}
