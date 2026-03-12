package equipoilerntale.model.entity;

import java.awt.Rectangle;

/**
 * Representa un objeto físico en el mapa que el jugador puede recoger.
 * Envuelve a un ItemModel y le añade posición y estado de recolección.
 */
public class WorldItem {
    private final ItemModel item;
    private final int x;
    private final int y;
    private final int size = 48; // Tamaño estándar para ítems en el mapa
    private boolean collected = false;

    public WorldItem(ItemModel item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public ItemModel getItem() {
        return item;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, size, size);
    }
}
