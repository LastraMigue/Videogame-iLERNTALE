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
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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
import equipoilerntale.model.combat.minigames.MazeRules;
import equipoilerntale.model.combat.minigames.MinigameRules;
import equipoilerntale.model.combat.minigames.ShieldRules;
import equipoilerntale.model.combat.minigames.ShooterRules;
import equipoilerntale.model.combat.minigames.TargetDodgeRules;
import equipoilerntale.model.combat.minigames.ThreeLinesRules;
import equipoilerntale.view.ui.Inventario;
import equipoilerntale.model.entity.ItemModel;
import equipoilerntale.view.ui.BarraVida;
import equipoilerntale.view.ui.PopupScare;
import java.awt.Toolkit;
import equipoilerntale.service.SoundService;

public class CombatPanel extends JPanel {
    private MainFrame mainFrame;
    private Image imagenFondo;

    private ArenaModel arenaModel;
    private InputHandler inputHandler;
    private CombatController combatController;
    private MinigameRules currentRules;
    private Object enemyTarget;
    private JPanel enemyIconPanel;
    private Image enemyImage;

    // Renderers (NUEVO)
    private MouseRenderer mouseRenderer;
    private BulletRenderer bulletRenderer;
    private ItemRenderer itemRenderer;

    // Inventario
    private Inventario inventario;
    private List<ItemModel> currentCombatItems;
    private boolean isInventoryActive = false;
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
    private boolean isDoubleDamageActive = false;

    // Fase Final Boss (Fase 2)
    private boolean isFinalBossPhase = false;

    // Vida Enemigo
    private BarraVida enemyHealthBar;

    private String centerTextMessage = "";
    private Font customFont;

    private JButton btnFight;
    private JButton btnAct;
    private JButton btnItem;
    private JButton btnMercy;

    private int damageFlashTicks = 0;
    private int shakeIntensity = 0;
    private int popupCooldown = 0;

    public CombatPanel(MainFrame frame) {
        this.mainFrame = frame;

        this.arenaModel = new ArenaModel();
        this.inputHandler = new InputHandler();
        this.combatController = new CombatController(arenaModel, inputHandler);
        this.inventario = Inventario.getInstance();

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
        inicializarUI();
    }

    /**
     * PREPARA EL COMBATE CON UN ENEMIGO ESPECÍFICO.
     */
    public void prepararCombate(Object enemy) {
        this.enemyTarget = enemy;
        this.isMinigameActive = false;
        this.isInventoryActive = false;
        this.centerTextMessage = "";
        this.currentRound = 1;
        if (arenaModel != null)
            arenaModel.setCurrentRound(1);
        this.lastGoodCollisions = 0;
        this.lastBadCollisions = 0;
        this.inputCooldown = 0;
        this.escudoPatito = 0;
        this.isDoubleDamageActive = false;

        if (inputHandler != null) {
            inputHandler.reset();
        }

        // Cargar imagen del enemigo
        equipoilerntale.service.AssetService assetService = equipoilerntale.service.AssetService.getInstance();
        if (enemy instanceof equipoilerntale.model.entity.Zombie) {
            equipoilerntale.model.entity.Zombie z = (equipoilerntale.model.entity.Zombie) enemy;
            this.enemyImage = assetService.getZombieSprite(z.getType(), equipoilerntale.model.entity.Direction.DOWN, 1);
            this.enemyHealthBar.setMaxHealth(z.getMaxHealth());
            this.enemyHealthBar.setHealth(z.getHealth());
        } else if (enemy instanceof equipoilerntale.model.entity.Boss) {
            equipoilerntale.model.entity.Boss b = (equipoilerntale.model.entity.Boss) enemy;
            this.enemyImage = assetService.getBossSprite("sergio");
            this.enemyHealthBar.setMaxHealth(equipoilerntale.model.entity.Boss.MAX_HEALTH);
            this.enemyHealthBar.setHealth(b.getHealth());
        }

        enableAllButtons();
        SoundService.getInstance().playBGM("/sound/combate.wav");
        requestFocusInWindow();
        repaint();
    }

    /**
     * PREPARA EL BOSS EN SU FASE FINAL (FASE 2): 200 HP, imagen sergiofinal,
     * controles invertidos, daño doble al jugador.
     */
    public void prepararFinalBoss() {
        this.isMinigameActive = false;
        this.isInventoryActive = false;
        this.centerTextMessage = "";
        this.currentRound = 1;
        if (arenaModel != null)
            arenaModel.setCurrentRound(1);
        this.lastGoodCollisions = 0;
        this.lastBadCollisions = 0;
        this.inputCooldown = 0;
        this.escudoPatito = 0;
        this.isDoubleDamageActive = false;
        this.isFinalBossPhase = true;
        this.popupCooldown = 0;
        if (inputHandler != null)
            inputHandler.reset();

        // Imagen del boss final
        try (InputStream is = equipoilerntale.service.AssetService.getInstance().getClass()
                .getResourceAsStream("/boss/sergio/sergiofinal.png")) {
            if (is != null) {
                BufferedImage imgFinal = ImageIO.read(is);
                this.enemyImage = equipoilerntale.service.AssetService.getInstance()
                        .scaleImage(imgFinal,
                                equipoilerntale.model.entity.Boss.WIDTH,
                                equipoilerntale.model.entity.Boss.HEIGHT);
            }
        } catch (IOException e) {
            System.err.println("Error cargando boss final: " + e.getMessage());
        }

        this.enemyTarget = null; // No hay entidad física en el mapa para fase 2
        this.enemyHealthBar.setMaxHealth(200);
        this.enemyHealthBar.setHealth(200);

        // Controles invertidos en la arena
        arenaModel.setReversedControls(true);

        enableAllButtons();
        SoundService.getInstance().playBGM("/sound/combatefinal.wav");
        requestFocusInWindow();
        repaint();
    }

    /**
     * REINICIA EL ESTADO DEL COMBATE (Para salir al menú o reiniciar juego).
     */
    public void reiniciarEstado() {
        this.isFinalBossPhase = false;
        this.popupCooldown = 0;
        if (arenaModel != null) {
            arenaModel.stopCombat();
            arenaModel.setReversedControls(false);
        }
        this.isMinigameActive = false;
        this.centerTextMessage = "";
        repaint();
    }

    public void updateCombat() {
        actualizarEnfriamientos();

        if (isInventoryActive) {
            gestionarEntradaMenuObjetos();
        } else if (isMinigameActive) {
            combatController.update();
            procesarDanyRecibido();
            procesarDanyHecho();
            actualizarTemporizadorMinijuego();
        }
    }

    private void actualizarEnfriamientos() {
        if (inputCooldown > 0) {
            inputCooldown--;
        }
        if (damageFlashTicks > 0) {
            damageFlashTicks--;
        }

        if (isFinalBossPhase) {
            popupCooldown++;
            if (popupCooldown >= 900) { // 15 segundos (15 * 60 fps)
                spawnScarePopups();
                popupCooldown = 0;
            }
        }
    }

    private void gestionarEntradaMenuObjetos() {
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
                        isDoubleDamageActive = true;
                    }

                    centerTextMessage = "USASTE " + selected.getNombre().toUpperCase();
                    isInventoryActive = false;
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
    }

    private void procesarDanyRecibido() {
        if (arenaModel == null)
            return;

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
                damageFlashTicks = 30; // 0.5s de parpadeo
                shakeIntensity = 10; // Intensidad de la sacudida
                SoundService.getInstance().playSFX("/sound/hitmalo.wav");

                // En fase final el boss inflige daño doble
                int finalDamage = isFinalBossPhase ? damageRecibido * 2 : damageRecibido;
                mainFrame.getPlayerHealthBar().takeDamage(finalDamage);

                if (mainFrame.getPlayerHealthBar().getHealth() <= 0) {
                    endMinigame();
                    mainFrame.cambiarPantalla("DERROTA");
                }
            }
        }
    }

    private void procesarDanyHecho() {
        if (arenaModel == null)
            return;

        int currentGood = arenaModel.getGoodCollisions();
        if (currentGood > lastGoodCollisions) {
            int hits = currentGood - lastGoodCollisions;
            lastGoodCollisions = currentGood;

            int damageHecho = isDoubleDamageActive ? (hits * 2) : hits;
            enemyHealthBar.takeDamage(damageHecho);
            SoundService.getInstance().playSFX("/sound/hitbueno.wav");

            // Sincronizar daño con el objeto real
            if (enemyTarget instanceof equipoilerntale.model.entity.Zombie) {
                ((equipoilerntale.model.entity.Zombie) enemyTarget).takeDamage(damageHecho);
            } else if (enemyTarget instanceof equipoilerntale.model.entity.Boss) {
                ((equipoilerntale.model.entity.Boss) enemyTarget).takeDamage(damageHecho);
            }

            // Comprobar si el enemigo ha muerto (Generalizado)
            if (enemyHealthBar.getHealth() <= 0) {
                isMinigameActive = false;
                arenaModel.stopCombat();
                arenaModel.setReversedControls(false); // Limpiar controles invertidos
                centerTextMessage = "VICTORIA";
                repaint();

                javax.swing.Timer winTimer = new javax.swing.Timer(1500,
                        new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent ev) {
                                if (isFinalBossPhase) {
                                    // Fase 2 superada: video final y vuelta al menú
                                    isFinalBossPhase = false;
                                    mainFrame.cambiarPantalla("FINAL_VIDEO");
                                } else if (enemyTarget instanceof equipoilerntale.model.entity.Boss) {
                                    mainFrame.triggerBossDefeated(enemyTarget);
                                } else {
                                    mainFrame.finalizarCombate(true, enemyTarget);
                                }
                            }
                        });
                winTimer.setRepeats(false);
                winTimer.start();
            }
        }
    }

    private void actualizarTemporizadorMinijuego() {
        if (currentRules != null) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Efecto Screen Shake y Fondo
        aplicarEfectoShake(g2d);
        renderFondo(g2d);

        // 2. HUD y Estadísticas (Enemigo, Ronda, Tiempo)
        renderHUDEnemigo(g2d);
        renderStats(g2d);

        // 3. Área de Juego (Arena)
        renderArena(g2d);

        // 4. Elementos de Juego (Proyectiles, Ratón, Reglas)
        renderElementosJuego(g2d);

        // 5. Menú de Objetos y Mensajes
        if (isInventoryActive) {
            itemRenderer.renderMenu(g2d, currentCombatItems, selectedItemIndex, customFont);
        }
        renderMensajesCentro(g2d);
    }

    private void aplicarEfectoShake(Graphics2D g2d) {
        if (shakeIntensity > 0) {
            int offsetX = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
            int offsetY = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
            g2d.translate(offsetX, offsetY);
            shakeIntensity--;
        }
    }

    private void renderFondo(Graphics2D g2d) {
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Marco de la imagen del enemigo
        g2d.setColor(Color.BLACK);
        g2d.fillRect(405, 30, 180, 180);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(405, 30, 180, 180);
    }

    private void renderHUDEnemigo(Graphics2D g2d) {
        if (enemyHealthBar != null) {
            enemyHealthBar.draw(g2d, 20, 35);
        }
    }

    private void renderStats(Graphics2D g2d) {
        // Recuadro RONDA
        g2d.setColor(Color.BLACK);
        g2d.fillRect(235, 160, 150, 50);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(235, 160, 150, 50);

        // Recuadro TIEMPO (si aplica)
        if (isMinigameActive) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(605, 160, 150, 50);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(605, 160, 150, 50);
        }

        if (customFont != null) {
            g2d.setFont(customFont.deriveFont(24f));
            g2d.setColor(Color.WHITE);
            g2d.drawString("RONDA: " + currentRound, 255, 192);

            if (isMinigameActive) {
                renderTemporizador(g2d);
            }
        }
    }

    private void renderTemporizador(Graphics2D g2d) {
        String timeText;
        if (currentRules != null && currentRules.isIntroActive()) {
            timeText = String.format("%02d:000", currentRules.getDurationInSeconds());
        } else {
            long now = System.currentTimeMillis();
            if (mainFrame.getMainController() != null &&
                mainFrame.getMainController().getGameState() == MainController.GameState.PAUSED) {
                now = lastUpdateTime; 
            }
            long timeLeft = Math.max(0, minigameEndTime - now);
            timeText = String.format("%02d:%03d", timeLeft / 1000, timeLeft % 1000);
        }
        g2d.drawString(timeText, 645, 192);
    }

    private void renderArena(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(200, 240, 600, 250);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(200, 240, 600, 250);
    }

    private void renderElementosJuego(Graphics2D g2d) {
        boolean drawEntities = !(isMinigameActive && currentRules != null && currentRules.isIntroActive());

        if (arenaModel != null && drawEntities) {
            if (arenaModel.getProjectiles() != null) {
                bulletRenderer.render(g2d, arenaModel.getProjectiles());
            }
            if (arenaModel.getMouse() != null) {
                mouseRenderer.render(g2d, arenaModel.getMouse(), damageFlashTicks > 0);
            }
        }

        if (isMinigameActive && currentRules != null) {
            currentRules.render(g2d, arenaModel);
        }
    }

    private void renderMensajesCentro(Graphics2D g2d) {
        if (centerTextMessage == null || centerTextMessage.isEmpty()) return;

        if (customFont != null) g2d.setFont(customFont);
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();

        String[] lines = centerTextMessage.split("\n");
        int linePadding = 5;
        int totalHeight = (fm.getHeight() * lines.length) + (linePadding * (lines.length - 1));
        int startY = 240 + (250 - totalHeight) / 2 + fm.getAscent();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int x = 200 + (600 - fm.stringWidth(line)) / 2;
            int y = startY + (i * (fm.getHeight() + linePadding));
            g2d.drawString(line, x, y);
        }
    }

    private void inicializarUI() {
        // PANEL PEQUEÑO PARA LA IMAGEN DEL ENEMIGO
        enemyIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (enemyImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    int drawW = getWidth() - 20;
                    int drawH = getHeight() - 20;
                    int x = (getWidth() - drawW) / 2;
                    int y = (getHeight() - drawH) / 2;
                    g2d.drawImage(enemyImage, x, y, drawW, drawH, null);
                }
            }
        };
        enemyIconPanel.setBounds(408, 33, 174, 174);
        enemyIconPanel.setOpaque(false);
        add(enemyIconPanel);

        // BOTONES DE ACCIÓN
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setBounds(0, 510, 1000, 70);
        buttonPanel.setOpaque(false);

        btnFight = createButton("fight", 40, 10);
        btnAct = createButton("act", 280, 10);
        btnItem = createButton("item", 520, 10);
        btnMercy = createButton("mercy", 760, 10);

        buttonPanel.add(btnFight);
        buttonPanel.add(btnAct);
        buttonPanel.add(btnItem);
        buttonPanel.add(btnMercy);

        add(buttonPanel);
    }

    private JButton createButton(String accion, int x, int y) {
        JButton button = new JButton();
        button.setBounds(x, y, 200, 60);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Cargar iconos (Normal y Presionado)
        button.setIcon(loadButtonIcon(accion, 1));
        button.setPressedIcon(loadButtonIcon(accion, 2));

        button.addActionListener(e -> handleButtonAction(accion));

        return button;
    }

    private ImageIcon loadButtonIcon(String action, int variant) {
        String path = "/attack/" + action + variant + ".png";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                return new ImageIcon(img.getScaledInstance(200, 60, Image.SCALE_SMOOTH));
            }
        } catch (IOException e) {
            System.err.println("Error cargando icono de botón [" + path + "]: " + e.getMessage());
        }
        return null;
    }

    private void handleButtonAction(String action) {
        SoundService.getInstance().playSFX("/sound/mouse_click.wav");
        switch (action) {
            case "fight":
                realizarAccionLuchar();
                break;
            case "act":
                realizarAccionActuar();
                break;
            case "item":
                realizarAccionObjeto();
                break;
            case "mercy":
                realizarAccionPiedad();
                break;
        }
    }

    private void iniciarMinijuegoAleatorio() {
        int randomIdx = new java.util.Random().nextInt(6);
        switch (randomIdx) {
            case 0: currentRules = new ClassicDodgeRules(); break;
            case 1: currentRules = new TargetDodgeRules(); break;
            case 2: currentRules = new ThreeLinesRules(); break;
            case 3: currentRules = new ShooterRules(); break;
            case 4: currentRules = new ShieldRules(); break;
            case 5: currentRules = new MazeRules(); break;
        }

        combatController.setRules(currentRules);
        combatController.startMinigame();

        if (isFinalBossPhase) {
            arenaModel.setReversedControls(true);
        }

        requestFocusInWindow();
        isMinigameActive = true;
        
        long startNow = System.currentTimeMillis();
        minigameEndTime = startNow + (currentRules.getDurationInSeconds() * 1000);
        lastUpdateTime = startNow;
        lastGoodCollisions = 0;
        lastBadCollisions = 0;
    }

    private void realizarAccionLuchar() {
        isInventoryActive = false;
        disableAllButtons();
        iniciarMinijuegoAleatorio();
    }

    private void realizarAccionActuar() {
        isInventoryActive = false;
        disableAllButtons();
        
        String loreMessage;
        if (enemyTarget instanceof equipoilerntale.model.entity.Zombie) {
            String[] zombieLore = {
                "Hambre... el cafe... \nnos cambio..",
                "iLERNA... prometio... \ninteligencia...\nsolo hay... hambre...",
                "Donde esta... mi examen? \nNo... siento la logica...",
                "El agua... las maquinas... \nsabian a codigo amargo...",
                "Solo... queriamos... \naprobar..."
            };
            loreMessage = zombieLore[new java.util.Random().nextInt(zombieLore.length)];
        } else if (isFinalBossPhase || enemyTarget instanceof equipoilerntale.model.entity.Boss) {
            String[] bossLore = {
                "Esto no compila \nen produccion! \nESTAIS SUSPENDIDOS!",
                "Has revisado el tema 4 \nsobre polimorfismo? \nMUERE!",
                "El examen final sera... \nvuestra tumba.",
                "iLERNA era solo el principio... \nel cafe hara el resto.",
                "Vuestro codigo es tan sucio \ncomo este instituto!"
            };
            loreMessage = bossLore[new java.util.Random().nextInt(bossLore.length)];
        } else {
            loreMessage = "El enemigo te ignora...";
        }

        centerTextMessage = loreMessage;
        repaint();

        Timer actTimer = new Timer(2500, ev -> {
            centerTextMessage = "";
            enableAllButtons();
            repaint();
        });
        actTimer.setRepeats(false);
        actTimer.start();
    }

    private void realizarAccionObjeto() {
        currentCombatItems = inventario.getObjetosCombate();
        if (currentCombatItems != null && !currentCombatItems.isEmpty()) {
            isInventoryActive = !isInventoryActive;
            selectedItemIndex = 0;
            centerTextMessage = "";
            requestFocusInWindow();
        } else {
            isInventoryActive = false;
        }
        repaint();
    }

    private void realizarAccionPiedad() {
        isInventoryActive = false;
        disableAllButtons();
        
        if (Math.random() <= 0.10) {
            centerTextMessage = "HAS TENIDO PIEDAD";
            repaint();
            Timer victoryTimer = new Timer(2000, ev -> mainFrame.finalizarCombate(true, enemyTarget));
            victoryTimer.setRepeats(false);
            victoryTimer.start();
        } else {
            centerTextMessage = "QUIERE MORDERTE";
            repaint();
            Timer minigameTimer = new Timer(1500, ev -> {
                centerTextMessage = "";
                iniciarMinijuegoAleatorio();
            });
            minigameTimer.setRepeats(false);
            minigameTimer.start();
        }
    }

    private void cargarFuente() {
        try (InputStream is = getClass().getResourceAsStream("/font/deltarune.ttf")) {
            if (is != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
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
        try (InputStream is = getClass().getResourceAsStream("/attack/ataque.jpg")) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                imagenFondo = img.getScaledInstance(1000, 600, Image.SCALE_DEFAULT);
            }
        } catch (IOException e) {
            System.err.println("Error cargando fondo de combate: " + e.getMessage());
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

        // Bloquear botón MERCI si el enemigo es un Boss o estamos en Fase 2
        if (isFinalBossPhase || enemyTarget instanceof equipoilerntale.model.entity.Boss) {
            btnMercy.setEnabled(false);
        } else {
            btnMercy.setEnabled(true);
        }
    }

    private void endMinigame() {
        if (!isMinigameActive)
            return;
        isMinigameActive = false;
        if (inputHandler != null) {
            inputHandler.reset();
        }
        arenaModel.stopCombat();
        currentRules = null;
        enableAllButtons();
        currentRound++;
        if (arenaModel != null)
            arenaModel.setCurrentRound(currentRound);
        isDoubleDamageActive = false;
        repaint();
    }

    private void spawnScarePopups() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        for (int i = 0; i < 5; i++) {
            int x = (int) (Math.random() * (screenSize.width - 250));
            int y = (int) (Math.random() * (screenSize.height - 150));
            new PopupScare(x, y, 200, 100).showFor(1000);
        }
    }
}