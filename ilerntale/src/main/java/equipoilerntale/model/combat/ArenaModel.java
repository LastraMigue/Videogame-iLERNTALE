package equipoilerntale.model.combat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaModel {
    private MouseModel mouse;
    private List<ProjectileModel> projectiles;
    private int goodCollisions = 0;
    private int badCollisions = 0;


    public void startCombat() {
        initMouseCenter();
        projectiles = new CopyOnWriteArrayList<>();
        goodCollisions = 0;
        badCollisions = 0;
    }

    // 
    public void initMouseCenter() {
        int x = 200, y = 240, width = 600, height = 250;
        int mouseStartX = x + (width / 2) - 15;
        int mouseStartY = y + (height / 2) - 15;
        mouse = new MouseModel(mouseStartX, mouseStartY);
    }

    public void stopCombat() {
        mouse = null;
        projectiles = null;
    }

    public void addProjectile(ProjectileModel projectile) {
        if (projectiles != null) {
            projectiles.add(projectile);
        }
    }

    public void clearProjectiles() {
        if (projectiles != null) {
            projectiles.clear();
        }
    }

    public void updateProjectiles() {
        if (projectiles == null)
            return;
        for (ProjectileModel proj : projectiles) {
            proj.mover();
        }
    }

    public void intentarMoverMouse(int dx, int dy) {
        if (mouse == null)
            return;
        int fX = mouse.getX() + (dx * 5);
        int fY = mouse.getY() + (dy * 5);
        if (fX >= 200 && fX <= 800 - mouse.getAncho())
            mouse.setX(fX);
        if (fY >= 240 && fY <= 490 - mouse.getAlto())
            mouse.setY(fY);
    }

    public MouseModel getMouse() {
        return mouse;
    }

    public List<ProjectileModel> getProjectiles() {
        return projectiles;
    }

    public void checkCollisions() {
        if (mouse == null || projectiles == null)
            return;

        for (ProjectileModel proj : projectiles) {
            if (!proj.isActive())
                continue;

            if (mouse.getBounds().intersects(proj.getBounds())) {
                proj.setActive(false);
                if (proj.getType() == 1) {
                    goodCollisions++;
                } else {
                    badCollisions++;
                }
            }
        }
    }

    public int getGoodCollisions() {
        return goodCollisions;
    }

    public int getBadCollisions() {
        return badCollisions;
    }

    public boolean allBulletsHit() {
        if (projectiles == null || projectiles.size() < 12)
            return false;
        for (ProjectileModel proj : projectiles) {
            if (proj.isActive()) {
                return false;
            }
        }
        return true;
    }
}