package equipoilerntale.view.ui;

import java.util.ArrayList;
import java.util.List;

import equipoilerntale.model.entity.ItemModel;

public class Inventario {

    private List<ItemModel> items;

    public Inventario() {
        this.items = new ArrayList<>();
        // Agregar objetos principales
        this.agregarItem(new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 3, true));
        this.agregarItem(
                new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 2, true));
        this.agregarItem(
                new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 2, true));
        this.agregarItem(new ItemModel("Llave", "Abre una puerta\ncerrada.", "/objects/llave.png", 1, false));
    }

    public void agregarItem(ItemModel item) {
        if (item != null) {
            items.add(item);
        }
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public ItemModel getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
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
