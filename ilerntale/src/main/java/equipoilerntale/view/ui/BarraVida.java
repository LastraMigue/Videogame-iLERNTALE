package equipoilerntale.view.ui;

import java.awt.Color;
import java.awt.Graphics;

public class BarraVida {

    // Lógica Barra de Vida
    // Vida máxima y Vida actual
    private int maxHealth;
    private int currentHealth;

    // Dimensiones
    private int width = 200;
    private int height = 20;

    public BarraVida(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void setHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
    }

    // Métodos para recibir daño y posible curación
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    public int getHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    // Apartado Gráfico de la Barra de Vida para dibujarla

    public void draw(Graphics g, int x, int y) {

        int healthWidth = (int) ((currentHealth / (double) maxHealth) * width);

        // Fondo
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        // Color según vida
        if (currentHealth > 60) {
            g.setColor(Color.GREEN);
        } else if (currentHealth > 30) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.RED);
        }

        // Vida actual
        g.fillRect(x, y, healthWidth, height);

        // Borde
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

}
