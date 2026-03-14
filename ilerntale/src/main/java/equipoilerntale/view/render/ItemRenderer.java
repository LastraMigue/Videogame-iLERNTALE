package equipoilerntale.view.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import equipoilerntale.model.entity.ItemModel;

/**
 * Clase encargada de dibujar los objetos y el inventario en la interfaz.
 */
public class ItemRenderer {

    /**
     * Renderiza la lista de objetos del inventario en forma de menú seleccionable.
     * Dibuja los nombres, cantidades, cursor de selección e información detallada del item.
     * 
     * @param g2d           Contexto gráfico 2D.
     * @param items         Lista de objetos en el inventario.
     * @param selectedIndex Índice del objeto actualmente resaltado.
     * @param font          Fuente base para los textos.
     */
    public void renderMenu(Graphics2D g2d, List<ItemModel> items, int selectedIndex, Font font) {
        if (items == null || items.isEmpty()) {
            g2d.setFont(font.deriveFont(28f));
            g2d.setColor(Color.WHITE);
            g2d.drawString("(Vacio)", 260, 290);
            return;
        }

        Font itemFont = font.deriveFont(26f);
        g2d.setFont(itemFont);
        g2d.setColor(Color.WHITE);

        int startX = 270;
        int startY = 300;
        int spacingY = 60;

        for (int i = 0; i < items.size(); i++) {
            ItemModel item = items.get(i);
            int yPos = startY + (i * spacingY);

            if (i == selectedIndex) {
                g2d.drawString("* ", startX - 30, yPos);
            }

            String label = item.getNombre() + " x" + item.getCantidad();
            g2d.drawString(label, startX, yPos);
        }

        ItemModel selected = items.get(selectedIndex);
        if (selected != null) {
            int rightX = 620;
            int rightY = 300;
            int imageSize = 80;

            // Dibujar Sprite manteniendo tamaño escalado de la original
            Image sprite = selected.getSprite();
            if (sprite != null) {
                int imgW = sprite.getWidth(null);
                int imgH = sprite.getHeight(null);
                if (imgW > 0 && imgH > 0) {
                    double ratio = Math.min((double) imageSize / imgW, (double) imageSize / imgH);
                    int drawW = (int) (imgW * ratio);
                    int drawH = (int) (imgH * ratio);
                    g2d.drawImage(sprite, rightX, rightY, drawW, drawH, null);
                } else {
                    g2d.drawImage(sprite, rightX, rightY, imageSize, imageSize, null);
                }
            }

            g2d.setFont(font.deriveFont(20f));
            String desc = selected.getDescripcion();
            if (desc != null) {
                String[] lines = desc.split("\n");
                int descY = rightY + imageSize + 30;
                int textX = rightX - 10;
                for (String line : lines) {
                    g2d.drawString(line, textX, descY);
                    descY += 24;
                }
            }
        }
    }
}
