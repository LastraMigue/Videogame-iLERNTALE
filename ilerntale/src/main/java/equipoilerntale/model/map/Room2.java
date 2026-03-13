package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.model.entity.WorldItem;

/**
 * IMPLEMENTACIÓN CONCRETA DE LA PRIMERA SALA: UN AULA DEL INSTITUTO.
 * ES LA SALA .
 */
public class Room2 extends AbstractRoom {

        @Override
        protected void initializeRoom() {

                this.name = "Aula 123";

                // CUIDADO: EXTENSIÓN JPG EN EL PASILLO
                this.backgroundPath = "/mapa/h1.jpg";

                // CONFIGURACIÓN DE ZOMBIES
                this.zombiesToSpawn = GameSettings.ZOMBIES_AULA_123;

                // EL ÁREA DE GENERACIÓN DE ZOMBIES (Ajustada para máxima seguridad)
                this.zombieSpawnArea = new Rectangle(400, 380, GameSettings.MAP_WIDTH - 600,
                                GameSettings.MAP_HEIGHT - 450);

                // CONFIGURACIÓN DE LOS LÍMITES/MUROS
                addWall(0, 0, GameSettings.MAP_WIDTH, 250);
                addWall(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10);
                addWall(1550, 0, 600, 350);
                addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
                addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

                // CONFIGURACIÓN DE LA SALIDA (VOLVER AL PASILLO)
                addDoor(600, 180, 130, 80, "Pasillo Principal", 905, 320);

                // OBJETOS DEL AULA 123
                addWorldItem(new WorldItem(
                                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                                1200, 450));
                addWorldItem(new WorldItem(
                                new ItemModel("Patito Aguante", "DEFENSA +3", "/objects/patitoaguante.png", 1, true),
                                800, 450));
        }
}