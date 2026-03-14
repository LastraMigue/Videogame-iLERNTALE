package equipoilerntale.service;

import equipoilerntale.model.entity.Direction;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Servicio centralizado para la carga y gestión de recursos visuales (imágenes y sprites).
 * Implementa el patrón Singleton para asegurar un único punto de acceso a la caché de recursos.
 */
public class AssetService {
    /** Registrador de eventos para depuración y errores. */
    private static final Logger LOG = Logger.getLogger(AssetService.class.getName());
    /** Instancia única del servicio. */
    private static AssetService instance;

    /** Caché de imágenes individuales cargadas mediante ruta. */
    private final Map<String, Image> imageCache = new HashMap<>();
    /** Caché de listas de frames (animaciones) de personajes. */
    private final Map<String, List<Image>> spriteCache = new HashMap<>();
    /** Sprites por defecto generados dinámicamente en caso de error de carga. */
    private final Map<Direction, List<Image>> defaultSprites = new HashMap<>();

    /** Tamaño de referencia para el escalado de sprites. */
    private int spriteSize = 48;

    /**
     * Constructor privado para cumplir con el patrón Singleton.
     */
    private AssetService() {
        initializeDefaultSprites();
    }

    /**
     * Obtiene la instancia única del servicio.
     * 
     * @return Instancia de {@link AssetService}.
     */
    public static AssetService getInstance() {
        if (instance == null) {
            instance = new AssetService();
        }
        return instance;
    }

    /**
     * Inicializa el servicio con un nuevo tamaño de sprite y limpia la caché.
     * 
     * @param size Nuevo tamaño de los sprites.
     */
    public void initialize(int size) {
        this.spriteSize = size;
        clearCache();
        initializeDefaultSprites();
    }

    /**
     * Genera sprites de reserva (rectángulos de colores) para usar cuando los archivos no existen.
     */
    private void initializeDefaultSprites() {
        Color[] colors = { Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.GRAY };
        Direction[] directions = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.IDLE };

        for (int i = 0; i < directions.length; i++) {
            List<Image> frames = new ArrayList<>();
            for (int f = 1; f <= 2; f++) {
                BufferedImage img = new BufferedImage(spriteSize, spriteSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(colors[i]);
                g.fillRoundRect(5, 5, spriteSize - 10, spriteSize - 10, 15, 15);
                g.setColor(Color.WHITE);
                g.drawString(directions[i].name().substring(0, 1) + f, 10, 20);
                g.dispose();
                frames.add(img);
            }
            defaultSprites.put(directions[i], frames);
        }
    }

    /**
     * Carga todos los frames de un personaje específico desde el sistema de archivos.
     * Los archivos deben seguir la convención: /{personaje}/{dirección}{número}{personaje}.png
     * 
     * @param characterName Nombre de la carpeta del personaje.
     * @param size Tamaño al que se deben escalar los frames.
     */
    public void loadCharacterSprites(String characterName, int size) {
        String baseName = characterName.toLowerCase().trim();
        LOG.info("======= CARGANDO SPRITES PARA: '" + baseName + "' (tamaño=" + size + ") =======");
        for (Direction dir : Direction.values()) {
            List<Image> frames = new ArrayList<>();
            int frameIdx = 1;
            boolean foundExtraFrames = true;

            while (foundExtraFrames && frameIdx <= 10) {
                String path = "/player/" + baseName + "/" + dir.getValue() + frameIdx + baseName + ".png";
                LOG.info("  Probando ruta: '" + path + "'");
                Image img = loadImage(path);

                if (img != null) {
                    frames.add(scaleImage(img, size, size));
                    LOG.info("  ✓ Frame " + frameIdx + " cargado para " + dir.name());
                    frameIdx++;
                } else {
                    LOG.warning("  ✗ No encontrado (frame " + frameIdx + ", dir=" + dir.name() + ")");
                    foundExtraFrames = false;
                }
            }

            String cacheKey = baseName + "_" + dir.name();
            if (frames.isEmpty()) {
                LOG.warning("NO SE ENCONTRARON FRAMES REALES PARA: " + cacheKey + ". El renderer usará fallback.");
                spriteCache.put(cacheKey, new ArrayList<>());
            } else {
                LOG.info("Frames cargados para [" + cacheKey + "]: " + frames.size());
                spriteCache.put(cacheKey, frames);
            }
        }
        LOG.info("======= FIN CARGA SPRITES =======");
    }

    /**
     * Recupera la lista completa de frames para un personaje y dirección.
     * 
     * @param characterName Nombre del personaje.
     * @param direction Dirección del movimiento.
     * @return Lista de imágenes o los sprites por defecto si no se han cargado.
     */
    public List<Image> getCharacterFrames(String characterName, Direction direction) {
        String key = characterName.toLowerCase().trim() + "_" + direction.name();
        if (!spriteCache.containsKey(key)) {
            return defaultSprites.get(direction);
        }
        List<Image> cached = spriteCache.get(key);
        return cached.isEmpty() ? null : cached;
    }

    /**
     * Obtiene un frame específico de la animación de un personaje.
     * 
     * @param characterName Nombre del personaje.
     * @param direction Dirección deseada.
     * @param frameIndex Índice del frame (se aplica módulo para rotación infinita).
     * @return Imagen del frame o null si no hay recursos disponibles.
     */
    public Image getCharacterSprite(String characterName, Direction direction, int frameIndex) {
        List<Image> frames = getCharacterFrames(characterName, direction);
        if (frames == null || frames.isEmpty())
            return null;
        int idx = frameIndex % frames.size();
        return frames.get(idx);
    }

    /**
     * Recupera un sprite de zombie con escalado automático.
     * 
     * @param type Tipo de zombie.
     * @param direction Dirección actual.
     * @param frameIndex Índice del frame de animación.
     * @return Imagen escalada.
     */
    public Image getZombieSprite(int type, Direction direction, int frameIndex) {
        String cacheKey = String.format("zombie_%d_%s_%d", type, direction.name(), frameIndex);

        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        String path = String.format("/enemies/zombie%d/%s%d%s.png",
                type, direction.getValue(), frameIndex, "zombie" + type);

        Image img = loadImage(path);
        if (img != null) {
            Image scaled = scaleImage(img, spriteSize, spriteSize);
            imageCache.put(cacheKey, scaled);
            return scaled;
        }

        LOG.warning("NO SE PUDO CARGAR EL SPRITE DEL ZOMBIE: " + path);
        return null;
    }

    /**
     * Recupera el sprite del jefe final.
     * 
     * @param bossName Nombre del jefe.
     * @return Imagen escalada a las dimensiones del jefe.
     */
    public Image getBossSprite(String bossName) {
        String cacheKey = "boss_" + bossName;
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        String path = String.format("/boss/%s/%snormal.png", bossName.toLowerCase(), bossName.toLowerCase());
        Image img = loadImage(path);

        if (img != null) {
            Image scaled = scaleImage(img, equipoilerntale.model.entity.Boss.WIDTH,
                    equipoilerntale.model.entity.Boss.HEIGHT);
            imageCache.put(cacheKey, scaled);
            return scaled;
        }

        LOG.warning("NO SE PUDO CARGAR EL SPRITE DEL BOSS: " + path);
        return null;
    }

    /**
     * Carga una imagen de forma segura desde los recursos del classpath.
     * Gestiona una caché interna para evitar lecturas de disco redundantes.
     * 
     * @param path Ruta absoluta interna al recurso.
     * @return Objeto {@link Image} o null en caso de error.
     */
    public Image loadImage(String path) {
        if (imageCache.containsKey(path))
            return imageCache.get(path);

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                LOG.warning("No se encontró la imagen: " + path);
                return null;
            }
            BufferedImage img = ImageIO.read(is);
            imageCache.put(path, img);
            return img;
        } catch (Exception e) {
            LOG.severe("Error cargando imagen " + path + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Escala una imagen utilizando interpolación bilineal para mantener la calidad.
     * 
     * @param original Imagen de origen.
     * @param targetWidth Ancho final.
     * @param targetHeight Alto final.
     * @return Nueva imagen reescalada.
     */
    public Image scaleImage(Image original, int targetWidth, int targetHeight) {
        if (original == null)
            return null;

        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return scaled;
    }

    /**
     * Carga un fondo mediante {@link #loadImage(String)}.
     * 
     * @param path Ruta del fondo.
     * @return Imagen del fondo.
     */
    public Image loadBackground(String path) {
        return loadImage(path);
    }

    /**
     * Limpia todas las cachés de imágenes y animaciones.
     */
    public void clearCache() {
        imageCache.clear();
        spriteCache.clear();
    }

    /**
     * Libera los recursos y elimina la instancia del Singleton.
     */
    public void dispose() {
        clearCache();
        defaultSprites.clear();
        instance = null;
    }
}
