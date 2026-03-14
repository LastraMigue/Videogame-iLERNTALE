package equipoilerntale.model.map;

import java.awt.Rectangle;
import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.model.entity.WorldItem;

/**
 * Implementación de la sala "Aula 125".
 * Contiene elementos clave como la llave y potenciadores de ataque.
 */
public class Room3 extends AbstractRoom {

    /**
     * Inicializa los componentes específicos del Aula 125:
     * Nombre, fondo, enemigos, muros, puertas y objetos (incluida la llave).
     */
    @Override
    protected void initializeRoom() {
        this.name = "Aula 125";
        this.backgroundPath = "/mapa/h2.jpg";

        this.zombiesToSpawn = GameSettings.ZOMBIES_AULA_125;
        this.zombieSpawnArea = new Rectangle(400, 380, GameSettings.MAP_WIDTH - 600, GameSettings.MAP_HEIGHT - 450);

        addWall(0, 0, GameSettings.MAP_WIDTH, 230);
        addWall(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10);
        addWall(1550, 0, 600, 350);
        addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
        addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

        addDoor(600, 180, 130, 80, "Pasillo Principal", 1200, 320);

        addWorldItem(new WorldItem(
                new ItemModel("Llave", "Abre una puerta", "/objects/llave.png", 1, false),
                GameSettings.MAP_WIDTH - 200, GameSettings.MAP_HEIGHT - 200));
        addWorldItem(new WorldItem(
                new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 1, true),
                800, 450));
        addWorldItem(new WorldItem(
                new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 1, true),
                1000, 400));
    }
}
