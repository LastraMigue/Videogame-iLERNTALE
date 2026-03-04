package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

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

        // Creamos un panel temporal negro (Base para feature)
        JPanel blackScreen = new JPanel();
        blackScreen.setBackground(Color.BLACK);
        blackScreen.setPreferredSize(new Dimension(800, 600));

        // Añadimos el panel al contenedor
        mainContainer.add(blackScreen, "BASE");

        add(mainContainer);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void cambiarPantalla(String nombre) {
        cardLayout.show(mainContainer, nombre);
    }
}