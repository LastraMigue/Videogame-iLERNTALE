package equipoilerntale.view.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Componente visual que representa una barra de salud.
 * Maneja tanto la lógica de puntos de vida como la renderización gráfica.
 */
public class BarraVida {

    /** Vida máxima permitida. */
    private int maxHealth;
    /** Vida actual del personaje. */
    private int currentHealth;

    /** Ancho de la barra en píxeles. */
    private int width = 200;
    /** Alto de la barra en píxeles. */
    private int height = 20;

    /** Nombre asociado a la barra (e.g. "JUGADOR"). */
    private String name;
    /** Fuente personalizada para el nombre y los números. */
    private Font customFont;

    /**
     * Constructor de la barra de vida.
     * 
     * @param maxHealth Vida máxima inicial.
     * @param name Nombre a mostrar sobre la barra.
     */
    public BarraVida(int maxHealth, String name) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.name = name;
        cargarFuente();
    }

    /**
     * Carga la fuente "Deltarune" desde los recursos.
     */
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

    /**
     * Actualiza la vida actual, asegurando que esté dentro de los límites [0, maxHealth].
     * 
     * @param health Nueva cantidad de vida.
     */
    public void setHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
    }

    /**
     * Establece una nueva vida máxima y ajusta la vida actual si es necesario.
     * 
     * @param maxHealth Nueva vida máxima.
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        // Opcionalmente ajustamos la vida actual a la nueva vida máxima si es menor
        if (this.currentHealth > maxHealth) {
            this.currentHealth = maxHealth;
        }
    }

    /**
     * Reduce la vida actual por una cantidad de daño recibida.
     * 
     * @param damage Puntos de vida a restar.
     */
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    /**
     * Incrementa la vida actual por una cantidad de curación.
     * 
     * @param amount Puntos de vida a añadir.
     */
    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    /** @return Vida actual. */
    public int getHealth() {
        return currentHealth;
    }

    /** @return Vida máxima. */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Dibuja la barra de vida en el contexto gráfico proporcionado.
     * Incluye el nombre, fondo, barra de color (dinámica según HP) y texto numérico.
     * 
     * @param g Contexto gráfico.
     * @param x Coordenada X.
     * @param y Coordenada Y.
     */
    public void draw(Graphics g, int x, int y) {

        if (name != null && !name.isEmpty()) {
            g.setColor(Color.WHITE);
            g.setFont(customFont);
            g.drawString(name, x, y - 5);
        }

        int healthWidth = (int) ((currentHealth / (double) maxHealth) * width);

        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        double percent = (double) currentHealth / maxHealth;
        if (percent > 0.60) {
            g.setColor(Color.GREEN);
        } else if (percent > 0.20) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }

        g.fillRect(x, y, healthWidth, height);

        Graphics2D g2d = (Graphics2D) g;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);
        g2d.setStroke(oldStroke);

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
