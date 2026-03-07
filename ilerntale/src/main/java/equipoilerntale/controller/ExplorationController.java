package equipoilerntale.controller;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import equipoilerntale.view.screens.SpriteManager;

/**
 * Senior Architect Refactor: ExplorationController
 * Centralizes movement, collision and animation state.
 */
public class ExplorationController {

    private static final Logger LOG = Logger.getLogger(ExplorationController.class.getName());

    // ============ CONFIGURACIÓN ============
    public static final boolean DEBUG_MODE = false;
    public static final int ANCHO_PANEL = 1000;
    public static final int ALTO_PANEL = 600;

    // ---------- Jugador ----------
    public static final int VELOCIDAD = 5;
    public static final int TAMANO_JUGADOR = 100;
    public static final int INICIO_JUGADOR_X = 30;
    public static final int INICIO_JUGADOR_Y = 300;

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
    private final SpriteManager spriteManager;
    private final Object mainFrame;

    // ---------- Zonas de colisión ----------
    private final List<Rectangle> paredes = new ArrayList<>();
    private Rectangle zonaPuerta;

    public ExplorationController(Object mainFrame, String nombrePersonaje) {
        this.mainFrame = mainFrame;
        this.inputHandler = new InputHandler();
        this.spriteManager = new SpriteManager(nombrePersonaje, TAMANO_JUGADOR);

        inicializarColisiones();
        LOG.info("ExplorationController (Senior Refactor) para " + nombrePersonaje);
    }

    private void inicializarColisiones() {
        // Paredes y obstáculos
        paredes.add(new Rectangle(0, 0, ANCHO_PANEL, 300)); // Hitbox superior
        paredes.add(new Rectangle(0, 0, 10, ALTO_PANEL)); // Pared Izq
        paredes.add(new Rectangle(ANCHO_PANEL - 10, 0, 10, ALTO_PANEL)); // Pared Der
        paredes.add(new Rectangle(0, 590, ANCHO_PANEL, 10)); // Hitbox inferior

        zonaPuerta = new Rectangle(630, 300, 70, 70);
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
            // Update direction if not currently moving vertically or prioritizing
            // horizontal
            if (inputHandler.preferHorizontal || (!inputHandler.upPressed && !inputHandler.downPressed)) {
                if (!direccionActual.equals(tempDir)) {
                    direccionActual = tempDir;
                    frameIndex = 0; // Reset animation on direction change
                }
            }
            if (!verificarColisiones(new Rectangle(jugadorX + dx, jugadorY, TAMANO_JUGADOR, TAMANO_JUGADOR))) {
                jugadorX += dx;
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
            if (!verificarColisiones(new Rectangle(jugadorX, jugadorY + dy, TAMANO_JUGADOR, TAMANO_JUGADOR))) {
                jugadorY += dy;
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
            List<Image> frames = spriteManager.getFrames(direccionActual);
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
        return spriteManager.getSprite(direccionActual, frameIndex);
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
        spriteManager.dispose();
        paredes.clear();
    }
}
