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

        // EL ÁREA DE GENERACIÓN DE ZOMBIES (Ajustada para estar debajo del muro
        // superior)
        this.zombieSpawnArea = new Rectangle(400, 300, GameSettings.MAP_WIDTH - 500, GameSettings.MAP_HEIGHT - 350);

        // BOSS AL FINAL DEL AULA
        this.bossSpawnArea = new Rectangle(GameSettings.MAP_WIDTH - 200, GameSettings.MAP_HEIGHT - 200, 100, 100);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS
        // MURO SUPERIOR E INFERIOR (Ajustado a la línea del suelo del aula)
        this.walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 250));
        this.walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10));

        // MURO SUPERIOR DERECHO (Esquina superior derecha bloqueada)
        this.walls.add(new Rectangle(1550, 0, 600, 350));

        // LÍMITES LATERALES
        this.walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        this.walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));

        // CONFIGURACIÓN DE LA SALIDA (VOLVER AL PASILLO)
        this.doors.add(new DoorModel(
                600, 180, 130, 80,
                "Pasillo Principal", 905, 320 // Aparece frente a la puerta 3A
        ));

        // OBJETOS DEL AULA 123
        this.items.add(new WorldItem(
                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                1200, 450
        ));
    }
}