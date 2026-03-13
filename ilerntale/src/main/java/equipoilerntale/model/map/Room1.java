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

        // BOSS AL FINAL DEL AULA
        this.bossSpawnArea = new Rectangle(GameSettings.MAP_WIDTH - 200, GameSettings.MAP_HEIGHT - 200, 100, 100);

        // CONFIGURACIÓN DE LOS LÍMITES/MUROS
        // MURO SUPERIOR E INFERIOR (Ajustado a la línea del suelo del aula)
        this.walls.add(new Rectangle(0, 0, GameSettings.MAP_WIDTH, 230));
        this.walls.add(new Rectangle(0, GameSettings.MAP_HEIGHT - 10, GameSettings.MAP_WIDTH, 10));

        // MURO SUPERIOR DERECHO (Ajustado para bloquear la esquina y los laterales de la puerta)
        this.walls.add(new Rectangle(1550, 0, 500, 350));

        // LÍMITES LATERALES
        this.walls.add(new Rectangle(0, 0, 10, GameSettings.MAP_HEIGHT));
        this.walls.add(new Rectangle(GameSettings.MAP_WIDTH - 10, 0, 10, GameSettings.MAP_HEIGHT));

        // CONFIGURACIÓN DE LA SALIDA (VOLVER AL PASILLO)
        this.doors.add(new DoorModel(
                1750, 220, 220, 250, // Bajamos Y a 220 para mejorar el acceso
                "Pasillo Principal", 1750, 320 // Aparece frente a la puerta 1B
        ));
    }
}
