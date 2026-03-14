package equipoilerntale.model.combat.projectiles;

/**
 * Representa un proyectil disparado por el jugador en minijuegos como Shooter.
 * Se mueve siempre hacia arriba (eje Y negativo).
 */
public class PlayerBullet extends StraightProjectile {
    /**
     * Constructor para la bala del jugador.
     * 
     * @param x Posición X inicial.
     * @param y Posición Y inicial.
     * @param size Tamaño de la bala.
     * @param speed Velocidad de ascenso.
     */
    public PlayerBullet(int x, int y, int size, int speed) {
        super(x, y, size, 0, -speed, 99);
    }
}
