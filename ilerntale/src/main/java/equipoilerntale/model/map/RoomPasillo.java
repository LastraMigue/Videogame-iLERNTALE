package equipoilerntale.model.map;

import java.awt.Rectangle;

import equipoilerntale.GameSettings;

/**
 * IMPLEMENTACIÓN CONCRETA DE LA PRIMERA SALA: EL PASILLO DEL INSTITUTO.
 * ES LA SALA PRINCIPAL DONDE COMIENZA EL JUEGO TRAS LA INTRODUCCIÓN.
 */
public class RoomPasillo extends AbstractRoom {

    @Override
    protected void initializeRoom() {
        this.name = "Pasillo Principal";

        // CUIDADO: EXTENSIÓN JPG EN EL PASILLO
        this.backgroundPath = "/mapa/pasillo.png";

        // CONFIGURACIÓN DE PUERTAS Y ZOMBIES
        this.zombiesToSpawn = GameSettings.ZOMBIE_CANTIDAD_INICIAL;

        // EL ÁREA DE GENERACIÓN DE ZOMBIES (CASI TODO EL MAPA MENOS LA ESQUINA INICIAL
        // SUPERIOR IZQ)
        this.zombieSpawnArea = new Rectangle(500, 150, GameSettings.MAP_WIDTH - 500, GameSettings.MAP_HEIGHT - 300);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS DEL MAPA PASILLO
        // MURO SUPERIOR E INFERIOR
        this.walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 300));
        this.walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10));
        // LÍMITES LATERALES
        this.walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        this.walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));

        // CONFIGURACIÓN DEL ÁREA DE TRANSICIÓN (PUERTAS)
        // La puerta final del pasillo (Aula 124)
        this.doors.add(new DoorModel(
                GameSettings.MAP_WIDTH - 780, 250, 100, 120,
                "Aula 124", 200, 500 // Coordenadas objetivo en Room1
        ));

        // La puerta penúltima del pasillo
        this.doors.add(new DoorModel(
                GameSettings.MAP_WIDTH - 1430, 250, 100, 120,
                "Aula 123", 200, 500));
    }
}
