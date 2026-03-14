package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

/**
 * Proyectil que se mueve en línea recta con velocidad constante.
 * Se desactiva automáticamente si sale considerablemente de los límites de la pantalla.
 */
public class StraightProjectile extends ProjectileModel {
    /** Ancho de pantalla para límites de despawn. */
    private int screenWidth = 1000;
    /** Alto de pantalla para límites de despawn. */
    private int screenHeight = 600;

    /**
     * Constructor para proyectiles rectos.
     * 
     * @param x Posición X.
     * @param y Posición Y.
     * @param size Tamaño.
     * @param dx Velocidad X.
     * @param dy Velocidad Y.
     * @param type Tipo.
     */
    public StraightProjectile(int x, int y, int size, int dx, int dy, int type) {
        super(x, y, size, dx, dy, type);
    }

    @Override
    public void mover() {
        x += dx;
        y += dy;

        if (x < -100 || x > screenWidth + 100 || y < -100 || y > screenHeight + 100) {
            setActive(false);
        }
    }
}
