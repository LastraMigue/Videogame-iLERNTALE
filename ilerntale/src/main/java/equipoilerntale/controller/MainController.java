package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.PausePanel;
import javax.swing.JPanel;

public class MainController implements Runnable {

    private MainFrame mainFrame;
    private Thread gameThread;
    private boolean running;
    private final int FPS = 60;

    public MainController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
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
                draw();
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

}