package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

public class TargetingProjectile extends ProjectileModel {
    private double currentX, currentY;
    private double deltaX, deltaY;
    private int screenWidth = 1000, screenHeight = 600;

    public TargetingProjectile(int startX, int startY, int targetX, int targetY, int size, int speed, int type) {
        super(startX, startY, size, 0, 0, type);
        this.currentX = startX;
        this.currentY = startY;

        // Calculate moving vector towards the target
        double dx = targetX - startX;
        double dy = targetY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            this.deltaX = (dx / distance) * speed;
            this.deltaY = (dy / distance) * speed;
        } else {
            this.deltaX = speed;
            this.deltaY = 0;
        }
    }

    @Override
    public void mover() {
        currentX += deltaX;
        currentY += deltaY;

        this.x = (int) Math.round(currentX);
        this.y = (int) Math.round(currentY);

        // Despawn bullet if it goes too far out of bounds
        if (x < -100 || x > screenWidth + 100 || y < -100 || y > screenHeight + 100) {
            setActive(false);
        }
    }
}
