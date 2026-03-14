package equipoilerntale.view.screens;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import equipoilerntale.view.MainFrame;

/**
 * Pantalla que se muestra cuando el jugador es derrotado.
 * Presenta una imagen de fondo y un botón para salir al menú principal.
 */
public class DerrotaScreen extends JPanel {
    /** Referencia al marco principal para gestionar el cambio de pantallas. */
    private MainFrame mainFrame;
    /** Imagen de fondo de la pantalla de derrota. */
    private Image backgroundImage;
    /** Botón para salir y volver al menú principal. */
    private JButton btnSalir;

    /**
     * Constructor de la pantalla de derrota.
     * Configura el diseño, carga los recursos visuales e inicializa el botón de salida.
     * 
     * @param frame Referencia al MainFrame.
     */
    /**
     * Constructor de la pantalla de derrota.
     * Configura el diseño, carga los recursos visuales e inicializa el botón de salida.
     * 
     * @param frame Referencia al MainFrame.
     */
    public DerrotaScreen(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(null);

        try (InputStream is = getClass().getResourceAsStream("/title/derrota.jpg")) {
            if (is != null) {
                backgroundImage = ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de derrota: " + e.getMessage());
        }

        try (InputStream is = getClass().getResourceAsStream("/title/salir.png")) {
            if (is != null) {
                Image btnImage = ImageIO.read(is);
                Image scaledBtnImage = btnImage.getScaledInstance(300, 100, Image.SCALE_SMOOTH);
                btnSalir = new JButton(new ImageIcon(scaledBtnImage));
                btnSalir.setBorderPainted(false);
                btnSalir.setContentAreaFilled(false);
                btnSalir.setFocusPainted(false);
                btnSalir.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

                btnSalir.setBounds(500 - (300 / 2), 400, 300, 100);

                btnSalir.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        equipoilerntale.service.SoundService.getInstance().playSFX("/sound/mouse_click.wav");
                        mainFrame.reiniciarJuego();
                        mainFrame.cambiarPantalla("MENU");
                    }
                });
                add(btnSalir);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del boton salir: " + e.getMessage());
        }
    }

    /**
     * Dibuja los componentes visuales del panel, incluyendo la imagen de fondo.
     * 
     * @param g El contexto gráfico para realizar el dibujo.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
