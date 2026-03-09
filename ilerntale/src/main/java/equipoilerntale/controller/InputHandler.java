package equipoilerntale.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

public class InputHandler implements KeyListener {

    private static final Logger LOG = Logger.getLogger(InputHandler.class.getName());

    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, ePressed, mPressed;
    public String lastHorizontal = "derecha";
    public String lastVertical = "abajo";
    public boolean preferHorizontal = true;

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
                    lastVertical = "arriba";
                    preferHorizontal = false;
                }
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = pressed;
                if (pressed) {
                    lastVertical = "abajo";
                    preferHorizontal = false;
                }
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = pressed;
                if (pressed) {
                    lastHorizontal = "izquierda";
                    preferHorizontal = true;
                }
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = pressed;
                if (pressed) {
                    lastHorizontal = "derecha";
                    preferHorizontal = true;
                }
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = pressed;
                break;
            case KeyEvent.VK_E:
                ePressed = pressed;
                break;
            case KeyEvent.VK_M:
                mPressed = pressed;
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        LOG.info(">>> Tecla presionada: " + KeyEvent.getKeyText(code) + " (code=" + code + ")");
        setPressed(code, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        LOG.info("<<< Tecla liberada: " + KeyEvent.getKeyText(code) + " (code=" + code + ")");
        setPressed(code, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}