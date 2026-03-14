package equipoilerntale.model.map;

import java.awt.Rectangle;

/**
 * Modela un área de transición (puerta) entre habitaciones.
 * Define la zona de activación y el destino del teletransporte.
 */
public class DoorModel {
    /** Rectángulo que define el área de colisión de la puerta. */
    private final Rectangle area;
    /** Nombre de la sala a la que conduce esta puerta. */
    private final String targetRoomName;
    /** Posición X de aparición en la nueva sala. */
    private final int targetPlayerX;
    /** Posición Y de aparición en la nueva sala. */
    private final int targetPlayerY;

    /**
     * Constructor de la puerta.
     * 
     * @param x Coordenada X del área.
     * @param y Coordenada Y del área.
     * @param width Ancho del área.
     * @param height Alto del área.
     * @param targetRoomName Nombre identificador de la sala destino.
     * @param targetPlayerX Posición X inicial en el destino.
     * @param targetPlayerY Posición Y inicial en el destino.
     */
    public DoorModel(int x, int y, int width, int height, String targetRoomName, int targetPlayerX, int targetPlayerY) {
        this.area = new Rectangle(x, y, width, height);
        this.targetRoomName = targetRoomName;
        this.targetPlayerX = targetPlayerX;
        this.targetPlayerY = targetPlayerY;
    }

    /**
     * Obtiene el área de colisión de la puerta.
     * 
     * @return Rectángulo sensible al contacto.
     */
    public Rectangle getArea() {
        return area;
    }

    /**
     * Obtiene el nombre de la sala destino.
     * 
     * @return Nombre de la habitación.
     */
    public String getTargetRoomName() {
        return targetRoomName;
    }

    /**
     * Obtiene la coordenada X de destino.
     * 
     * @return Coordenada X.
     */
    public int getTargetPlayerX() {
        return targetPlayerX;
    }

    /**
     * Obtiene la coordenada Y de destino.
     * 
     * @return Coordenada Y.
     */
    public int getTargetPlayerY() {
        return targetPlayerY;
    }
}
