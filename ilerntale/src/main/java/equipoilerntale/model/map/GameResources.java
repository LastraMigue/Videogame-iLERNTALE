package equipoilerntale.model.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class GameResources {

    private static final Logger LOG = Logger.getLogger(GameResources.class.getName());

    private static GameResources instance;
    private final Map<String, Image> imageCache = new HashMap<>();
    private final Map<String, List<Image>> spriteCache = new HashMap<>();
    private final Map<String, List<Image>> defaultSprites = new HashMap<>();

    private final int defaultSpriteSize;

    private GameResources(int defaultSpriteSize) {
        this.defaultSpriteSize = defaultSpriteSize;
        initializeDefaultSprites();
    }

    public static GameResources getInstance() {
        if (instance == null) {
            instance = new GameResources(32);
        }
        return instance;
    }

    public static void initialize(int spriteSize) {
        instance = new GameResources(spriteSize);
    }

    private void initializeDefaultSprites() {
        String[] directions = { "arriba", "abajo", "izquierda", "derecha" };
        Color[] colors = { Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE };

        for (int i = 0; i < directions.length; i++) {
            List<Image> frames = new ArrayList<>();
            for (int f = 1; f <= 2; f++) {
                BufferedImage img = new BufferedImage(defaultSpriteSize, defaultSpriteSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(colors[i]);
                g.fillRoundRect(5, 5, defaultSpriteSize - 10, defaultSpriteSize - 10, 15, 15);
                g.setColor(Color.WHITE);
                g.drawString(directions[i].substring(0, 1) + f, 10, 20);
                g.dispose();
                frames.add(img);
            }
            defaultSprites.put(directions[i], frames);
        }
    }

    public void loadCharacterSprites(String characterName, int spriteSize) {
        String key = characterName.toLowerCase().trim();
        String[] directions = { "arriba", "abajo", "izquierda", "derecha" };

        for (String dir : directions) {
            List<Image> frames = new ArrayList<>();
            int frameIdx = 1;
            boolean searching = true;

            while (searching) {
                String path = String.format("/player/%s/%s%d%s.png", key, dir, frameIdx, key);
                Image img = loadImage(path);
                if (img != null) {
                    frames.add(img.getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH));
                    frameIdx++;
                } else {
                    searching = false;
                }
                if (frameIdx > 10) break;
            }

            if (frames.isEmpty()) {
                LOG.warning("No sprites found for " + dir + " (" + key + "). Using defaults.");
                List<Image> defaultFrames = defaultSprites.get(dir);
                if (defaultFrames != null) {
                    List<Image> scaledDefaults = new ArrayList<>();
                    for (Image df : defaultFrames) {
                        scaledDefaults.add(df.getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH));
                    }
                    spriteCache.put(key + "_" + dir, scaledDefaults);
                }
            } else {
                LOG.info("Loaded " + frames.size() + " frames for " + dir + " of " + key);
                spriteCache.put(key + "_" + dir, frames);
            }
        }
    }

    public List<Image> getCharacterFrames(String characterName, String direction) {
        String key = characterName.toLowerCase().trim() + "_" + direction;
        List<Image> frames = spriteCache.get(key);
        if (frames == null || frames.isEmpty()) {
            return defaultSprites.getOrDefault(direction, defaultSprites.get("abajo"));
        }
        return frames;
    }

    public Image getCharacterSprite(String characterName, String direction, int frameIndex) {
        List<Image> frames = getCharacterFrames(characterName, direction);
        if (frames == null || frames.isEmpty()) return null;
        int idx = Math.max(0, Math.min(frameIndex, frames.size() - 1));
        return frames.get(idx);
    }

    public Image loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        URL url = getClass().getResource(path);
        if (url == null) {
            LOG.warning("Image not found: " + path);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        if (img.getWidth(null) == -1) {
            return null;
        }
        imageCache.put(path, img);
        return img;
    }

    public Image loadImage(String path, int width, int height) {
        String cacheKey = path + "_" + width + "x" + height;
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }
        Image base = loadImage(path);
        if (base == null) return null;
        Image scaled = base.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageCache.put(cacheKey, scaled);
        return scaled;
    }

    public Image loadBackground(String path, int width, int height) {
        return loadImage(path, width, height);
    }

    public Image loadMap(String mapName, int width, int height) {
        String path = "/mapa/" + mapName + ".png";
        return loadImage(path, width, height);
    }

    public Image loadDialoguePortrait(String characterName, int width, int height) {
        String path = "/dialogue/" + characterName + ".png";
        return loadImage(path, width, height);
    }

    public Image loadMenuImage(String imageName) {
        String path = "/title/" + imageName;
        return loadImage(path);
    }

    public void preloadImages(List<String> paths) {
        for (String path : paths) {
            loadImage(path);
        }
    }

    public void clearCache() {
        imageCache.clear();
        spriteCache.clear();
    }

    public void dispose() {
        imageCache.clear();
        spriteCache.clear();
        defaultSprites.clear();
        instance = null;
    }
}
