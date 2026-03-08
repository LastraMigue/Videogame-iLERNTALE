package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.model.map.GameResources;
import java.util.logging.Logger;

public class MainController implements Runnable {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private MainFrame mainFrame;
    private Thread gameThread;
    private boolean running;
    private final int FPS = 60;

    // Controladores del juego
    private ExplorationController explorationController;

    public MainController(MainFrame mainFrame, String personaje) {
        this.mainFrame = mainFrame;
        GameResources.initialize(32);
        inicializarControladores(personaje);
    }

    private void inicializarControladores(String personaje) {
        LOG.info("Inicializando controladores para: " + personaje);

        // Inicializar ExplorationController con el personaje seleccionado
        explorationController = new ExplorationController(mainFrame, personaje);

        LOG.info("Controladores inicializados");
    }

    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        LOG.info("Game thread iniciado");
    }

    public void stopGameThread() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            LOG.severe("Error al detener el game thread: " + e.getMessage());
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

            // El renderizado lo maneja Swing automáticamente
        }

        LOG.info("Game thread detenido");
    }

    private void update() {
        // Actualizar Exploration si está activo
        if (explorationController != null) {
            explorationController.update();
        }
    }

    // ============ GETTERS ============

    public ExplorationController getExplorationController() {
        return explorationController;
    }

    public boolean isRunning() {
        return running;
    }

    // ============ CICLO DE VIDA ============

    public void dispose() {
        LOG.info("MainController dispose");
        stopGameThread();

        if (explorationController != null) {
            explorationController.dispose();
            explorationController = null;
        }
    }
}
