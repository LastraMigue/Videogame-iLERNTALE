package equipoilerntale.model;

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

public class ResourceManager {

    private static final Logger LOG = Logger.getLogger(ResourceManager.class.getName());
    private static final Map<String, List<Image>> SPRITES_CACHE = new HashMap<>();
    private static final Map<String, List<Image>> DEFAULT_SPRITES = new HashMap<>();
    private static int spriteSize = 48;
    private static boolean initialized = false;

    private ResourceManager() {
    }

    public static void initialize(int tamanoSprite) {
        spriteSize = tamanoSprite;
        if (!initialized) {
            inicializarDefaultSprites();
            initialized = true;
        }
    }

    private static void inicializarDefaultSprites() {
        String[] direcciones = { "arriba", "abajo", "izquierda", "derecha" };
        Color[] colores = { Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE };

        for (int i = 0; i < direcciones.length; i++) {
            List<Image> frames = new ArrayList<>();
            for (int f = 1; f <= 2; f++) {
                BufferedImage img = new BufferedImage(spriteSize, spriteSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(colores[i]);
                g.fillRoundRect(5, 5, spriteSize - 10, spriteSize - 10, 15, 15);
                g.setColor(Color.WHITE);
                g.drawString(direcciones[i].substring(0, 1) + f, 10, 20);
                g.dispose();
                frames.add(img);
            }
            DEFAULT_SPRITES.put(direcciones[i], frames);
        }
    }

    public static BufferedImage getPlayerSprite(String nombrePersonaje, String direccion, int frameIndex) {
        String cacheKey = nombrePersonaje.toLowerCase().trim() + "_" + direccion;

        if (!SPRITES_CACHE.containsKey(cacheKey)) {
            cargarSpritesPersonaje(nombrePersonaje, direccion);
        }

        List<Image> frames = SPRITES_CACHE.getOrDefault(cacheKey, DEFAULT_SPRITES.get(direccion));
        if (frames == null || frames.isEmpty()) {
            frames = DEFAULT_SPRITES.get("abajo");
        }

        if (frames == null) {
            return null;
        }

        int idx = Math.max(0, Math.min(frameIndex, frames.size() - 1));
        Image img = frames.get(idx);
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage buffered = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buffered.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return buffered;
    }

    private static void cargarSpritesPersonaje(String nombrePersonaje, String direccion) {
        String nombre = nombrePersonaje.toLowerCase().trim();
        List<Image> frames = new ArrayList<>();
        int frameIdx = 1;
        boolean buscando = true;

        while (buscando) {
            String ruta = String.format("/player/%s/%s%d%s.png", nombre, direccion, frameIdx, nombre);
            Image img = cargarImagen(ruta);
            if (img != null) {
                frames.add(img);
                frameIdx++;
            } else {
                buscando = false;
            }
            if (frameIdx > 10) {
                break;
            }
        }

        String cacheKey = nombre + "_" + direccion;
        if (frames.isEmpty()) {
            LOG.warning("No se encontraron sprites para " + direccion + " (" + nombre + "). Usando defaults.");
            SPRITES_CACHE.put(cacheKey, DEFAULT_SPRITES.get(direccion));
        } else {
            LOG.info("Cargados " + frames.size() + " frames para " + direccion + " de " + nombre);
            SPRITES_CACHE.put(cacheKey, frames);
        }
    }

    private static Image cargarImagen(String ruta) {
        URL url = ResourceManager.class.getResource(ruta);
        if (url == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        if (img.getWidth(null) == -1) {
            return null;
        }
        return img.getScaledInstance(spriteSize, spriteSize, Image.SCALE_SMOOTH);
    }

    public static Image cargarFondo(String ruta, int ancho, int alto) {
        URL url = ResourceManager.class.getResource(ruta);
        if (url == null) {
            LOG.warning("Fondo no encontrado: " + ruta);
            return null;
        }
        return new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    }

    public static void dispose() {
        SPRITES_CACHE.clear();
    }
}
