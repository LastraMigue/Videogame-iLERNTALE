package equipoilerntale.model.entity;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Modela un objeto del inventario del jugador.
 * Contiene información visual, descriptiva y lógica de uso.
 */
public class ItemModel {

    /** Nombre del objeto. */
    private String nombre;
    /** Descripción de la utilidad del objeto. */
    private String descripcion;
    /** Imagen representativa del objeto. */
    private Image sprite;
    /** Cantidad disponible en el inventario. */
    private int cantidad;
    /** Indica si se puede usar durante un combate. */
    private boolean esUsableEnCombate;
    /** Estado que indica si ha sido utilizado recientemente. */
    private boolean usado = false;

    /**
     * Constructor para modelos de objetos.
     * 
     * @param nombre Nombre del ítem.
     * @param descripcion Detalle de lo que hace.
     * @param rutaImagen Ruta al recurso de imagen.
     * @param cantidad Stock inicial.
     * @param esUsableEnCombate true si es un objeto de combate.
     */
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

    /**
     * Obtiene el nombre del objeto.
     * 
     * @return Nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la descripción del objeto.
     * 
     * @return Descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene la imagen del objeto.
     * 
     * @return Sprite cargado.
     */
    public Image getSprite() {
        return sprite;
    }

    /**
     * Obtiene la cantidad en posesión.
     * 
     * @return Cantidad.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de objetos.
     * 
     * @param cantidad Nuevo stock.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Indica si es usable en combate.
     * 
     * @return true si es de uso en combate.
     */
    public boolean isEsUsableEnCombate() {
        return esUsableEnCombate;
    }

    /**
     * Reduce en uno la cantidad disponible (si es mayor a cero).
     */
    public void consumir() {
        if (cantidad > 0) {
            cantidad--;
        }
    }

    /**
     * Indica si el objeto ya fue usado.
     * 
     * @return true si está marcado como usado.
     */
    public boolean isUsado() {
        return usado;
    }

    /**
     * Establece el estado de uso del objeto.
     * 
     * @param usado true para marcar como usado.
     */
    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
