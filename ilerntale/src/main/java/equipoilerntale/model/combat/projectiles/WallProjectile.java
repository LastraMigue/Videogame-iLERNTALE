package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

/**
 * Representa una pared que actúa como un proyectil estático o móvil.
 * A diferencia de otros proyectiles, no desaparece al impactar con el jugador (deactivateOnHit = false).
 */
public class WallProjectile extends ProjectileModel {

    /**
     * Constructor para muros del laberinto.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param width Ancho del muro.
     * @param height Alto del muro.
     */
    public WallProjectile(int x, int y, int width, int height) {
        super(x, y, width, height, 0, 0, 10);
        this.deactivateOnHit = false;
    }

    @Override
    public void mover() {
        x += dx;
        y += dy;
    }
}
