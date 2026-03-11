package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.controller.MainController;
import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.ExplorationPanel;
import equipoilerntale.view.screens.GamePanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;
import equipoilerntale.view.screens.TutorialPanel;
import equipoilerntale.view.screens.VideoScreen;
import equipoilerntale.view.ui.CajaTexto;
import equipoilerntale.controller.MainController;

/**
 * MARCO PRINCIPAL DE LA APLICACIÓN.
 * GESTIONA EL INTERCAMBIO DE PANTALLAS Y LA INICIALIZACIÓN DE CONTROLADORES.
 */
public class MainFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(MainFrame.class.getName());

    private CardLayout cardLayout;
    private JLayeredPane layeredPane;
    private JPanel contenedor;
    private String pantallaActual = "MENU";
    private MainMenu menu;
    private CharacterSelector personajes;
    private PausePanel pause;
    private CombatPanel combate;
    private VideoScreen videoScreen;
    private ExplorationPanel exploracion;
    private GamePanel gamePanel;
    private TutorialPanel tutorial;
    private JPanel dialogueContainer;
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
        // ESTABLECER ICONO DEL JFRAME
        java.net.URL iconURL = getClass().getResource("/title/titozeio.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            LOG.warning("No se pudo encontrar el icono de la ventana: /title/titozeio.png");
        }

        // INICIALIZAR CONTROLADORES
        inicializarControladores();

        // INICIALIZAR PANELES DE LAS PANTALLAS
        menu = new MainMenu(this);
        personajes = new CharacterSelector(this);
        pause = new PausePanel(this);
        combate = new CombatPanel(this);
        videoScreen = new VideoScreen(this);
        tutorial = new TutorialPanel(this);
        gamePanel = new GamePanel(this);

        // CREAR EXPLORATIONPANEL CON EL MANAGER LÓGICO
        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);

        // AÑADIR PANELES AL CONTENEDOR DE CARDLAYOUT
        contenedor.add(menu, "MENU");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(combate, "COMBATE");
        videoScreen.setName("VIDEO"); // Útil para exclusiones
        contenedor.add(videoScreen, "VIDEO");
        contenedor.add(exploracion, "EXPLORACION");
        contenedor.add(gamePanel, "GAME");
        contenedor.add(tutorial, "TUTORIAL");

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

        // Capa de diálogos (entre el juego y la pausa/palette)
        dialogueContainer = new JPanel(null);
        dialogueContainer.setOpaque(false);
        dialogueContainer.setBounds(0, 0, 1000, 600);
        dialogueContainer.setVisible(false); // Inicialmente oculto para no bloquear interacción
        layeredPane.add(dialogueContainer, JLayeredPane.MODAL_LAYER);

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
        // DETENER DIÁLOGOS ANTES DE CAMBIAR (HARD STOP)
        detenerDialogosExistentes();

        this.pantallaActual = nombre;
        cardLayout.show(contenedor, nombre);

        // Si vamos a la pantalla del video, iniciamos el video
        if (nombre.equals("VIDEO")) {
            videoScreen.playVideo();
        }

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

    public void togglePause() {
        if (pause.isVisible()) {
            pause.setVisible(false);
            if (mainController != null)
                mainController.resumeGame();
            
            // REANUDAR DIÁLOGOS SI ES NECESARIO
            reanudarDialogosExistentes();
            
            Component comp = getPanelActual();
            if (comp != null) comp.requestFocusInWindow();
            
        } else {
            // DETENER DIÁLOGOS AL PAUSAR
            detenerDialogosExistentes();

            pause.setVisible(true);
            if (mainController != null)
                mainController.pauseGame();
            // Asegurar que el panel de pausa se redibuje
            pause.repaint();
        }
    }

    /**
     * PAUSA LOS DIÁLOGOS EN CUALQUIER PANEL QUE ESTÉ ACTIVO.
     */
    private void detenerDialogosExistentes() {
        hideDialogue();
        JPanel actual = getPanelActual();
        if (actual instanceof ExplorationPanel) {
            ((ExplorationPanel) actual).pausarDialogo();
        } else if (actual instanceof GamePanel) {
            ((GamePanel) actual).detenerDialogoBucle();
        }
    }

    /**
     * REANUDA LOS DIÁLOGOS SI EL PANEL LO SOPORTA.
     */
    private void reanudarDialogosExistentes() {
        JPanel actual = getPanelActual();
        if (actual instanceof ExplorationPanel) {
            ((ExplorationPanel) actual).reanudarDialogo();
        }
    }

    public void showDialogue(String text) {
        showDialogue(text, 450);
    }

    public void showDialogue(String text, int y) {
        dialogueContainer.removeAll();
        JPanel panel = CajaTexto.crearPanel(text);
        // Posicionamiento dinámico: centrado horizontalmente, Y ajustable
        panel.setLocation(250, y);
        dialogueContainer.add(panel);
        dialogueContainer.setVisible(true); // Mostrar contenedor al lanzar diálogo
        dialogueContainer.revalidate();
        dialogueContainer.repaint();
    }

    public void hideDialogue() {
        dialogueContainer.removeAll();
        dialogueContainer.setVisible(false); // Ocultar al cerrar diálogo
        dialogueContainer.revalidate();
        dialogueContainer.repaint();
    }

    private boolean puedePausar() {
        // No permitir pausa en ciertas pantallas
        return !pantallaActual.equals("MENU") &&
                !pantallaActual.equals("VIDEO") &&
                !pantallaActual.equals("PERSONAJES") &&
                !pantallaActual.equals("TUTORIAL");
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
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

        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);
        gamePanel = new GamePanel(this);

        // Refrescar el contenedor de pantallas
        contenedor.removeAll();
        contenedor.add(menu, "MENU");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(combate, "COMBATE");
        videoScreen.setName("VIDEO");
        contenedor.add(videoScreen, "VIDEO");
        contenedor.add(exploracion, "EXPLORACION");
        contenedor.add(gamePanel, "GAME");
        contenedor.add(tutorial, "TUTORIAL");

        // CRÍTICO: sin esto el CardLayout no muestra los paneles nuevos
        contenedor.revalidate();
        contenedor.repaint();

        LOG.info("PERSONAJE SELECCIONADO: " + this.personajeSeleccionado);
    }

    public JPanel getPanelActual() {
        for (Component comp : contenedor.getComponents()) {
            if (comp.isVisible() && comp instanceof JPanel) {
                return (JPanel) comp;
            }
        }
        return null;
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

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
