package equipoilerntale.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Manejador de entrada de teclado que implementa KeyListener.
 * Traduce los eventos de teclado en estados booleanos para las teclas de acción
 * del juego.
 */
public class InputHandler implements KeyListener {

    /** Constante para la dirección arriba. */
    public static final String DIR_UP = "arriba";
    /** Constante para la dirección abajo. */
    public static final String DIR_DOWN = "abajo";
    /** Constante para la dirección izquierda. */
    public static final String DIR_LEFT = "izquierda";
    /** Constante para la dirección derecha. */
    public static final String DIR_RIGHT = "derecha";

    /** Indica si la tecla arriba (W o Flecha Arriba) está pulsada. */
    private boolean upPressed;
    /** Indica si la tecla abajo (S o Flecha Abajo) está pulsada. */
    private boolean downPressed;
    /** Indica si la tecla izquierda (A o Flecha Izquierda) está pulsada. */
    private boolean leftPressed;
    /** Indica si la tecla derecha (D o Flecha Derecha) está pulsada. */
    private boolean rightPressed;
    /** Indica si la tecla Enter está pulsada. */
    private boolean enterPressed;
    /** Indica si la tecla acción (E) está pulsada. */
    private boolean ePressed;
    /** Última dirección horizontal registrada. */
    private String lastHorizontal = DIR_RIGHT;
    /** Última dirección vertical registrada. */
    private String lastVertical = DIR_DOWN;
    /** Preferencia de animación entre horizontal (true) o vertical (false). */
    private boolean preferHorizontal = true;

    /**
     * Establece el estado de presión de una tecla específica y actualiza las
     * direcciones.
     * 
     * @param keyCode Código de la tecla procesada.
     * @param pressed true si está pulsada, false si se ha soltado.
     */
    public void setPressed(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = pressed;
                if (pressed) {
                    lastVertical = DIR_UP;
                    preferHorizontal = false;
                }
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = pressed;
                if (pressed) {
                    lastVertical = DIR_DOWN;
                    preferHorizontal = false;
                }
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = pressed;
                if (pressed) {
                    lastHorizontal = DIR_LEFT;
                    preferHorizontal = true;
                }
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = pressed;
                if (pressed) {
                    lastHorizontal = DIR_RIGHT;
                    preferHorizontal = true;
                }
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = pressed;
                break;
            case KeyEvent.VK_E:
                ePressed = pressed;
                break;
        }
    }

    /** @return true si la dirección arriba está activa. */
    public boolean isUpPressed() {
        return upPressed;
    }

    /** @return true si la dirección abajo está activa. */
    public boolean isDownPressed() {
        return downPressed;
    }

    /** @return true si la dirección izquierda está activa. */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /** @return true si la dirección derecha está activa. */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /** @return true si la tecla Enter está activa. */
    public boolean isEnterPressed() {
        return enterPressed;
    }

    /** @return true si la tecla de acción (E) está activa. */
    public boolean isEPressed() {
        return ePressed;
    }

    /** @return La última dirección horizontal registrada. */
    public String getLastHorizontal() {
        return lastHorizontal;
    }

    /** @return La última dirección vertical registrada. */
    public String getLastVertical() {
        return lastVertical;
    }

    /** @return true si se prefiere la animación horizontal. */
    public boolean isPreferHorizontal() {
        return preferHorizontal;
    }

    /**
     * Reinicia todos los estados de las teclas a falso (soltadas).
     */
    public void reset() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        enterPressed = false;
        ePressed = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        setPressed(code, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        setPressed(code, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
