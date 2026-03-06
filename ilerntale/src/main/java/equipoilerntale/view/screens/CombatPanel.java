package equipoilerntale.view.screens;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
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
        upperRectangle.setBounds(405, 30, 180, 180);
        add(upperRectangle);

        JPanel centerRectangle = createRectangle(600, 250);
        centerRectangle.setBounds(200, 240, 600, 250);
        add(centerRectangle);

        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBounds(0, 510, 1000, 70);
        add(buttonPanel);
    }

    private JPanel createRectangle(int width, int height) {
        JPanel rectangle = new JPanel();
        rectangle.setPreferredSize(new Dimension(width, height));
        rectangle.setMaximumSize(new Dimension(width, height));
        rectangle.setMinimumSize(new Dimension(width, height));
        rectangle.setBackground(Color.BLACK);
        rectangle.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        return rectangle;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setOpaque(false);

        btnFight = createButton("fight");
        btnAct = createButton("act");
        btnItem = createButton("item");
        btnMercy = createButton("mercy");

        btnFight.setBounds(40, 10, 200, 60);
        btnAct.setBounds(280, 10, 200, 60);
        btnItem.setBounds(520, 10, 200, 60);
        btnMercy.setBounds(760, 10, 200, 60);

        buttonPanel.add(btnFight);
        buttonPanel.add(btnAct);
        buttonPanel.add(btnItem);
        buttonPanel.add(btnMercy);

        return buttonPanel;
    }

    private JButton createButton(String accion) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(200, 60));
        button.setMaximumSize(new Dimension(200, 60));
        button.setMinimumSize(new Dimension(200, 60));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setIconTextGap(0);
        button.setText("");

        URL urlImagen = getClass().getResource("/attack/" + accion + "1.png");
        if (urlImagen != null) {
            ImageIcon icon = new ImageIcon(urlImagen);
            Image img = icon.getImage();
            Image imgEscalada = img.getScaledInstance(200, 60, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(imgEscalada));
        }

        URL urlImagenPressed = getClass().getResource("/attack/" + accion + "2.png");
        if (urlImagenPressed != null) {
            ImageIcon iconPressed = new ImageIcon(urlImagenPressed);
            Image imgPressed = iconPressed.getImage();
            Image imgEscaladaPressed = imgPressed.getScaledInstance(200, 60, Image.SCALE_SMOOTH);

            button.setPressedIcon(new ImageIcon(imgEscaladaPressed));
        }

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
        }
    }

    private ImageIcon asignarImagenCombate(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null)
            return null;
        return new ImageIcon(url);
    }
}