package equipoilerntale.model.entity;

/**
 * Representa las direcciones posibles de movimiento y estado de las entidades.
 */
public enum Direction {
    /** Dirección hacia arriba. */
    UP("arriba"),
    /** Dirección hacia abajo. */
    DOWN("abajo"),
    /** Dirección hacia la izquierda. */
    LEFT("izquierda"),
    /** Dirección hacia la derecha. */
    RIGHT("derecha"),
    /** Estado de inactividad (por defecto mirando hacia abajo). */
    IDLE("abajo");

    /** Valor de texto asociado a la dirección para carga de recursos. */
    private final String value;

    /**
     * Constructor de la enumeración.
     * 
     * @param value Texto identificador.
     */
    Direction(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor de texto asociado a la dirección.
     * 
     * @return Cadena de texto.
     */
    public String getValue() {
        return value;
    }

    /**
     * Convierte una cadena de texto a un valor de Direction.
     * 
     * @param text Texto a convertir.
     * @return Dirección correspondiente o DOWN por defecto.
     */
    public static Direction fromString(String text) {
        for (Direction d : Direction.values()) {
            if (d.value.equalsIgnoreCase(text)) {
                return d;
            }
        }
        return DOWN;
    }
}
