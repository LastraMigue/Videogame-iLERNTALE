package equipoilerntale.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import equipoilerntale.service.SoundService;

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

    // CONSTANTES DE PANTALLAS
    public static final String SCREEN_MENU = "MENU";
    public static final String SCREEN_PERSONAJES = "PERSONAJES";
    public static final String SCREEN_COMBATE = "COMBATE";
    public static final String SCREEN_DERROTA = "DERROTA";
    public static final String SCREEN_VIDEO = "VIDEO";
    public static final String SCREEN_TRANSFORMACION = "TRANSFORMACION_VIDEO";
    public static final String SCREEN_FINAL_VIDEO = "FINAL_VIDEO";
    public static final String SCREEN_EXPLORACION = "EXPLORACION";
    public static final String SCREEN_GAME = "GAME";
    public static final String SCREEN_TUTORIAL = "TUTORIAL";

    private CardLayout cardLayout;
    private JLayeredPane layeredPane;
    private JPanel contenedor;
    private String pantallaActual = SCREEN_MENU;
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
    private JPanel transitionOverlay;
    private String personajeSeleccionado = "";

    // CONTROLADORES ACCESIBLES
    private MainController mainController;
    private ExplorationManager explorationManager;

    private BarraVida playerHealthBar;
    private JPanel hudPanel;
    private Image keyIcon;

    // TEMPORIZADOR DE DIÁLOGOS
    private javax.swing.Timer dialogueTimer;
    private long dialogueEndTime;
    private int remainingDialogueTime = 0;
    private String currentTimedText = "";

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
        // ESTABLECER ICONO DEL JFRAME (JAR compatible)
        try (InputStream is = getClass().getResourceAsStream("/title/titozeio.png")) {
            if (is != null) {
                setIconImage(ImageIO.read(is));
            } else {
                LOG.warning("No se pudo encontrar el icono de la ventana: /title/titozeio.png");
            }
        } catch (IOException e) {
            LOG.severe("Error cargando icono de ventana: " + e.getMessage());
        }

        // INICIALIZAR CONTROLADORES
        inicializarControladores();

        // INICIALIZAR VIDA
        playerHealthBar = new BarraVida(50, "JUGADOR");

        // INICIALIZAR PANELES DE LAS PANTALLAS
        setupScreens();

        // CARGAR ICONO DE LLAVE
        this.keyIcon = equipoilerntale.service.AssetService.getInstance().loadImage("/objects/llave.png");

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
                    // Dibujar arriba a la derecha (Barra de vida)
                    int healhX = getWidth() - 220;
                    int healthY = 35;
                    playerHealthBar.draw(g, healhX, healthY);

                    // Dibujar icono de llave si la tiene
                    if (keyIcon != null) {
                        boolean tieneLlave = false;
                        for (equipoilerntale.model.entity.ItemModel item : equipoilerntale.view.ui.Inventario.getInstance().getItems()) {
                            if ("Llave".equals(item.getNombre()) && item.getCantidad() > 0 && !item.isUsado()) {
                                tieneLlave = true;
                                break;
                            }
                        }
                        if (tieneLlave) {
                            g.drawImage(keyIcon, healhX - 55, healthY - 5, 40, 40, null);
                        }
                    }
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

        // Capa para transiciones (entre HUD y Pausa)
        transitionOverlay = new JPanel(null);
        transitionOverlay.setBounds(0, 0, 1000, 600);
        transitionOverlay.setOpaque(false);
        transitionOverlay.setVisible(false);
        transitionOverlay.setFocusable(false);
        layeredPane.add(transitionOverlay, JLayeredPane.PALETTE_LAYER);

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
        cambiarPantalla(SCREEN_MENU);

        // INICIAR EL HILO LÓGICO DEL JUEGO (GAME LOOP)
        mainController.startGameThread();

        // INICIAR BUCLE DE RENDERIZADO (60 FPS)
        iniciarRenderLoop();

        LOG.info("MAINFRAME INICIALIZADO CORRECTAMENTE");
    }

    private void setupScreens() {
        // Solo instanciamos los paneles que son estáticos (no cambian con el personaje)
        // si no han sido creados ya.
        if (menu == null) menu = new MainMenu(this);
        if (personajes == null) personajes = new CharacterSelector(this);
        if (pause == null) pause = new PausePanel(this);
        if (combate == null) combate = new CombatPanel(this);
        if (derrota == null) derrota = new DerrotaScreen(this);
        if (videoScreen == null) {
            videoScreen = new VideoScreen(this);
            videoScreen.setName(SCREEN_VIDEO);
        }
        if (transformacionVideo == null) transformacionVideo = new TransformacionVideoScreen(this);
        if (finalVideoScreen == null) finalVideoScreen = new FinalVideoScreen(this);
        if (tutorial == null) tutorial = new TutorialPanel(this);

        // Los paneles que dependen del personaje se recrean siempre
        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);
        gamePanel = new GamePanel(this);

        // Refrescar el contenedor
        contenedor.removeAll();
        contenedor.add(menu, SCREEN_MENU);
        contenedor.add(personajes, SCREEN_PERSONAJES);
        contenedor.add(combate, SCREEN_COMBATE);
        contenedor.add(derrota, SCREEN_DERROTA);
        contenedor.add(videoScreen, SCREEN_VIDEO);
        contenedor.add(transformacionVideo, SCREEN_TRANSFORMACION);
        contenedor.add(finalVideoScreen, SCREEN_FINAL_VIDEO);
        contenedor.add(exploracion, SCREEN_EXPLORACION);
        contenedor.add(gamePanel, SCREEN_GAME);
        contenedor.add(tutorial, SCREEN_TUTORIAL);
        
        contenedor.revalidate();
        contenedor.repaint();
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
        String pantallaAnterior = this.pantallaActual;
        this.pantallaActual = nombre;

        // No transicionar si es la misma pantalla (evita fallos al inicio)
        if (pantallaAnterior.equals(nombre)) {
            ejecutarCambioInstantaneo(nombre);
            return;
        }

        // Determinar si se requiere transición
        boolean deMenuATutorial = (pantallaAnterior.equals(SCREEN_MENU) && nombre.equals(SCREEN_TUTORIAL)) 
                               || (pantallaAnterior.equals(SCREEN_TUTORIAL) && nombre.equals(SCREEN_MENU));
        
        // El usuario pidió omitir transiciones desde el PausePanel.
        // Como el PausePanel no es una "pantalla" en el CardLayout per se,
        // detectamos si el panel de pausa estaba visible antes de cambiar.
        boolean desdePausa = (pause != null && pause.isVisible());

        boolean requiereTransicion = !deMenuATutorial && !desdePausa;

        if (requiereTransicion) {
            ejecutarCambioConTransicion(nombre);
        } else {
            ejecutarCambioInstantaneo(nombre);
        }
    }

    private void ejecutarCambioInstantaneo(String nombre) {
        logicaCambioPantalla(nombre);
        cardLayout.show(contenedor, nombre);
        postCambioPantalla(nombre);
    }

    private void ejecutarCambioConTransicion(String nombre) {
        // 1. Capturar pantalla actual (solo si el contenedor tiene tamaño válido)
        final BufferedImage snapshot;
        if (contenedor.getWidth() > 0 && contenedor.getHeight() > 0) {
            snapshot = new BufferedImage(contenedor.getWidth(), contenedor.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = snapshot.createGraphics();
            contenedor.paintAll(g);
            g.dispose();
        } else {
            snapshot = null;
        }

        // 2. Ejecutar lógica de cambio (detener sonidos, diálogos, etc)
        logicaCambioPantalla(nombre);

        // 3. Cambiar panel en el CardLayout (invisible debajo del overlay)
        cardLayout.show(contenedor, nombre);

        // 4. Ejecutar lógica post-cambio INMEDIATAMENTE para evitar fallos de lógica
        postCambioPantalla(nombre);

        // 5. Iniciar overlay de transición
        if (snapshot != null) {
            transitionOverlay.removeAll();
            FadeOverlay fade = new FadeOverlay(snapshot);
            fade.setBounds(0, 0, 1000, 600);
            transitionOverlay.add(fade);
            transitionOverlay.setVisible(true);

            javax.swing.Timer timer = new javax.swing.Timer(16, null);
            timer.addActionListener(e -> {
                fade.updateAlpha();
                if (fade.isFinished()) {
                    timer.stop();
                    transitionOverlay.setVisible(false);
                    transitionOverlay.removeAll();
                }
                transitionOverlay.repaint();
            });
            timer.start();
        }
    }

    private void logicaCambioPantalla(String nombre) {
        // DETENER DIÁLOGOS ANTES DE CAMBIAR (HARD STOP)
        detenerDialogosExistentes();

        // DETENER CUALQUIER MÚSICA DE FONDO ANTERIOR AL CAMBIAR DE PANTALLA
        if (!nombre.equals(SCREEN_COMBATE) && !nombre.equals(SCREEN_TUTORIAL) && !nombre.equals(SCREEN_PERSONAJES)
                && !nombre.equals(SCREEN_MENU)) {
            SoundService.getInstance().stopBGM();
        }
    }

    private void postCambioPantalla(String nombre) {
        // Si vamos a la pantalla del video intro, iniciamos el video
        if (nombre.equals(SCREEN_VIDEO)) {
            videoScreen.playVideo();
        }

        // GESTIÓN DE MÚSICA DE FONDO (BGM)
        switch (nombre) {
            case SCREEN_MENU:
            case SCREEN_TUTORIAL:
            case SCREEN_PERSONAJES:
                SoundService.getInstance().playBGM("/sound/menu.wav");
                break;
            case SCREEN_EXPLORACION:
                SoundService.getInstance().playBGM("/sound/mapa.wav");
                break;
            case SCREEN_COMBATE:
                break;
            case SCREEN_GAME:
                SoundService.getInstance().playBGM("/sound/dialogo.wav");
                break;
            default:
                break;
        }

        if (nombre.equals(SCREEN_TRANSFORMACION)) {
            transformacionVideo.playVideo();
        }
        if (nombre.equals(SCREEN_FINAL_VIDEO)) {
            finalVideoScreen.playVideo();
        }

        boolean mostrarHUD = nombre.equals(SCREEN_EXPLORACION) || nombre.equals(SCREEN_COMBATE);
        if (hudPanel != null) {
            hudPanel.setVisible(mostrarHUD);
        }
        if (!mostrarHUD && dialogueContainer != null) {
            dialogueContainer.setVisible(false);
        }

        if (!"EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.dispose();
        }

        // Foco
        if ("EXPLORACION".equals(nombre) && exploracion != null) {
            exploracion.reset();
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
     * DERROTA AL BOSS FASE 1, LO ELIMINA DEL MAPA Y REPRODUCE EL VIDEO DE
     * TRANSFORMACIÓN.
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

            // REANUDAR TEMPORIZADOR DE DIÁLOGO
            if (remainingDialogueTime > 0) {
                showTimedDialogue(currentTimedText, remainingDialogueTime);
                remainingDialogueTime = 0;
            }

            Component comp = getPanelActual();
            if (comp != null)
                comp.requestFocusInWindow();

        } else {
            // DETENER DIÁLOGOS AL PAUSAR, PERO SIN OCULTARLOS VISUALMENTE
            pausarDialogosExistentes();

            // PAUSAR TEMPORIZADOR DE DIÁLOGO
            if (dialogueTimer != null && dialogueTimer.isRunning()) {
                remainingDialogueTime = (int) (dialogueEndTime - System.currentTimeMillis());
                if (remainingDialogueTime < 0)
                    remainingDialogueTime = 0;
                dialogueTimer.stop();
            }

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

    /**
     * Muestra un diálogo que desaparece automáticamente tras el tiempo indicado.
     */
    public void showTimedDialogue(String text, int delay) {
        if (dialogueTimer != null) {
            dialogueTimer.stop();
        }
        currentTimedText = text;
        dialogueEndTime = System.currentTimeMillis() + delay;
        showDialogue(text);
        dialogueTimer = new javax.swing.Timer(delay, e -> hideDialogue());
        dialogueTimer.setRepeats(false);
        dialogueTimer.start();
    }

    public void hideDialogue() {
        if (dialogueTimer != null) {
            dialogueTimer.stop();
        }
        remainingDialogueTime = 0;
        currentTimedText = "";
        dialogueContainer.removeAll();
        dialogueContainer.setVisible(false); // Ocultar al cerrar diálogo
        dialogueContainer.revalidate();
        dialogueContainer.repaint();
    }

    private boolean puedePausar() {
        // No permitir pausa en ciertas pantallas
        return !pantallaActual.equals(SCREEN_MENU) &&
                !pantallaActual.equals(SCREEN_VIDEO) &&
                !pantallaActual.equals(SCREEN_TUTORIAL) &&
                !pantallaActual.equals(SCREEN_PERSONAJES);
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

        setupScreens();

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

    /**
     * CLASE INTERNA PARA EFECTO DE TRANSICIÓN (FADE OUT).
     */
    private static class FadeOverlay extends JPanel {
        private final BufferedImage previousScreen;
        private float alpha = 1.0f;
        private boolean finished = false;

        public FadeOverlay(BufferedImage previousScreen) {
            this.previousScreen = previousScreen;
            setOpaque(false);
        }

        public void updateAlpha() {
            alpha -= 0.05f; // Ajustar para velocidad (aprox 400ms a 60fps)
            if (alpha <= 0) {
                alpha = 0;
                finished = true;
            }
        }

        public boolean isFinished() {
            return finished;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (previousScreen != null && alpha > 0) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(previousScreen, 0, 0, null);
                g2d.dispose();
            }
        }
    }
}
