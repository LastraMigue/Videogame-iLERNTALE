package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

public class StraightProjectile extends ProjectileModel {
    private int screenWidth = 1000, screenHeight = 600;

    public StraightProjectile(int x, int y, int size, int dx, int dy, int type) {
        super(x, y, size, dx, dy, type);
    }

    @Override
    public void mover() {
        x += dx;
        y += dy;

        // Despawn bullet if it goes too far out of bounds
        if (x < -100 || x > screenWidth + 100 || y < -100 || y > screenHeight + 100) {
            setActive(false);
        }
    }
}
