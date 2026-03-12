package equipoilerntale.view.screens;

import java.awt.*;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import equipoilerntale.view.MainFrame;

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
            @Override public void mouseClicked(MouseEvent e) { e.consume(); }
            @Override public void mousePressed(MouseEvent e) { e.consume(); }
            @Override public void mouseReleased(MouseEvent e) { e.consume(); }
            @Override public void mouseEntered(MouseEvent e) { e.consume(); }
            @Override public void mouseExited(MouseEvent e) { e.consume(); }
            @Override public void mouseMoved(MouseEvent e) { e.consume(); }
            @Override public void mouseDragged(MouseEvent e) { e.consume(); }
            @Override public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) { e.consume(); }
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
            System.out.println("No se pudo cargar la fuente Deltarune, usando Monospaced.");
        }

        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        btnReanudar = createImageButton("/title/reanudar.png", "Reanudar");
        btnReanudar.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir = createImageButton("/title/salir.png", "Salir");
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);

        btnReanudar.addActionListener(e -> {
            mainFrame.togglePause(); // Usa el nuevo método togglePause de MainFrame
        });

        btnSalir.addActionListener(e -> {
            mainFrame.togglePause(); // Ocultar el overlay
            mainFrame.cambiarPantalla("MENU");
        });

        menuContainer.add(titulo);
        menuContainer.add(Box.createVerticalStrut(30));
        menuContainer.add(btnReanudar);
        menuContainer.add(Box.createVerticalStrut(20));
        menuContainer.add(btnSalir);

        fondoOscuro.add(menuContainer);

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
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setForeground(Color.WHITE);
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

}