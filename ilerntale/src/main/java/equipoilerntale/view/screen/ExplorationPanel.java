package equipoilerntale.view.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.BasicStroke;
import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.service.AssetService;
import equipoilerntale.view.MainFrame;
import equipoilerntale.view.render.*;

/**
 * PANEL DE ESCENA PARA LA EXPLORACIÓN.
 */
public class ExplorationPanel extends JPanel {
    private final MainFrame mainFrame;
    private final ExplorationManager manager;
    private final MapRenderer mapRenderer = new MapRenderer();
    private final PlayerRenderer playerRenderer = new PlayerRenderer();
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    private Image background;
    private int cameraX, cameraY;

    /**
     * CONSTRUCTOR DEL PANEL DE EXPLORACIÓN.
     */
    public ExplorationPanel(MainFrame mainFrame, String characterName, ExplorationManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setPreferredSize(new Dimension(GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA));
        setFocusable(true);
        addKeyListener(manager.getInputHandler());
        this.background = AssetService.getInstance().loadBackground("/mapa/Pasillo2.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (manager == null)
            return;
        Graphics2D g2d = (Graphics2D) g;
        calculateCamera();
        RenderContext ctx = new RenderContext(g2d, getWidth(), getHeight(), cameraX, cameraY);
        ctx.translateCamera();
        mapRenderer.drawBackground(ctx, background);
        
        // DIBUJA LOS MUROS SI EL MODO DEBUG ESTÁ ACTIVADO
        if (manager.isDebugMurosVisibles()) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            for (Rectangle wall : manager.getWalls()) {
                g2d.draw(wall);
            }
            // DIBUJA EL ÁREA DE LA PUERTA EN VERDE
            g2d.setColor(Color.GREEN);
            g2d.draw(manager.getDoorArea());
        }
        
        mapRenderer.drawZoneLabel(ctx, manager.getDoorArea(), "Aula 124");
        playerRenderer.drawPlayer(ctx, manager.getPlayerCurrentSprite(), manager.getPlayer());
        for (Zombie z : manager.getActiveZombies())
            zombieRenderer.drawZombie(ctx, z);
        ctx.restoreCamera();
    }

    private void calculateCamera() {
        cameraX = Math.max(0,
                Math.min(manager.getPlayer().getX() - getWidth() / 2, GameSettings.MAP_WIDTH - getWidth()));
        cameraY = Math.max(0,
                Math.min(manager.getPlayer().getY() - getHeight() / 2, GameSettings.MAP_HEIGHT - getHeight()));
    }

    public void requestRender() {
        if (isShowing())
            repaint();
    }

    public void dispose() {
        if (manager != null)
            manager.deactivate();
    }

    public void reset() {
        if (manager != null)
            manager.activate();
    }
}
