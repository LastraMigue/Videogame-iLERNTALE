# iLERNTALE - Diseño técnico

## UML de Clases

```mermaid
classDiagram
    direction TB

    class MainFrame {
        <<JFrame>>
        -GamePanel panelJuego
        +MainFrame()
    }

    class GamePanel {
        <<JPanel +Runnable>>
        -Thread hiloJuego
        -int ANCHO, ALTO
        -int tamanoTile, FPS
        +KeyHandler manejadorTeclado
        +TileManager gestorTiles
        +CollisionChecker detectorColisiones
        +Jugador jugador
        +EstadoJuego estadoJuego
        +iniciarHiloJuego()
        +actualizar()
        +paintComponent(Graphics)
    }

    class KeyHandler {
        <<KeyListener>>
        +arribaPulsado, abajoPulsado, izquierdaPulsado, derechaPulsado
        +keyPressed(KeyEvent)
        +keyReleased(KeyEvent)
    }

    class TileManager {
        -GamePanel gp
        -int[][] datosMapa
        -int tamanoTile, anchoMapa, altoMapa
        +dibujar(Graphics2D, int, int)
        +esSolido(int, int)
    }

    class CollisionChecker {
        -GamePanel gp
        -int tamanoTile
        +verificarTile(Entity)
        +verificarColision(int, int, int, int)
    }

    class Entidad {
        <<abstract>>
        #int x, y, velocidad
        #String direccion
        #Rectangle areaSolida
        #boolean colisionActiva
        +getX(), setX(int)
        +getY(), setY(int)
        +getDireccion()
        +actualizarAnimacion()
    }

    class Jugador {
        <<extiende Entidad>>
        -String nombre
        -int vidaMaxima, vidaActual
        -SistemaNivel sistemaNivel
        +actualizar(KeyHandler, CollisionChecker)
    }

    class EntidadEnemigo {
        <<extiende Entidad>>
        -GamePanel gp
        -Thread hiloEnemigo
        -int tamanoEnemigo, velocidad
        -boolean ejecutando
        +iniciar(), detener()
        +ejecutar()
    }

    class Jefe {
        <<extiende EntidadEnemigo>>
        -int fase, vidaMaxima
        -boolean cargaActiva
    }

    class EstadoJuego {
        <<enum>>
        MENU
        EXPLORACION
        COMBATE
        JUGADOR_TURNO
        ENEMIGO_TURNO
        MINIJUEGO
        GAME_OVER
        VICTORIA
    }

    class SistemaNivel {
        -int nivel, experiencia
        -int experienciaParaSiguienteNivel
        +añadirExperiencia(int)
    }

    class Minijuego {
        <<interface>>
        +iniciar(int)
        +procesarInput(String)
        +estaCompletado()
        +esExitoso()
    }

    class PresentadorJuego {
        -GamePanel vista
        -Jugador jugador
        -TileManager[] mapas
        -EntidadEnemigo[] enemigos
        -Jefe jefe
        -PresentadorCombate presentadorCombate
        -EstadoJuego estadoJuego
        +nuevaPartida()
        +actualizar()
    }

    class PresentadorCombate {
        -Jugador jugador
        -EntidadEnemigo enemigoActual
        -Minijuego minijuegoActual
        +iniciarCombate(EntidadEnemigo)
        +procesarInputMinijuego(String)
    }

    MainFrame --> GamePanel : contiene
    GamePanel --> KeyHandler : usa
    GamePanel --> TileManager : usa
    GamePanel --> CollisionChecker : usa
    GamePanel --> PresentadorJuego
    GamePanel --> EstadoJuego
    CollisionChecker --> TileManager : consulta
    Jugador --> SistemaNivel
    Jugador --> Entidad
    EntidadEnemigo --> Entidad
    Jefe --> EntidadEnemigo
    PresentadorJuego --> Jugador
    PresentadorJuego --> TileManager
    PresentadorJuego --> EntidadEnemigo
    PresentadorJuego --> Jefe
    PresentadorJuego --> PresentadorCombate
    PresentadorCombate --> Minijuego
```

---

## GamePanel (JPanel)

GamePanel es el panel donde se renderiza todo el juego. Implementa Runnable para ejecutar el game loop en un thread separado.

```java
public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public final int originalTileSize = 48;
    public final int scale = 4;
    public final int tileSize = originalTileSize * scale;

    private Thread gameThread;
    private boolean running;
    private final int FPS = 60;

    public KeyHandler keyH = new KeyHandler();
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Jugador jugador = new Jugador("Migue");

    public EstadoJuego gameState = EstadoJuego.EXPLORACION;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyH);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        running = true;
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null && running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                delta--;
            }

            repaint();
        }
    }

    private void update() {
        if (gameState == EstadoJuego.EXPLORACION) {
            jugador.update(keyH, cChecker);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        if (gameState == EstadoJuego.EXPLORACION) {
            tileM.draw(g2);

            BufferedImage image = ResourceManager.getPlayerSprite(
                    jugador.getNombre(),
                    jugador.getDireccion(),
                    jugador.getSpriteNum());

            if (image != null) {
                g2.drawImage(image, jugador.getX(), jugador.getY(), tileSize, tileSize, null);
            }
        }

        if (gameState == EstadoJuego.MENU) {
            // Dibujar menú
        }

        g2.dispose();
    }
}
```

---

## Puntos clave

1. **MainFrame** solo crea la ventana y arranca el game thread
2. **GamePanel** maneja todo el renderizado y la lógica
3. **Game loop** usa delta time para mantener 60 FPS constantes
4. **Doble buffer** activado para evitar parpadeo
5. **Thread separado** para no bloquear el EDT de Swing
