package equipoilerntale.view.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Cursor;
import java.net.URL;

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
    private JButton btnOpciones;
    private JButton btnSalir;

    /**
     * CONSTRUCTOR DEL MENÚ PRINCIPAL.
     */
    public MainMenu(MainFrame frame) {
        this.mainFrame = frame;

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null); // Diseño absoluto, usamos coordenadas

        inicializarComponentes();
        cargarImagenMenu();
    }

    private JButton crearBoton(String texto, int x, int y) {
        JButton boton = new JButton(texto);

        // Cargar fuente
        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
            if (fontStream != null) {
                Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(32f);
                boton.setFont(deltaruneFont);
            } else {
                throw new IOException("No se encontró el archivo de la fuente.");
            }
        } catch (FontFormatException | IOException e) {
            boton.setFont(new Font("Monospaced", Font.BOLD, 32));
            System.out.println("No se pudo cargar la fuente Deltarune para " + texto + ", usando Monospaced.");
        }

        // Estilo visual
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setForeground(Color.WHITE);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBounds(x, y, 300, 60);

        return boton;
    }

    private void inicializarComponentes() {
        // Inicializar botones con el helper - Situados en la mitad inferior (Total
        // alto: 600)
        // Usamos un intervalo de 55px (altura botón 60px) para compactarlos un poco más
        btnJugar = crearBoton("JUGAR", 350, 320);
        btnIntro = crearBoton("INTRO", 350, 375);
        btnTutorial = crearBoton("TUTORIAL", 350, 430);
        btnOpciones = crearBoton("OPCIONES", 350, 485);
        btnSalir = crearBoton("SALIR", 350, 540);

        // Añadir funcionalidades existentes
        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.cambiarPantalla("VIDEO");
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Añadir al panel
        add(btnJugar);
        add(btnIntro);
        add(btnTutorial);
        add(btnOpciones);
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