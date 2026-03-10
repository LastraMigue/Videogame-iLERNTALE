package equipoilerntale.view.screens;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.swing.*;

import equipoilerntale.view.MainFrame;

public class TutorialPanel extends JPanel {

    private MainFrame mainFrame;
    private JButton btnSalir;
    private Image imagenFondo;
    private Font deltaruneFont;

    public TutorialPanel(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(1000, 600));
        setLayout(null); // Diseño absoluto
        cargarRecursos();
        inicializarComponentes();
    }

    private void cargarRecursos() {
        // Cargar Fondo
        try {
            URL url = getClass().getResource("/title/menu1.jpg");
            if (url != null) {
                imagenFondo = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el fondo del tutorial: " + e.getMessage());
        }

        // Cargar Fuente
        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
            if (fontStream != null) {
                deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("No se pudo cargar la fuente del tutorial: " + e.getMessage());
        }
    }

    private Font getFontDeltarune(float size) {
        if (deltaruneFont != null) {
            return deltaruneFont.deriveFont(size);
        }
        return new Font("Monospaced", Font.BOLD, (int) size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);

            // Oscurecer el fondo con un overlay negro semi-transparente
            g.setColor(new Color(0, 0, 0, 230)); // Ajusta el 100 para más o menos oscuridad
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void inicializarComponentes() {
        // --- Título ---
        JLabel lblTitulo = new JLabel("TUTORIAL", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 1000, 60);
        lblTitulo.setFont(getFontDeltarune(56f));
        add(lblTitulo);

        // --- Secciones (Rectángulos Negros con Borde Blanco) ---
        JPanel pnlMov = crearSeccion("MOVIMIENTO", 50, 120, 280, 350);
        añadirFilaTutorial(pnlMov, "/controls/keyboard_w.png", "ARRIBA", 70);
        añadirFilaTutorial(pnlMov, "/controls/keyboard_a.png", "IZQUIERDA", 130);
        añadirFilaTutorial(pnlMov, "/controls/keyboard_s.png", "ABAJO", 190);
        añadirFilaTutorial(pnlMov, "/controls/keyboard_d.png", "DERECHA", 250);
        add(pnlMov);

        JPanel pnlCombat = crearSeccion("COMBATE", 360, 120, 280, 350);
        añadirFilaTutorial(pnlCombat, "/controls/keyboard_w.png", "ARRIBA", 60);
        añadirFilaTutorial(pnlCombat, "/controls/keyboard_a.png", "IZQUIERDA", 105);
        añadirFilaTutorial(pnlCombat, "/controls/keyboard_s.png", "ABAJO", 150);
        añadirFilaTutorial(pnlCombat, "/controls/keyboard_d.png", "DERECHA", 195);
        añadirFilaTutorial(pnlCombat, "/attack/bueno.png", "ATACAS", 240);
        añadirFilaTutorial(pnlCombat, "/attack/malo.png", "PILLAS", 285);
        add(pnlCombat);

        JPanel pnlFunc = crearSeccion("FUNCIONES", 670, 120, 280, 350);
        añadirFilaTutorial(pnlFunc, "E", "ABRIR PUERTA", 70);
        añadirFilaTutorial(pnlFunc, "/controls/keyboard_escape.png", "PAUSA", 120);
        add(pnlFunc);

        // --- Botón Salir (Imagen) ---
        btnSalir = createImageButton("/title/salir.png", "SALIR");
        btnSalir.setBounds(400, 500, 200, 60);

        btnSalir.addActionListener(e -> mainFrame.cambiarPantalla("MENU"));
        add(btnSalir);
    }

    private JButton createImageButton(String imagePath, String fallbackText) {
        JButton button = new JButton();

        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image img = icon.getImage();
            if (img != null) {
                Image scaledImg = img.getScaledInstance(200, 60, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
            }
        }

        if (button.getIcon() == null) {
            button.setText(fallbackText);
            button.setFont(getFontDeltarune(32f));
            button.setForeground(Color.WHITE);
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel crearSeccion(String titulo, int x, int y, int w, int h) {
        JPanel panel = new JPanel(null);
        panel.setBounds(x, y, w, h);
        panel.setBackground(new Color(0, 0, 0, 200)); // Negro semi-transparente
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        panel.setOpaque(true);

        JLabel lblSeccion = new JLabel(titulo, SwingConstants.CENTER);
        lblSeccion.setForeground(Color.YELLOW);
        lblSeccion.setFont(getFontDeltarune(24f));
        lblSeccion.setBounds(0, 10, w, 40);
        panel.add(lblSeccion);

        return panel;
    }

    private void añadirFilaTutorial(JPanel seccion, String iconoOTexto, String descripcion, int yPos) {
        // Icono de la tecla
        JLabel lblIcono = new JLabel("", SwingConstants.CENTER);
        lblIcono.setForeground(Color.WHITE);
        lblIcono.setFont(getFontDeltarune(20f));

        try {
            URL url = getClass().getResource(iconoOTexto);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                lblIcono.setIcon(new ImageIcon(img));
            } else {
                // Fallback: si no es ruta de recurso o no existe, mostrar texto con borde
                lblIcono.setText(iconoOTexto.toUpperCase());
                lblIcono.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        } catch (Exception e) {
            lblIcono.setText(iconoOTexto.toUpperCase());
            lblIcono.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        }
        lblIcono.setBounds(30, yPos, 40, 40);
        seccion.add(lblIcono);

        // Dos puntos
        JLabel lblColon = new JLabel(":", SwingConstants.CENTER);
        lblColon.setForeground(Color.WHITE);
        lblColon.setFont(getFontDeltarune(20f));
        lblColon.setBounds(80, yPos, 20, 40);
        seccion.add(lblColon);

        // Descripción
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setForeground(Color.WHITE);
        lblDesc.setFont(getFontDeltarune(18f));
        lblDesc.setBounds(110, yPos, 150, 40);
        seccion.add(lblDesc);
    }
}
