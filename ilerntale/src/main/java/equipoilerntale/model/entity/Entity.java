package equipoilerntale.model.entity;

import java.awt.Rectangle;
import java.util.List;

/**
 * CLASE BASE ABSTRACTA PARA TODAS LAS ENTIDADES DEL JUEGO (JUGADOR, ZOMBIE).
 * CONTIENE PROPIEDADES COMUNES Y LÓGICA COMPARTIDA DE MOVIMIENTO Y COLISIONES.
 */
public abstract class Entity {
    protected int x;
    protected int y;
    protected int size;
    protected String name;
    protected Direction direction;
    protected boolean isMoving;
    protected int mapWidth;
    protected int mapHeight;

    /**
     * CONSTRUCTOR BASE PARA TODAS LAS ENTIDADES.
     * INICIALIZA LA POSICIÓN, TAMAÑO, NOMBRE Y LÍMITES DEL MAPA.
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

    // ============ GETTERS Y SETTERS ============

    /**
     * OBTIENE LA COORDENADA X DE LA ENTIDAD.
     */
    public int getX() {
        return x;
    }

    /**
     * ESTABLECE LA COORDENADA X DE LA ENTIDAD.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * OBTIENE LA COORDENADA Y DE LA ENTIDAD.
     */
    public int getY() {
        return y;
    }

    /**
     * ESTABLECE LA COORDENADA Y DE LA ENTIDAD.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * OBTIENE EL TAMAÑO DE LA ENTIDAD.
     */
    public int getSize() {
        return size;
    }

    /**
     * OBTIENE LA DIRECCIÓN ACTUAL DE LA ENTIDAD.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * ESTABLECE LA DIRECCIÓN DE LA ENTIDAD.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * INDICA SI LA ENTIDAD SE ESTÁ MOVIENDO ACTUALMENTE.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * ESTABLECE EL ESTADO DE MOVIMIENTO DE LA ENTIDAD.
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * OBTIENE EL RECTÁNGULO DE COLISIÓN DE LA ENTIDAD CON SU TAMAÑO COMPLETO.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /**
     * OBTIENE EL HITBOX DE LA ENTIDAD EN UNA POSICIÓN ESPECÍFICA.
     * POR DEFECTO ES IGUAL A getBounds(), PERO PUEDE SER SOBRESCRITO PARA
     * COLISIONES MÁS PRECISAS.
     */
    public Rectangle getHitbox(int currentX, int currentY) {
        return new Rectangle(currentX, currentY, size, size);
    }

    /**
     * VERIFICA SI ESTA ENTIDAD INTERSECTA CON UN RECTÁNGULO DADO USANDO SU HITBOX.
     */
    public boolean intersects(Rectangle other) {
        return getHitbox(x, y).intersects(other);
    }

    /**
     * LÓGICA CENTRALIZADA DE COLISIONES PARA CUALQUIER ENTIDAD.
     * 
     * @return TRUE SI EL MOVIMIENTO FUE EXITOSO (SIN COLISIÓN).
     */
    public boolean moveIfNoCollision(int dx, int dy, List<Rectangle> walls) {
        int nextX = x + dx;
        int nextY = y + dy;

        // LÍMITES DEL MAPA
        nextX = Math.max(0, Math.min(nextX, mapWidth - size));
        nextY = Math.max(150, Math.min(nextY, mapHeight - size - 10));

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
