package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import javax.swing.*;

import equipoilerntale.controller.ExplorationController;
import equipoilerntale.controller.MainController;
import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.ExploredPanel;
import equipoilerntale.view.screens.GamePanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;

public class MainFrame extends JFrame {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    private CardLayout cardLayout;
    private JPanel contenedor;
    private MainMenu menu;
    private CharacterSelector personajes;
    private GamePanel mapa;
    private PausePanel pause;
    private CombatPanel combate;
    private ExploredPanel exploracion;
    private String personajeSeleccionado = "";

    // Controladores
    private MainController mainController;
    private ExplorationController explorationController;

    public MainFrame() {
        // Configuración básica de la ventana
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        setTitle("iLERNTALE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        // Inicializar controladores
        inicializarControladores();

        // Inicializar paneles
        menu = new MainMenu(this);
        mapa = new GamePanel(this);
        personajes = new CharacterSelector(this);
        pause = new PausePanel(this);
        combate = new CombatPanel(this);

        // Crear ExploredPanel con el controller
        exploracion = new ExploredPanel(this, personajeSeleccionado, explorationController);

        // Añadir paneles al contenedor
        contenedor.add(menu, "MENU");
        contenedor.add(mapa, "MAPA");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(pause, "PAUSE");
        contenedor.add(combate, "COMBATE");
        contenedor.add(exploracion, "EXPLORACION");

        add(contenedor);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Iniciar game loop
        mainController.startGameThread();

        // Iniciar timer de renderizado
        iniciarRenderLoop();

        LOG.info("MainFrame inicializado correctamente");
    }

    private void inicializarControladores() {
        LOG.info("Inicializando controladores...");

        // Usar personaje seleccionado o "migue" como default inicial
        String personaje = (personajeSeleccionado == null || personajeSeleccionado.isEmpty()) ? "migue"
                : personajeSeleccionado;

        // Crear MainController con el personaje
        mainController = new MainController(this, personaje);

        // Obtener exploration controller del main controller
        explorationController = mainController.getExplorationController();

        LOG.info("Controladores inicializados");
    }

    private void iniciarRenderLoop() {
        // Timer para repintar el panel activo a 60 FPS
        javax.swing.Timer renderTimer = new javax.swing.Timer(16, e -> {
            if (exploracion != null) {
                exploracion.requestRender();
            }
        });
        renderTimer.start();
    }

    public void cambiarPantalla(String nombre) {
        LOG.info("Cambiando a pantalla: " + nombre);
        cardLayout.show(contenedor, nombre);

        // Dar foco automáticamente a la exploración si se entra en ella
        if ("EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.requestFocusInWindow();
        }
    }

    public void setPersonajeSeleccionado(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            LOG.warning("nombrePersonaje no puede ser null o vacío");
            nombre = "migue";
        }
        this.personajeSeleccionado = nombre.trim();

        // Recrear el controller
        if (mainController != null) {
            mainController.dispose();
        }

        inicializarControladores();

        // REINICIAR el thread del juego para el nuevo controlador
        if (mainController != null) {
            mainController.startGameThread();
        }

        // Recrear el panel con el nuevo controller
        exploracion = new ExploredPanel(this, personajeSeleccionado, explorationController);
        contenedor.removeAll();
        contenedor.add(menu, "MENU");
        contenedor.add(mapa, "MAPA");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(pause, "PAUSE");
        contenedor.add(combate, "COMBATE");
        contenedor.add(exploracion, "EXPLORACION");

        LOG.info("Personaje seleccionado: " + this.personajeSeleccionado);
    }

    public String getPersonajeSeleccionado() {
        return personajeSeleccionado;
    }

    public ExploredPanel getExploracion() {
        return exploracion;
    }

    public CharacterSelector getPersonajes() {
        return personajes;
    }

    public GamePanel getMapa() {
        return mapa;
    }

    public MainController getMainController() {
        return mainController;
    }

    public ExplorationController getExplorationController() {
        return explorationController;
    }
}
