package equipoilerntale;

/**
 * CONFIGURACIÓN GLOBAL (GameSettings).
 * Centraliza todos los "números mágicos" del proyecto para que sean fáciles de
 * cambiar.
 */
public class GameSettings {

    // --- Resolución General ---
    public static final int ANCHO_PANTALLA = 1000;
    public static final int ALTO_PANTALLA = 600;
    public static final int FPS_OBJETIVO = 60;

    // --- Parámetros de Exploración ---
    public static final int PLAYER_VELOCIDAD = 5;
    public static final int PLAYER_TAMANO = 60;
    public static final int PLAYER_INICIO_X = 30;
    public static final int PLAYER_INICIO_Y = 300;
    public static final long MS_ENTRE_FRAMES = 100;

    // --- Parámetros del Mapa (Pasillo Largo) ---
    public static final int MAP_WIDTH = 2048;
    public static final int MAP_HEIGHT = 860;

    // --- Parámetros de Narrativa (Intro) ---
    public static final int INTRO_WIDTH = ANCHO_PANTALLA; // Igual que el resto de pantallas (1000px)
    public static final int INTRO_HEIGHT = ALTO_PANTALLA; // Igual que el resto de pantallas (600px)
    public static final int NPC_DIALOGO_TAMANO = 256;

    // --- Rutas de Recursos ---
    public static final String RUTA_MAPAS = "/mapa/";
    public static final String RUTA_PLAYER = "/player/";
    public static final String RUTA_DIALOGO = "/dialogue/";

    // --- Parámetros de Zombie ---
    public static final int ZOMBIE_TAMANO = 50;
    public static final int ZOMBIE_VELOCIDAD = 3; // Reducido: el jugador tiene velocidad 5
    public static final int ZOMBIE_SALUD = 25;
    public static final int ZOMBIE_DANO = 1;
    public static final int ZOMBIE_CANTIDAD_INICIAL = 50;
    public static final int ZOMBIES_PASILLO = 20;
    public static final int ZOMBIES_AULA_124 = 20;
    public static final int ZOMBIES_AULA_123 = 20;
    public static final String RUTA_ZOMBIE = "/zombie/";
}
