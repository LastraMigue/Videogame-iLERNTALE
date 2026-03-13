package equipoilerntale.model.entity;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ItemModel {

    private String nombre;
    private String descripcion;
    private Image sprite;
    private int cantidad;
    private boolean esUsableEnCombate;
    private boolean usado = false;

    public ItemModel(String nombre, String descripcion, String rutaImagen, int cantidad, boolean esUsableEnCombate) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.esUsableEnCombate = esUsableEnCombate;

        try {
            URL url = getClass().getResource(rutaImagen);
            if (url != null) {
                this.sprite = new ImageIcon(url).getImage();
            } else {
                System.err.println("No se encontró el objeto: " + rutaImagen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Image getSprite() {
        return sprite;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isEsUsableEnCombate() {
        return esUsableEnCombate;
    }

    public void consumir() {
        if (cantidad > 0) {
            cantidad--;
        }
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
