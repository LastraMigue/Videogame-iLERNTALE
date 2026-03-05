package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;

import equipoilerntale.view.screens.MainMenu;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MainFrame() {
        // Configuración básica de la ventana
        setTitle("iLERNTALE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        // Setup del CardLayout (Gestor de pantallas)
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Creamos el menú principal pasándole la referencia al frame
        MainMenu mainMenu = new MainMenu();
        mainMenu.setPreferredSize(new Dimension(800, 600));
        mainContainer.add(mainMenu, "MENU");

        // Creamos un panel temporal negro para el juego
        JPanel blackScreen = new JPanel();
        blackScreen.setBackground(Color.BLACK);
        blackScreen.setPreferredSize(new Dimension(800, 600));
        mainContainer.add(blackScreen, "GAME");

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void cambiarPantalla(String nombre) {
        cardLayout.show(mainContainer, nombre);
    }
}