package equipoilerntale.model.combat;

import java.awt.Rectangle;

/**
 * Clase base abstracta para todos los proyectiles en los minijuegos de combate.
 * Define las propiedades físicas comunes como posición, dimensiones, velocidad y tipo.
 */
public abstract class ProjectileModel {
    /** Posición X actual. */
    protected int x;
    /** Posición Y actual. */
    protected int y;
    /** Tamaño general (usado para inicializar ancho/alto). */
    protected int size;
    /** Ancho del proyectil. */
    protected int width;
    /** Alto del proyectil. */
    protected int height;
    /** Velocidad en el eje X. */
    protected int dx;
    /** Velocidad en el eje Y. */
    protected int dy;
    /** Tipo de proyectil (determina comportamiento y colisiones). */
    protected int type;
    /** Indica si el proyectil está activo y debe procesarse. */
    protected boolean active = true;
    /** Indica si el proyectil debe desactivarse al impactar con el ratón. */
    protected boolean deactivateOnHit = true;

    /**
     * Constructor para proyectiles cuadrados.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param size Tamaño del lado.
     * @param dx Velocidad X.
     * @param dy Velocidad Y.
     * @param type Tipo.
     */
    public ProjectileModel(int x, int y, int size, int dx, int dy, int type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.width = size;
        this.height = size;
        this.dx = dx;
        this.dy = dy;
        this.type = type;
    }

    /**
     * Constructor para proyectiles rectangulares.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param width Ancho.
     * @param height Alto.
     * @param dx Velocidad X.
     * @param dy Velocidad Y.
     * @param type Tipo.
     */
    public ProjectileModel(int x, int y, int width, int height, int dx, int dy, int type) {
        this.x = x;
        this.y = y;
        this.size = Math.max(width, height);
        this.width = width;
        this.height = height;
        this.dx = dx;
        this.dy = dy;
        this.type = type;
    }

    /**
     * Método abstracto para actualizar el movimiento del proyectil según sus reglas.
     */
    public abstract void mover();

    /**
     * Obtiene los límites de colisión del proyectil.
     * 
     * @return Rectángulo que representa el área del proyectil.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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

    /** @return Tamaño de referencia. */
    public int getSize() {
        return size;
    }

    /** @return Velocidad X actual. */
    public int getDx() {
        return dx;
    }

    /** @return Velocidad Y actual. */
    public int getDy() {
        return dy;
    }

    /** @param dx Nueva velocidad X. */
    public void setDx(int dx) {
        this.dx = dx;
    }

    /** @param dy Nueva velocidad Y. */
    public void setDy(int dy) {
        this.dy = dy;
    }

    /** @return El tipo de proyectil. */
    public int getType() {
        return type;
    }

    /** @return true si el proyectil sigue activo. */
    public boolean isActive() {
        return active;
    }

    /** @param active Nuevo estado de actividad. */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @return Ancho actual. */
    public int getWidth() {
        return width;
    }

    /** @return Alto actual. */
    public int getHeight() {
        return height;
    }

    /** @return true si se desactiva al impactar. */
    public boolean isDeactivateOnHit() {
        return deactivateOnHit;
    }

    /** @param deactivateOnHit Nuevo comportamiento de impacto. */
    public void setDeactivateOnHit(boolean deactivateOnHit) {
        this.deactivateOnHit = deactivateOnHit;
    }
}