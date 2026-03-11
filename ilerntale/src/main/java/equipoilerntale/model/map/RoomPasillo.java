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
        this.backgroundPath = "/mapa/pasillo.jpg";

        // CONFIGURACIÓN DE PUERTAS Y ZOMBIES
        this.zombiesToSpawn = GameSettings.ZOMBIES_PASILLO;

        // EL ÁREA DE GENERACIÓN DE ZOMBIES (Ajustada para estar debajo del muro
        // superior)
        this.zombieSpawnArea = new Rectangle(500, 320, GameSettings.MAP_WIDTH - 600, GameSettings.MAP_HEIGHT - 350);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS DEL MAPA PASILLO
        // MURO SUPERIOR E INFERIOR
        this.walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 230));
        this.walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 5, GameSettings.MAP_WIDTH, 5));

        // MURO SUPERIOR DERECHO (Esquina superior derecha bloqueada)
        this.walls.add(new Rectangle(1550, 0, 600, 350));

        // LÍMITES LATERALES
        this.walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        this.walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));

        // CONFIGURACIÓN DEL ÁREA DE TRANSICIÓN (PUERTAS)
        // Puerta Metálica al final (Aula 124)
        this.doors.add(new DoorModel(
                1750, 150, 220, 250,
                "Aula 124", 1750, 320 // Aparece en el aula frente a la puerta metálica
        ));

        // Puerta 3A (Aula 123)
        this.doors.add(new DoorModel(
                840, 200, 130, 80, // Aumentada altura para facilitar detección
                "Aula 123", 600, 320 // Aparece en el aula fuera del muro (Y=320)
        ));

        // NUEVA: Puerta de Madera (Aula 125)
        this.doors.add(new DoorModel(
                1200, 200, 130, 80, // Posición central en el pasillo
                "Aula 125", 600, 320 // Aparece en el aula fuera del muro (Y=320)
        ));
    }
}
