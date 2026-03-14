package equipoilerntale.model.entity;

import java.awt.Rectangle;

/**
 * Representa un objeto físico en el mapa que el jugador puede recoger.
 * Envuelve a un ItemModel y le añade posición y estado de recolección.
 */
public class WorldItem {
    /** Modelo lógico del objeto. */
    private final ItemModel item;
    /** Posición X en el mapa. */
    private final int x;
    /** Posición Y en el mapa. */
    private final int y;
    /** Tamaño estándar para la representación visual del ítem en el mapa. */
    private final int size = 48;
    /** Indica si el objeto ya ha sido recogido por el jugador. */
    private boolean collected = false;

    /**
     * Constructor para ítems situados en el mundo.
     * 
     * @param item Modelo del ítem a representar.
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     */
    public WorldItem(ItemModel item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene el modelo del ítem asociado.
     * 
     * @return El {@link ItemModel} correspondiente.
     */
    public ItemModel getItem() {
        return item;
    }

    /**
     * Obtiene la posición X.
     * 
     * @return Coordenada X.
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la posición Y.
     * 
     * @return Coordenada Y.
     */
    public int getY() {
        return y;
    }

    /**
     * Obtiene el tamaño del ítem en el mapa.
     * 
     * @return Tamaño en píxeles.
     */
    public int getSize() {
        return size;
    }

    /**
     * Indica si el ítem ha sido recolectado.
     * 
     * @return true si ya fue recogido.
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Establece el estado de recolección del ítem.
     * 
     * @param collected true para marcarlo como recogido.
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Obtiene el hitbox para detección de proximidad con el jugador.
     * 
     * @return Rectángulo de colisión.
     */
    public Rectangle getHitbox() {
        return new Rectangle(x, y, size, size);
    }
}
