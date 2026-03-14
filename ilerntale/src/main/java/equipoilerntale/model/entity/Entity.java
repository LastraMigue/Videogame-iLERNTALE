package equipoilerntale.model.entity;

import java.awt.Rectangle;
import java.util.List;

/**
 * Clase base abstracta para todas las entidades del juego.
 * Gestiona propiedades comunes como posición, tamaño, dirección y lógica básica de movimiento/colisión.
 */
public abstract class Entity {
    /** Coordenada X actual. */
    protected int x;
    /** Coordenada Y actual. */
    protected int y;
    /** Tamaño base de la entidad. */
    protected int size;
    /** Nombre identificador de la entidad. */
    protected String name;
    /** Dirección actual de movimiento o vista. */
    protected Direction direction;
    /** Indica si la entidad se está desplazando. */
    protected boolean isMoving;
    /** Ancho del mapa para límites de movimiento. */
    protected int mapWidth;
    /** Alto del mapa para límites de movimiento. */
    protected int mapHeight;

    /**
     * Constructor base para entidades.
     * 
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param size Tamaño inicial.
     * @param name Nombre de la entidad.
     * @param mapWidth Límite horizontal del mapa.
     * @param mapHeight Límite vertical del mapa.
     */
    public Entity(int x, int y, int size, String name, int mapWidth, int mapHeight) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.name = name;
        this.direction = Direction.DOWN;
        this.isMoving = false;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    /**
     * Obtiene la coordenada X de la entidad.
     * 
     * @return Coordenada X.
     */
    public int getX() {
        return x;
    }

    /**
     * Establece la coordenada X de la entidad.
     * 
     * @param x Nueva coordenada X.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Obtiene la coordenada Y de la entidad.
     * 
     * @return Coordenada Y.
     */
    public int getY() {
        return y;
    }

    /**
     * Establece la coordenada Y de la entidad.
     * 
     * @param y Nueva coordenada Y.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Obtiene el tamaño de la entidad.
     * 
     * @return Tamaño en píxeles.
     */
    public int getSize() {
        return size;
    }

    /**
     * Obtiene el nombre de la entidad.
     * 
     * @return Nombre.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la dirección actual de la entidad.
     * 
     * @return Valor de {@link Direction}.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Establece la dirección de la entidad.
     * 
     * @param direction Nueva dirección.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Indica si la entidad está en movimiento.
     * 
     * @return true si se está moviendo.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Establece el estado de movimiento.
     * 
     * @param moving true para activar el estado de movimiento.
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * Obtiene el rectángulo delimitador completo de la entidad.
     * 
     * @return Rectángulo de tamaño (size x size).
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /**
     * Obtiene el hitbox preciso para colisiones en una posición dada.
     * 
     * @param currentX Coordenada X a evaluar.
     * @param currentY Coordenada Y a evaluar.
     * @return Rectángulo de colisión.
     */
    public Rectangle getHitbox(int currentX, int currentY) {
        return new Rectangle(currentX, currentY, size, size);
    }

    /**
     * Verifica si el hitbox de la entidad colisiona con otro rectángulo.
     * 
     * @param other Rectángulo externo.
     * @return true si hay intersección.
     */
    public boolean intersects(Rectangle other) {
        return getHitbox(x, y).intersects(other);
    }

    /**
     * Intenta mover la entidad aplicando detección de colisiones con muros y límites del mapa.
     * 
     * @param dx Desplazamiento en X.
     * @param dy Desplazamiento en Y.
     * @param walls Lista de rectángulos que representan obstáculos.
     * @return true si el movimiento fue permitido y se actualizó la posición.
     */
    public boolean moveIfNoCollision(int dx, int dy, List<Rectangle> walls) {
        int nextX = x + dx;
        int nextY = y + dy;

        nextX = Math.max(0, Math.min(nextX, mapWidth - size));
        nextY = Math.max(0, Math.min(nextY, mapHeight - size - 10));

        Rectangle nextHitbox = getHitbox(nextX, nextY);

        for (Rectangle wall : walls) {
            if (nextHitbox.intersects(wall)) {
                return false;
            }
        }

        this.x = nextX;
        this.y = nextY;
        return true;
    }
}
