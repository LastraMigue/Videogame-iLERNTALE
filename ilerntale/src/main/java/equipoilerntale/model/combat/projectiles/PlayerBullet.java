package equipoilerntale.model.combat.projectiles;

public class PlayerBullet extends StraightProjectile {
    public PlayerBullet(int x, int y, int size, int speed) {
        // dx=0 (no se mueve a los lados), dy=-speed (va hacia arriba)
        // type=99 para diferenciarlo de las balas enemigas
        super(x, y, size, 0, -speed, 99);
    }
}
