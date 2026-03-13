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
                                1750, 220, 220, 250, // Bajamos Y a 220 para emparejar con el aula
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

                // OBJETOS DEL PASILLO
                this.items.add(new WorldItem(
                                new ItemModel("Pelota Ataque", "GOLPES x2\n(RONDA)", "/objects/pelotaataque.png", 1,
                                                true),
                                1000, 400));
                this.items.add(new WorldItem(
                                new ItemModel("Botella Vida", "PS +30", "/objects/botellavida.png", 1, true),
                                1200, 450));
        }
}
