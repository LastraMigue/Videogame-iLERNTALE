package equipoilerntale.model.combat;

import java.awt.Rectangle;

public class MouseModel {
    private int x, y;
    private int ancho = 20;
    private int alto = 30;
    private int velocidad = 3;

    public MouseModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public void mover(int dx, int dy) {
        x += dx * velocidad;
        y += dy * velocidad;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}
