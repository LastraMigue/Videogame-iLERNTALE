package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.GamePanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;
import equipoilerntale.view.screens.VideoScreen;
import equipoilerntale.controller.MainController;

import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JLayeredPane layeredPane;
    private JPanel contenedor;
    private String pantallaActual = "MENU";
    private MainMenu menu;
    private CharacterSelector personajes;
    private GamePanel mapa;
    private PausePanel pause;
    private CombatPanel combate;
    private VideoScreen videoScreen;
    private MainController mainController;

    public MainFrame() {
        // Configuración básica de la ventana
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout); // El contenedor que baraja los paneles
        setTitle("iLERNTALE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        // Inicializamos los paneles y pasamos el 'this' (el JFrame) por si necesitamos
        // controlar el cambiio desde dentro de los paneles
        // Creamos un panel temporal azul (Base para feature)
        menu = new MainMenu(this);
        mapa = new GamePanel(this);
        personajes = new CharacterSelector(this);
        pause = new PausePanel(this);
        combate = new CombatPanel(this);
        videoScreen = new VideoScreen(this);

        // Añadimos el panel al contenedor con un nombre único
        contenedor.add(menu, "MENU");
        contenedor.add(mapa, "MAPA");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(combate, "COMBATE");
        videoScreen.setName("VIDEO"); // Útil para exclusiones
        contenedor.add(videoScreen, "VIDEO");

        // Configuramos el LayeredPane para el overlay
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 600));

        // Capa inferior: El contenedor principal con CardLayout
        contenedor.setBounds(0, 0, 1000, 600);
        layeredPane.add(contenedor, JLayeredPane.DEFAULT_LAYER);

        // Capa superior: El panel de pausa (inicialmente invisible)
        pause.setBounds(0, 0, 1000, 600);
        pause.setVisible(false);
        layeredPane.add(pause, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);

        // Registro global de la tecla ESC
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (puedePausar()) {
                    togglePause();
                    return true; // Consumir el evento
                }
            }
            return false;
        });

        cardLayout.show(contenedor, "MENU");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void cambiarPantalla(String nombre) {
        this.pantallaActual = nombre;
        cardLayout.show(contenedor, nombre);

        // Si vamos a la pantalla del video, iniciamos el video
        if (nombre.equals("VIDEO")) {
            videoScreen.playVideo();
        }
    }

    public void togglePause() {
        if (pause.isVisible()) {
            pause.setVisible(false);
            if (mainController != null)
                mainController.resumeGame();
        } else {
            pause.setVisible(true);
            if (mainController != null)
                mainController.pauseGame();
            // Aseguramos que el panel de pausa se redibuje
            pause.repaint();
        }
    }

    private boolean puedePausar() {
        // No permitir pausa en ciertas pantallas
        return !pantallaActual.equals("MENU") &&
                !pantallaActual.equals("VIDEO") &&
                !pantallaActual.equals("PERSONAJES");
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public MainController getMainController() {
        return mainController;
    }

    public GamePanel getMapa() {
        return mapa;
    }

    public JPanel getPanelActual() {
        for (Component comp : contenedor.getComponents()) {
            if (comp.isVisible() && comp instanceof JPanel) {
                return (JPanel) comp;
            }
        }
        return null;
    }
}