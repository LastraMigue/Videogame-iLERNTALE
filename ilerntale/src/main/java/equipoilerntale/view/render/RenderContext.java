package equipoilerntale.view.render;

import java.awt.Graphics2D;

/**
 * Contexto que encapsula los elementos necesarios para el renderizado coordinado.
 * Almacena el objeto gráfico y la posición de la cámara (desplazamiento).
 */
public class RenderContext {
    /** Contexto gráfico principal donde se realizan los dibujos. */
    private final Graphics2D g2d;
    /** Desplazamiento horizontal de la cámara. */
    private final int cameraX;
    /** Desplazamiento vertical de la cámara. */
    private final int cameraY;

    /**
     * Crea un nuevo contexto de renderizado con una cámara definida.
     * 
     * @param g2d          Objeto Graphics2D.
     * @param screenWidth  Ancho de la pantalla.
     * @param screenHeight Alto de la pantalla.
     * @param cameraX      Posición X de desplazamiento de cámara.
     * @param cameraY      Posición Y de desplazamiento de cámara.
     */
    public RenderContext(Graphics2D g2d, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        this.g2d = g2d;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
    }

    /**
     * @return El contexto gráfico Graphics2D.
     */
    public Graphics2D getGraphics() {
        return g2d;
    }

    /**
     * @return Desplazamiento X de la cámara.
     */
    public int getCameraX() {
        return cameraX;
    }

    /**
     * @return Desplazamiento Y de la cámara.
     */
    public int getCameraY() {
        return cameraY;
    }

    /**
     * Aplica la traslación negativa de la cámara al contexto gráfico.
     * Esto hace que los objetos dibujados después se muevan acorde al desplazamiento de la cámara.
     */
    public void translateCamera() {
        g2d.translate(-cameraX, -cameraY);
    }

    /**
     * Restaura la traslación de la cámara (vuelve al origen).
     */
    public void restoreCamera() {
        g2d.translate(cameraX, cameraY);
    }
}
