package equipoilerntale.view.render;

import java.awt.Graphics2D;

/**
 * CONTEXTO ENCAPSULADO PARA EL RENDERIZADO.
 */
public class RenderContext {
    private final Graphics2D g2d;
    private final int cameraX;
    private final int cameraY;

    /**
     * CONSTRUCTOR DEL CONTEXTO DE RENDERIZADO.
     */
    public RenderContext(Graphics2D g2d, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        this.g2d = g2d;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
    }

    /**
     * OBTIENE EL CONTEXTO GRÁFICO 2D.
     */
    public Graphics2D getGraphics() {
        return g2d;
    }

    /**
     * OBTIENE LA POSICIÓN X DE LA CÁMARA.
     */
    public int getCameraX() {
        return cameraX;
    }

    /**
     * OBTIENE LA POSICIÓN Y DE LA CÁMARA.
     */
    public int getCameraY() {
        return cameraY;
    }

    /**
     * APLICA LA TRANSFORMACIÓN DE CÁMARA AL CONTEXTO GRÁFICO.
     */
    public void translateCamera() {
        g2d.translate(-cameraX, -cameraY);
    }

    /**
     * RESTAURA LA TRANSFORMACIÓN DE CÁMARA AL CONTEXTO GRÁFICO.
     */
    public void restoreCamera() {
        g2d.translate(cameraX, cameraY);
    }
}
