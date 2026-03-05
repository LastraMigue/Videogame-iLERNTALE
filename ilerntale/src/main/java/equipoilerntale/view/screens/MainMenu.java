package equipoilerntale.view.screens;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.*;
// Prueba de importar color
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

// import equipoilerntale.view.MainFrame;

public class MainMenu extends JPanel {

    // Atributos (botones)
    JButton botonIniciarPartida, botonOpciones, botonSalir;

    // Traer la imagen
    // private Image imagenMenu;

    // Constructor
    public MainMenu() {

        // Distribución vertical para los botones (Box Layout)
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Dejar el fondo vacío por defecto (el gris es para probar)
        setBackground(Color.GRAY);

        // Centrar botones y añadir espacio
        botonIniciarPartida = new JButton("Iniciar Partida");
        botonIniciarPartida.setAlignmentX(CENTER_ALIGNMENT);
        botonIniciarPartida.setOpaque(false);
        botonIniciarPartida.setForeground(Color.WHITE);
        botonIniciarPartida.setFocusPainted(false);
        botonIniciarPartida.setContentAreaFilled(false);
        botonIniciarPartida.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        botonIniciarPartida.setFont(new Font("Arial", Font.BOLD, 20));

        /*
         * Esto hay que revisarlo porque como listener sólo habría que
         * traer si se pulsa Enter, que se definirá en Controlador
         * y si se pulsa, se pasará al GamePanel
         * 
         * botonIniciarPartida.addActionListener(new ActionListener() {
         * 
         * @Override
         * public void actionPerformed(ActionEvent e) {
         * mainFrame.cambiarPantalla("GAME");
         * }
         * });
         */

        botonOpciones = new JButton("Opciones");
        botonOpciones.setAlignmentX(CENTER_ALIGNMENT);
        botonOpciones.setOpaque(false);
        botonOpciones.setForeground(Color.WHITE);
        botonOpciones.setFocusPainted(false);
        botonOpciones.setContentAreaFilled(false);
        botonOpciones.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        /*
         * Ajustar el listener
         * botonOpciones.addActionListener(new ActionListener() {
         * 
         * @Override
         * public void actionPerformed(ActionEvent e) {
         * JOptionPane.showMessageDialog(mainFrame, "Panel de Opciones en desarrollo",
         * "Opciones",
         * JOptionPane.INFORMATION_MESSAGE);
         * }
         * });
         */

        botonSalir = new JButton("Salir");
        botonSalir.setAlignmentX(CENTER_ALIGNMENT);
        botonSalir.setOpaque(false);
        botonSalir.setForeground(Color.WHITE);
        botonSalir.setFocusPainted(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        /*
         * Ajustar el listener
         * 
         * botonSalir.addActionListener(new ActionListener() {
         * 
         * @Override
         * public void actionPerformed(ActionEvent e) {
         * int confirm = JOptionPane.showConfirmDialog(mainFrame,
         * "¿Estás seguro de que quieres salir?", "Salir",
         * JOptionPane.YES_NO_OPTION);
         * if (confirm == JOptionPane.YES_OPTION) {
         * System.exit(0);
         * }
         * }
         * });
         */

        // Añadir espacio vertical antes de los botones para centrar
        this.add(Box.createVerticalGlue());
        this.add(botonIniciarPartida);
        this.add(Box.createVerticalStrut(20));
        this.add(botonOpciones);
        this.add(Box.createVerticalStrut(20));
        this.add(botonSalir);
        this.add(Box.createVerticalGlue());
    }

    // Método para Asignar la imagen de fondo del menú

    @Override
    public void paintComponent(Graphics g) {
        Dimension dimension = this.getSize();
        // Para el proyecto ILERNTALE sería "/resource/title/menu1.jpg"
        ImageIcon icon = new ImageIcon(getClass().getResource("/resource/title/menu1.jpg"));
        g.drawImage(icon.getImage(), 0, 0, dimension.width, dimension.height, null);
        setOpaque(false);
        super.paintChildren(g);
    }

}
