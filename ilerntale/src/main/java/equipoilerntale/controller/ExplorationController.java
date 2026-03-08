package equipoilerntale.controller;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.model.map.GameResources;

/**
 * Senior Architect Refactor: ExplorationController
 * Centralizes movement, collision and animation state.
 */
public class ExplorationController {

    private static final Logger LOG = Logger.getLogger(ExplorationController.class.getName());

    // ============ CONFIGURACIÓN ============
    public static final boolean DEBUG_MODE = false;
    public static final int ANCHO_PANEL = GameSettings.ANCHO_PANTALLA;
    public static final int ALTO_PANEL = GameSettings.ALTO_PANTALLA;
    public static final int MAP_WIDTH = GameSettings.MAP_WIDTH;
    public static final int MAP_HEIGHT = GameSettings.MAP_HEIGHT;

    // ---------- Jugador ----------
    public static final int VELOCIDAD = GameSettings.PLAYER_VELOCIDAD;
    public static final int TAMANO_JUGADOR = GameSettings.PLAYER_TAMANO;
    public static final int INICIO_JUGADOR_X = GameSettings.PLAYER_INICIO_X;
    public static final int INICIO_JUGADOR_Y = GameSettings.PLAYER_INICIO_Y;

    // ---------- Animación ----------
    private static final long FRAME_DELAY_MS = 100; // 120ms per frame
    private long lastFrameTime = 0;
    private int frameIndex = 0; // 0 is idle, 1..N are walking

    // ============ ESTADO DEL JUGADOR ============
    private int jugadorX = INICIO_JUGADOR_X;
    private int jugadorY = INICIO_JUGADOR_Y;
    private String direccionActual = "derecha";
    private boolean moviendose = false;

    // ---------- Componentes ----------
    private final InputHandler inputHandler;
    private final GameResources resources;
    private final String nombrePersonaje;
    private final Object mainFrame;

    // ---------- Zonas de colisión ----------
    private final List<Rectangle> paredes = new ArrayList<>();
    private Rectangle zonaPuerta;

    public ExplorationController(Object mainFrame, String nombrePersonaje) {
        this.mainFrame = mainFrame;
        this.nombrePersonaje = nombrePersonaje;
        this.inputHandler = new InputHandler();

        GameResources.getInstance().loadCharacterSprites(nombrePersonaje, TAMANO_JUGADOR);
        this.resources = GameResources.getInstance();

        inicializarColisiones();
        LOG.info("ExplorationController (Senior Refactor) para " + nombrePersonaje);
    }

    private void inicializarColisiones() {
        // Paredes del mapa completo (4500 x 600)
        // Pared superior
        paredes.add(new Rectangle(0, 0, MAP_WIDTH, 300));
        // Pared inferior
        paredes.add(new Rectangle(0, MAP_HEIGHT - 10, MAP_WIDTH, 10));
        // Pared izquierda
        paredes.add(new Rectangle(0, 0, 10, MAP_HEIGHT));
        // Pared derecha
        paredes.add(new Rectangle(MAP_WIDTH - 10, 0, 10, MAP_HEIGHT));

        // Obstáculos del pasillo (columnas/arcadas)
        /*
         * for (int x = 400; x < MAP_WIDTH - 200; x += 500) {
         * // Pared superior en cada arco
         * paredes.add(new Rectangle(x, 150, 80, 200));
         * // Pared inferior en cada arco
         * paredes.add(new Rectangle(x, 400, 80, 200));
         * }
         */

        // Puerta al final del pasillo
        zonaPuerta = new Rectangle(MAP_WIDTH - 150, 300, 100, 100);
    }

    public void update() {
        int oldX = jugadorX;
        int oldY = jugadorY;

        // 1. Manejar Movimiento
        moverEnEjes();

        // Si hay alguna tecla de movimiento pulsada, animamos (incluso contra bordes)
        boolean teclasPulsadas = inputHandler.upPressed || inputHandler.downPressed ||
                inputHandler.leftPressed || inputHandler.rightPressed;

        moviendose = teclasPulsadas;

        // 3. Actualizar Animación
        actualizarEstadoAnimacion();

        // 4. Interacción
        verificarInteraccion();
    }

    private void moverEnEjes() {
        // Movimiento X
        int dx = 0;
        if (inputHandler.leftPressed)
            dx -= VELOCIDAD;
        else if (inputHandler.rightPressed)
            dx += VELOCIDAD;

        if (dx != 0) {
            String tempDir = (dx < 0) ? "izquierda" : "derecha";
            if (inputHandler.preferHorizontal || (!inputHandler.upPressed && !inputHandler.downPressed)) {
                if (!direccionActual.equals(tempDir)) {
                    direccionActual = tempDir;
                    frameIndex = 0;
                }
            }
            int nuevoX = jugadorX + dx;
            nuevoX = Math.max(0, Math.min(nuevoX, MAP_WIDTH - TAMANO_JUGADOR));
            if (!verificarColisiones(new Rectangle(nuevoX, jugadorY, TAMANO_JUGADOR, TAMANO_JUGADOR))) {
                jugadorX = nuevoX;
            }
        }

        // Movimiento Y
        int dy = 0;
        if (inputHandler.upPressed)
            dy -= VELOCIDAD;
        else if (inputHandler.downPressed)
            dy += VELOCIDAD;

        if (dy != 0) {
            String tempDir = (dy < 0) ? "arriba" : "abajo";
            if (!inputHandler.preferHorizontal || (dx == 0)) {
                if (!direccionActual.equals(tempDir)) {
                    direccionActual = tempDir;
                    frameIndex = 0;
                }
            }
            int nuevoY = jugadorY + dy;
            nuevoY = Math.max(150, Math.min(nuevoY, MAP_HEIGHT - TAMANO_JUGADOR - 10));
            if (!verificarColisiones(new Rectangle(jugadorX, nuevoY, TAMANO_JUGADOR, TAMANO_JUGADOR))) {
                jugadorY = nuevoY;
            }
        }
    }

    private void actualizarEstadoAnimacion() {
        if (!moviendose) {
            frameIndex = 0; // Idle frame
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= FRAME_DELAY_MS) {
            List<Image> frames = resources.getCharacterFrames(nombrePersonaje, direccionActual);
            if (frames != null && !frames.isEmpty()) {
                // Advance frame (start at index 1 for walking loop if frame 0 is idle)
                // If only 2 frames exist, cycle between 0 and 1.
                // If 3 frames exist: 0(idle), then loop 1, 2...
                int totalFrames = frames.size();
                if (totalFrames > 1) {
                    frameIndex = (frameIndex + 1) % totalFrames;
                    // If frameIndex becomes 0, and we want 0 to be ONLY idle:
                    // frameIndex = 1 + (frameIndex % (totalFrames - 1)); // This depends on sprite
                    // set
                }
            }
            lastFrameTime = now;
        }
    }

    private void verificarInteraccion() {
        if (inputHandler.ePressed) {
            if (getHitboxJugador().intersects(zonaPuerta)) {
                // Reset flag for E to avoid loop
                inputHandler.ePressed = false;
                cambiarPantalla("COMBATE");
            }
        }
    }

    public Image getCurrentSprite() {
        return resources.getCharacterSprite(nombrePersonaje, direccionActual, frameIndex);
    }

    private boolean verificarColisiones(Rectangle hitbox) {
        for (Rectangle pared : paredes) {
            if (hitbox.intersects(pared))
                return true;
        }
        return false;
    }

    private void cambiarPantalla(String pantalla) {
        if (mainFrame instanceof equipoilerntale.view.MainFrame) {
            ((equipoilerntale.view.MainFrame) mainFrame).cambiarPantalla(pantalla);
        }
    }

    // ============ GETTERS ============
    public int getJugadorX() {
        return jugadorX;
    }

    public int getJugadorY() {
        return jugadorY;
    }

    public String getDireccionActual() {
        return direccionActual;
    }

    public Rectangle getHitboxJugador() {
        return new Rectangle(jugadorX, jugadorY, TAMANO_JUGADOR, TAMANO_JUGADOR);
    }

    public List<Rectangle> getParedes() {
        return paredes;
    }

    public Rectangle getZonaPuerta() {
        return zonaPuerta;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void dispose() {
        resources.dispose();
        paredes.clear();
    }
}
