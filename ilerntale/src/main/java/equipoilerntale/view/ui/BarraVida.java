package equipoilerntale.view.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class BarraVida {

    // Lógica Barra de Vida
    // Vida máxima y Vida actual
    private int maxHealth;
    private int currentHealth;

    // Dimensiones
    private int width = 200;
    private int height = 20;

    private String name;
    private Font customFont;

    public BarraVida(int maxHealth, String name) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.name = name;
        cargarFuente();
    }

    private void cargarFuente() {
        try {
            java.net.URL fontUrl = getClass().getResource("/font/deltarune.ttf");
            if (fontUrl != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                customFont = baseFont.deriveFont(Font.BOLD, 18f);
            } else {
                customFont = new Font("Monospaced", Font.BOLD, 18);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Monospaced", Font.BOLD, 18);
        }
    }

    public void setHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        // Opcionalmente ajustamos la vida actual a la nueva vida máxima si es menor
        if (this.currentHealth > maxHealth) {
            this.currentHealth = maxHealth;
        }
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

        // Dibujar el nombre
        if (name != null && !name.isEmpty()) {
            g.setColor(Color.WHITE);
            g.setFont(customFont);
            g.drawString(name, x, y - 5);
        }

        int healthWidth = (int) ((currentHealth / (double) maxHealth) * width);

        // Fondo
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        // Color según porcentaje de vida
        double percent = (double) currentHealth / maxHealth;
        if (percent > 0.60) {
            g.setColor(Color.GREEN);
        } else if (percent > 0.20) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }

        // Vida actual
        g.fillRect(x, y, healthWidth, height);

        // Borde igualando el tamaño de trazo
        Graphics2D g2d = (Graphics2D) g;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);
        g2d.setStroke(oldStroke);

        // Texto (e.g. 50/50)
        String textoVida = currentHealth + " / " + maxHealth;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(textoVida);
        int textAscent = fm.getAscent();

        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + textAscent;
        g.drawString(textoVida, textX, textY);
    }

}
