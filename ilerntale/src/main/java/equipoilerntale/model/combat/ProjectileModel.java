package equipoilerntale.model.combat;

import java.awt.Rectangle;

public abstract class ProjectileModel {
    protected int x, y;
    protected int size;
    protected int width, height;
    protected int dx, dy;
    protected int type;
    protected boolean active = true;
    protected boolean deactivateOnHit = true;

    public ProjectileModel(int x, int y, int size, int dx, int dy, int type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.width = size;
        this.height = size;
        this.dx = dx;
        this.dy = dy;
        this.type = type;
    }

    public ProjectileModel(int x, int y, int width, int height, int dx, int dy, int type) {
        this.x = x;
        this.y = y;
        this.size = Math.max(width, height);
        this.width = width;
        this.height = height;
        this.dx = dx;
        this.dy = dy;
        this.type = type;
    }

    public abstract void mover();

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDeactivateOnHit() {
        return deactivateOnHit;
    }

    public void setDeactivateOnHit(boolean deactivateOnHit) {
        this.deactivateOnHit = deactivateOnHit;
    }
}