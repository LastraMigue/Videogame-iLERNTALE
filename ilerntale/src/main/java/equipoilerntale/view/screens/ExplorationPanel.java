package equipoilerntale.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import equipoilerntale.GameSettings;
import equipoilerntale.controller.ExplorationManager;
import equipoilerntale.model.entity.Zombie;
import equipoilerntale.model.entity.Boss;
import equipoilerntale.model.map.DoorModel;
import equipoilerntale.service.AssetService;
import equipoilerntale.view.MainFrame;
import equipoilerntale.view.render.*;
import equipoilerntale.view.ui.CajaTexto;

/**
 * PANEL PRINCIPAL DEL JUEGO.
 * CONTIENE:
 * - INTRO: Diálogos narrativos de Soraya y Jesica
 * - EXPLORACIÓN: Juego con jugador, zombies y mapa
 */
public class ExplorationPanel extends JPanel {

    private static final Logger LOG = Logger.getLogger(ExplorationPanel.class.getName());

    // ==================== CONSTANTES ====================
    private static final String[][] SCRIPT_INTRO = {
            { "Soraya", "¡Hola! ¡Hola!¡Hola!Soy Soraya. Bienvenido al mundo de iLERNTALE." },
            { "Jesica", "¡Y yo soy Jesica! ¡Qué emoción tenerte aquí!" },
            { "Soraya", "En iLERNTALE exploraremos el instituto juntos." },
            { "Jesica", "¡Vamos a explorar juntos! ¡Usa las flechas!" }
    };

    private static final int RETRASO_DIALOGO = 3000;
    private static final int DURACION_DIALOGO = 4000;

    // ==================== ATRIBUTOS ====================
    private final MainFrame mainFrame;
    private final ExplorationManager manager;
    private final MapRenderer mapRenderer = new MapRenderer();
    private final PlayerRenderer playerRenderer = new PlayerRenderer();
    private final ZombieRenderer zombieRenderer = new ZombieRenderer();
    private final BossRenderer bossRenderer = new BossRenderer();

    private Image backgroundIntro;
    private Image currentBackground;

    private final JLabel labelFondoIntro = new JLabel();
    private final JLabel labelSoraya = new JLabel();
    private final JLabel labelJesica = new JLabel();

    private int cameraX, cameraY;
    private boolean modoIntro = true;
    private int indicePasos = 0;
    private Timer timerHistoria;

    // ==================== CONSTRUCTOR ====================
    public ExplorationPanel(MainFrame mainFrame, String characterName, ExplorationManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;

        setPreferredSize(new Dimension(GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA));
        setLayout(null);
        setFocusable(true);
        addKeyListener(manager.getInputHandler());

        inicializarRecursosIntro();
        inicializarRecursosExploracion();
        configurarCicloVida();

        LOG.info("EXPLORATIONPANEL INICIALIZADO");
    }

    // ==================== INICIALIZACIÓN ====================
    private void inicializarRecursosIntro() {
        backgroundIntro = cargarFondoIntroRecortado("/mapa/pasillo.png", GameSettings.ANCHO_PANTALLA,
                GameSettings.ALTO_PANTALLA);
        ImageIcon iconoSoraya = cargarImagen("/dialogue/soraya.png", 256, 256);
        ImageIcon iconoJesica = cargarImagen("/dialogue/jesica.png", 256, 256);

        if (backgroundIntro != null) {
            labelFondoIntro.setIcon(new ImageIcon(backgroundIntro));
        }
        labelFondoIntro.setBounds(0, 0, GameSettings.ANCHO_PANTALLA, GameSettings.ALTO_PANTALLA);

        if (iconoSoraya != null) {
            labelSoraya.setIcon(iconoSoraya);
        }
        labelSoraya.setBounds(0, 344, 256, 256);
        labelSoraya.setVisible(false);

        if (iconoJesica != null) {
            labelJesica.setIcon(iconoJesica);
        }
        labelJesica.setBounds(750, 344, 256, 256);
        labelJesica.setVisible(false);

        add(labelFondoIntro);
        add(labelSoraya);
        add(labelJesica);

        // Asegurar que los personajes queden ENCIMA del fondo (Z-order 0 = frente)
        setComponentZOrder(labelJesica, 0);
        setComponentZOrder(labelSoraya, 1);
        setComponentZOrder(labelFondoIntro, 2);
    }

    private void inicializarRecursosExploracion() {
        // EL FONDO SE CARGARÁ DINÁMICAMENTE POR HABITACIÓN EN dibujarExploracion(),
        // AQUÍ NO HACEMOS NADA PERO SE MANTIENE POR SI AÑADES OTROS EFECTOS GLOBALES.
        Image backgroundExploracion = cargarFondo("/mapa/pasillo.png", GameSettings.ANCHO_PANTALLA,
                GameSettings.ALTO_PANTALLA);
    }

    private void configurarCicloVida() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (modoIntro) {
                    indicePasos = 0;
                    timerHistoria = new Timer(RETRASO_DIALOGO, evt -> avanzarHistoria());
                    timerHistoria.setRepeats(false);
                    timerHistoria.start();
                } else if (manager != null) {
                    manager.activate();
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (timerHistoria != null && timerHistoria.isRunning()) {
                    timerHistoria.stop();
                }
            }
        });
    }

    // ==================== CARGA DE RECURSOS ====================
    private Image cargarFondoIntroRecortado(String ruta, int anchoPantalla, int altoPantalla) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.warning("NO SE ENCONTRÓ EL RECURSO: " + ruta);
            return null;
        }
        try {
            // 1. Cargamos la imagen original
            Image originalImg = new ImageIcon(url).getImage();

            // 2. Creamos un buffer con el tamaño exacto al que se escalaría el MAPA
            // COMPLETO de exploración
            BufferedImage mapaCompleto = new BufferedImage(GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = mapaCompleto.createGraphics();
            // Lo pintamos ajustándolo a su tamaño de Exploración real
            g2d.drawImage(originalImg, 0, 0, GameSettings.MAP_WIDTH, GameSettings.MAP_HEIGHT, null);
            g2d.dispose();

            // 3. Recortamos el trozo exacto de la pantalla de la esquina superior izquierda
            // (0,0) u otra si la cámara inicia ahí.
            // Esto asegura que se vea EXACTAMENTE igual que cuando arranca el juego en x=0,
            // sin distorsión.
            return mapaCompleto.getSubimage(0, 0, anchoPantalla, altoPantalla);

        } catch (Exception e) {
            LOG.warning("Error recortando fondo intro: " + e.getMessage());
            return null;
        }
    }

    private Image cargarFondo(String ruta, int w, int h) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.warning("NO SE ENCONTRÓ EL RECURSO: " + ruta);
            return null;
        }
        return new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    private ImageIcon cargarImagen(String ruta, int w, int h) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.warning("NO SE ENCONTRÓ EL RECURSO: " + ruta);
            return null;
        }
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // ==================== SISTEMA DE DIÁLOGO ====================
    private void avanzarHistoria() {
        if (indicePasos >= SCRIPT_INTRO.length) {
            finalizarIntro();
            return;
        }

        String personaje = SCRIPT_INTRO[indicePasos][0];
        String texto = SCRIPT_INTRO[indicePasos][1];

        labelSoraya.setVisible(personaje.equals("Soraya"));
        labelJesica.setVisible(personaje.equals("Jesica"));

        Point p = (isShowing()) ? getLocationOnScreen() : new Point(0, 0);
        JDialog caja = CajaTexto.crearDialogo(mainFrame, texto, p.x + 250, p.y + 450);

        Timer timerCierre = new Timer(DURACION_DIALOGO, e -> {
            caja.dispose();
            indicePasos++;
            avanzarHistoria();
        });
        timerCierre.setRepeats(false);
        timerCierre.start();
        caja.setVisible(true);
    }

    private void finalizarIntro() {
        if (timerHistoria != null && timerHistoria.isRunning()) {
            timerHistoria.stop();
        }

        for (java.awt.Window w : java.awt.Window.getWindows()) {
            if (w instanceof JDialog) {
                w.dispose();
            }
        }

        modoIntro = false;
        labelFondoIntro.setVisible(false);
        labelSoraya.setVisible(false);
        labelJesica.setVisible(false);

        if (manager != null) {
            manager.activate();
        }
        
        requestFocusInWindow();
        LOG.info("INTRO FINALIZADA - EXPLORACIÓN INICIADA");
    }

    public void reiniciarIntro() {
        modoIntro = true;
        indicePasos = 0;
        labelFondoIntro.setVisible(true);
        labelSoraya.setVisible(false);
        labelJesica.setVisible(false);
        requestFocusInWindow();
        LOG.info("INTRO REINICIADA");
    }

    // ==================== RENDERIZADO ====================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (modoIntro) {
            // Los componentes JLabel ya se dibujan automáticamente
        } else {
            dibujarExploracion(g);
        }
    }

    private void dibujarExploracion(Graphics g) {
        if (manager == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        calculateCamera();
        RenderContext ctx = new RenderContext(g2d, getWidth(), getHeight(), cameraX, cameraY);
        ctx.translateCamera();

        // CARGAR EL FONDO DINÁMICAMENTE SEGÚN LA HABITACIÓN ACTUAL
        if (manager.getCurrentRoom() != null) {
            if (currentBackground == null || !currentBackground
                    .equals(AssetService.getInstance().loadBackground(manager.getCurrentRoom().getBackgroundPath()))) {
                currentBackground = AssetService.getInstance()
                        .loadBackground(manager.getCurrentRoom().getBackgroundPath());
            }
            mapRenderer.drawBackground(ctx, currentBackground);
        }

        if (manager.isDebugMurosVisibles() && manager.getCurrentRoom() != null) {
            for (DoorModel door : manager.getCurrentRoom().getDoors()) {
                mapRenderer.drawZoneLabel(ctx, door.getArea(), door.getTargetRoomName());
            }
        }

        playerRenderer.drawPlayer(ctx, manager.getPlayerCurrentSprite(), manager.getPlayer());

        for (Zombie z : manager.getActiveZombies()) {
            zombieRenderer.drawZombie(ctx, z);
        }

        for (Boss b : manager.getActiveBosses()) {
            bossRenderer.drawBoss(ctx, b);
        }

        if (manager.isDebugMurosVisibles() && manager.getCurrentRoom() != null) {
            g2d.setStroke(new BasicStroke(2));

            // Dibujar muros y puerta
            g2d.setColor(Color.RED);
            for (Rectangle wall : manager.getCurrentRoom().getWalls()) {
                g2d.draw(wall);
            }
            g2d.setColor(Color.GREEN);
            for (DoorModel door : manager.getCurrentRoom().getDoors()) {
                g2d.draw(door.getArea());
            }

            // Dibujar hitboxes
            g2d.setColor(Color.BLUE);
            g2d.draw(manager.getPlayer().getHitbox(manager.getPlayer().getX(), manager.getPlayer().getY()));
            for (Zombie z : manager.getActiveZombies()) {
                g2d.draw(z.getHitbox(z.getX(), z.getY()));
            }
            for (Boss b : manager.getActiveBosses()) {
                g2d.draw(b.getHitbox(b.getX(), b.getY()));
            }
        }

        ctx.restoreCamera();
    }

    private void calculateCamera() {
        if (manager == null || manager.getPlayer() == null)
            return;

        cameraX = Math.max(0,
                Math.min(manager.getPlayer().getX() - getWidth() / 2, GameSettings.MAP_WIDTH - getWidth()));
        cameraY = Math.max(0,
                Math.min(manager.getPlayer().getY() - getHeight() / 2, GameSettings.MAP_HEIGHT - getHeight()));
    }

    // ==================== EVENTOS PÚBLICOS ====================
    public void requestRender() {
        if (isShowing())
            repaint();
    }

    public void dispose() {
        if (manager != null)
            manager.deactivate();
        if (timerHistoria != null && timerHistoria.isRunning())
            timerHistoria.stop();
    }

    public void reset() {
        if (modoIntro) {
            reiniciarIntro();
        } else if (manager != null) {
            manager.activate();
        }
    }

    public boolean isModoIntro() {
        return modoIntro;
    }
}
