package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.model.entity.WorldItem;

/**
 * Implementación de la sala "Aula 123".
 * Contiene objetos recolectables de curación y defensa.
 */
public class Room2 extends AbstractRoom {

    /**
     * Inicializa los componentes específicos del Aula 123:
     * Nombre, fondo, enemigos, muros, puertas y objetos del mundo.
     */
    @Override
    protected void initializeRoom() {
        this.name = "Aula 123";
        this.backgroundPath = "/mapa/h1.jpg";

        this.zombiesToSpawn = GameSettings.ZOMBIES_AULA_123;
        this.zombieSpawnArea = new Rectangle(400, 380, GameSettings.MAP_WIDTH - 600, GameSettings.MAP_HEIGHT - 450);

        addWall(0, 0, GameSettings.MAP_WIDTH, 250);
        addWall(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10);
        addWall(1550, 0, 600, 350);
        addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
        addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

        addDoor(600, 180, 130, 80, "Pasillo Principal", 905, 320);

        addWorldItem(new WorldItem(
                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                1200, 450));
        addWorldItem(new WorldItem(
                new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 1, true),
                800, 450));
    }
}