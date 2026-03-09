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
    public static final int PLAYER_TAMANO = 100;
    public static final int PLAYER_INICIO_X = 30;
    public static final int PLAYER_INICIO_Y = 300;
    public static final long MS_ENTRE_FRAMES = 100;

    // --- Parámetros del Mapa (Pasillo Largo) ---
    public static final int MAP_WIDTH = 4500;
    public static final int MAP_HEIGHT = 600;

    // --- Parámetros de Narrativa (Intro) ---
    public static final int INTRO_WIDTH = ANCHO_PANTALLA; // Igual que el resto de pantallas (1000px)
    public static final int INTRO_HEIGHT = ALTO_PANTALLA; // Igual que el resto de pantallas (600px)
    public static final int NPC_DIALOGO_TAMANO = 256;

    // --- Rutas de Recursos ---
    public static final String RUTA_MAPAS = "/mapa/";
    public static final String RUTA_PLAYER = "/player/";
    public static final String RUTA_DIALOGO = "/dialogue/";

    // --- Parámetros de Zombie ---
    public static final int ZOMBIE_TAMANO = 80;
    public static final int ZOMBIE_VELOCIDAD = 3; // Reducido: el jugador tiene velocidad 5
    public static final int ZOMBIE_SALUD = 100;
    public static final int ZOMBIE_DANO = 10;
    public static final int ZOMBIE_CANTIDAD_INICIAL = 10; // Menos zombies para no colapsar al jugador
    public static final String RUTA_ZOMBIE = "/zombie/";
}
