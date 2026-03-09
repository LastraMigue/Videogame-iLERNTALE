package equipoilerntale.view.render;

import java.awt.*;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.service.AssetService;

/**
 * ESPECIALISTA EN RENDERIZAR ZOMBIES.
 */
public class ZombieRenderer {
    /**
     * DIBUJA AL ZOMBIE EN EL CONTEXTO ESPECIFICADO.
     * SOLO DIBUJA SI EL ZOMBIE ESTÁ VIVO.
     */
    public void drawZombie(RenderContext ctx, Zombie zombie) {
        if (!zombie.isAlive())
            return;
        Image sprite = AssetService.getInstance().getZombieSprite(zombie.getType(), zombie.getDirection(),
                zombie.getFrameIndex());
        Graphics2D g2d = ctx.getGraphics();
        if (sprite != null) {
            g2d.drawImage(sprite, zombie.getX(), zombie.getY(), zombie.getSize(), zombie.getSize(), null);
        }
        drawHealthBar(ctx, zombie);
    }

    private void drawHealthBar(RenderContext ctx, Zombie zombie) {
        Graphics2D g2d = ctx.getGraphics();
        int x = zombie.getX();
        int y = zombie.getY() - 10;
        int width = zombie.getSize();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, width, 5);
        float ratio = (float) zombie.getHealth() / Zombie.MAX_HEALTH;
        g2d.setColor(ratio > 0.5 ? Color.GREEN : Color.RED);
        g2d.fillRect(x + 1, y + 1, (int) ((width - 2) * ratio), 3);
    }
}
