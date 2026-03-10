package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

public class BouncingProjectile extends ProjectileModel {

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
