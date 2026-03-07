package equipoilerntale.view.screens;

import java.awt.*;
import javax.swing.*;
import equipoilerntale.controller.ExplorationController;
import equipoilerntale.view.MainFrame;

/**
 * Senior Architect Refactor: ExploredPanel
 * Pure view class. Only renders data from the controller.
 */
public class ExploredPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ExplorationController controller;
    private Image background;

    public ExploredPanel(MainFrame mainFrame, String personaje, ExplorationController controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setPreferredSize(new Dimension(ExplorationController.ANCHO_PANEL, ExplorationController.ALTO_PANEL));
        setFocusable(true);
        addKeyListener(controller.getInputHandler());

        cargarRecursos("/mapa/Pasillo1.png");
    }

    public void cargarRecursos(String rutaFondo) {
        // We reuse the controller's sprite manager if we want, or use a temporary one
        // For backgrounds, we just load them directly here or via a utility
        try {
            java.net.URL url = getClass().getResource(rutaFondo);
            if (url != null) {
                background = new ImageIcon(url).getImage().getScaledInstance(
                        ExplorationController.ANCHO_PANEL,
                        ExplorationController.ALTO_PANEL,
                        Image.SCALE_SMOOTH);
            }
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw Background
        if (background != null) {
            g2d.drawImage(background, 0, 0, null);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Objects & Players
        if (controller != null) {
            renderizarObjetos(g2d);
            renderizarJugador(g2d);

            if (ExplorationController.DEBUG_MODE) {
                renderizarDebug(g2d);
            }
        }
    }

    private void renderizarObjetos(Graphics2D g2d) {
        // Draw Aula 124 Label
        Rectangle door = controller.getZonaPuerta();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "Aula 124";
        int width = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, door.x + (door.width / 2) - (width / 2), door.y - 10);
    }

    private void renderizarJugador(Graphics2D g2d) {
        Image sprite = controller.getCurrentSprite();
        int x = controller.getJugadorX();
        int y = controller.getJugadorY();

        if (sprite != null) {
            g2d.drawImage(sprite, x, y, null);
        } else {
            // Fallback square
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, ExplorationController.TAMANO_JUGADOR, ExplorationController.TAMANO_JUGADOR);
        }
    }

    private void renderizarDebug(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        for (Rectangle r : controller.getParedes()) {
            g2d.draw(r);
        }
        g2d.setColor(Color.BLUE);
        g2d.draw(controller.getHitboxJugador());
        g2d.draw(controller.getZonaPuerta());
    }

    public void requestRender() {
        repaint();
    }
}
