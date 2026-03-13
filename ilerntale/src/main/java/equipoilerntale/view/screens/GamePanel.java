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

public class GamePanel extends JPanel {
    private static final Logger LOG = Logger.getLogger(GamePanel.class.getName());
    private MainFrame mainFrame;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private static final int TAMANO_PERSONAJE = 256;
    private static final int X_SORAYA = 0;
    private static final int Y_SORAYA = 344;
    private static final int X_JESICA = 544;
    private static final int Y_JESICA = 344;

    private JLabel labelFondo;
    private JLabel labelSoraya;
    private JLabel labelJesica;
    private ImageIcon iconoSoraya;
    private ImageIcon iconoJesica;
    private Timer timerSiguienteDialogo;
    private Timer timerCierre;
    private Timer timerPausaEntreDialogos;
    private JButton btnSkip;

    private static class PasoDialogo {
        String personaje;
        String texto;

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
                    "Recordad: WASD o flechas para moveros, E para puertas y ESC para pausar. Hay objetos por el mapa. ¡Suerte!")
    };

    private int indicePasos = 0;

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

    private void skipIntro() {
        LOG.info("Intro saltada por el usuario.");
        detenerDialogoBucle();
        mainFrame.cambiarPantalla("EXPLORACION");
    }

    private void configurarEventos() {
        // Anclaje dinámico de personajes (especialmente Jesica en la esquina inferior
        // derecha)
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

    private void reubicarPersonajes() {
        if (labelSoraya != null) {
            labelSoraya.setBounds(0, getHeight() - TAMANO_PERSONAJE, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
        }
        if (labelJesica != null) {
            labelJesica.setBounds(getWidth() - TAMANO_PERSONAJE, getHeight() - TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    TAMANO_PERSONAJE);
        }
    }

    private ImageIcon cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.severe("ERROR: No se encontró la imagen en la ruta: " + ruta);
            return null;
        }
        LOG.info("Cargando imagen: " + url);
        return new ImageIcon(url);
    }

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

    private void cargarNPCs() {
        LOG.info("Cargando NPCs...");

        iconoSoraya = cargarImagen("/dialogue/soraya.png");
        LOG.info("Soraya: " + (iconoSoraya != null ? "CARGADA" : "FALLIDA"));

        if (iconoSoraya != null) {
            Image imgSoraya = iconoSoraya.getImage().getScaledInstance(TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    Image.SCALE_DEFAULT);
            labelSoraya = new JLabel(new ImageIcon(imgSoraya));
            labelSoraya.setBounds(X_SORAYA, Y_SORAYA, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
            labelSoraya.setVisible(false); // Ocultar inicialmente
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
            LOG.info("Soraya añadida (oculta)");
        }

        iconoJesica = cargarImagen("/dialogue/jesica.png");
        LOG.info("Jesica: " + (iconoJesica != null ? "CARGADA" : "FALLIDA"));

        if (iconoJesica != null) {
            Image imgJesica = iconoJesica.getImage().getScaledInstance(TAMANO_PERSONAJE, TAMANO_PERSONAJE,
                    Image.SCALE_DEFAULT);
            labelJesica = new JLabel(new ImageIcon(imgJesica));
            labelJesica.setBounds(X_JESICA, Y_JESICA, TAMANO_PERSONAJE, TAMANO_PERSONAJE);
            labelJesica.setVisible(false); // Ocultar inicialmente
            labelJesica.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    // Click manual no reinicia, solo informa
                    LOG.info("Click en Jesica detectado");
                }

            });
            add(labelJesica);
            LOG.info("Jesica añadida (oculta)");
        }

        setComponentZOrder(labelFondo, getComponentCount() - 1);

        LOG.info("Total componentes en panel: " + getComponentCount());
    }

    private void gestionarVisibilidad(boolean visible, String nombre) {
        if ("Soraya".equals(nombre) && labelSoraya != null) {
            labelSoraya.setVisible(visible);
        } else if ("Jesica".equals(nombre) && labelJesica != null) {
            labelJesica.setVisible(visible);
        }
    }

    public void iniciarDialogoBucle() {
        if (timerSiguienteDialogo != null && timerSiguienteDialogo.isRunning()) {
            return;
        }

        indicePasos = 0; // Reset index each time panel is shown

        // Timer de 3 segundos para el primer diálogo
        timerSiguienteDialogo = new Timer(3000, e -> lanzarSiguienteDialogo());
        timerSiguienteDialogo.setRepeats(false);
        timerSiguienteDialogo.start();
        LOG.info("Sistema de secuencia narrativa iniciado");
    }

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

    public void detenerDialogoBucle() {
        pausarDialogoBucle();
        mainFrame.hideDialogue();
        gestionarVisibilidad(false, "Soraya");
        gestionarVisibilidad(false, "Jesica");
        LOG.info("Sistema de secuencia narrativa detenido");
    }

    public void reanudarDialogoBucle() {
        if (indicePasos < secuenciaDialogos.length) {
            LOG.info("Reanudando secuencia narrativa desde paso: " + indicePasos);
            lanzarSiguienteDialogo();
        }
    }

}
