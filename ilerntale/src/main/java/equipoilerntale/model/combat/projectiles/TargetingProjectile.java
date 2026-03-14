package equipoilerntale.model.combat.projectiles;

import equipoilerntale.model.combat.ProjectileModel;

/**
 * Proyectil que se dirige hacia una posición objetivo (normalmente la del jugador) al ser creado.
 * Una vez definida la trayectoria, sigue una línea recta.
 */
public class TargetingProjectile extends ProjectileModel {
    /** Posición X en alta precisión. */
    private double currentX;
    /** Posición Y en alta precisión. */
    private double currentY;
    /** Incremento X calculado para alcanzar el objetivo. */
    private double deltaX;
    /** Incremento Y calculado para alcanzar el objetivo. */
    private double deltaY;
    /** Ancho de pantalla para límites. */
    private int screenWidth = 1000;
    /** Alto de pantalla para límites. */
    private int screenHeight = 600;

    /**
     * Constructor para proyectiles con apuntado inicial.
     * 
     * @param startX Posición inicial X.
     * @param startY Posición inicial Y.
     * @param targetX Posición objetivo X.
     * @param targetY Posición objetivo Y.
     * @param size Tamaño del proyectil.
     * @param speed Velocidad de desplazamiento.
     * @param type Tipo de proyectil.
     */
    public TargetingProjectile(int startX, int startY, int targetX, int targetY, int size, int speed, int type) {
        super(startX, startY, size, 0, 0, type);
        this.currentX = startX;
        this.currentY = startY;

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

        if (x < -100 || x > screenWidth + 100 || y < -100 || y > screenHeight + 100) {
            setActive(false);
        }
    }
}
