package equipoilerntale.view.ui;

import java.util.ArrayList;
import java.util.List;

import equipoilerntale.model.entity.ItemModel;

/**
 * Sistema de inventario global del juego (Singleton).
 * Gestiona la colección de objetos que el jugador posee, permitiendo agregar,
 * eliminar y filtrar objetos para el combate.
 */
public class Inventario {

    /** Única instancia del inventario. */
    private static Inventario instance;
    /** Lista de modelos de ítems contenidos en el inventario. */
    private List<ItemModel> items;

    /**
     * Obtiene la instancia única del Inventario.
     * 
     * @return Instancia Singleton de Inventario.
     */
    public static Inventario getInstance() {
        if (instance == null) {
            instance = new Inventario();
        }
        return instance;
    }

    /**
     * Constructor privado del Inventario.
     * Inicializa la lista de ítems con algunos objetos predeterminados.
     */
    private Inventario() {
        this.items = new ArrayList<>();
        this.agregarItem(new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 0, true));
        this.agregarItem(new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 0, true));
        this.agregarItem(new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 0, true));
        this.agregarItem(new ItemModel("Llave", "Abre una puerta\ncerrada.", "/objects/llave.png", 0, false));
    }

    /**
     * Agrega un ítem al inventario. Si el ítem ya existe por nombre,
     * incrementa su cantidad acumulada.
     * 
     * @param item Modelo del ítem a agregar.
     */
    public void agregarItem(ItemModel item) {
        if (item == null)
            return;

        for (ItemModel existing : items) {
            if (existing.getNombre().equals(item.getNombre())) {
                existing.setCantidad(existing.getCantidad() + item.getCantidad());
                return;
            }
        }

        items.add(item);
    }

    /**
     * Elimina un ítem del inventario por su nombre (reduce cantidad o elimina si es necesario).
     */
    public void eliminarItem(String nombre) {
        if (nombre == null) return;
        for (ItemModel item : items) {
            if (item.getNombre().equals(nombre)) {
                if (item.getCantidad() > 0) {
                    item.setCantidad(item.getCantidad() - 1);
                }
                return;
            }
        }
    }

    /** @return Lista completa de ítems en el inventario. */
    public List<ItemModel> getItems() {
        return items;
    }

    /**
     * Obtiene un ítem por su índice en la lista.
     * 
     * @param index Índice del ítem.
     * @return ItemModel si el índice es válido, null en caso contrario.
     */
    public ItemModel getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Vacía el inventario y restablece los objetos iniciales predeterminados.
     */
    public void limpiar() {
        this.items.clear();
        this.agregarItem(new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 0, true));
        this.agregarItem(new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 0, true));
        this.agregarItem(new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 0, true));
        this.agregarItem(new ItemModel("Llave", "Abre una puerta\ncerrada.", "/objects/llave.png", 0, false));
    }

    /**
     * @return Solo los objetos que tienen cantidad > 0 y son usables en combate.
     */
    public List<ItemModel> getObjetosCombate() {
        List<ItemModel> combateItems = new ArrayList<>();
        for (ItemModel item : items) {
            if (item.isEsUsableEnCombate() && item.getCantidad() > 0) {
                combateItems.add(item);
            }
        }
        return combateItems;
    }
}
