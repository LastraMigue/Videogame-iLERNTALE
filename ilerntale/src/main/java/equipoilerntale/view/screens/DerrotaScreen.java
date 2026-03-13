package equipoilerntale.view.screens;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import equipoilerntale.view.MainFrame;

public class DerrotaScreen extends JPanel {
    private MainFrame mainFrame;
    private Image backgroundImage;
    private JButton btnSalir;

    public DerrotaScreen(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(null); // Absolute positioning

        try {
            backgroundImage = ImageIO.read(new File("ilerntale/src/main/resources/title/derrota.jpg"));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de derrota: " + e.getMessage());
        }

        try {
            Image btnImage = ImageIO.read(new File("ilerntale/src/main/resources/title/salir.png"));
            Image scaledBtnImage = btnImage.getScaledInstance(300, 100, Image.SCALE_SMOOTH);
            btnSalir = new JButton(new ImageIcon(scaledBtnImage));
            btnSalir.setBorderPainted(false);
            btnSalir.setContentAreaFilled(false);
            btnSalir.setFocusPainted(false);
            btnSalir.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

            // Posicionar el boton justo debajo del centro o donde se requiera
            // The user said "justo debajo" which means in the lower part of the screen
            // Standard resolution is 1000x600 based on MainFrame
            btnSalir.setBounds(500 - (300 / 2), 400, 300, 100);

            btnSalir.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainFrame.reiniciarJuego();
                    mainFrame.cambiarPantalla("MENU");
                }
            });
            add(btnSalir);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del boton salir: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Draw background image scaled to the panel size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
