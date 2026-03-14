package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.screens.CombatPanel;
import javax.swing.JPanel;

/**
 * Controlador principal del juego.
 * Gestiona el hilo principal del juego (Game Loop) y la actualización de los controladores específicos.
 */
public class MainController implements Runnable {

    /** Marco principal de la aplicación. */
    private MainFrame mainFrame;
    /** Hilo de ejecución del juego. */
    private Thread gameThread;
    /** Indica si el bucle principal del juego está en funcionamiento. */
    private boolean running;
    /** Fotogramas por segundo (FPS) del bucle de juego. */
    private final int FPS = 60;

    /** Gestor de la fase de exploración. */
    private ExplorationManager explorationManager;

    /**
     * Constructor del controlador principal.
     * 
     * @param mainFrame Referencia al marco principal de la interfaz.
     * @param personaje Identificador del personaje seleccionado.
     */
    public MainController(MainFrame mainFrame, String personaje) {
        this.mainFrame = mainFrame;
        inicializarControladores(personaje);
    }

    /**
     * Inicializa los controladores específicos del juego.
     * 
     * @param personaje Nombre del personaje para configurar sus recursos.
     */
    private void inicializarControladores(String personaje) {
        explorationManager = new ExplorationManager(mainFrame, personaje);
    }

    /**
     * Inicia el hilo de ejecución del juego si no está ya corriendo.
     */
    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Detiene el hilo de ejecución del juego de forma segura.
     */
    public void stopGameThread() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Bucle principal del juego que controla la tasa de actualización mediante el uso de delta-timing.
     */
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                delta--;
            }
        }
    }

    /**
     * Enumeración de los posibles estados lógicos del juego.
     */
    public enum GameState {
        /** El juego se está ejecutando normalmente. */
        PLAYING,
        /** El juego está pausado, la lógica no se actualiza. */
        PAUSED
    }

    /** Estado actual del juego. */
    private GameState state = GameState.PLAYING;

    /**
     * Actualiza la lógica de juego según el estado actual y la pantalla visible.
     * Se ejecuta periódicamente según la tasa de FPS.
     */
    private void update() {
        if (state == GameState.PAUSED) {
            return;
        }

        JPanel panelActual = mainFrame.getPanelActual();

        if (panelActual instanceof CombatPanel) {
            ((CombatPanel) panelActual).updateCombat();
        }

        if (explorationManager != null) {
            explorationManager.update();
        }
    }

    /**
     * Establece el estado del juego como pausado.
     */
    public void pauseGame() {
        state = GameState.PAUSED;
    }

    /**
     * Establece el estado del juego como en ejecución.
     */
    public void resumeGame() {
        state = GameState.PLAYING;
    }

    /**
     * Alterna entre el estado de pausa y ejecución.
     */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            pauseGame();
        } else {
            resumeGame();
        }
    }

    /**
     * Obtiene el estado actual del juego.
     * 
     * @return El GameState actual.
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Obtiene el gestor de exploración activo.
     * 
     * @return Instancia de ExplorationManager.
     */
    public ExplorationManager getExplorationManager() {
        return explorationManager;
    }

    /**
     * Indica si el hilo del juego está activo.
     * 
     * @return true si el bucle principal está corriendo.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Detiene el hilo y limpia los recursos de los gestores dependientes.
     */
    public void dispose() {
        stopGameThread();

        if (explorationManager != null) {
            explorationManager.cleanup();
            explorationManager = null;
        }
    }
}
