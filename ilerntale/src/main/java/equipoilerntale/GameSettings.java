package equipoilerntale;

/**
 * Clase que centraliza la configuración global y parámetros del juego.
 * Facilita el ajuste de "números mágicos" y rutas de recursos en un solo lugar.
 */
public class GameSettings {

    /** Ancho de la ventana de juego en píxeles. */
    public static final int ANCHO_PANTALLA = 1000;
    /** Alto de la ventana de juego en píxeles. */
    public static final int ALTO_PANTALLA = 600;
    /** Tasa de refresco lógica objetivo (fotogramas por segundo). */
    public static final int FPS_OBJETIVO = 60;

    /** Velocidad de movimiento del jugador en píxeles por frame. */
    public static final int PLAYER_VELOCIDAD = 5;
    /** Tamaño del sprite del jugador (cuadrado) en píxeles. */
    public static final int PLAYER_TAMANO = 80;
    /** Posición inicial X del jugador al comenzar. */
    public static final int PLAYER_INICIO_X = 30;
    /** Posición inicial Y del jugador al comenzar. */
    public static final int PLAYER_INICIO_Y = 300;
    /** Tiempo en milisegundos entre cada frame de animación. */
    public static final long MS_ENTRE_FRAMES = 100;

    /** Ancho total del mapa de exploración en píxeles. */
    public static final int MAP_WIDTH = 2048;
    /** Alto total del mapa de exploración en píxeles. */
    public static final int MAP_HEIGHT = 860;

    /** Ancho de la pantalla de introducción. */
    public static final int INTRO_WIDTH = ANCHO_PANTALLA;
    /** Alto de la pantalla de introducción. */
    public static final int INTRO_HEIGHT = ALTO_PANTALLA;
    /** Tamaño del sprite del NPC durante los diálogos. */
    public static final int NPC_DIALOGO_TAMANO = 256;

    /** Ruta base para los recursos de los mapas. */
    public static final String RUTA_MAPAS = "/mapa/";
    /** Ruta base para los sprites del jugador. */
    public static final String RUTA_PLAYER = "/player/";
    /** Ruta base para los recursos de diálogos. */
    public static final String RUTA_DIALOGO = "/dialogue/";

    /** Tamaño del sprite de los zombies en píxeles. */
    public static final int ZOMBIE_TAMANO = 70;
    /** Velocidad de movimiento de los zombies. */
    public static final int ZOMBIE_VELOCIDAD = 3;
    /** Salud base de los zombies. */
    public static final int ZOMBIE_SALUD = 100;
    /** Daño que inflige un zombie al jugador por segundo de contacto. */
    public static final int ZOMBIE_DANO = 1;
    /** Cantidad total de zombies a generar inicialmente. */
    public static final int ZOMBIE_CANTIDAD_INICIAL = 50;
    /** Cantidad de zombies situados en el pasillo. */
    public static final int ZOMBIES_PASILLO = 10;
    /** Cantidad de zombies situados en el aula 124. */
    public static final int ZOMBIES_AULA_124 = 8;
    /** Cantidad de zombies situados en el aula 123. */
    public static final int ZOMBIES_AULA_123 = 8;
    /** Cantidad de zombies situados en el aula 125. */
    public static final int ZOMBIES_AULA_125 = 8;
    /** Radio en píxeles en el que un zombie detecta al jugador para perseguirlo. */
    public static final int ZOMBIE_DETECTION_RADIUS = 400;
    /** Ruta base para los sprites de los zombies. */
    public static final String RUTA_ZOMBIE = "/zombie/";
}
