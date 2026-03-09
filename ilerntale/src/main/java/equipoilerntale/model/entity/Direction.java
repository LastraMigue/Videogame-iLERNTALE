package equipoilerntale.model.entity;

/**
 * REPRESENTA LAS DIRECCIONES POSIBLES DE MOVIMIENTO Y ESTADO DEL PERSONAJE.
 */
public enum Direction {
    UP("arriba"),
    DOWN("abajo"),
    LEFT("izquierda"),
    RIGHT("derecha"),
    IDLE("abajo");

    private final String value;

    /**
     * CONSTRUCTOR DE LA ENUMERACIÓN DE DIRECCIONES.
     */
    Direction(String value) {
        this.value = value;
    }

    /**
     * OBTIENE EL VALOR DE TEXTO ASOCIADO A LA DIRECCIÓN.
     */
    public String getValue() {
        return value;
    }

    /**
     * CONVIERTE UNA CADENA DE TEXTO A UN VALOR DE DIRECTION.
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
