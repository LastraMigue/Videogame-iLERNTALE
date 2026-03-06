package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import equipoilerntale.view.MainFrame;

public class CombatPanel extends JPanel {
    private MainFrame mainFrame;
    private Image imagenFondo;

    private JButton btnFight;
    private JButton btnAct;
    private JButton btnItem;
    private JButton btnMercy;

    public CombatPanel(MainFrame frame) {
        this.mainFrame = frame;

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null);
        setOpaque(false);

        cargarImagenCombate();
        inicializarPaneles();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void inicializarPaneles() {
        JPanel upperRectangle = createRectangle(180, 180);
        upperRectangle.setBounds(400, 50, 180, 180);
        upperRectangle.setBackground(Color.BLACK);
        add(upperRectangle);

        JPanel centerRectangle = createRectangle(600, 250);
        centerRectangle.setBounds(200, 280, 600, 250);
        centerRectangle.setBackground(Color.BLACK);
        add(centerRectangle);

        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBounds(100, 550, 1000, 50);
        add(buttonPanel);
    }

    private JPanel createRectangle(int width, int height) {
        JPanel rectangle = new JPanel();
        rectangle.setPreferredSize(new Dimension(width, height));
        rectangle.setMaximumSize(new Dimension(width, height));
        rectangle.setMinimumSize(new Dimension(width, height));
        rectangle.setBackground(new Color(0, 0, 0, 200));
        rectangle.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        rectangle.setOpaque(false);
        return rectangle;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);

        JButton btnFight = createButton("fight");
        JButton btnAct = createButton("act");
        JButton btnItem = createButton("item");
        JButton btnMercy = createButton("mercy");

        // Set Bounds para colocar los botones
        btnFight.setBounds(50, 100, 200, 50);
        btnAct.setBounds(250, 480, 200, 50);
        btnItem.setBounds(450, 480, 200, 50);
        btnMercy.setBounds(650, 480, 200, 50);

        buttonPanel.add(btnFight);
        buttonPanel.add(btnAct);
        buttonPanel.add(btnItem);
        buttonPanel.add(btnMercy);

        return buttonPanel;
    }

    private JButton createButton(String accion) {
        JButton button = new JButton(accion);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setMinimumSize(new Dimension(200, 50));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setIconTextGap(0);
        button.setText("");

        String imagePath = "ilerntale/src/main/resources/attack/" + accion + "2.png";

        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage();
        Image imgEscalada = img.getScaledInstance(200, 50, Image.SCALE_SMOOTH);
        ImageIcon iconEscalado = new ImageIcon(imgEscalada);

        button.setIcon(iconEscalado);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        return button;
    }

    private void cargarImagenCombate() {
        ImageIcon imagenMenuCombate = asignarImagenCombate("/attack/ataque.jpg");

        if (imagenMenuCombate != null) {
            imagenFondo = imagenMenuCombate.getImage().getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
            System.out.println("Fondo de combate cargado correctamente");
        } else {
            setBackground(new Color(20, 20, 30));
            System.err.println("ERROR: No se pudo cargar la imagen de fondo");
        }
    }

    private ImageIcon asignarImagenCombate(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontro la imagen: " + ruta);
            return null;
        }
        return new ImageIcon(url);
    }
}
