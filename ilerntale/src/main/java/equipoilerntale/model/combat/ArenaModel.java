package equipoilerntale.model.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaModel {
    private MouseModel mouse;
    private List<ProjectileModel> projectiles;

    public void startCombat() {
        int x = 200, y = 240, width = 600, height = 250;
        int mouseStartX = x + (width / 2) - 15;
        int mouseStartY = y + (height / 2) - 15;

        mouse = new MouseModel(mouseStartX, mouseStartY);
        projectiles = new ArrayList<>();
    }

    public void spawnProjectile() {
        // Límite de proyectiles en pantalla
        if (projectiles.size() >= 10)
            return;

        Random rand = new Random();
        int size = rand.nextInt(25) + 15; // Tamaño entre 15 y 40
        int speed = rand.nextInt(1) + 2; // Velocidad entre 3 y 5
        int borde = rand.nextInt(4); // 0: Arriba, 1: Abajo, 2: Izq, 3: Der

        int x = 0, y = 0, dx = 0, dy = 0;

        switch (borde) {
            case 0: // Arriba
                x = 200 + rand.nextInt(600 - size);
                y = 240;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = speed;
                break;
            case 1: // Abajo
                x = 200 + rand.nextInt(600 - size);
                y = 490 - size;
                dx = (rand.nextBoolean() ? 1 : -1) * speed;
                dy = -speed;
                break;
            case 2: // Izquierda
                x = 200;
                y = 240 + rand.nextInt(250 - size);
                dx = speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
            case 3: // Derecha
                x = 800 - size;
                y = 240 + rand.nextInt(250 - size);
                dx = -speed;
                dy = (rand.nextBoolean() ? 1 : -1) * speed;
                break;
        }

        projectiles.add(new ProjectileModel(x, y, size, dx, dy));
    }

    public void actualizarProjectiles() {
        for (ProjectileModel proj : projectiles) {
            proj.mover();

            // Límites del recuadro: x=200 a 800, y=240 a 490
            // Rebotes en X
            if (proj.getX() <= 200) {
                proj.setX(200);
                proj.setDx(Math.abs(proj.getDx()));
            } else if (proj.getX() >= 800 - proj.getSize()) {
                proj.setX(800 - proj.getSize());
                proj.setDx(-Math.abs(proj.getDx()));
            }

            // Rebotes en Y
            if (proj.getY() <= 240) {
                proj.setY(240);
                proj.setDy(Math.abs(proj.getDy()));
            } else if (proj.getY() >= 490 - proj.getSize()) {
                proj.setY(490 - proj.getSize());
                proj.setDy(-Math.abs(proj.getDy()));
            }
        }
    }

    public void intentarMoverMouse(int dx, int dy) {
        int futuraX = mouse.getX() + (dx * 5);
        int futuraY = mouse.getY() + (dy * 5);

        int compIzquierda = 0;
        int compDerecha = 0;
        int compArriba = 0;
        int compAbajo = 0;

        int limiteIzquierdo = 200 - compIzquierda;
        int limiteDerecho = (200 + 600) - mouse.getAncho() + compDerecha;
        int limiteArriba = 240 - compArriba;
        int limiteAbajo = (240 + 250) - mouse.getAlto() + compAbajo;

        if (futuraX < limiteIzquierdo)
            futuraX = limiteIzquierdo;
        if (futuraX > limiteDerecho)
            futuraX = limiteDerecho;
        if (futuraY < limiteArriba)
            futuraY = limiteArriba;
        if (futuraY > limiteAbajo)
            futuraY = limiteAbajo;

        mouse.setX(futuraX);
        mouse.setY(futuraY);
    }

    public MouseModel getMouse() {
        return mouse;
    }

    public List<ProjectileModel> getProjectiles() {
        return projectiles;
    }
}