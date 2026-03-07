package equipoilerntale.controller;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.screens.CombatPanel;
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

    private void update() {
        // Obtenemos el panel activo desde MainFrame (asegúrate de tener este método en
        // MainFrame)
        JPanel panelActual = mainFrame.getPanelActual();

        if (panelActual instanceof CombatPanel) {
            ((CombatPanel) panelActual).updateCombat();
        }
    }

    private void draw() {
        mainFrame.repaint();
    }
}