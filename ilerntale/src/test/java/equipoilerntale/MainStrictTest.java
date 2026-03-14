package equipoilerntale;

import org.junit.Test;
import static org.junit.Assert.*;
import equipoilerntale.model.entity.*;
import equipoilerntale.model.combat.*;
import equipoilerntale.model.combat.projectiles.StraightProjectile;
import equipoilerntale.model.combat.minigames.ShooterRules;
import equipoilerntale.controller.InputHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Test estricto para la rama Main.
 * Realiza simulaciones de lógica compleja y verifica comportamientos avanzados.
 */
public class MainStrictTest {

    @Test
    public void testZombieSeparation() {
        List<Zombie> zombies = new ArrayList<>();
        // Dos zombies en la misma posición exacta
        zombies.add(new Zombie(100, 100, 1000, 1000));
        zombies.add(new Zombie(100, 100, 1000, 1000));
        
        Zombie z1 = zombies.get(0);
        Zombie z2 = zombies.get(1);
        
        // Simular movimiento hacia jugador en 200,200
        z1.updateMovement(200, 200, List.of(), zombies);
        z2.updateMovement(200, 200, List.of(), zombies);
        
        // No deben estar en la misma posición (la fuerza de separación debe actuar)
        assertFalse("Los zombies no deben solaparse perfectamente tras moverse", 
            z1.getX() == z2.getX() && z1.getY() == z2.getY());
    }

    @Test
    public void testBossPhase2Logic() {
        ArenaModel arena = new ArenaModel();
        arena.startCombat();
        
        // Simulamos activar fase 2 (controles invertidos)
        arena.setReversedControls(true);
        assertTrue("Los controles deben estar invertidos en fase 2", arena.isReversedControls());
        
        int startX = arena.getMouse().getX();
        // Intentamos mover derecha (dx=1). Con controles invertidos, debería ir a la izquierda.
        arena.intentarMoverMouse(1, 0); 
        assertTrue("El movimiento debe ser inverso (X disminuye)", arena.getMouse().getX() < startX);
    }

    @Test
    public void testArenaCollisions() {
        ArenaModel arena = new ArenaModel();
        arena.startCombat();
        
        // Proyectil tipo 1 (Bueno) en la posición del ratón
        int mx = arena.getMouse().getX();
        int my = arena.getMouse().getY();
        arena.addProjectile(new StraightProjectile(mx, my, 10, 0, 0, 1));
        
        arena.checkCollisions();
        assertEquals("Debe haber una colisión buena", 1, arena.getGoodCollisions());
        
        // Proyectil tipo 0 (Malo)
        arena.addProjectile(new StraightProjectile(mx, my, 10, 0, 0, 0));
        arena.checkCollisions();
        assertEquals("Debe haber una colisión mala", 1, arena.getBadCollisions());
    }

    @Test
    public void testShooterMinigameLogic() {
        ArenaModel arena = new ArenaModel();
        arena.startCombat();
        ShooterRules rules = new ShooterRules();
        InputHandler input = new InputHandler();
        
        rules.start(arena);
        // Simular 100 ticks para que pase la intro
        for(int i=0; i<150; i++) rules.update(arena, input);
        
        assertFalse("La intro del minijuego debería haber terminado", rules.isIntroActive());
        
        // Simular disparo (Enter)
        input.setPressed(java.awt.event.KeyEvent.VK_ENTER, true);
        rules.update(arena, input);
        
        boolean bulletFound = false;
        for(ProjectileModel p : arena.getProjectiles()) {
            if(p.getType() == 99) { // 99 es PlayerBullet
                bulletFound = true;
                break;
            }
        }
        assertTrue("El jugador debería haber disparado una bala", bulletFound);
    }

    @Test
    public void testMapConsistencyAcrossRooms() {
        // Verificar que las constantes de zombies por sala sumen lo correcto
        int totalExpected = GameSettings.ZOMBIES_PASILLO + 
                            GameSettings.ZOMBIES_AULA_124 + 
                            GameSettings.ZOMBIES_AULA_123 + 
                            GameSettings.ZOMBIES_AULA_125;
                            
        assertTrue("Configuración de zombies inconsistente", 
            GameSettings.ZOMBIE_CANTIDAD_INICIAL >= totalExpected);
            
        // Verificar existencia de rutas críticas
        assertNotNull(GameSettings.RUTA_MAPAS);
        assertNotNull(GameSettings.RUTA_PLAYER);
    }
}
