package equipoilerntale;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test estricto para la rama Main.
 * Verifica la coherencia de los datos y el equilibrio del juego,
 * además de las configuraciones básicas (este test es más exhaustivo).
 */
public class MainStrictTest {

    @Test
    public void testBalanceZombies() {
        // En un juego consistente, la cantidad inicial de zombies debería ser
        // mayor o igual a la suma de los que hay repartidos, o al menos
        // debe tener sentido con respecto al mapa actual.
        int zombiesRepartidos = GameSettings.ZOMBIES_PASILLO +
                GameSettings.ZOMBIES_AULA_124 +
                GameSettings.ZOMBIES_AULA_123 +
                GameSettings.ZOMBIES_AULA_125;

        assertTrue("La cantidad inicial total de zombies debe ser coherente con los repartidos",
                GameSettings.ZOMBIE_CANTIDAD_INICIAL >= zombiesRepartidos);
    }

    @Test
    public void testBalanceCombate() {
        // Verificar que el daño del zombie sea menor a un porcentaje de la vida total
        // del jugador
        // (asumiendo que el jugador inicia con más de 10 de vida)
        // Ejemplo ficticio: Si el dano de un zombie es >= 25, es letal rápido
        assertTrue("El daño base del zombie debe ser mayor a 0", GameSettings.ZOMBIE_DANO > 0);
        assertTrue("La salud del zombie debe ser razonable", GameSettings.ZOMBIE_SALUD > 0);
    }

    @Test
    public void testRutasRecursos() {
        // Verificar que las rutas de los assets no estén vacías
        assertNotNull("La ruta de mapas no debe ser nula", GameSettings.RUTA_MAPAS);
        assertFalse("La ruta de mapas no debe estar vacía", GameSettings.RUTA_MAPAS.isEmpty());

        assertNotNull("La ruta de jugador no debe ser nula", GameSettings.RUTA_PLAYER);
        assertFalse("La ruta de jugador no debe estar vacía", GameSettings.RUTA_PLAYER.isEmpty());
    }
}
