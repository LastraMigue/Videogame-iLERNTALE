package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;

/**
 * Implementación de la sala "Aula 124".
 * Es una de las salas iniciales del instituto.
 */
public class Room1 extends AbstractRoom {

    /**
     * Inicializa los componentes específicos del Aula 124:
     * Nombre, fondo, enemigos (zombies y boss), áreas de spawn, muros y puertas de salida.
     */
    @Override
    protected void initializeRoom() {
        this.name = "Aula 124";
        this.backgroundPath = "/mapa/hfinal.jpg";

        this.zombiesToSpawn = GameSettings.ZOMBIES_AULA_124;
        this.zombieSpawnArea = new Rectangle(400, 380, GameSettings.MAP_WIDTH - 600, GameSettings.MAP_HEIGHT - 450);
        this.bossSpawnArea = new Rectangle(50, GameSettings.MAP_HEIGHT / 2 - 50, 100, 100);

        addWall(0, 0, GameSettings.MAP_WIDTH, 230);
        addWall(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10);
        addWall(1550, 0, 500, 350);
        addWall(0, 0, 10, GameSettings.MAP_HEIGHT);
        addWall(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT);

        addDoor(1750, 220, 220, 250, "Pasillo Principal", 1750, 320);
    }
}
