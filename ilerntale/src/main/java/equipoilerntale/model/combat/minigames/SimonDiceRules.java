package equipoilerntale.model.combat.minigames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import equipoilerntale.controller.InputHandler;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.MouseModel;

/**
 * Minijuego Simón Dice: Memorizar y repetir una secuencia de direcciones.
 */
public class SimonDiceRules implements MinigameRules {

    private enum State { SHOWING, INPUT, SUCCESS, FAILURE }
    private State currentState;
    private List<Integer> sequence;
    private List<Integer> playerInput;
    private int showIndex = 0;
    private int tickCounter = 0;
    private final Random rand = new Random();
    
    // 0: Arriba, 1: Derecha, 2: Abajo, 3: Izquierda
    private int activeHighlight = -1;
    private boolean inputHandled = false;

    @Override
    public void start(ArenaModel arena) {
        arena.startCombat();
        arena.clearProjectiles();
        sequence = new ArrayList<>();
        playerInput = new ArrayList<>();
        // Generar secuencia de 4 pasos
        for (int i = 0; i < 4; i++) {
            sequence.add(rand.nextInt(4));
        }
        currentState = State.SHOWING;
        showIndex = 0;
        tickCounter = 0;
        activeHighlight = -1;
        
        // El ratón es estático en el centro
        MouseModel mouse = arena.getMouse();
        if (mouse != null) {
            mouse.setX(500 - (mouse.getAncho() / 2));
            mouse.setY(365 - (mouse.getAlto() / 2));
        }
    }

    @Override
    public void update(ArenaModel arena, InputHandler input) {
        tickCounter++;

        switch (currentState) {
            case SHOWING:
                if (tickCounter % 40 == 0) {
                    if (activeHighlight == -1) {
                        if (showIndex < sequence.size()) {
                            activeHighlight = sequence.get(showIndex);
                            showIndex++;
                        } else {
                            currentState = State.INPUT;
                            tickCounter = 0;
                        }
                    } else {
                        activeHighlight = -1;
                    }
                }
                break;

            case INPUT:
                int currentPress = -1;
                if (input.upPressed) currentPress = 0;
                else if (input.rightPressed) currentPress = 1;
                else if (input.downPressed) currentPress = 2;
                else if (input.leftPressed) currentPress = 3;

                if (currentPress != -1) {
                    if (!inputHandled) {
                        playerInput.add(currentPress);
                        activeHighlight = currentPress;
                        inputHandled = true;
                        
                        // Verificar entrada
                        if (playerInput.get(playerInput.size() - 1) != sequence.get(playerInput.size() - 1)) {
                            currentState = State.FAILURE;
                            arena.addBadCollision();
                        } else if (playerInput.size() == sequence.size()) {
                            currentState = State.SUCCESS;
                            arena.addGoodCollision();
                            arena.addGoodCollision(); // Doble daño por memoria
                        }
                    }
                } else {
                    inputHandled = false;
                    activeHighlight = -1;
                }
                break;

            case SUCCESS:
            case FAILURE:
                // El minijuego termina en el siguiente frame indicado por isFinished
                break;
        }
    }

    @Override
    public void render(Graphics2D g2d, ArenaModel arena) {
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        
        String msg = "";
        if (currentState == State.SHOWING) msg = "¡MEMORIZA!";
        else if (currentState == State.INPUT) msg = "¡REPITE!";
        else if (currentState == State.SUCCESS) msg = "¡BIEN HECHO!";
        else if (currentState == State.FAILURE) msg = "¡ERROR!";
        
        g2d.drawString(msg, 450, 270);

        // Dibujar botones de dirección
        drawDirectionBtn(g2d, 500, 310, 0, "UP");    // Arriba
        drawDirectionBtn(g2d, 560, 365, 1, "RIGHT"); // Derecha
        drawDirectionBtn(g2d, 500, 420, 2, "DOWN");  // Abajo
        drawDirectionBtn(g2d, 440, 365, 3, "LEFT");  // Izquierda
    }

    private void drawDirectionBtn(Graphics2D g2d, int cx, int cy, int dir, String label) {
        int size = 50;
        int x = cx - size / 2;
        int y = cy - size / 2;
        
        if (activeHighlight == dir) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(x - 2, y - 2, size + 4, size + 4);
        }
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(x, y, size, size);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, size, size);
        g2d.drawString(label.substring(0, 1), x + 15, y + 32);
    }

    @Override
    public boolean isIntroActive() {
        return false;
    }

    @Override
    public boolean isFinished(ArenaModel arena) {
        return currentState == State.SUCCESS || currentState == State.FAILURE;
    }

    @Override
    public int getDurationInSeconds() {
        return 12;
    }
}
