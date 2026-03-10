package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.Timer;

import equipoilerntale.controller.MainController;
// Importar el InputHandler para detectar el controlador de cambiar al menú de Pausa
import equipoilerntale.controller.InputHandler;
import equipoilerntale.view.MainFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    private MainFrame mainFrame;
    private MainController controller;
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

    private static class PasoDialogo {
        String personaje;
        String texto;

        PasoDialogo(String p, String t) {
            this.personaje = p;
            this.texto = t;
        }
    }

    // Implementar métodos de la interfaz para el manejo de las teclas

    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, escapePressed;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)
            upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)
            downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)
            rightPressed = true;
        if (code == KeyEvent.VK_ENTER)
            enterPressed = true;
        if (code == KeyEvent.VK_ESCAPE)
            escapePressed = true;
        mostrarMenuPausa();

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)
            upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)
            downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)
            rightPressed = false;
        if (code == KeyEvent.VK_ENTER)
            enterPressed = false;
        if (code == KeyEvent.VK_ESCAPE)
            escapePressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    } // No se usa, pero debe estar por la interfaz

    private PasoDialogo[] secuenciaDialogos = {
            new PasoDialogo("Soraya", "¡Hola! Soy Soraya."),
            new PasoDialogo("Jesica", "¡Hola! Soy Jessica."),
            new PasoDialogo("Soraya", "Bienvenido al mundo de iLERNTALE."),
            new PasoDialogo("Jesica", "¡Vamos a explorar juntos!")
    };

    private int indicePasos = 0;

    public GamePanel(MainFrame frame) {
        this.mainFrame = frame;
        this.controller = frame.getMainController();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        setLayout(null);

        cargarFondo();
        cargarNPCs();
        configurarEventos();
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
            System.err.println("ERROR: No se encontró la imagen en la ruta: " + ruta);
            return null;
        }
        System.out.println("Cargando imagen: " + url);
        return new ImageIcon(url);
    }

    private void cargarFondo() {
        ImageIcon iconoFondo = cargarImagen("/mapa/Pasillo.png");
        if (iconoFondo != null) {
            Image imagenEscalada = iconoFondo.getImage().getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
            labelFondo = new JLabel(new ImageIcon(imagenEscalada));
            labelFondo.setBounds(0, 0, 1000, 600);
            labelFondo.setOpaque(false);
            add(labelFondo);
            System.out.println("Fondo cargado correctamente");
        } else {
            labelFondo = new JLabel("FONDO NO CARGADO");
            labelFondo.setBounds(0, 0, WIDTH, HEIGHT);
            add(labelFondo);
        }
    }

    private void cargarNPCs() {
        System.out.println("Cargando NPCs...");

        iconoSoraya = cargarImagen("/dialogue/soraya.png");
        System.out.println("Soraya: " + (iconoSoraya != null ? "CARGADA" : "FALLIDA"));

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
            System.out.println("Soraya añadida (oculta)");
        }

        iconoJesica = cargarImagen("/dialogue/jesica.png");
        System.out.println("Jesica: " + (iconoJesica != null ? "CARGADA" : "FALLIDA"));

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
                    System.out.println("Click en Jesica detectado");
                }

            });
            add(labelJesica);
            System.out.println("Jesica añadida (oculta)");
        }

        setComponentZOrder(labelFondo, getComponentCount() - 1);

        System.out.println("Total componentes en panel: " + getComponentCount());
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

        // Timer de 3 segundos para el primer diálogo
        timerSiguienteDialogo = new Timer(3000, e -> lanzarSiguienteDialogo());
        timerSiguienteDialogo.setRepeats(false);
        timerSiguienteDialogo.start();
        System.out.println("Sistema de secuencia narrativa iniciado");
    }

    private void lanzarSiguienteDialogo() {
        if (!isShowing() || indicePasos >= secuenciaDialogos.length) {
            return;
        }

        PasoDialogo paso = secuenciaDialogos[indicePasos];
        ImageIcon iconoActual = paso.personaje.equals("Soraya") ? iconoSoraya : iconoJesica;

        if (iconoActual != null) {
            gestionarVisibilidad(true, paso.personaje);
            mostrarDialogo(paso.personaje, iconoActual, paso.texto);
            indicePasos++;
        }
    }

    public void detenerDialogoBucle() {
        if (timerSiguienteDialogo != null) {
            timerSiguienteDialogo.stop();
            System.out.println("Sistema de turnos detenido");
        }
    }

    private void mostrarDialogo(String nombre, ImageIcon imagen, String texto) {
        // Dimensiones del diálogo tipo "videojuego"
        int anchoDialogo = 500;
        int altoDialogo = 120;

        JDialog dialogo = new JDialog(mainFrame);
        dialogo.setUndecorated(true); // Sin bordes ni barra de título
        dialogo.setSize(anchoDialogo, altoDialogo);
        dialogo.setModal(false);

        // Panel principal del diálogo (negro con borde blanco fino)
        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(java.awt.Color.BLACK);
        panelContenido.setLayout(null);
        panelContenido.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.WHITE, 2));
        dialogo.setContentPane(panelContenido);

        // Área de texto estilizada
        JTextArea textoArea = new JTextArea(texto);
        textoArea.setBounds(110, 20, 360, 80);
        textoArea.setEditable(false);
        textoArea.setLineWrap(true);
        textoArea.setWrapStyleWord(true);
        textoArea.setBackground(java.awt.Color.BLACK);
        textoArea.setForeground(java.awt.Color.WHITE);
        textoArea.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.BOLD, 16));
        panelContenido.add(textoArea);

        // Boton Invisible/Invisible para permitir interactividad (o simplemente omitir
        // si es auto)
        JButton botonInvisible = new JButton();
        botonInvisible.setBounds(0, 0, anchoDialogo, altoDialogo);
        botonInvisible.setOpaque(false);
        botonInvisible.setContentAreaFilled(false);
        botonInvisible.setBorderPainted(false);
        botonInvisible.addActionListener(e -> dialogo.dispose());
        panelContenido.add(botonInvisible);

        // CÁLCULO DE POSICIÓN
        // Intentamos centrarlo entre Soraya (izq) y Jesica (der)
        int xCentro = this.getWidth() / 2;
        int yBase = this.getHeight() - altoDialogo - 20; // 20px de margen inferior

        // Convertir coordenadas locales a coordenadas de pantalla para el JDialog
        java.awt.Point ubicacionPanel = this.getLocationOnScreen();
        dialogo.setLocation(ubicacionPanel.x + (xCentro - anchoDialogo / 2), ubicacionPanel.y + yBase);

        // TIMER PARA CIERRE AUTOMÁTICO (4 segundos)
        Timer timerAutoCierre = new Timer(4000, e -> {
            if (dialogo.isShowing()) {
                dialogo.dispose();
            }
        });
        timerAutoCierre.setRepeats(false);
        timerAutoCierre.start();

        // Al cerrar el diálogo, programamos el siguiente paso de la secuencia
        dialogo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                gestionarVisibilidad(false, nombre);
                if (indicePasos < secuenciaDialogos.length) {
                    Timer pausa = new Timer(1500, evt -> lanzarSiguienteDialogo());
                    pausa.setRepeats(false);
                    pausa.start();
                } else {
                    System.out.println("Fin de la secuencia narrativa.");
                }
            }
        });

        dialogo.setVisible(true);
    }

    // Método para el Menú Pausa
    // Aquí se cambia de panel para mostrar el PausePanel
    public void mostrarMenuPausa() {
        if (escapePressed) {
            mainFrame.getMainController().pauseGame();
            mainFrame.cambiarPantalla("PAUSE");
            escapePressed = false;
        }
    }

}
