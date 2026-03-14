package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.model.entity.WorldItem;

/**
 * Implementación de la sala "Pasillo Principal".
 * Es el eje central del instituto que conecta con las distintas aulas.
 */
public class RoomPasillo extends AbstractRoom {

    /**
     * Inicializa los componentes específicos del Pasillo Principal:
     * Nombre, fondo, enemigos, muros, puertas de acceso a aulas y objetos.
     */
    @Override
    protected void initializeRoom() {
        this.name = "Pasillo Principal";
        this.backgroundPath = "/mapa/pasillo.jpg";

        this.zombiesToSpawn = GameSettings.ZOMBIES_PASILLO;
        this.zombieSpawnArea = new Rectangle(500, 380, GameSettings.MAP_WIDTH - 800, GameSettings.MAP_HEIGHT - 450);

        addWall(0, 0, GameSettings.MAP_WIDTH, 230);
        addWall(0, GameSettings.MAP_HEIGHT - 5, GameSettings.MAP_WIDTH, 5);
        addWall(1550, 0, 600, 350);
        addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
        addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

        addDoor(1750, 220, 220, 250, "Aula 124", 1750, 320);
        addDoor(840, 200, 130, 80, "Aula 123", 600, 320);
        addDoor(1200, 200, 130, 80, "Aula 125", 600, 320);

        addWorldItem(new WorldItem(
                new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 1, true),
                1000, 400));
        addWorldItem(new WorldItem(
                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                1200, 450));
    }
}
