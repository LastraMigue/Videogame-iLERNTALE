package equipoilerntale.view.screens;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import equipoilerntale.controller.MainController;
import equipoilerntale.view.MainFrame;

public class PausePanel extends JPanel {
    private MainFrame mainFrame;
    private MainController controller;

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
        this.controller = frame.getMainController();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        // Fondo de prueba
        setBackground(Color.GRAY);

        TransparentPanel contenidoPrincipal = new TransparentPanel(new Color(0, 0, 0, 150));
        contenidoPrincipal.setLayout(new BorderLayout(0, 0));
        add(contenidoPrincipal, BorderLayout.CENTER);

        JLabel titulo = new JLabel("PAUSA");

        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
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

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setOpaque(false);
        panelTitulo.add(titulo);

        contenidoPrincipal.add(panelTitulo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panelBotones.setOpaque(false);

        JButton btnReanudar = new JButton("Reanudar");
        JButton btnSalir = new JButton("Salir");

        Font fontBotones = new Font("Arial", Font.BOLD, 30);
        Color colorBoton = new Color(50, 50, 50);
        Color colorTexto = Color.WHITE;

        Dimension tamanoBoton = new Dimension(200, 60);

        btnReanudar.setFont(fontBotones);
        btnReanudar.setBackground(colorBoton);
        btnReanudar.setForeground(colorTexto);
        btnReanudar.setFocusPainted(false);
        btnReanudar.setBorder(BorderFactory.createLineBorder(colorTexto, 2));
        btnReanudar.setMaximumSize(tamanoBoton);
        btnReanudar.setAlignmentX(CENTER_ALIGNMENT);

        btnSalir.setFont(fontBotones);
        btnSalir.setBackground(colorBoton);
        btnSalir.setForeground(colorTexto);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createLineBorder(colorTexto, 2));
        btnSalir.setMaximumSize(tamanoBoton);
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);

        btnReanudar.addActionListener(e -> {
            mainFrame.cambiarPantalla("MAPA");
            if (mainFrame.getMainController() != null) {
                mainFrame.getMainController().resumeGame();
            }
        });

        btnSalir.addActionListener(e -> {
            mainFrame.cambiarPantalla("MENU");
        });

        panelBotones.add(btnReanudar);
        panelBotones.add(btnSalir);

        contenidoPrincipal.add(panelBotones, BorderLayout.SOUTH);

    }

}
