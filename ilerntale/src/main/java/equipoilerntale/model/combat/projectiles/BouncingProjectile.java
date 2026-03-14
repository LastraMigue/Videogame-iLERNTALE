package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

/**
 * Proyectil que rebota en los bordes de la arena de combate.
 */
public class BouncingProjectile extends ProjectileModel {

    /**
     * Constructor para proyectiles rebotadores.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param size Tamaño del proyectil.
     * @param dx Velocidad inicial en X.
     * @param dy Velocidad inicial en Y.
     * @param type Tipo de proyectil.
     */
    public BouncingProjectile(int x, int y, int size, int dx, int dy, int type) {
        super(x, y, size, dx, dy, type);
    }

    @Override
    public void mover() {
        x += dx;
        y += dy;

        if (x <= 200 || x >= 800 - size) {
            dx = dx * -1;
        }
        if (y <= 240 || y >= 490 - size) {
            dy = dy * -1;
        }
    }
}
