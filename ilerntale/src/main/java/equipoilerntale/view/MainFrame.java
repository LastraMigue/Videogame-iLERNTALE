package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.util.logging.Logger;
import javax.swing.*;

import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.controller.MainController;
import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.ExplorationPanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;

/**
 * MARCO PRINCIPAL DE LA APLICACIÓN.
 * GESTIONA EL INTERCAMBIO DE PANTALLAS Y LA INICIALIZACIÓN DE CONTROLADORES.
 */
public class MainFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(MainFrame.class.getName());

    private CardLayout cardLayout;
    private JPanel contenedor;
    private MainMenu menu;
    private CharacterSelector personajes;
    private PausePanel pause;
    private CombatPanel combate;
    private ExplorationPanel exploracion;
    private String personajeSeleccionado = "";

    // CONTROLADORES ACCESIBLES
    private MainController mainController;
    private ExplorationManager explorationManager;

    /**
     * CONSTRUCTOR DEL MARCO PRINCIPAL.
     * CONFIGURA LA VENTANA, INICIALIZA CONTROLADORES Y PANELES.
     */
    public MainFrame() {
        // CONFIGURACIÓN BÁSICA DE LA VENTANA
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        setTitle("iLERNTALE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        // INICIALIZAR CONTROLADORES
        inicializarControladores();

        // INICIALIZAR PANELES DE LAS PANTALLAS
        menu = new MainMenu(this);
        personajes = new CharacterSelector(this);
        pause = new PausePanel(this);
        combate = new CombatPanel(this);

        // CREAR EXPLORATIONPANEL CON EL MANAGER LÓGICO
        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);

        // AÑADIR PANELES AL CONTENEDOR DE CARDLAYOUT
        contenedor.add(menu, "MENU");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(pause, "PAUSE");
        contenedor.add(combate, "COMBATE");
        contenedor.add(exploracion, "EXPLORACION");

        add(contenedor);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // INICIAR EL HILO LÓGICO DEL JUEGO (GAME LOOP)
        mainController.startGameThread();

        // INICIAR BUCLE DE RENDERIZADO (60 FPS)
        iniciarRenderLoop();

        LOG.info("MAINFRAME INICIALIZADO CORRECTAMENTE");
    }

    private void inicializarControladores() {
        LOG.info("INICIALIZANDO CONTROLADORES...");

        // USAR PERSONAJE SELECCIONADO O "MIGUE" POR DEFECTO
        String personaje = (personajeSeleccionado == null || personajeSeleccionado.isEmpty()) ? "migue"
                : personajeSeleccionado;

        // CREAR MAINCONTROLLER Y OBTENER EL MANAGER DE EXPLORACIÓN
        mainController = new MainController(this, personaje);
        explorationManager = mainController.getExplorationManager();

        LOG.info("CONTROLADORES INICIALIZADOS");
    }

    private void iniciarRenderLoop() {
        // TIMER PARA REPINTAR EL PANEL ACTIVO A 60 FPS
        // El isShowing() del ExplorationPanel ya filtra renders innecesarios
        javax.swing.Timer renderTimer = new javax.swing.Timer(16, e -> {
            if (exploracion != null) {
                exploracion.requestRender();
            }
        });
        renderTimer.start();
    }

    /**
     * CAMBIA LA PANTALLA VISIBLE ACTUALMENTE.
     */
    public void cambiarPantalla(String nombre) {
        LOG.info("CAMBIANDO A PANTALLA: " + nombre);

        // Al salir de EXPLORACION: desactivar (pausa) en lugar de destruir assets
        if (!"EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.dispose(); // Llama a manager.deactivate() — no destruye assets
        }

        cardLayout.show(contenedor, nombre);

        // Dar foco y activar lógica de juego cuando se muestra EXPLORACION
        if ("EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.requestFocusInWindow();
            exploracion.reset(); // Llama a manager.activate() — genera zombies y activa updates
        }
    }

    /**
     * DEFINE EL PERSONAJE SELECCIONADO Y RECONSTRUYE LOS CONTROLADORES.
     */
    public void setPersonajeSeleccionado(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            LOG.warning("EL NOMBRE NO PUEDE SER NULO. USANDO MIGUE.");
            nombre = "migue";
        }
        this.personajeSeleccionado = nombre.trim();

        // Limpiar el manager anterior
        if (mainController != null) {
            mainController.dispose();
        }

        inicializarControladores();
        mainController.startGameThread();

        // Recrear el panel de exploración con el nuevo personaje y manager
        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);

        // Refrescar el contenedor de pantallas
        contenedor.removeAll();
        contenedor.add(menu, "MENU");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(pause, "PAUSE");
        contenedor.add(combate, "COMBATE");
        contenedor.add(exploracion, "EXPLORACION");

        // CRÍTICO: sin esto el CardLayout no muestra los paneles nuevos
        contenedor.revalidate();
        contenedor.repaint();

        LOG.info("PERSONAJE SELECCIONADO: " + this.personajeSeleccionado);
    }

    // ============ GETTERS ============

    /**
     * OBTIENE EL NOMBRE DEL PERSONAJE SELECCIONADO.
     */
    public String getPersonajeSeleccionado() {
        return personajeSeleccionado;
    }

    public ExplorationPanel getExploracion() {
        return exploracion;
    }

    /**
     * OBTIENE EL SELECTOR DE PERSONAJES.
     */
    public CharacterSelector getPersonajes() {
        return personajes;
    }

    /**
     * OBTIENE EL CONTROLADOR PRINCIPAL DEL JUEGO.
     */
    public MainController getMainController() {
        return mainController;
    }

    /**
     * OBTIENE EL GESTOR DE EXPLORACIÓN.
     */
    public ExplorationManager getExplorationManager() {
        return explorationManager;
    }
}
