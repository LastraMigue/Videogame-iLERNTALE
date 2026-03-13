package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;

/**
 * IMPLEMENTACIÓN CONCRETA DE LA PRIMERA SALA: UN AULA DEL INSTITUTO.
 * ES LA SALA .
 */
public class Room1 extends AbstractRoom {

    @Override
    protected void initializeRoom() {

        this.name = "Aula 124";

        // CUIDADO: EXTENSIÓN JPG EN EL PASILLO
        this.backgroundPath = "/mapa/hfinal.jpg";

        // CONFIGURACIÓN DE ZOMBIES
        this.zombiesToSpawn = GameSettings.ZOMBIES_AULA_124;

        // EL ÁREA DE GENERACIÓN DE ZOMBIES (Ajustada para máxima seguridad)
        this.zombieSpawnArea = new Rectangle(400, 380, GameSettings.MAP_WIDTH - 600, GameSettings.MAP_HEIGHT - 450);

        // BOSS A LA IZQUIERDA DEL AULA
        this.bossSpawnArea = new Rectangle(50, GameSettings.MAP_HEIGHT / 2 - 50, 100, 100);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS
        addWall(0, 0, GameSettings.MAP_WIDTH, 230);
        addWall(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10);
        addWall(1550, 0, 500, 350);
        addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
        addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

        // CONFIGURACIÓN DE LA SALIDA (VOLVER AL PASILLO)
        addDoor(1750, 220, 220, 250, "Pasillo Principal", 1750, 320);
    }
}
