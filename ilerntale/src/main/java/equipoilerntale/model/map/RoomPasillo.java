package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.model.entity.WorldItem;

/**
 * IMPLEMENTACIÓN CONCRETA DE LA PRIMERA SALA: EL PASILLO DEL INSTITUTO.
 * ES LA SALA PRINCIPAL DONDE COMIENZA EL JUEGO TRAS LA INTRODUCCIÓN.
 */
public class RoomPasillo extends AbstractRoom {

        @Override
        protected void initializeRoom() {
                this.name = "Pasillo Principal";

                // CUIDADO: EXTENSIÓN JPG EN EL PASILLO
                this.backgroundPath = "/mapa/pasillo.jpg";

                // CONFIGURACIÓN DE PUERTAS Y ZOMBIES
                this.zombiesToSpawn = GameSettings.ZOMBIES_PASILLO;

                // EL ÁREA DE GENERACIÓN DE ZOMBIES (Ajustada para máxima seguridad)
                this.zombieSpawnArea = new Rectangle(500, 380, GameSettings.MAP_WIDTH - 800,
                                GameSettings.MAP_HEIGHT - 450);

                // CONFIGURACIÓN DE LOS LÍMITES/MUROS DEL MAPA PASILLO
                addWall(0, 0, GameSettings.MAP_WIDTH, 230);
                addWall(0, GameSettings.MAP_HEIGHT - 5, GameSettings.MAP_WIDTH, 5);
                addWall(1550, 0, 600, 350);
                addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
                addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

                // CONFIGURACIÓN DEL ÁREA DE TRANSICIÓN (PUERTAS)
                addDoor(1750, 220, 220, 250, "Aula 124", 1750, 320);
                addDoor(840, 200, 130, 80, "Aula 123", 600, 320);
                addDoor(1200, 200, 130, 80, "Aula 125", 600, 320);

                // OBJETOS DEL PASILLO
                addWorldItem(new WorldItem(
                                new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 1, true),
                                1000, 400));
                addWorldItem(new WorldItem(
                                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                                1200, 450));
        }
}
