package equipoilerntale.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    public static final String DIR_UP = "arriba";
    public static final String DIR_DOWN = "abajo";
    public static final String DIR_LEFT = "izquierda";
    public static final String DIR_RIGHT = "derecha";

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean enterPressed;
    private boolean ePressed;
    private String lastHorizontal = DIR_RIGHT;
    private String lastVertical = DIR_DOWN;
    private boolean preferHorizontal = true;

    /**
     * ESTABLECE EL ESTADO DE PRESIÓN DE UNA TECLA ESPECÍFICA.
     * ACTUALIZA LA ÚLTIMA DIRECCIÓN HORIZONTAL O VERTICAL SEGÚN LA TECLA.
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

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isEnterPressed() {
        return enterPressed;
    }

    public boolean isEPressed() {
        return ePressed;
    }

    public String getLastHorizontal() {
        return lastHorizontal;
    }

    public String getLastVertical() {
        return lastVertical;
    }

    public boolean isPreferHorizontal() {
        return preferHorizontal;
    }

    /**
     * REINICIA TODOS LOS ESTADOS DE LAS TECLAS A FALSE.
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