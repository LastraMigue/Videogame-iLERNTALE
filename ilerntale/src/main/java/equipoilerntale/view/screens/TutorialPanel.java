package equipoilerntale.view.screens;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;

import equipoilerntale.view.MainFrame;

/**
 * Panel que muestra las instrucciones y mecánicas básicas del juego al usuario.
 * Dividido en secciones visuales que explican el movimiento, el combate y otras funciones.
 */
public class TutorialPanel extends JPanel {

    /** Referencia al marco principal para gestionar el regreso al menú. */
    private MainFrame mainFrame;
    /** Botón para volver al menú principal. */
    private JButton btnSalir;
    /** Imagen de fondo decorativa para el panel de tutorial. */
    private Image imagenFondo;
    /** Fuente personalizada "Deltarune" utilizada en el panel. */
    private Font deltaruneFont;

    /**
     * Constructor del Panel de Tutorial.
     * 
     * @param frame Referencia al marco principal de la aplicación.
     */
    public TutorialPanel(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(1000, 600));
        setLayout(null); // Diseño absoluto
        cargarRecursos();
        inicializarComponentes();
    }

    /**
     * Carga los recursos gráficos y la fuente personalizada desde el sistema de archivos.
     */
    private void cargarRecursos() {
        try (InputStream is = getClass().getResourceAsStream("/title/menu1.jpg")) {
            if (is != null) {
                imagenFondo = ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el fondo del tutorial: " + e.getMessage());
        }

        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
            if (fontStream != null) {
                deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("No se pudo cargar la fuente del tutorial: " + e.getMessage());
        }
    }

    /**
     * Obtiene una instancia de la fuente Deltarune con el tamaño especificado.
     * 
     * @param size Tamaño de la fuente.
     * @return Font configurada.
     */
    private Font getFontDeltarune(float size) {
        if (deltaruneFont != null) {
            return deltaruneFont.deriveFont(size);
        }
        return new Font("Monospaced", Font.BOLD, (int) size);
    }

    /**
     * Dibuja los componentes visuales del tutorial, aplicando un overlay oscuro 
     * para mejorar la legibilidad de los textos.
     * 
     * @param g El contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);

            // Oscurecer el fondo con un overlay negro semi-transparente
            g.setColor(new Color(0, 0, 0, 240)); // Ajusta el 100 para más o menos oscuridad
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Inicializa y configura los elementos de la interfaz: título, secciones y botón de salida.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("TUTORIAL", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 1000, 60);
        lblTitulo.setFont(getFontDeltarune(56f));
        add(lblTitulo);

        JPanel pnlMov = crearSeccion("", 50, 120, 280, 350, "/title/movimiento.jpg");
        add(pnlMov);

        JPanel pnlCombat = crearSeccion("", 360, 120, 280, 350, "/title/combate.jpg");
        add(pnlCombat);

        JPanel pnlFunc = crearSeccion("", 670, 120, 280, 350, "/title/funciones.jpg");
        add(pnlFunc);

        btnSalir = createImageButton("/title/salir.png", "SALIR");
        btnSalir.setBounds(400, 500, 200, 60);

        btnSalir.addActionListener(e -> {
            equipoilerntale.service.SoundService.getInstance().playSFX("/sound/mouse_click.wav");
            mainFrame.cambiarPantalla("MENU");
        });
        add(btnSalir);
    }

    /**
     * Crea un botón gráfico con imagen y efectos visuales de interacción.
     * 
     * @param imagePath Ruta a la imagen del botón.
     * @param fallbackText Texto de respaldo si falla la carga de imagen.
     * @return JButton configurado.
     */
    private JButton createImageButton(String imagePath, String fallbackText) {
        JButton button = new JButton();

        try (InputStream is = getClass().getResourceAsStream(imagePath)) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    // 1. Imagen en estado normal
                    int anchoNormal = 200;
                    int altoNormal = 60;
                    Image normalImg = img.getScaledInstance(anchoNormal, altoNormal, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(normalImg));

                    // 2. Imagen en estado pulsado (SÍNCRONO Y CENTRADO)
                    int anchoPulsado = (int) (anchoNormal * 0.9);
                    int altoPulsado = (int) (altoNormal * 0.9);

                    // Creamos lienzo transparente de tamaño completo
                    java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(
                            anchoNormal, altoNormal, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    java.awt.Graphics2D g2 = canvas.createGraphics();

                    // Suavizado de bordes
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Calculamos offsets
                    int offX = (anchoNormal - anchoPulsado) / 2;
                    int offY = (altoNormal - altoPulsado) / 2;

                    // Dibujamos la imagen original escalándola directamente en el canvas (SÍNCRONO)
                    g2.drawImage(img, offX, offY, anchoPulsado, altoPulsado, null);
                    g2.dispose();

                    button.setPressedIcon(new ImageIcon(canvas));
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando botón tutorial " + imagePath + ": " + e.getMessage());
        }

        if (button.getIcon() == null) {
            button.setText(fallbackText);
            button.setFont(getFontDeltarune(32f));
            button.setForeground(Color.WHITE);
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // FORZAR CENTRADO DE ICONOS: Asegura que el escalado sea simétrico
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(0);
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));

        return button;
    }

    /**
     * Crea una sección visual informativa dentro del tutorial.
     * 
     * @param titulo Título de la sección.
     * @param x Posición X.
     * @param y Posición Y.
     * @param w Ancho.
     * @param h Alto.
     * @param imgPath Ruta a la imagen descriptiva de la sección.
     * @return JPanel configurado como sección.
     */
    private JPanel crearSeccion(String titulo, int x, int y, int w, int h, String imgPath) {
        JPanel panel = new JPanel(null) {
            private Image bgImg;
            {
                try (InputStream is = getClass().getResourceAsStream(imgPath)) {
                    if (is != null)
                        bgImg = ImageIO.read(is);
                } catch (IOException e) {
                    System.err.println("Error cargando sección " + imgPath);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImg != null) {
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);

                    // Capa oscura para legibilidad
                    g.setColor(new Color(0, 0, 0, 0));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(new Color(0, 0, 0, 0));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setBounds(x, y, w, h);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        panel.setOpaque(false);

        JLabel lblSeccion = new JLabel(titulo, SwingConstants.CENTER);
        lblSeccion.setForeground(Color.YELLOW);
        lblSeccion.setFont(getFontDeltarune(24f));
        lblSeccion.setBounds(0, 10, w, 40);
        panel.add(lblSeccion);

        return panel;
    }
}
