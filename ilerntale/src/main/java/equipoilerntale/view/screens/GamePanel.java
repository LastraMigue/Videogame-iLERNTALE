package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import equipoilerntale.view.MainFrame;

/**
 * Panel que presenta la secuencia narrativa inicial del juego mediante diálogos.
 * Utiliza personajes (Soraya y Jesica) para introducir el contexto histórico y 
 * mecánicas básicas al jugador.
 */
public class GamePanel extends JPanel {
    /** Logger para el registro de eventos de la secuencia narrativa. */
    private static final Logger LOG = Logger.getLogger(GamePanel.class.getName());
    /** Referencia al marco principal para gestionar transiciones. */
    private MainFrame mainFrame;
    /** Ancho base de la resolución del panel. */
    public static final int WIDTH = 800;
    /** Alto base de la resolución del panel. */
    public static final int HEIGHT = 600;

    /** Tamaño base para los sprites de los personajes NPC. */
    private static final int TAMANO_PERSONAJE = 256;
    /** Posición X de Soraya en pantalla. */
    private static final int X_SORAYA = 0;
    /** Posición Y de Soraya en pantalla. */
    private static final int Y_SORAYA = 344;
    /** Posición X de Jesica en pantalla. */
    private static final int X_JESICA = 544;
    /** Posición Y de Jesica en pantalla. */
    private static final int Y_JESICA = 344;

    /** Etiqueta para mostrar la imagen de fondo. */
    private JLabel labelFondo;
    /** Etiqueta para mostrar el sprite de Soraya. */
    private JLabel labelSoraya;
    /** Etiqueta para mostrar el sprite de Jesica. */
    private JLabel labelJesica;
    /** Icono cargado de Soraya. */
    private ImageIcon iconoSoraya;
    /** Icono cargado de Jesica. */
    private ImageIcon iconoJesica;
    /** Temporizador para avanzar al siguiente paso del diálogo. */
    private Timer timerSiguienteDialogo;
    /** Temporizador para cerrar el globo de diálogo actual. */
    private Timer timerCierre;
    /** Temporizador para la pausa entre frases consecutivas. */
    private Timer timerPausaEntreDialogos;
    /** Botón para saltar la secuencia introductoria. */
    private JButton btnSkip;

    /**
     * Estructura que representa un paso individual en la secuencia de diálogos.
     */
    private static class PasoDialogo {
        /** Nombre del personaje que habla. */
        String personaje;
        /** Texto a mostrar en el globo de diálogo. */
        String texto;

        /**
         * Crea un nuevo paso de diálogo.
         * 
         * @param p Nombre del personaje.
         * @param t Texto del diálogo.
         */
        PasoDialogo(String p, String t) {
            this.personaje = p;
            this.texto = t;
        }
    }

    private PasoDialogo[] secuenciaDialogos = {
            new PasoDialogo("Soraya",
                    "Hola, soy la memoria virtual de Soraya, vuestra profesora de Programación. El instituto ha sido infectado."),
            new PasoDialogo("Soraya",
                    "Dos profesores, hartos de bajo nivel de sus alumnos, fabricaron una fórmula para potenciar su desarrollo cognitivo."),
            new PasoDialogo("Jesica", "Hola, soy la memoria virtual de Jessica, vuestra profe de Lenguaje de Marcas."),
            new PasoDialogo("Jesica",
                    "Introdujeron la fórmula en el agua de las máquinas de café, pero los resultados no fueron los esperados..."),
            new PasoDialogo("Soraya",
                    "Jessica y yo sucumbimos al café. El instituto ahora está infestado de Zombies dominados por esos profesores."),
            new PasoDialogo("Soraya", "¡Acabad con ellos o estaréis TODOS SUSPENSOS! Sois nuestra última esperanza."),
            new PasoDialogo("Jesica",
                    "Recordad: WASD o flechas para moveros, E para puertas y ESC para pausar. Busca objetos curiosos por el mapa. ¡Suerte!")
    };

    private int indicePasos = 0;

    /**
     * Constructor del panel GamePanel.
     * Inicializa la interfaz, carga recursos de personajes y configura eventos de ciclo de vida.
     * 
     * @param frame Referencia al marco principal de la aplicación.
     */
    public GamePanel(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        setLayout(null);

        cargarFondo();
        cargarNPCs();
        crearBotonSkip();
        configurarEventos();
    }

    /**
     * Crea e inicializa el botón de "SKIP" para permitir saltar la secuencia.
     */
    private void crearBotonSkip() {
        btnSkip = new JButton("SKIP >>");

        // Cargar fuente Deltarune
        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/deltarune.ttf");
            if (fontStream != null) {
                Font deltaruneFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);
                btnSkip.setFont(deltaruneFont);
            }
        } catch (FontFormatException | IOException e) {
            btnSkip.setFont(new Font("Monospaced", Font.BOLD, 20));
        }

        // Estilo visual
        btnSkip.setOpaque(false);
        btnSkip.setContentAreaFilled(false);
        btnSkip.setBorderPainted(false);
        btnSkip.setFocusPainted(false);
        btnSkip.setForeground(Color.WHITE);
        btnSkip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Posicionar en la esquina superior derecha
        btnSkip.setBounds(850, 20, 120, 40);

        btnSkip.addActionListener(e -> skipIntro());

        add(btnSkip);
        setComponentZOrder(btnSkip, 0); // Asegurar que esté por encima de todo
    }

    /**
     * Salta la secuencia narrativa actual y transiciona directamente al modo exploración.
     */
    private void skipIntro() {
        LOG.info("Intro saltada por el usuario.");
        detenerDialogoBucle();
        mainFrame.cambiarPantalla("EXPLORACION");
    }

    private void configurarEventos() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                reubicarPersonajes();
            }

            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                iniciarDialogoBucle();
            }

            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                detenerDialogoBucle();
            }
        });
    }

    /**
     * Reposiciona dinámicamente las etiquetas de los personajes basadas en el tamaño actual del panel.
     */
    private void reubicarPersonajes() {
        if (labelSoraya != null) {
            labelSoraya.setBounds(0, getHeight() - TAMANO_PERSONAJE, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
        }
        if (labelJesica != null) {
            labelJesica.setBounds(getWidth() - TAMANO_PERSONAJE, getHeight() - TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    TAMANO_PERSONAJE);
        }
    }

    /**
     * Carga una imagen de forma segura desde los recursos.
     * 
     * @param ruta Ruta relativa al recurso.
     * @return ImageIcon cargado o null si hay error.
     */
    private ImageIcon cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.severe("ERROR: No se encontró la imagen en la ruta: " + ruta);
            return null;
        }
        LOG.info("Cargando imagen: " + url);
        return new ImageIcon(url);
    }

    /**
     * Carga y escala la imagen de fondo del panel de diálogos.
     */
    private void cargarFondo() {
        ImageIcon iconoFondo = cargarImagen("/title/dialogo.jpg");
        if (iconoFondo != null) {
            Image imagenEscalada = iconoFondo.getImage().getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
            labelFondo = new JLabel(new ImageIcon(imagenEscalada));
            labelFondo.setBounds(0, 0, 1000, 600);
            labelFondo.setOpaque(false);
            add(labelFondo);
            LOG.info("Fondo cargado correctamente");
        } else {
            labelFondo = new JLabel("FONDO NO CARGADO");
            labelFondo.setBounds(0, 0, WIDTH, HEIGHT);
            add(labelFondo);
        }
    }

    /**
     * Carga las imágenes de los NPCs (Soraya y Jesica) y configura sus etiquetas.
     */
    private void cargarNPCs() {
        LOG.info("Cargando NPCs...");

        iconoSoraya = cargarImagen("/dialogue/soraya.png");
        LOG.info("Soraya: " + (iconoSoraya != null ? "CARGADA" : "FALLIDA"));

        if (iconoSoraya != null) {
            Image imgSoraya = iconoSoraya.getImage().getScaledInstance(TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    Image.SCALE_DEFAULT);
            labelSoraya = new JLabel(new ImageIcon(imgSoraya));
            labelSoraya.setBounds(X_SORAYA, Y_SORAYA, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
            labelSoraya.setVisible(false);
            labelSoraya.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Si se hace clic, podemos forzar el inicio si no ha empezado
                    if (indicePasos == 0 && (timerSiguienteDialogo == null || !timerSiguienteDialogo.isRunning())) {
                        lanzarSiguienteDialogo();
                    }
                }
            });
            add(labelSoraya);
        }

        iconoJesica = cargarImagen("/dialogue/jesica.png");
        LOG.info("Jesica: " + (iconoJesica != null ? "CARGADA" : "FALLIDA"));

        if (iconoJesica != null) {
            Image imgJesica = iconoJesica.getImage().getScaledInstance(TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    Image.SCALE_DEFAULT);
            labelJesica = new JLabel(new ImageIcon(imgJesica));
            labelJesica.setBounds(X_JESICA, Y_JESICA, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
            labelJesica.setVisible(false);
            labelJesica.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                }

            });
            add(labelJesica);
        }

        setComponentZOrder(labelFondo, getComponentCount() - 1);

        LOG.info("Total componentes en panel: " + getComponentCount());
    }

    /**
     * Alterna la visibilidad de los sprites de los personajes.
     * 
     * @param visible Cierto para mostrar, falso para ocultar.
     * @param nombre Nombre del personaje ("Soraya" o "Jesica").
     */
    private void gestionarVisibilidad(boolean visible, String nombre) {
        if ("Soraya".equals(nombre) && labelSoraya != null) {
            labelSoraya.setVisible(visible);
        } else if ("Jesica".equals(nombre) && labelJesica != null) {
            labelJesica.setVisible(visible);
        }
    }

    /**
     * Inicia el bucle de diálogos si no está ya en ejecución.
     */
    public void iniciarDialogoBucle() {
        if (timerSiguienteDialogo != null && timerSiguienteDialogo.isRunning()) {
            return;
        }

        indicePasos = 0;

        timerSiguienteDialogo = new Timer(3000, e -> lanzarSiguienteDialogo());
        timerSiguienteDialogo.setRepeats(false);
        timerSiguienteDialogo.start();
    }

    /**
     * Lanza el siguiente diálogo en la secuencia, gestionando visibilidad gráfica y sonido.
     */
    private void lanzarSiguienteDialogo() {
        if (!isShowing() || indicePasos >= secuenciaDialogos.length) {
            return;
        }

        PasoDialogo paso = secuenciaDialogos[indicePasos];
        ImageIcon iconoActual = paso.personaje.equals("Soraya") ? iconoSoraya : iconoJesica;

        if (iconoActual != null) {
            gestionarVisibilidad(true, paso.personaje);
            mainFrame.showDialogue(paso.texto, 350);

            // Timer para cerrar este diálogo y preparar el siguiente (4 segundos)
            timerCierre = new Timer(6000, e -> {
                mainFrame.hideDialogue();
                gestionarVisibilidad(false, paso.personaje);
                indicePasos++;

                if (indicePasos < secuenciaDialogos.length) {
                    // Pausa de 1.5 segundos entre frases
                    timerPausaEntreDialogos = new Timer(1500, evt -> lanzarSiguienteDialogo());
                    timerPausaEntreDialogos.setRepeats(false);
                    timerPausaEntreDialogos.start();
                } else {
                    LOG.info("Fin de la secuencia narrativa. Cambiando a EXPLORACION.");
                    mainFrame.cambiarPantalla("EXPLORACION");
                }
            });
            timerCierre.setRepeats(false);
            timerCierre.start();
        }
    }

    /**
     * Pausa todos los temporizadores activos de la secuencia narrativa.
     */
    public void pausarDialogoBucle() {
        if (timerSiguienteDialogo != null) {
            timerSiguienteDialogo.stop();
        }
        if (timerCierre != null) {
            timerCierre.stop();
        }
        if (timerPausaEntreDialogos != null) {
            timerPausaEntreDialogos.stop();
        }
        LOG.info("Sistema de secuencia narrativa pausado");
    }

    /**
     * Detiene la secuencia, oculta diálogos y personajes, y pausa temporizadores.
     */
    public void detenerDialogoBucle() {
        pausarDialogoBucle();
        mainFrame.hideDialogue();
        gestionarVisibilidad(false, "Soraya");
        gestionarVisibilidad(false, "Jesica");
        LOG.info("Sistema de secuencia narrativa detenido");
    }

    /**
     * Reanuda la secuencia narrativa desde el punto en que se dejó.
     */
    public void reanudarDialogoBucle() {
        if (indicePasos < secuenciaDialogos.length) {
            LOG.info("Reanudando secuencia narrativa desde paso: " + indicePasos);
            lanzarSiguienteDialogo();
        }
    }

}
