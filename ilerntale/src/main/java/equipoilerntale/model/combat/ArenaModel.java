package equipoilerntale.model.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaModel {
    private MouseModel mouse;
    private List<ProjectileModel> projectiles;
    private int nextType = 0;
    private int goodCollisions = 0;
    private int badCollisions = 0;

    public void startCombat() {
        int x = 200, y = 240, width = 600, height = 250;
        int mouseStartX = x + (width / 2) - 15;
        int mouseStartY = y + (height / 2) - 15;

        mouse = new MouseModel(mouseStartX, mouseStartY);
        projectiles = new ArrayList<>();
        goodCollisions = 0;
        badCollisions = 0;
    }

    public void stopCombat() {
        mouse = null;
        projectiles = null;
    }

    public void spawnProjectile() {
        if (projectiles.size() >= 12)
            return;

        Random rand = new Random();
        int size = rand.nextInt(21) + 15;
        int speed = rand.nextInt(2) + 2;
        int borde = rand.nextInt(4);

        int type = nextType;
        nextType = (nextType == 0) ? 1 : 0;

        int x = 0, y = 0, dx = 0, dy = 0;

        switch (borde) {
            case 0:
                x = 200 + rand.nextInt(600 - size);
                y = 240;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = speed;
                break;
            case 1:
                x = 200 + rand.nextInt(600 - size);
                y = 490 - size;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = -speed;
                break;
            case 2:
                x = 200;
                y = 240 + rand.nextInt(250 - size);
                dx = speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
            case 3:
                x = 800 - size;
                y = 240 + rand.nextInt(250 - size);
                dx = -speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
        }

        projectiles.add(new ProjectileModel(x, y, size, dx, dy, type));
    }

    public void actualizarProjectiles() {
        for (ProjectileModel proj : projectiles) {
            proj.mover();
            if (proj.getX() <= 200 || proj.getX() >= 800 - proj.getSize())
                proj.setDx(proj.getDx() * -1);
            if (proj.getY() <= 240 || proj.getY() >= 490 - proj.getSize())
                proj.setDy(proj.getDy() * -1);
        }
    }

    public void intentarMoverMouse(int dx, int dy) {
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
        if (projectiles == null || projectiles.isEmpty())
            return false;
        for (ProjectileModel proj : projectiles) {
            if (proj.isActive()) {
                return false;
            }
        }
        return true;
    }
}