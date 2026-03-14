package equipoilerntale.view.render;

import java.awt.*;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.service.AssetService;

/**
 * Especialista en renderizar enemigos tipo Zombie en el mapa.
 */
public class ZombieRenderer {
    /**
     * Dibuja un zombie en el contexto de renderizado utilizando su sprite animado correspondiente.
     * Solo realiza el dibujado si el zombie está vivo.
     * 
     * @param ctx    Contexto de renderizado.
     * @param zombie Instancia del zombie a dibujar.
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
    }
}
