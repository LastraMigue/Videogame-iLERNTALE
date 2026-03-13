package equipoilerntale.view.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import equipoilerntale.view.MainFrame;

public class MainMenu extends JPanel {

    private MainFrame mainFrame;
    private JButton btnJugar;
    private JButton btnIntro;
    private JButton btnTutorial;
    private JButton btnSalir;
    private Image imagenFondo;

    /**
     * CONSTRUCTOR DEL MENÚ PRINCIPAL.
     */
    public MainMenu(MainFrame frame) {
        this.mainFrame = frame;

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null); // Diseño absoluto, usamos coordenadas

        cargarRecursos();
        inicializarComponentes();
    }

    private void cargarRecursos() {
        try (InputStream is = getClass().getResourceAsStream("/title/menu1.jpg")) {
            if (is != null) {
                imagenFondo = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Error cargando fondo del menú: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void inicializarComponentes() {
        // Botones situados en la mitad inferior
        btnJugar = createImageButton("/title/jugar.png", "JUGAR");
        btnJugar.setBounds(400, 300, 200, 60);
        btnJugar.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.SCREEN_PERSONAJES));

        btnIntro = createImageButton("/title/intro.png", "INTRO");
        btnIntro.setBounds(400, 370, 200, 60);
        btnIntro.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.SCREEN_VIDEO));

        btnTutorial = createImageButton("/title/tutorial.png", "TUTORIAL");
        btnTutorial.setBounds(400, 440, 200, 60);
        btnTutorial.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.SCREEN_TUTORIAL));

        btnSalir = createImageButton("/title/salir.png", "SALIR");
        btnSalir.setBounds(400, 510, 200, 60);
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnJugar);
        add(btnIntro);
        add(btnTutorial);
        add(btnSalir);
    }

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
            System.err.println("Error cargando botón " + imagePath + ": " + e.getMessage());
        }

        if (button.getIcon() == null) {
            button.setText(fallbackText);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setForeground(Color.WHITE);
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // FORZAR CENTRADO DE ICONOS: Asegura que el escalado sea simétrico
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        button.setIconTextGap(0);
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));

        return button;
    }

    private ImageIcon asignarImagenMenu(String ruta) {
        try (InputStream is = getClass().getResourceAsStream(ruta)) {
            if (is == null) {
                System.err.println("No se encontro la imagen: " + ruta);
                return null;
            }
            return new ImageIcon(ImageIO.read(is));
        } catch (IOException e) {
            System.err.println("Error cargando imagen para el menú: " + e.getMessage());
            return null;
        }
    }
}