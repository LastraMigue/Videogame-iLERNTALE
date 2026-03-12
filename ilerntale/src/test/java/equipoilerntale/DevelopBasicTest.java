package equipoilerntale;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test básico para la rama Develop.
 * Verifica que las configuraciones básicas del juego tengan sentido
 * y no contengan valores inválidos.
 */
public class DevelopBasicTest {

    @Test
    public void testResolucionPantalla() {
        // La resolución debe ser positiva y razonable
        assertTrue("El ancho de la pantalla debe ser mayor que 0", GameSettings.ANCHO_PANTALLA > 0);
        assertTrue("El alto de la pantalla debe ser mayor que 0", GameSettings.ALTO_PANTALLA > 0);
    }

    @Test
    public void testVelocidadJugador() {
        // La velocidad del jugador no debe ser negativa ni 0
        assertTrue("La velocidad del jugador debe ser al menos 1", GameSettings.PLAYER_VELOCIDAD > 0);
    }

    @Test
    public void testFpsObjetivo() {
        // Los FPS deben ser razonables
        assertTrue("Los FPS objetivo deben ser al menos 30", GameSettings.FPS_OBJETIVO >= 30);
    }
}
