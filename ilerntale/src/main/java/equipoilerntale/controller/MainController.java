package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.PausePanel;
import javax.swing.JPanel;
import equipoilerntale.service.AssetService;
import java.util.logging.Logger;

/**
 * CONTROLADOR PRINCIPAL DEL JUEGO.
 * GESTIONA EL HILO DEL JUEGO Y LA ACTUALIZACIÓN DE LOS CONTROLADORES
 * ESPECÍFICOS.
 */
public class MainController implements Runnable {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private MainFrame mainFrame;
    private Thread gameThread;
    private boolean running;
    private final int FPS = 60;

    // CONTROLADORES DE FASES DEL JUEGO
    private ExplorationManager explorationManager;

    /**
     * CONSTRUCTOR DEL CONTROLADOR PRINCIPAL.
     * INICIALIZA LOS CONTROLADORES ESPECÍFICOS SEGÚN EL PERSONAJE SELECCIONADO.
     */
    public MainController(MainFrame mainFrame, String personaje) {
        this.mainFrame = mainFrame;
        // NOTA: NO llamar initialize() aquí — borraría los sprites cargados por
        // ExplorationManager
        inicializarControladores(personaje);
    }

    /**
     * INICIALIZA LOS CONTROLADORES ESPECÍFICOS DEL JUEGO.
     * CREA EL EXPLORATIONMANAGER CON EL PERSONAJE SELECCIONADO.
     */
    private void inicializarControladores(String personaje) {
        LOG.info("INICIALIZANDO CONTROLADORES PARA: " + personaje);

        // INICIALIZAR EXPLORATIONMANAGER CON EL PERSONAJE SELECCIONADO
        explorationManager = new ExplorationManager(mainFrame, personaje);

        LOG.info("CONTROLADORES INICIALIZADOS");
    }

    /**
     * INICIA EL HILO DE EJECUCIÓN DEL JUEGO.
     */
    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        LOG.info("HILO DEL JUEGO INICIADO");
    }

    /**
     * DETIENE EL HILO DE EJECUCIÓN DEL JUEGO.
     */
    public void stopGameThread() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            LOG.severe("ERROR AL DETENER EL HILO DEL JUEGO: " + e.getMessage());
        }
    }

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

        LOG.info("HILO DEL JUEGO DETENIDO");
    }

    // Enum para los estados Jugando o en Pausa
    public enum GameState {
        PLAYING,
        PAUSED
    }

    // Estado en pausa
    private GameState state = GameState.PLAYING;

    private void update() {
        // Obtenemos el panel activo desde MainFrame (asegúrate de tener este método en
        // MainFrame)
        JPanel panelActual = mainFrame.getPanelActual();

        if (panelActual instanceof CombatPanel) {
            ((CombatPanel) panelActual).updateCombat();
        }

        // Aquí se llamará a los sub-controladores (Exploration, Combat, etc.)

        // Subcontrolador de estado panel/menu (Jugando o en Pausa)
        if (state == GameState.PAUSED) {
            return; // Si está en pausa no se actualiza el juego
        }

        // ExplorationManager filtra internamente si está activo o no
        if (explorationManager != null) {
            explorationManager.update();
        }
    }

    // Sección para controlar EN JUEGO y EN PAUSA
    // Puede que haya que meterlo en el método Update

    // Métodos para que el juego esté "en juego" o "en pausa"

    public void pauseGame() {
        state = GameState.PAUSED;

    }

    public void resumeGame() {
        state = GameState.PLAYING;
    }

    // Seleccionar modo "Pausa" si está jugando o "Reanudar" si está pausado
    public void togglePause() {
        if (state == GameState.PLAYING) {
            pauseGame();
        } else {
            resumeGame();

        }
    }

    public GameState getGameState() {
        return state;
    }

    private void draw() {
        mainFrame.repaint();
    }

    // ============ GETTERS ============

    /**
     * OBTIENE EL GESTOR DE EXPLORACIÓN ACTUAL.
     */
    public ExplorationManager getExplorationManager() {
        return explorationManager;
    }

    /**
     * INDICA SI EL HILO DEL JUEGO ESTÁ EN EJECUCIÓN.
     */
    public boolean isRunning() {
        return running;
    }

    // ============ CICLO DE VIDA ============

    /**
     * LIBERA LOS RECURSOS DEL CONTROLADOR.
     */
    public void dispose() {
        LOG.info("MAINCONTROLLER DISPOSE");
        stopGameThread();

        if (explorationManager != null) {
            explorationManager.cleanup();
            explorationManager = null;
        }
    }
}
