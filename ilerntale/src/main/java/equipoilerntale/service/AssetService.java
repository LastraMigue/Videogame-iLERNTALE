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
 * SERVICIO UNIFICADO DE CARGA DE RECURSOS.
 * GESTIONA SPRITES, FONDOS Y CACHÉ MEDIANTE UN PATRÓN SINGLETON.
 */
public class AssetService {
    private static final Logger LOG = Logger.getLogger(AssetService.class.getName());
    private static AssetService instance;

    private final Map<String, Image> imageCache = new HashMap<>();
    private final Map<String, List<Image>> spriteCache = new HashMap<>();
    private final Map<Direction, List<Image>> defaultSprites = new HashMap<>();

    private int spriteSize = 48; // VALOR POR DEFECTO

    private AssetService() {
        initializeDefaultSprites();
    }

    /**
     * OBTIENE LA INSTANCIA ÚNICA DEL SERVICIO (PATRÓN SINGLETON).
     */
    public static AssetService getInstance() {
        if (instance == null) {
            instance = new AssetService();
        }
        return instance;
    }

    /**
     * INICIALIZA EL SERVICIO CON UN TAMAÑO DE SPRITE ESPECÍFICO Y LIMPIA LA CACHÉ
     * ACTUAL.
     */
    public void initialize(int size) {
        this.spriteSize = size;
        clearCache();
        initializeDefaultSprites();
    }

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
     * CARGA LOS SPRITES DE ANIMACIÓN DEL PERSONAJE PARA TODAS LAS DIRECCIONES.
     */
    public void loadCharacterSprites(String characterName, int size) {
        String baseName = characterName.toLowerCase().trim();
        LOG.info("======= CARGANDO SPRITES PARA: '" + baseName + "' (tamaño=" + size + ") =======");
        for (Direction dir : Direction.values()) {
            List<Image> frames = new ArrayList<>();
            int frameIdx = 1;
            boolean foundExtraFrames = true;

            while (foundExtraFrames && frameIdx <= 10) {
                // FORMATO REAL DEL ARCHIVO: {dirección}{nº}{personaje}.png
                // Ejemplo: /player/migue/abajo1migue.png
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
                // DEJAR LA CLAVE VACÍA: getCharacterSprite devolverá null, y PlayerRenderer
                // usará su propio fallback de color sólido — más claro que un rectángulo
                // generado
                LOG.warning("NO SE ENCONTRARON FRAMES REALES PARA: " + cacheKey + ". El renderer usará fallback.");
                spriteCache.put(cacheKey, new ArrayList<>()); // Lista vacía → sprite null → fallback visible
            } else {
                LOG.info("Frames cargados para [" + cacheKey + "]: " + frames.size());
                spriteCache.put(cacheKey, frames);
            }
        }
        LOG.info("======= FIN CARGA SPRITES =======");
    }

    // ============ RECUPERACIÓN DE RECURSOS ============

    /**
     * OBTIENE LOS FRAMES DE ANIMACIÓN PARA UN PERSONAJE Y DIRECCIÓN.
     */
    public List<Image> getCharacterFrames(String characterName, Direction direction) {
        String key = characterName.toLowerCase().trim() + "_" + direction.name();
        // Solo devolver defaultSprites si la clave ni siquiera existe en caché
        // Si existe pero está vacía (carga fallida) → devolver null para que el
        // renderer use su fallback
        if (!spriteCache.containsKey(key)) {
            return defaultSprites.get(direction); // Primera vez, antes de cargar
        }
        List<Image> cached = spriteCache.get(key);
        return cached.isEmpty() ? null : cached;
    }

    /**
     * OBTIENE UN SPRITE ESPECÍFICO DEL PERSONAJE SEGÚN DIRECCIÓN Y FRAME.
     */
    public Image getCharacterSprite(String characterName, Direction direction, int frameIndex) {
        List<Image> frames = getCharacterFrames(characterName, direction);
        if (frames == null || frames.isEmpty())
            return null; // PlayerRenderer dibujará un rectángulo de fallback visible
        int idx = frameIndex % frames.size(); // Índice circular seguro
        return frames.get(idx);
    }

    /**
     * CARGADOR ESPECÍFICO PARA SPRITES DE ZOMBIE.
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
     * CARGADOR ESPECÍFICO PARA SPRITES DE JEFES FINALES.
     */
    public Image getBossSprite(String bossName) {
        String cacheKey = "boss_" + bossName;
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        // FORMATO: /boss/sergio/sergionormal.png
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
     * CARGA UNA IMAGEN DESDE LA RUTA ESPECIFICADA.
     * UTILIZA CACHÉ PARA EVITAR CARGAR LA MISMA IMAGEN VARIAS VECES.
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
     * ESCALA UNA IMAGEN AL TAMAÑO ESPECIFICADO.
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
     * CARGA UNA IMAGEN DE FONDO DESDE LA RUTA ESPECIFICADA.
     */
    public Image loadBackground(String path) {
        return loadImage(path);
    }

    /**
     * LIMPIA LA CACHÉ DE IMÁGENES Y SPRITES.
     */
    public void clearCache() {
        imageCache.clear();
        spriteCache.clear();
    }

    /**
     * LIBERA TODOS LOS RECURSOS DEL SERVICIO.
     * ELIMINA LA INSTANCIA SINGLETON.
     */
    public void dispose() {
        clearCache();
        defaultSprites.clear();
        instance = null;
    }
}
