package equipoilerntale.view.screens;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;

import equipoilerntale.view.MainFrame;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.controller.CombatController;
import equipoilerntale.controller.InputHandler;
import equipoilerntale.view.renderers.MouseRenderer;
import equipoilerntale.view.renderers.BulletRenderer;

public class CombatPanel extends JPanel {
    private MainFrame mainFrame;
    private Image imagenFondo;

    private ArenaModel arenaModel;
    private InputHandler inputHandler;
    private CombatController combatController;

    // Renderers (NUEVO)
    private MouseRenderer mouseRenderer;
    private BulletRenderer bulletRenderer;

    private JButton btnFight;
    private JButton btnAct;
    private JButton btnItem;
    private JButton btnMercy;

    public CombatPanel(MainFrame frame) {
        this.mainFrame = frame;

        this.arenaModel = new ArenaModel();
        this.inputHandler = new InputHandler();
        this.combatController = new CombatController(arenaModel, inputHandler);

        // Instanciamos los pintores
        this.mouseRenderer = new MouseRenderer();
        this.bulletRenderer = new BulletRenderer();

        this.addKeyListener(inputHandler);
        this.setFocusable(true);

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null);
        setOpaque(false);

        cargarImagenCombate();
        inicializarPaneles();
    }

    public void updateCombat() {
        combatController.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Fondo e interfaz
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }

        g2d.setColor(Color.BLACK);
        g2d.fillRect(405, 30, 180, 180);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(405, 30, 180, 180);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(200, 240, 600, 250);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(200, 240, 600, 250);

        // 2. DIBUJAR LOS ELEMENTOS CON LOS RENDERERS
        if (arenaModel != null) {
            bulletRenderer.render(g2d, arenaModel.getProjectiles());
            mouseRenderer.render(g2d, arenaModel.getMouse());
        }
    }

    private void inicializarPaneles() {
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBounds(0, 510, 1000, 70);
        add(buttonPanel);
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
            button.setIcon(
                    new ImageIcon(new ImageIcon(urlImagen).getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH)));
        }

        URL urlImagenPressed = getClass().getResource("/attack/" + accion + "2.png");
        if (urlImagenPressed != null) {
            button.setPressedIcon(new ImageIcon(
                    new ImageIcon(urlImagenPressed).getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH)));
        }

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (accion) {
                    case "fight":
                        arenaModel.startCombat();
                        requestFocusInWindow();
                        break;
                    case "act":
                        break;
                    case "item":
                        break;
                    case "mercy":
                        break;
                }
            }
        });

        return button;
    }

    private void cargarImagenCombate() {
        URL url = getClass().getResource("/attack/ataque.jpg");
        if (url != null) {
            imagenFondo = new ImageIcon(url).getImage().getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
        }
    }
}