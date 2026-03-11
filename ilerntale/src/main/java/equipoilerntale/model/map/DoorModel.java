package equipoilerntale.model.map;

import java.awt.Rectangle;

/**
 * MODELO DE PUERTA QUE DEFINE SU POSICIÓN Y DESTINO.
 */
public class DoorModel {
    private final Rectangle area;
    private final String targetRoomName;
    private final int targetPlayerX;
    private final int targetPlayerY;

    public DoorModel(int x, int y, int width, int height, String targetRoomName, int targetPlayerX, int targetPlayerY) {
        this.area = new Rectangle(x, y, width, height);
        this.targetRoomName = targetRoomName;
        this.targetPlayerX = targetPlayerX;
        this.targetPlayerY = targetPlayerY;
    }

    public Rectangle getArea() {
        return area;
    }

    public String getTargetRoomName() {
        return targetRoomName;
    }

    public int getTargetPlayerX() {
        return targetPlayerX;
    }

    public int getTargetPlayerY() {
        return targetPlayerY;
    }
}
