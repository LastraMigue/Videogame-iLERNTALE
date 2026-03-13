package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.controller.MainController;
import equipoilerntale.view.screens.CharacterSelector;
import equipoilerntale.view.screens.CombatPanel;
import equipoilerntale.view.screens.DerrotaScreen;
import equipoilerntale.view.screens.ExplorationPanel;
import equipoilerntale.view.screens.GamePanel;
import equipoilerntale.view.screens.MainMenu;
import equipoilerntale.view.screens.PausePanel;
import equipoilerntale.view.screens.TutorialPanel;
import equipoilerntale.view.screens.VideoScreen;
import equipoilerntale.view.screens.TransformacionVideoScreen;
import equipoilerntale.view.screens.FinalVideoScreen;
import equipoilerntale.view.ui.CajaTexto;
import equipoilerntale.view.ui.BarraVida;

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
    private DerrotaScreen derrota;
    private VideoScreen videoScreen;
    private TransformacionVideoScreen transformacionVideo;
    private FinalVideoScreen finalVideoScreen;
    private ExplorationPanel exploracion;
    private GamePanel gamePanel;
    private TutorialPanel tutorial;
    private JPanel dialogueContainer;
    private String personajeSeleccionado = "";

    // CONTROLADORES ACCESIBLES
    private MainController mainController;
    private ExplorationManager explorationManager;

    // HUD Y VIDA
    private BarraVida playerHealthBar;
    private JPanel hudPanel;

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

        // INICIALIZAR VIDA
        playerHealthBar = new BarraVida(50, "JUGADOR");

        // INICIALIZAR PANELES DE LAS PANTALLAS
        menu = new MainMenu(this);
        personajes = new CharacterSelector(this);
        pause = new PausePanel(this);
        combate = new CombatPanel(this);
        derrota = new DerrotaScreen(this);
        videoScreen = new VideoScreen(this);
        transformacionVideo = new TransformacionVideoScreen(this);
        finalVideoScreen = new FinalVideoScreen(this);
        tutorial = new TutorialPanel(this);
        gamePanel = new GamePanel(this);

        // CREAR EXPLORATIONPANEL CON EL MANAGER LÓGICO
        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);

        // AÑADIR PANELES AL CONTENEDOR DE CARDLAYOUT
        contenedor.add(menu, "MENU");
        contenedor.add(personajes, "PERSONAJES");
        contenedor.add(combate, "COMBATE");
        contenedor.add(derrota, "DERROTA");
        videoScreen.setName("VIDEO"); // Útil para exclusiones
        contenedor.add(videoScreen, "VIDEO");
        contenedor.add(transformacionVideo, "TRANSFORMACION_VIDEO");
        contenedor.add(finalVideoScreen, "FINAL_VIDEO");
        contenedor.add(exploracion, "EXPLORACION");
        contenedor.add(gamePanel, "GAME");
        contenedor.add(tutorial, "TUTORIAL");

        // Configuramos el LayeredPane para el overlay
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 600));

        // Capa inferior: El contenedor principal con CardLayout
        contenedor.setBounds(0, 0, 1000, 600);
        layeredPane.add(contenedor, JLayeredPane.DEFAULT_LAYER);

        // Capa para diálogos
        dialogueContainer = new JPanel(null);
        dialogueContainer.setBounds(0, 0, 1000, 600);
        dialogueContainer.setOpaque(false);
        dialogueContainer.setVisible(false);
        dialogueContainer.setFocusable(false); // No bloquear clics
        layeredPane.add(dialogueContainer, JLayeredPane.POPUP_LAYER);

        // HUD: Vida del jugador
        hudPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if ((pantallaActual.equals("EXPLORACION") || pantallaActual.equals("COMBATE"))
                        && !dialogueContainer.isVisible()) {
                    // Dibujar arriba a la derecha
                    playerHealthBar.draw(g, getWidth() - 220, 35);
                }
            }
        };
        hudPanel.setBounds(0, 0, 1000, 600);
        hudPanel.setOpaque(false);
        hudPanel.setFocusable(false);
        layeredPane.add(hudPanel, JLayeredPane.POPUP_LAYER);

        // Capa para el panel de pausa (encima de todo)
        pause.setBounds(0, 0, 1000, 600);
        pause.setVisible(false);
        layeredPane.add(pause, JLayeredPane.DRAG_LAYER);

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

        // Cambiar a la pantalla inicial del MENU
        cambiarPantalla("MENU");

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
            if (hudPanel != null) {
                hudPanel.repaint();
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

        // Si vamos a la pantalla del video intro, iniciamos el video
        if (nombre.equals("VIDEO")) {
            videoScreen.playVideo();
        }
        // Si vamos a la pantalla de transformación del boss
        if (nombre.equals("TRANSFORMACION_VIDEO")) {
            transformacionVideo.playVideo();
        }
        // Si vamos al video final
        if (nombre.equals("FINAL_VIDEO")) {
            finalVideoScreen.playVideo();
        }

        // Gestión de visibilidad del HUD y Diálogos para no bloquear el ratón
        boolean mostrarHUD = nombre.equals("EXPLORACION") || nombre.equals("COMBATE");
        if (hudPanel != null) {
            hudPanel.setVisible(mostrarHUD);
        }
        // El diálogo solo se muestra cuando hay texto, pero aseguramos que esté oculto al cambiar
        if (!mostrarHUD && dialogueContainer != null) {
            dialogueContainer.setVisible(false);
        }

        // Al salir de EXPLORACION: desactivar (pausa) en lugar de destruir assets
        if (!"EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.dispose(); // Llama a manager.deactivate() — no destruye assets
        }

        cardLayout.show(contenedor, nombre);

        // Dar foco y activar lógica de juego cuando se muestra EXPLORACION
        if ("EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.reset(); // Llama a manager.activate()
            exploracion.requestFocusInWindow();
        } else if ("COMBATE".equals(nombre) && combate != null) {
            combate.requestFocusInWindow();
        } else {
            JPanel actual = getPanelActual();
            if (actual != null) {
                actual.requestFocusInWindow();
            }
        }
    }

    /**
     * INICIA EL COMBATE CON UN ENEMIGO ESPECÍFICO.
     */
    public void entrarCombate(Object enemy) {
        if (combate != null) {
            combate.prepararCombate(enemy);
            cambiarPantalla("COMBATE");
        }
    }

    /**
     * FINALIZA EL COMBATE Y VUELVE A LA EXPLORACIÓN.
     * 
     * @param victoria Indica si el jugador ganó (elimina al enemigo) o no.
     */
    public void finalizarCombate(boolean victoria, Object enemy) {
        if (victoria && explorationManager != null) {
            explorationManager.removeEnemy(enemy);
        }
        cambiarPantalla("EXPLORACION");
    }

    /**
     * DERROTA AL BOSS FASE 1, LO ELIMINA DEL MAPA Y REPRODUCE EL VIDEO DE TRANSFORMACIÓN.
     * Al terminar el video, se inicia automáticamente la Fase 2.
     */
    public void triggerBossDefeated(Object boss) {
        if (explorationManager != null) {
            explorationManager.removeEnemy(boss);
        }
        cambiarPantalla("TRANSFORMACION_VIDEO");
    }

    /**
     * INICIA LA FASE 2 DEL BOSS DIRECTAMENTE en el CombatPanel.
     * Se llama desde TransformacionVideoScreen al terminar el video.
     */
    public void triggerPhase2() {
        if (combate != null) {
            combate.prepararFinalBoss();
            cambiarPantalla("COMBATE");
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
            if (comp != null)
                comp.requestFocusInWindow();

        } else {
            // DETENER DIÁLOGOS AL PAUSAR, PERO SIN OCULTARLOS VISUALMENTE
            pausarDialogosExistentes();

            pause.setVisible(true);
            if (mainController != null)
                mainController.pauseGame();
            // Asegurar que el panel de pausa se redibuje
            pause.repaint();
        }
    }

    /**
     * PAUSA LOS DIÁLOGOS EN CUALQUIER PANEL QUE ESTÉ ACTIVO SIN OCULTARLOS.
     */
    private void pausarDialogosExistentes() {
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).pausarDialogoBucle();
        }
    }

    /**
     * DETIENE Y OCULTA LOS DIÁLOGOS COMPLETAMENTE (al cambiar de pantalla).
     */
    private void detenerDialogosExistentes() {
        hideDialogue();
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).detenerDialogoBucle();
        }
    }

    /**
     * REANUDA LOS DIÁLOGOS SI EL PANEL LO SOPORTA.
     */
    private void reanudarDialogosExistentes() {
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).reanudarDialogoBucle();
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
        
        // Solo mostrar si la pantalla actual lo permite o es necesario
        dialogueContainer.setVisible(true); 
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
                !pantallaActual.equals("TUTORIAL") &&
                !pantallaActual.equals("PERSONAJES");
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
        contenedor.add(derrota, "DERROTA");
        videoScreen.setName("VIDEO");
        contenedor.add(videoScreen, "VIDEO");
        contenedor.add(transformacionVideo, "TRANSFORMACION_VIDEO");
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

    /**
     * REINICIA EL ESTADO COMPLETO DEL JUEGO.
     */
    public void reiniciarJuego() {
        // Reiniciar inventario
        equipoilerntale.view.ui.Inventario.getInstance().limpiar();
        // Reiniciar vida
        if (playerHealthBar != null) {
            playerHealthBar.setHealth(playerHealthBar.getMaxHealth());
        }
        // Reiniciar estados de combate (fase boss, controles, etc)
        if (combate != null) {
            combate.reiniciarEstado();
        }
        // Reiniciar variables y controladores reconstruyendo el ExplorationManager
        setPersonajeSeleccionado(getPersonajeSeleccionado());
        
        LOG.info("JUEGO REINICIADO COMPLETAMENTE");
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

    public BarraVida getPlayerHealthBar() {
        return playerHealthBar;
    }
}
