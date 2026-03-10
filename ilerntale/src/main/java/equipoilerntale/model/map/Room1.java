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
        this.backgroundPath = "/mapa/room.jpg";

        // CONFIGURACIÓN DE ZOMBIES
        this.zombiesToSpawn = GameSettings.ZOMBIE_CANTIDAD_INICIAL;

        // EL ÁREA DE GENERACIÓN DE ZOMBIES (CASI TODO EL MAPA MENOS LA ESQUINA INICIAL
        // SUPERIOR IZQ)
        this.zombieSpawnArea = new Rectangle(500, 150, GameSettings.MAP_WIDTH - 500, GameSettings.MAP_HEIGHT - 300);

        // BOSS AL FINAL DEL AULA
        this.bossSpawnArea = new Rectangle(GameSettings.MAP_WIDTH - 200, GameSettings.MAP_HEIGHT - 200, 100, 100);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS DEL MAPA PASILLO
        // MURO SUPERIOR E INFERIOR
        this.walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 330));
        this.walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 5, GameSettings.MAP_WIDTH, 5));
        // LÍMITES LATERALES
        this.walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        this.walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));

        // CONFIGURACIÓN DEL ÁREA DE TRANSICIÓN (LA PUERTA PARA VOLVER AL PASILLO)
        // La colocamos a la izquierda de la pantalla
        this.doors.add(new DoorModel(
                2150, 200, 180, 220, // Coordenadas de la puerta interactuable
                "Pasillo Principal", GameSettings.MAP_WIDTH - 780, 250 // Coordenadas objetivo en RoomPasillo
        ));
    }
}
