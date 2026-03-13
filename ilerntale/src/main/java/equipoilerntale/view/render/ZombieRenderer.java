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
        // drawHealthBar(ctx, zombie); // Eliminado a petición para que no se vea en el mapa
    }
}
