package equipoilerntale.view.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import equipoilerntale.view.MainFrame;

public class MainMenu extends JPanel {

    private MainFrame mainFrame;
    private JButton btnJugar;
    private JButton btnSalir;

    public MainMenu(MainFrame frame) {
        this.mainFrame = frame;

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null); // Diseño absoluto, usamos coordenadas

        inicializarComponentes();
        cargarImagenMenu();
    }

    private void inicializarComponentes() {
        btnJugar = new JButton("NUEVA PARTIDA");
        btnJugar.setFont(new Font("Arial", Font.BOLD, 24));
        btnJugar.setOpaque(false);
        btnJugar.setContentAreaFilled(false);
        btnJugar.setBorderPainted(false);
        btnJugar.setFocusPainted(false);
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setBounds(350, 400, 300, 60);
        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // El cambio que querías: directo, limpio y unificado
                mainFrame.cambiarPantalla("VIDEO");
            }
        });

        btnSalir = new JButton("SALIR");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 24));
        btnSalir.setOpaque(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBounds(350, 480, 300, 60);
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(btnJugar);
        add(btnSalir);
    }

    private ImageIcon asignarImagenMenu(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontro la imagen: " + ruta);
            return null;
        }
        return new ImageIcon(url);
    }

    private void cargarImagenMenu() {
        ImageIcon imagenMenu = asignarImagenMenu("/title/menu1.jpg");

        int ancho = 1000;
        int alto = 600;

        if (imagenMenu != null) {
            Image imagenEscalada = imagenMenu.getImage().getScaledInstance(ancho, alto, Image.SCALE_DEFAULT);
            JLabel labelMenu = new JLabel(new ImageIcon(imagenEscalada));
            labelMenu.setBounds(0, 0, ancho, alto);
            add(labelMenu);
            System.out.println("Menu cargado correctamente");
        } else {
            setBackground(new Color(20, 20, 30));
            System.err.println("ERROR: No se pudo cargar la imagen de fondo");
        }
    }
}