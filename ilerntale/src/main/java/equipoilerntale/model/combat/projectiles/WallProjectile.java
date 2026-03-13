package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

/**
 * Representa una pared que actúa como un proyectil estático o móvil
 * pero que no desaparece al impactar.
 */
public class WallProjectile extends ProjectileModel {

    public WallProjectile(int x, int y, int width, int height) {
        super(x, y, width, height, 0, 0, 10); // Tipo 10 para paredes
        this.deactivateOnHit = false;
    }

    @Override
    public void mover() {
        // Por defecto las paredes del laberinto suelen ser estáticas,
        // pero heredan la capacidad de moverse si se les asigna dx/dy.
        x += dx;
        y += dy;
    }
}
