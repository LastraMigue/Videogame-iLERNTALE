package equipoilerntale.view.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Senior Architect Refactor: SpriteManager
 * Handles automated sprite loading, frame detection, and caching.
 */
public class SpriteManager {

    private static final Logger LOG = Logger.getLogger(SpriteManager.class.getName());

    private final String nombrePersonaje;
    private final int tamanoSprite;

    // Structure: Map<Direction, List<Frames>>
    private final Map<String, List<Image>> spritesCache = new HashMap<>();
    private final Map<String, List<Image>> defaultSprites = new HashMap<>();

    public SpriteManager(String nombrePersonaje, int tamanoSprite) {
        this.nombrePersonaje = nombrePersonaje.toLowerCase().trim();
        this.tamanoSprite = tamanoSprite;
        inicializarDefaultSprites();
        cargarSpritesAutomatico();
    }

    private void inicializarDefaultSprites() {
        String[] direcciones = { "arriba", "abajo", "izquierda", "derecha" };
        Color[] colores = { Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE };

        for (int i = 0; i < direcciones.length; i++) {
            List<Image> frames = new ArrayList<>();
            // Create 2 default frames for each direction
            for (int f = 1; f <= 2; f++) {
                BufferedImage img = new BufferedImage(tamanoSprite, tamanoSprite, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(colores[i]);
                g.fillRoundRect(5, 5, tamanoSprite - 10, tamanoSprite - 10, 15, 15);
                g.setColor(Color.WHITE);
                g.drawString(direcciones[i].substring(0, 1) + f, 10, 20);
                g.dispose();
                frames.add(img);
            }
            defaultSprites.put(direcciones[i], frames);
        }
    }

    private void cargarSpritesAutomatico() {
        String[] direcciones = { "arriba", "abajo", "izquierda", "derecha" };

        for (String dir : direcciones) {
            List<Image> frames = new ArrayList<>();
            int frameIdx = 1;
            boolean buscando = true;

            while (buscando) {
                // Construction pattern: /player/baku/derecha1baku.png
                String ruta = String.format("/player/%s/%s%d%s.png",
                        nombrePersonaje, dir, frameIdx, nombrePersonaje);

                Image img = cargarImagen(ruta);
                if (img != null) {
                    frames.add(img);
                    frameIdx++;
                } else {
                    buscando = false;
                }

                // Safety break for malformed folders
                if (frameIdx > 10)
                    break;
            }

            if (frames.isEmpty()) {
                LOG.warning("No se encontraron sprites para " + dir + " (" + nombrePersonaje + "). Usando defaults.");
                spritesCache.put(dir, defaultSprites.get(dir));
            } else {
                LOG.info("Cargados " + frames.size() + " frames para " + dir + " de " + nombrePersonaje);
                spritesCache.put(dir, frames);
            }
        }
    }

    private Image cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null)
            return null;

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        if (img.getWidth(null) == -1)
            return null;

        return img.getScaledInstance(tamanoSprite, tamanoSprite, Image.SCALE_SMOOTH);
    }

    public Image cargarFondo(String ruta, int ancho, int alto) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            LOG.warning("Fondo no encontrado: " + ruta);
            return null;
        }
        return new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    }

    public List<Image> getFrames(String direccion) {
        return spritesCache.getOrDefault(direccion, defaultSprites.get("abajo"));
    }

    public Image getSprite(String direccion, int frameIndex) {
        List<Image> frames = getFrames(direccion);
        if (frames == null || frames.isEmpty())
            return null;

        // Ensure index is within bounds (safety)
        int idx = Math.max(0, Math.min(frameIndex, frames.size() - 1));
        return frames.get(idx);
    }

    public void dispose() {
        spritesCache.clear();
        defaultSprites.clear();
    }
}
