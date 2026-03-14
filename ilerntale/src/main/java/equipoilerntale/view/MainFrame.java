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
 * Marco principal (ventana) de la aplicación.
 * Centraliza la gestión del flujo entre pantallas, la inicialización de controladores,
 * la gestión del HUD, diálogos y el bucle de renderizado.
 */
public class MainFrame extends JFrame {

    /** Registrador de eventos para la clase MainFrame. */
    private static final Logger LOG = Logger.getLogger(MainFrame.class.getName());

    public static final String SCREEN_MENU = "MENU";
    /** Nombre de la pantalla de selección de personajes. */
    public static final String SCREEN_PERSONAJES = "PERSONAJES";
    /** Nombre de la pantalla de combate (minijuegos). */
    public static final String SCREEN_COMBATE = "COMBATE";
    /** Nombre de la pantalla de derrota (Game Over). */
    public static final String SCREEN_DERROTA = "DERROTA";
    /** Nombre de la pantalla de video introductorio. */
    public static final String SCREEN_VIDEO = "VIDEO";
    /** Nombre de la pantalla de video de transformación del jefe. */
    public static final String SCREEN_TRANSFORMACION = "TRANSFORMACION_VIDEO";
    /** Nombre de la pantalla de video final de créditos. */
    public static final String SCREEN_FINAL_VIDEO = "FINAL_VIDEO";
    /** Nombre de la pantalla de exploración libre (mapas). */
    public static final String SCREEN_EXPLORACION = "EXPLORACION";
    /** Nombre de la pantalla de diálogos o eventos (GamePanel). */
    public static final String SCREEN_GAME = "GAME";
    /** Nombre de la pantalla del tutorial. */
    public static final String SCREEN_TUTORIAL = "TUTORIAL";

    /** Gestor de diseño para intercambiar entre pantallas. */
    private CardLayout cardLayout;
    /** Panel de capas para superponer HUD, diálogos, efectos y menús. */
    private JLayeredPane layeredPane;
    /** Contenedor principal donde residen todas las pantallas del CardLayout. */
    private JPanel contenedor;
    /** Identificador de la pantalla que se muestra actualmente. */
    private String pantallaActual = SCREEN_MENU;
    
    /** Panel del menú principal. */
    private MainMenu menu;
    /** Panel de selección de personajes. */
    private CharacterSelector personajes;
    /** Panel de pausa superpuesto. */
    private PausePanel pause;
    /** Panel de gestión del combate. */
    private CombatPanel combate;
    /** Pantalla de derrota. */
    private DerrotaScreen derrota;
    /** Pantalla de video inicial. */
    private VideoScreen videoScreen;
    /** Pantalla de video de transformación de jefe. */
    private TransformacionVideoScreen transformacionVideo;
    /** Pantalla de video de créditos finales. */
    private FinalVideoScreen finalVideoScreen;
    /** Panel de exploración en tiempo real. */
    private ExplorationPanel exploracion;
    /** Panel para secuencias de juego y diálogos. */
    private GamePanel gamePanel;
    /** Panel con las instrucciones del tutorial. */
    private TutorialPanel tutorial;
    /** Contenedor invisible para mostrar cajas de texto de diálogo. */
    private JPanel dialogueContainer;
    /** Panel de overlay para efectos de fundido (fade) entre pantallas. */
    private JPanel transitionOverlay;
    /** Nombre del personaje seleccionado por el usuario. */
    private String personajeSeleccionado = "";

    /** Controlador central de la lógica del juego. */
    private MainController mainController;
    /** Gestor específico de la exploración y eventos del mapa. */
    private ExplorationManager explorationManager;

    /** Barra de salud visual del jugador en el HUD. */
    private BarraVida playerHealthBar;
    /** Panel de la capa superior para elementos de interfaz. */
    private JPanel hudPanel;
    /** Icono visual que indica si el jugador posee la llave. */
    private Image keyIcon;

    /** Temporizador para gestionar la desaparición de diálogos automáticos. */
    private javax.swing.Timer dialogueTimer;
    /** Momento exacto en ms en el que debe finalizar el diálogo actual. */
    private long dialogueEndTime;
    /** Tiempo restante del diálogo pausado al entrar en el menú de pausa. */
    private int remainingDialogueTime = 0;
    /** Último texto mostrado en un diálogo temporizado. */
    private String currentTimedText = "";

    /**
     * CONSTRUCTOR DEL MARCO PRINCIPAL.
     * CONFIGURA LA VENTANA, INICIALIZA CONTROLADORES Y PANELES.
     */
    public MainFrame() {
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        setTitle("iLERNTALE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        try (InputStream is = getClass().getResourceAsStream("/title/titozeio.png")) {
            if (is != null) {
                setIconImage(ImageIO.read(is));
            } else {
                LOG.warning("No se pudo encontrar el icono de la ventana: /title/titozeio.png");
            }
        } catch (IOException e) {
            LOG.severe("Error cargando icono de ventana: " + e.getMessage());
        }

        inicializarControladores();

        playerHealthBar = new BarraVida(50, "JUGADOR");

        setupScreens();

        this.keyIcon = equipoilerntale.service.AssetService.getInstance().loadImage("/objects/llave.png");

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 600));

        contenedor.setBounds(0, 0, 1000, 600);
        layeredPane.add(contenedor, JLayeredPane.DEFAULT_LAYER);

        dialogueContainer = new JPanel(null);
        dialogueContainer.setBounds(0, 0, 1000, 600);
        dialogueContainer.setOpaque(false);
        dialogueContainer.setVisible(false);
        dialogueContainer.setFocusable(false);
        layeredPane.add(dialogueContainer, JLayeredPane.POPUP_LAYER);

        hudPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if ((pantallaActual.equals("EXPLORACION") || pantallaActual.equals("COMBATE"))
                        && !dialogueContainer.isVisible()) {
                    int healhX = getWidth() - 220;
                    int healthY = 35;
                    playerHealthBar.draw(g, healhX, healthY);

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

        pause.setBounds(0, 0, 1000, 600);
        pause.setVisible(false);
        layeredPane.add(pause, JLayeredPane.DRAG_LAYER);

        transitionOverlay = new JPanel(null);
        transitionOverlay.setBounds(0, 0, 1000, 600);
        transitionOverlay.setOpaque(false);
        transitionOverlay.setVisible(false);
        transitionOverlay.setFocusable(false);
        layeredPane.add(transitionOverlay, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (puedePausar()) {
                    togglePause();
                    return true;
                }
            }
            return false;
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        cambiarPantalla(SCREEN_MENU);
        mainController.startGameThread();

        iniciarRenderLoop();
    }

    /**
     * Instancia o recrea los paneles de todas las pantallas posibles.
     * Algunos paneles dependen del personaje seleccionado y se reconstruyen.
     */
    private void setupScreens() {
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

        exploracion = new ExplorationPanel(this, personajeSeleccionado, explorationManager);
        gamePanel = new GamePanel(this);

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

    /**
     * Inicializa los controladores principales con el personaje actual o el predeterminado.
     */
    private void inicializarControladores() {
        String personaje = (personajeSeleccionado == null || personajeSeleccionado.isEmpty()) ? "migue"
                : personajeSeleccionado;

        mainController = new MainController(this, personaje);
        explorationManager = mainController.getExplorationManager();
    }

    /**
     * Inicia un temporizador de Swing para solicitar el renderizado del HUD y paneles activos.
     */
    private void iniciarRenderLoop() {
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

        if (pantallaAnterior.equals(nombre)) {
            ejecutarCambioInstantaneo(nombre);
            return;
        }

        boolean deMenuATutorial = (pantallaAnterior.equals(SCREEN_MENU) && nombre.equals(SCREEN_TUTORIAL)) 
                               || (pantallaAnterior.equals(SCREEN_TUTORIAL) && nombre.equals(SCREEN_MENU));
        
        boolean desdePausa = (pause != null && pause.isVisible());

        boolean requiereTransicion = !deMenuATutorial && !desdePausa;

        if (requiereTransicion) {
            ejecutarCambioConTransicion(nombre);
        } else {
            ejecutarCambioInstantaneo(nombre);
        }
    }

    /**
     * Ejecuta el cambio de pantalla sin animaciones.
     * 
     * @param nombre Destino.
     */
    private void ejecutarCambioInstantaneo(String nombre) {
        logicaCambioPantalla(nombre);
        cardLayout.show(contenedor, nombre);
        postCambioPantalla(nombre);
    }

    /**
     * Ejecuta un cambio de pantalla con efecto de fade out.
     * Captura el estado actual del contenedor para el fundido.
     * 
     * @param nombre Destino.
     */
    private void ejecutarCambioConTransicion(String nombre) {
        final BufferedImage snapshot;
        if (contenedor.getWidth() > 0 && contenedor.getHeight() > 0) {
            snapshot = new BufferedImage(contenedor.getWidth(), contenedor.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = snapshot.createGraphics();
            contenedor.paintAll(g);
            g.dispose();
        } else {
            snapshot = null;
        }

        logicaCambioPantalla(nombre);
        cardLayout.show(contenedor, nombre);
        postCambioPantalla(nombre);

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

    /**
     * Limpia el estado de diálogos y audio antes de realizar un cambio de pantalla.
     * 
     * @param nombre Próxima pantalla.
     */
    private void logicaCambioPantalla(String nombre) {
        detenerDialogosExistentes();

        if (!nombre.equals(SCREEN_COMBATE) && !nombre.equals(SCREEN_TUTORIAL) && !nombre.equals(SCREEN_PERSONAJES)
                && !nombre.equals(SCREEN_MENU)) {
            SoundService.getInstance().stopBGM();
        }
    }

    /**
     * Realiza gestiones posteriores al cambio de visibilidad de una pantalla,
     * como iniciar audio específico o transferir el foco del teclado.
     * 
     * @param nombre Pantalla recién activada.
     */
    private void postCambioPantalla(String nombre) {
        if (nombre.equals(SCREEN_VIDEO)) {
            videoScreen.playVideo();
        }

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
     * Inicia el modo combate, preparando el panel de combate y cambiando la pantalla.
     * 
     * @param enemy Objeto que representa al enemigo contra el que se lucha.
     */
    public void entrarCombate(Object enemy) {
        if (combate != null) {
            combate.prepararCombate(enemy);
            cambiarPantalla("COMBATE");
        }
    }

    /**
     * Termina el combate, aplicando consecuencias (como eliminar enemigos) y volviendo al mapa.
     * 
     * @param victoria true si el jugador derrotó al enemigo.
     * @param enemy El enemigo involucrado.
     */
    public void finalizarCombate(boolean victoria, Object enemy) {
        if (victoria && explorationManager != null) {
            explorationManager.removeEnemy(enemy);
        }
        cambiarPantalla("EXPLORACION");
    }

    /**
     * Activa el evento de derrota de un jefe en fase de exploración y lanza el video.
     * 
     * @param boss Instancia del jefe derrotado.
     */
    public void triggerBossDefeated(Object boss) {
        if (explorationManager != null) {
            explorationManager.removeEnemy(boss);
        }
        cambiarPantalla("TRANSFORMACION_VIDEO");
    }

    /**
     * Prepara e inicia la fase 2 de combate final.
     */
    public void triggerPhase2() {
        if (combate != null) {
            combate.prepararFinalBoss();
            cambiarPantalla("COMBATE");
        }
    }

    /**
     * Alterna la visibilidad del menú de pausa y detiene/reanuda la lógica del juego.
     */
    public void togglePause() {
        if (pause.isVisible()) {
            pause.setVisible(false);
            if (mainController != null)
                mainController.resumeGame();

            reanudarDialogosExistentes();

            if (remainingDialogueTime > 0) {
                showTimedDialogue(currentTimedText, remainingDialogueTime);
                remainingDialogueTime = 0;
            }

            Component comp = getPanelActual();
            if (comp != null)
                comp.requestFocusInWindow();

        } else {
            pausarDialogosExistentes();

            if (dialogueTimer != null && dialogueTimer.isRunning()) {
                remainingDialogueTime = (int) (dialogueEndTime - System.currentTimeMillis());
                if (remainingDialogueTime < 0)
                    remainingDialogueTime = 0;
                dialogueTimer.stop();
            }

            pause.setVisible(true);
            if (mainController != null)
                mainController.pauseGame();
            pause.repaint();
        }
    }

    /**
     * Pausa las animaciones de texto o bucles de diálogo en el GamePanel.
     */
    private void pausarDialogosExistentes() {
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).pausarDialogoBucle();
        }
    }

    /**
     * Detiene y limpia cualquier diálogo que se esté mostrando actualmente.
     */
    private void detenerDialogosExistentes() {
        hideDialogue();
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).detenerDialogoBucle();
        }
    }

    /**
     * Reanuda los bucles de diálogo pausados.
     */
    private void reanudarDialogosExistentes() {
        JPanel actual = getPanelActual();
        if (actual instanceof GamePanel) {
            ((GamePanel) actual).reanudarDialogoBucle();
        }
    }

    /**
     * Muestra una caja de diálogo con texto en la posición por defecto.
     * 
     * @param text El contenido del diálogo.
     */
    public void showDialogue(String text) {
        showDialogue(text, 450);
    }

    /**
     * Muestra un diálogo en una coordenada Y específica.
     * 
     * @param text Texto del mensaje.
     * @param y Altura de la caja de texto.
     */
    public void showDialogue(String text, int y) {
        dialogueContainer.removeAll();
        JPanel panel = CajaTexto.crearPanel(text);
        panel.setLocation(250, y);
        dialogueContainer.add(panel);

        dialogueContainer.setVisible(true);
        dialogueContainer.revalidate();
        dialogueContainer.repaint();
    }

    /**
     * Muestra un diálogo que se cerrará automáticamente tras un retraso.
     * 
     * @param text El contenido del diálogo.
     * @param delay Tiempo de permanencia en milisegundos.
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

    /**
     * Oculta y limpia el contenedor de diálogos actual.
     */
    public void hideDialogue() {
        if (dialogueTimer != null) {
            dialogueTimer.stop();
        }
        remainingDialogueTime = 0;
        currentTimedText = "";
        dialogueContainer.removeAll();
        dialogueContainer.setVisible(false);
        dialogueContainer.revalidate();
        dialogueContainer.repaint();
    }

    /**
     * Verifica si se permite pausar el juego en el estado actual.
     * 
     * @return true si el juego no está en pantallas restringidas (Menú, Video, etc).
     */
    private boolean puedePausar() {
        return !pantallaActual.equals(SCREEN_MENU) &&
                !pantallaActual.equals(SCREEN_VIDEO) &&
                !pantallaActual.equals(SCREEN_TUTORIAL) &&
                !pantallaActual.equals(SCREEN_PERSONAJES);
    }

    /** @param controller El nuevo controlador principal. */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    /**
     * Establece el personaje del jugador y reconstruye el estado necesario.
     * 
     * @param nombre Nombre del nuevo personaje seleccionado.
     */
    public void setPersonajeSeleccionado(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            LOG.warning("EL NOMBRE NO PUEDE SER NULO. USANDO MIGUE.");
            nombre = "migue";
        }
        this.personajeSeleccionado = nombre.trim();

        if (mainController != null) {
            mainController.dispose();
        }

        inicializarControladores();
        mainController.startGameThread();

        setupScreens();
    }

    /**
     * Obtiene el componente JPanel que está visible en el CardLayout.
     * 
     * @return Panel actual o null si no se encuentra.
     */
    public JPanel getPanelActual() {
        for (Component comp : contenedor.getComponents()) {
            if (comp.isVisible() && comp instanceof JPanel) {
                return (JPanel) comp;
            }
        }
        return null;
    }

    /**
     * Restablece completamente el estado del juego (inventario, vida, enemigos, combate)
     * para permitir una nueva partida.
     */
    public void reiniciarJuego() {
        equipoilerntale.view.ui.Inventario.getInstance().limpiar();
        if (playerHealthBar != null) {
            playerHealthBar.setHealth(playerHealthBar.getMaxHealth());
        }
        if (combate != null) {
            combate.reiniciarEstado();
        }
        setPersonajeSeleccionado(getPersonajeSeleccionado());
    }

    // ============ GETTERS ============

    /** @return Nombre del personaje activo. */
    public String getPersonajeSeleccionado() {
        return personajeSeleccionado;
    }

    /** @return Panel de exploración actual. */
    public ExplorationPanel getExploracion() {
        return exploracion;
    }

    /** @return Selector de personajes. */
    public CharacterSelector getPersonajes() {
        return personajes;
    }

    /** @return Controlador principal. */
    public MainController getMainController() {
        return mainController;
    }

    /** @return Gestor de exploración. */
    public ExplorationManager getExplorationManager() {
        return explorationManager;
    }

    /** @return Representación visual de la barra de salud del jugador. */
    public BarraVida getPlayerHealthBar() {
        return playerHealthBar;
    }

    /**
     * Clase interna para gestionar superposiciones con canales alfa variables (fundidos).
     */
    private static class FadeOverlay extends JPanel {
        private final BufferedImage previousScreen;
        private float alpha = 1.0f;
        private boolean finished = false;

        /**
         * Crea un overlay basado en una captura de pantalla previa.
         * 
         * @param previousScreen Imagen capturada de la pantalla saliente.
         */
        public FadeOverlay(BufferedImage previousScreen) {
            this.previousScreen = previousScreen;
            setOpaque(false);
        }

        /**
         * Reduce el canal alfa para simular la desaparición del panel.
         */
        public void updateAlpha() {
            alpha -= 0.05f;
            if (alpha <= 0) {
                alpha = 0;
                finished = true;
            }
        }

        /** @return true si el fundido se ha completado (alpha = 0). */
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
