package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;

import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.GamePanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;
import equipoilerntale.view.screens.VideoScreen;

import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contenedor;
    private MainMenu menu;
    private CharacterSelector personajes;
    private GamePanel mapa;
    private PausePanel pause;
    private CombatPanel combate;
    private VideoScreen videoScreen;

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
        contenedor.add(pause, "PAUSE");
        contenedor.add(combate, "COMBATE");
        contenedor.add(videoScreen, "VIDEO");

        add(contenedor);

        cardLayout.show(contenedor, "MENU");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void cambiarPantalla(String nombre) {
        cardLayout.show(contenedor, nombre);

        // Si vamos a la pantalla del video, iniciamos el video
        if (nombre.equals("VIDEO")) {
            videoScreen.playVideo();
        }
    }

    public GamePanel getMapa() {
        return mapa;
    }
}