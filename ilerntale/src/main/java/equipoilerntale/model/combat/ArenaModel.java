package equipoilerntale.model.combat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modelo que representa la arena de combate y gestiona sus entidades.
 * Controla el estado del ratón (jugador en combate), los proyectiles, las colisiones y las rondas.
 */
public class ArenaModel {
    /** Instancia del modelo del ratón (jugador). */
    private MouseModel mouse;
    /** Lista de proyectiles activos en la arena. */
    private List<ProjectileModel> projectiles;
    /** Contador de colisiones beneficiosas (item/ataque acertado). */
    private int goodCollisions = 0;
    /** Contador de colisiones perjudiciales (daño recibido). */
    private int badCollisions = 0;
    /** Número de la ronda o fase actual del minijuego. */
    private int currentRound = 1;
    /** Indica si los controles de movimiento están invertidos. */
    private boolean reversedControls = false;

    /**
     * Inicializa el estado para un nuevo combate.
     * Restablece contadores, inicializa la lista de proyectiles y centra el ratón.
     */
    public void startCombat() {
        initMouseCenter();
        projectiles = new CopyOnWriteArrayList<>();
        goodCollisions = 0;
        badCollisions = 0;
        reversedControls = false;
    }

    /**
     * Centra el ratón en el área de juego de la arena de combate.
     */
    public void initMouseCenter() {
        int x = 200, y = 240, width = 600, height = 250;
        int mouseStartX = x + (width / 2) - 15;
        int mouseStartY = y + (height / 2) - 15;
        mouse = new MouseModel(mouseStartX, mouseStartY);
    }

    /**
     * Detiene el combate y limpia las referencias a las entidades.
     */
    public void stopCombat() {
        mouse = null;
        projectiles = null;
        goodCollisions = 0;
        badCollisions = 0;
        reversedControls = false;
    }

    /**
     * Añade un proyectil a la lista de proyectiles activos.
     * 
     * @param projectile El proyectil a añadir.
     */
    public void addProjectile(ProjectileModel projectile) {
        if (projectiles != null) {
            projectiles.add(projectile);
        }
    }

    /**
     * Elimina todos los proyectiles de la arena.
     */
    public void clearProjectiles() {
        if (projectiles != null) {
            projectiles.clear();
        }
    }

    /**
     * Actualiza la posición de todos los proyectiles activos.
     */
    public void updateProjectiles() {
        if (projectiles == null)
            return;
        for (ProjectileModel proj : projectiles) {
            proj.mover();
        }
    }

    /**
     * Establece si los controles deben estar invertidos.
     * 
     * @param reversed true para invertir, false para normal.
     */
    public void setReversedControls(boolean reversed) {
        this.reversedControls = reversed;
    }

    /**
     * Comprueba si los controles están invertidos.
     * 
     * @return true si están invertidos.
     */
    public boolean isReversedControls() {
        return reversedControls;
    }

    /**
     * Intenta mover el ratón según un desplazamiento dado, respetando límites y controles invertidos.
     * 
     * @param dx Desplazamiento en el eje X.
     * @param dy Desplazamiento en el eje Y.
     */
    public void intentarMoverMouse(int dx, int dy) {
        if (mouse == null)
            return;
        
        if (reversedControls) {
            dx = -dx;
            dy = -dy;
        }
        int fX = mouse.getX() + (dx * 5);
        int fY = mouse.getY() + (dy * 5);
        if (fX >= 200 && fX <= 800 - mouse.getAncho())
            mouse.setX(fX);
        if (fY >= 240 && fY <= 490 - mouse.getAlto())
            mouse.setY(fY);
    }

    /**
     * Obtiene el modelo del ratón.
     * 
     * @return El MouseModel actual.
     */
    public MouseModel getMouse() {
        return mouse;
    }

    /**
     * Obtiene la lista de proyectiles de la arena.
     * 
     * @return Lista de proyectiles.
     */
    public List<ProjectileModel> getProjectiles() {
        return projectiles;
    }

    /**
     * Ejecuta la detección de colisiones entre el ratón y los proyectiles activos.
     */
    public void checkCollisions() {
        if (mouse == null || projectiles == null) return;

        for (ProjectileModel proj : projectiles) {
            if (!proj.isActive()) continue;

            if (mouse.getBounds().intersects(proj.getBounds())) {
                handleCollision(proj);
            }
        }
    }

    /**
     * Gestiona las consecuencias de una colisión con un proyectil específico.
     * 
     * @param proj El proyectil que ha colisionado.
     */
    private void handleCollision(ProjectileModel proj) {
        if (proj.isDeactivateOnHit()) {
            proj.setActive(false);
        }
        
        if (proj.getType() == 1) {
            goodCollisions++;
        } else if (proj.getType() != 10) {
            badCollisions++;
        }
    }

    /**
     * Obtiene el número de colisiones beneficiosas acumuladas.
     * 
     * @return Número de colisiones buenas.
     */
    public int getGoodCollisions() {
        return goodCollisions;
    }

    /**
     * Obtiene el número de colisiones perjudiciales acumuladas.
     * 
     * @return Número de colisiones malas.
     */
    public int getBadCollisions() {
        return badCollisions;
    }

    /**
     * Incrementa manualmente el contador de colisiones buenas.
     */
    public void addGoodCollision() {
        this.goodCollisions++;
    }

    /**
     * Incrementa manualmente el contador de colisiones malas.
     */
    public void addBadCollision() {
        this.badCollisions++;
    }

    /**
     * Verifica si todos los proyectiles de la ronda han sido impactados o desactivados.
     * 
     * @return true si no quedan balas activas.
     */
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

    /**
     * Obtiene el número de la ronda actual.
     * 
     * @return Ronda actual.
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Establece el número de la ronda actual.
     * 
     * @param round Nueva ronda.
     */
    public void setCurrentRound(int round) {
        this.currentRound = round;
    }
}