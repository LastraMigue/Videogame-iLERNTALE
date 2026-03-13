package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.screens.CombatPanel;
import javax.swing.JPanel;

/**
 * CONTROLADOR PRINCIPAL DEL JUEGO.
 * GESTIONA EL HILO DEL JUEGO Y LA ACTUALIZACIÓN DE LOS CONTROLADORES
 * ESPECÍFICOS.
 */
public class MainController implements Runnable {

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
        // INICIALIZAR EXPLORATIONMANAGER CON EL PERSONAJE SELECCIONADO
        explorationManager = new ExplorationManager(mainFrame, personaje);
    }

    /**
     * INICIA EL HILO DE EJECUCIÓN DEL JUEGO.
     */
    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
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
            e.printStackTrace();
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
    }

    // Enum para los estados Jugando o en Pausa
    public enum GameState {
        PLAYING,
        PAUSED
    }

    // Estado en pausa
    private GameState state = GameState.PLAYING;

    /**
     * ACTUALIZA LA LÓGICA DE JUEGO SEGÚN EL ESTADO ACTUAL Y LA PANTALLA VISIBLE.
     * ESTE MÉTODO ES LLAMADO DESDE EL HILO DEL JUEGO A UN RITMO DE 60 FPS.
     */
    private void update() {
        // SI EL JUEGO ESTÁ EN PAUSA, NO SE ACTUALIZA NADA (EL TIEMPO SE DETIENE)
        if (state == GameState.PAUSED) {
            return;
        }

        JPanel panelActual = mainFrame.getPanelActual();

        // 1. ACTUALIZAR LÓGICA DE COMBATE (SI CORRESPONDE)
        if (panelActual instanceof CombatPanel) {
            ((CombatPanel) panelActual).updateCombat();
        }

        // 2. ACTUALIZAR LÓGICA DE EXPLORACIÓN
        // (ExplorationManager ya filtra internamente si debe correr o no mediante su
        // flag 'active')
        if (explorationManager != null) {
            explorationManager.update();
        }
    }

    /**
     * PAUSA LA EJECUCIÓN DE LA LÓGICA DEL JUEGO.
     */
    public void pauseGame() {
        state = GameState.PAUSED;
    }

    /**
     * REANUDA LA EJECUCIÓN DE LA LÓGICA DEL JUEGO.
     */
    public void resumeGame() {
        state = GameState.PLAYING;
    }

    /**
     * CAMBIA ENTRE EL ESTADO DE PAUSA Y EL DE JUEGO.
     */
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
        stopGameThread();

        if (explorationManager != null) {
            explorationManager.cleanup();
            explorationManager = null;
        }
    }
}
