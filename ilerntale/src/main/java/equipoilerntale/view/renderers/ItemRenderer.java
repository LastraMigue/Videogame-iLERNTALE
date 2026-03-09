package equipoilerntale.view.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import equipoilerntale.model.entities.ItemModel;

public class ItemRenderer {

    public void renderMenu(Graphics2D g2d, List<ItemModel> items, int selectedIndex, Font font) {
        if (items == null || items.isEmpty()) {
            g2d.setFont(font.deriveFont(28f));
            g2d.setColor(Color.WHITE);
            g2d.drawString("(Vacio)", 260, 290);
            return;
        }

        Font itemFont = font.deriveFont(26f); // Tamaño del texto de items
        g2d.setFont(itemFont);
        g2d.setColor(Color.WHITE);

        // El recuadro va desde X:200 hasta X:800 (Ancho: 600)
        // El recuadro va desde Y:240 hasta Y:490 (Alto: 250)

        int startX = 270; // Margen 40px por la izq (el asterisco se dibuja en 240)
        int startY = 300; // Margen sup ~40px
        int spacingY = 60; // Expandimos el espaciado para aprovechar el alto

        // Dibujar lista de items
        for (int i = 0; i < items.size(); i++) {
            ItemModel item = items.get(i);
            int yPos = startY + (i * spacingY);

            // Dibujar cursor " * " si está seleccionado
            if (i == selectedIndex) {
                g2d.drawString("* ", startX - 30, yPos);
            }

            String label = item.getNombre() + " x" + item.getCantidad();
            g2d.drawString(label, startX, yPos);
        }

        // Mostrar info del item seleccionado a la derecha
        ItemModel selected = items.get(selectedIndex);
        if (selected != null) {
            int rightX = 620; // Movido más a la derecha (antes 550)
            int rightY = 300; // Margen superior alineado
            int imageSize = 80; // Tamaño máximo de la imagen

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

            // Dibujar Descripción (separar por saltos de línea)
            g2d.setFont(font.deriveFont(20f)); // Letra más pequeña para descripciones
            String desc = selected.getDescripcion();
            if (desc != null) {
                String[] lines = desc.split("\n");
                int descY = rightY + imageSize + 30; // 30px de separación entre imagen y texto
                int textX = rightX - 10; // Llevado más a la izquierda respecto a la imagen
                for (String line : lines) {
                    g2d.drawString(line, textX, descY);
                    descY += 24;
                }
            }
        }
    }
}
