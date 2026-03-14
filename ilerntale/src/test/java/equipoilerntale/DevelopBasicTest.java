package equipoilerntale;

import org.junit.Test;
import static org.junit.Assert.*;
import equipoilerntale.model.entity.Player;
import equipoilerntale.view.ui.BarraVida;
import java.awt.Rectangle;

/**
 * Test básico para la rama Develop.
 * Verifica la integridad de los componentes fundamentales.
 */
public class DevelopBasicTest {

    @Test
    public void testGameSettings() {
        assertTrue("Ancho pantalla inválido", GameSettings.ANCHO_PANTALLA >= 800);
        assertTrue("Alto pantalla inválido", GameSettings.ALTO_PANTALLA >= 600);
        assertTrue("FPS objetivo demasiado bajo", GameSettings.FPS_OBJETIVO >= 30);
    }

    @Test
    public void testBarraVidaLogic() {
        BarraVida bv = new BarraVida(100, "Test");
        assertEquals(100, bv.getHealth());
        
        bv.takeDamage(30);
        assertEquals(70, bv.getHealth());
        
        bv.heal(50);
        assertEquals(100, bv.getHealth()); // Clamping at max
        
        bv.takeDamage(200);
        assertEquals(0, bv.getHealth()); // Clamping at 0
    }

    @Test
    public void testPlayerInitialization() {
        Player p = new Player(1000, 1000);
        assertEquals(GameSettings.PLAYER_TAMANO, p.getSize());
        assertNotNull(p.getHitbox(p.getX(), p.getY()));
        
        Rectangle hitbox = p.getHitbox(p.getX(), p.getY());
        assertTrue("Hitbox debe estar dentro del sprite", 
            hitbox.width <= p.getSize() && hitbox.height <= p.getSize());
    }

    @Test
    public void testMovementClamping() {
        // Test que el jugador no salga del mapa
        Player p = new Player(0, 0, 800, 600);
        p.moveIfNoCollision(-10, -10, java.util.List.of());
        assertTrue("X no debe ser negativa", p.getX() >= 0);
        assertTrue("Y no debe ser negativa", p.getY() >= 0);
        
        p.setX(790);
        p.moveIfNoCollision(50, 0, java.util.List.of());
        assertTrue("X no debe exceder mapWidth", p.getX() <= 800 - p.getSize());
    }
}
