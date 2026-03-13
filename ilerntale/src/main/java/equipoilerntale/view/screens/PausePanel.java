package equipoilerntale.view.screens;

import java.awt.*;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import equipoilerntale.view.MainFrame;
import equipoilerntale.service.SoundService;

public class PausePanel extends JPanel {
    private MainFrame mainFrame;
    private JButton btnReanudar;
    private JButton btnSalir;

    private static class TransparentPanel extends JPanel {
        private final Color overlayColor;

        public TransparentPanel(Color color) {
            this.overlayColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(overlayColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

    public PausePanel(MainFrame frame) {
        this.mainFrame = frame;

        setLayout(new BorderLayout());
        setOpaque(false); // Importante para ver lo que hay debajo
        setPreferredSize(new Dimension(800, 600));

        TransparentPanel fondoOscuro = new TransparentPanel(new Color(0, 0, 0, 150));
        fondoOscuro.setLayout(new GridBagLayout());

        // BLOQUEO DE INPUT: Consumir todos los eventos de ratón
        MouseAdapter blocker = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                e.consume();
            }
        };
        fondoOscuro.addMouseListener(blocker);
        fondoOscuro.addMouseMotionListener(blocker);
        fondoOscuro.addMouseWheelListener(blocker);

        add(fondoOscuro, BorderLayout.CENTER);

        JPanel menuContainer = new JPanel();
        menuContainer.setBackground(Color.BLACK);
        menuContainer.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        // Añadir padding interno al contenedor
        menuContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)));

        JLabel titulo = new JLabel("PAUSA");

        try (InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf")) {
            if (fontStream != null) {
                Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(80f);
                titulo.setFont(deltaruneFont);
            } else {
                throw new IOException("No se encontró el archivo de la fuente.");
            }
        } catch (FontFormatException | IOException e) {
            titulo.setFont(new Font("Monospaced", Font.BOLD, 32));
        }

        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        btnReanudar = createImageButton("/title/reanudar.png", "Reanudar");
        btnReanudar.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir = createImageButton("/title/salir.png", "Salir");
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);

        btnReanudar.addActionListener(e -> {
            equipoilerntale.service.SoundService.getInstance().playSFX("/sound/mouse_click.wav");
            mainFrame.togglePause(); // Usa el nuevo método togglePause de MainFrame
        });

        btnSalir.addActionListener(e -> {
            equipoilerntale.service.SoundService.getInstance().playSFX("/sound/mouse_click.wav");
            mainFrame.togglePause(); // Ocultar el overlay
            mainFrame.reiniciarJuego();
            mainFrame.cambiarPantalla(MainFrame.SCREEN_MENU);
        });

        menuContainer.add(titulo);
        menuContainer.add(Box.createVerticalStrut(20));

        // CONTROLES DE VOLUMEN
        JLabel lblVolumen = new JLabel("VOLUMEN");
        lblVolumen.setForeground(Color.WHITE);
        lblVolumen.setAlignmentX(CENTER_ALIGNMENT);

        // Intentar usar la misma fuente que el título pero más pequeña
        lblVolumen.setFont(titulo.getFont().deriveFont(24f));

        JSlider sliderVolumen = new JSlider(JSlider.HORIZONTAL, 0, 100,
                (int) (SoundService.getInstance().getVolume() * 100));
        sliderVolumen.setBackground(Color.BLACK);
        sliderVolumen.setForeground(Color.WHITE);
        sliderVolumen.setPreferredSize(new Dimension(200, 40));
        sliderVolumen.setMaximumSize(new Dimension(250, 40));
        sliderVolumen.setAlignmentX(CENTER_ALIGNMENT);

        sliderVolumen.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float volume = sliderVolumen.getValue() / 100f;
                SoundService.getInstance().setVolume(volume);
            }
        });

        menuContainer.add(lblVolumen);
        menuContainer.add(sliderVolumen);
        menuContainer.add(Box.createVerticalStrut(30));

        menuContainer.add(btnReanudar);
        menuContainer.add(Box.createVerticalStrut(20));
        menuContainer.add(btnSalir);

        fondoOscuro.add(menuContainer);

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

}