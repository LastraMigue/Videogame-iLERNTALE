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
import java.awt.Font;
import java.awt.FontMetrics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;
import java.util.List;

import equipoilerntale.view.MainFrame;
import equipoilerntale.view.render.BulletRenderer;
import equipoilerntale.view.render.ItemRenderer;
import equipoilerntale.view.render.MouseRenderer;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.controller.CombatController;
import equipoilerntale.controller.InputHandler;
import equipoilerntale.controller.MainController;
import equipoilerntale.model.combat.minigames.ClassicDodgeRules;
import equipoilerntale.model.combat.minigames.MinigameRules;
import equipoilerntale.model.combat.minigames.ShieldRules;
import equipoilerntale.model.combat.minigames.ShooterRules;
import equipoilerntale.model.combat.minigames.TargetDodgeRules;
import equipoilerntale.model.combat.minigames.ThreeLinesRules;
import equipoilerntale.view.ui.Inventario;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.view.ui.BarraVida;

public class CombatPanel extends JPanel {
    private MainFrame mainFrame;
    private Image imagenFondo;

    private ArenaModel arenaModel;
    private InputHandler inputHandler;
    private CombatController combatController;
    private MinigameRules currentRules;

    // Renderers (NUEVO)
    private MouseRenderer mouseRenderer;
    private BulletRenderer bulletRenderer;
    private ItemRenderer itemRenderer;

    // Inventario
    private Inventario inventario;
    private List<ItemModel> currentCombatItems;
    private boolean isItemMenuOpen = false;
    private int selectedItemIndex = 0;
    private int inputCooldown = 0;

    // Minigame & Round state
    private int currentRound = 1;
    // Timer del combate
    private long minigameEndTime = 0;
    private long lastUpdateTime = 0;
    private boolean isMinigameActive = false;

    // Collisions Tracking para daño
    private int lastGoodCollisions = 0;
    private int lastBadCollisions = 0;

    // Buffos de Objetos
    private int escudoPatito = 0;
    private boolean dobleDañoRonda = false;

    // Vida Enemigo
    private BarraVida enemyHealthBar;

    private String centerTextMessage = "";
    private Font customFont;

    private JButton btnFight;
    private JButton btnAct;
    private JButton btnItem;
    private JButton btnMercy;

    public CombatPanel(MainFrame frame) {
        this.mainFrame = frame;

        this.arenaModel = new ArenaModel();
        this.inputHandler = new InputHandler();
        this.combatController = new CombatController(arenaModel, inputHandler);
        this.inventario = new Inventario();

        // Instanciamos los pintores
        this.mouseRenderer = new MouseRenderer();
        this.bulletRenderer = new BulletRenderer();
        this.itemRenderer = new ItemRenderer();

        // Inicializamos barra de enemigo
        this.enemyHealthBar = new BarraVida(25, "ENEMIGO");

        this.addKeyListener(inputHandler);
        this.setFocusable(true);

        setPreferredSize(new Dimension(1000, 600));
        setLayout(null);
        setOpaque(false);

        cargarImagenCombate();
        cargarFuente();
        inicializarPaneles();
    }

    public void updateCombat() {
        if (inputCooldown > 0) {
            inputCooldown--;
        }

        if (isItemMenuOpen) {
            if (inputCooldown == 0 && currentCombatItems != null && !currentCombatItems.isEmpty()) {
                if (inputHandler.upPressed) {
                    selectedItemIndex--;
                    if (selectedItemIndex < 0) {
                        selectedItemIndex = currentCombatItems.size() - 1;
                    }
                    inputCooldown = 10;
                    repaint();
                } else if (inputHandler.downPressed) {
                    selectedItemIndex++;
                    if (selectedItemIndex >= currentCombatItems.size()) {
                        selectedItemIndex = 0;
                    }
                    inputCooldown = 10;
                    repaint();
                } else if (inputHandler.enterPressed) {
                    // Consumir objeto
                    ItemModel selected = currentCombatItems.get(selectedItemIndex);
                    if (selected != null) {
                        selected.consumir(); // Disminuye la cantidad

                        String itemName = selected.getNombre();
                        if (itemName.equals("Botella Vida")) {
                            mainFrame.getPlayerHealthBar().heal(30);
                        } else if (itemName.equals("Patito Aguante")) {
                            escudoPatito = 3;
                        } else if (itemName.equals("Pelota Ataque")) {
                            dobleDañoRonda = true;
                        }

                        centerTextMessage = "USASTE " + selected.getNombre().toUpperCase();
                        isItemMenuOpen = false;
                        repaint();

                        Timer msgTimer = new Timer(1500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                centerTextMessage = "";
                                repaint();
                                enableAllButtons();
                            }
                        });
                        msgTimer.setRepeats(false);
                        msgTimer.start();
                    }
                    inputCooldown = 15;
                }
            }
        } else {
            combatController.update();

            // Damage check
            if (arenaModel != null) {
                int currentBad = arenaModel.getBadCollisions();
                if (currentBad > lastBadCollisions) {
                    int damageRecibido = currentBad - lastBadCollisions;
                    lastBadCollisions = currentBad;

                    if (escudoPatito > 0) {
                        int absorbido = Math.min(escudoPatito, damageRecibido);
                        escudoPatito -= absorbido;
                        damageRecibido -= absorbido;
                    }

                    if (damageRecibido > 0) {
                        mainFrame.getPlayerHealthBar().takeDamage(damageRecibido);
                        if (mainFrame.getPlayerHealthBar().getHealth() <= 0) {
                            endMinigame();
                            mainFrame.cambiarPantalla("DERROTA");
                        }
                    }
                }

                int currentGood = arenaModel.getGoodCollisions();
                if (currentGood > lastGoodCollisions) {
                    int hits = currentGood - lastGoodCollisions;
                    lastGoodCollisions = currentGood;

                    int damageHecho = dobleDañoRonda ? (hits * 2) : hits;
                    enemyHealthBar.takeDamage(damageHecho);
                }
            }

            if (isMinigameActive && currentRules != null) {
                long now = System.currentTimeMillis();

                // Si la diferencia de tiempo es muy grande, asumimos que hubo una pausa
                if (lastUpdateTime > 0) {
                    long delta = now - lastUpdateTime;
                    if (delta > 50) { // Si pasan más de 50ms (por pausa o lag), congelamos el tiempo
                        minigameEndTime += delta;
                    }
                }
                lastUpdateTime = now;

                if (currentRules.isIntroActive()) {
                    // Pausa el minijuego empujando el tiempo final hacia adelante
                    minigameEndTime = now + (currentRules.getDurationInSeconds() * 1000);
                } else {
                    if (now >= minigameEndTime) {
                        endMinigame();
                    }
                }

                repaint();
                if (combatController.isMinigameFinished()) {
                    endMinigame();
                }
            }
        }
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

        // UI Vida Enemigo Arriba a la Izquierda
        if (enemyHealthBar != null) {
            enemyHealthBar.draw(g2d, 20, 35);
        }

        // Recuadros para RONDA y TIEMPO "a ras" del central (Y=95, Alto=50)
        // Cuadro central: X=405 a 585

        // Recuadro RONDA (Izquierda: X=245, Ancho=150) -> termina en 395 (a 10px del
        // central)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(235, 160, 150, 50);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(235, 160, 150, 50);

        // Recuadro TIEMPO (Derecha: X=595, Ancho=150) -> empieza a 10px del central
        if (isMinigameActive) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(605, 160, 150, 50);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(605, 160, 150, 50);
        }

        // Draw round and timer text inside their new squares
        if (customFont != null) {
            g2d.setFont(customFont.deriveFont(24f));
            g2d.setColor(Color.WHITE);
            // Centrado aproximado en el recuadro izquierdo (Y=128)
            g2d.drawString("RONDA: " + currentRound, 255, 192);

            if (isMinigameActive) {
                if (currentRules != null && currentRules.isIntroActive()) {
                    long seconds = currentRules.getDurationInSeconds();
                    String timeText = String.format("%02d:000", seconds);
                    g2d.drawString(timeText, 645, 192);
                } else {
                    long nowForTimer = System.currentTimeMillis();
                    if (mainFrame.getMainController() != null &&
                            mainFrame.getMainController().getGameState() == MainController.GameState.PAUSED) {
                        nowForTimer = lastUpdateTime; // Congela el tiempo visualmente
                    }

                    long timeLeft = minigameEndTime - nowForTimer;
                    if (timeLeft < 0)
                        timeLeft = 0;
                    long seconds = timeLeft / 1000;
                    long millis = timeLeft % 1000;
                    String timeText = String.format("%02d:%03d", seconds, millis);
                    g2d.drawString(timeText, 645, 192);
                }
            }
        }

        g2d.setColor(Color.BLACK);
        g2d.fillRect(200, 240, 600, 250);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(200, 240, 600, 250);

        // 2. DIBUJAR LOS ELEMENTOS CON LOS RENDERERS
        boolean drawEntities = true;
        if (isMinigameActive && currentRules != null && currentRules.isIntroActive()) {
            drawEntities = false;
        }

        if (arenaModel != null && drawEntities) {
            if (arenaModel.getProjectiles() != null) {
                bulletRenderer.render(g2d, arenaModel.getProjectiles());
            }
            if (arenaModel.getMouse() != null) {
                mouseRenderer.render(g2d, arenaModel.getMouse());
            }
        }

        // Extras of current minigame
        if (isMinigameActive && currentRules != null) {
            currentRules.render(g2d, arenaModel);
        }

        // 3. MENÚ DE OBJETOS
        if (isItemMenuOpen) {
            itemRenderer.renderMenu(g2d, currentCombatItems, selectedItemIndex, customFont);
        }

        // 3. MENSAJE EN EL CENTRO
        if (centerTextMessage != null && !centerTextMessage.isEmpty()) {
            if (customFont != null) {
                g2d.setFont(customFont);
            }
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(centerTextMessage);
            int x = 200 + (600 - stringWidth) / 2;
            int y = 240 + (250 - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(centerTextMessage, x, y);
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
                        isItemMenuOpen = false;
                        disableAllButtons();

                        int randomMinigame = new java.util.Random().nextInt(5);
                        switch (randomMinigame) {
                            case 0:
                                currentRules = new ClassicDodgeRules();
                                break;
                            case 1:
                                currentRules = new TargetDodgeRules();
                                break;
                            case 2:
                                currentRules = new ThreeLinesRules();
                                break;
                            case 3:
                                currentRules = new ShooterRules();
                                break;
                            case 4:
                                currentRules = new ShieldRules();
                                break;
                        }

                        combatController.setRules(currentRules);
                        combatController.startMinigame();

                        requestFocusInWindow();

                        isMinigameActive = true;
                        int duration = currentRules.getDurationInSeconds() * 1000;
                        long startNow = System.currentTimeMillis();
                        minigameEndTime = startNow + duration;
                        lastUpdateTime = startNow;
                        lastGoodCollisions = 0;
                        lastBadCollisions = 0;
                        break;
                    case "act":
                        break;
                    case "item":
                        currentCombatItems = inventario.getObjetosCombate();
                        if (currentCombatItems != null && !currentCombatItems.isEmpty()) {
                            // Se cambia de true a un toggle y no se bloquean los botones
                            isItemMenuOpen = !isItemMenuOpen;
                            selectedItemIndex = 0;
                            centerTextMessage = ""; // Limpiar cualquier texto de combate
                            requestFocusInWindow();
                            repaint();
                        } else {
                            // Si no hay objetos usables en combate y con cantidad > 0, ciérralo.
                            isItemMenuOpen = false;
                            repaint();
                        }
                        break;
                    case "mercy":
                        isItemMenuOpen = false;
                        disableAllButtons();
                        double chance = Math.random();
                        if (chance <= 0.10) {
                            centerTextMessage = "HAS TENIDO PIEDAD";
                            repaint();
                            // Temporizador para cambio de layout en el futuro
                            Timer transitionTimer = new Timer(2000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent ev) {
                                    // TODO: Cambiar layout aquí
                                    centerTextMessage = "";
                                    repaint();
                                    enableAllButtons();
                                }
                            });
                            transitionTimer.setRepeats(false);
                            transitionTimer.start();
                        } else {
                            centerTextMessage = "QUIERE MORDERTE";
                            repaint();
                            // Pausa para leer el texto, luego inicio de combate
                            Timer minigameTimer = new Timer(1500, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent ev) {
                                    centerTextMessage = "";

                                    int randomMinigame = new java.util.Random().nextInt(5);
                                    switch (randomMinigame) {
                                        case 0:
                                            currentRules = new ClassicDodgeRules();
                                            break;
                                        case 1:
                                            currentRules = new TargetDodgeRules();
                                            break;
                                        case 2:
                                            currentRules = new ThreeLinesRules();
                                            break;
                                        case 3:
                                            currentRules = new ShooterRules();
                                            break;
                                        case 4:
                                            currentRules = new ShieldRules();
                                            break;
                                    }

                                    combatController.setRules(currentRules);
                                    combatController.startMinigame();

                                    requestFocusInWindow();

                                    isMinigameActive = true;
                                    int duration = currentRules.getDurationInSeconds() * 1000;
                                    long startNow = System.currentTimeMillis();
                                    minigameEndTime = startNow + duration;
                                    lastUpdateTime = startNow;
                                    lastGoodCollisions = 0;
                                    lastBadCollisions = 0;
                                    repaint();
                                }
                            });
                            minigameTimer.setRepeats(false);
                            minigameTimer.start();
                        }
                        break;
                }
            }
        });

        return button;
    }

    private void cargarFuente() {
        try {
            URL fontUrl = getClass().getResource("/font/deltarune.ttf");
            if (fontUrl != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                customFont = baseFont.deriveFont(Font.BOLD, 40f);
            } else {
                customFont = new Font("Monospaced", Font.BOLD, 40);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Monospaced", Font.BOLD, 40);
        }
    }

    private void cargarImagenCombate() {
        URL url = getClass().getResource("/attack/ataque.jpg");
        if (url != null) {
            imagenFondo = new ImageIcon(url).getImage().getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
        }
    }

    private void disableAllButtons() {
        btnFight.setEnabled(false);
        btnAct.setEnabled(false);
        btnItem.setEnabled(false);
        btnMercy.setEnabled(false);
    }

    private void enableAllButtons() {
        btnFight.setEnabled(true);
        btnAct.setEnabled(true);
        btnItem.setEnabled(true);
        btnMercy.setEnabled(true);
    }

    private void endMinigame() {
        if (!isMinigameActive)
            return;
        isMinigameActive = false;
        arenaModel.stopCombat();
        currentRules = null;
        enableAllButtons();
        currentRound++;
        dobleDañoRonda = false;
        repaint();
    }
}