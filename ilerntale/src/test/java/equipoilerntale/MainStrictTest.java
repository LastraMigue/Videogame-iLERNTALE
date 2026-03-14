package equipoilerntale;

import org.junit.BeforeClass;
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
 */
public class MainStrictTest {

    @BeforeClass
    public static void setUpEntornoHeadless() {
        // Configuración para evitar errores gráficos en servidores sin pantalla (ej. GitHub Actions)
        System.setProperty("java.awt.headless", "true");
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException | NoClassDefFoundError e) {
            // Ignorar si el Toolkit ya está inicializado o no se encuentra JavaFX
        }
    }

    @Test
    public void testZombieSeparation() {
        List<Zombie> zombies = new ArrayList<>();
        zombies.add(new Zombie(100, 100, 1000, 1000));
        zombies.add(new Zombie(100, 100, 1000, 1000));
        
        Zombie z1 = zombies.get(0);
        Zombie z2 = zombies.get(1);
        
        z1.updateMovement(200, 200, List.of(), zombies);
        z2.updateMovement(200, 200, List.of(), zombies);
        
        assertFalse("Los zombies no deben solaparse perfectamente tras moverse", 
            z1.getX() == z2.getX() && z1.getY() == z2.getY());
    }

    @Test
    public void testBossPhase2Logic() {
        ArenaModel arena = new ArenaModel();
        arena.startCombat();
        
        arena.setReversedControls(true);
        assertTrue("Los controles deben estar invertidos en fase 2", arena.isReversedControls());
        
        int startX = arena.getMouse().getX();
        arena.intentarMoverMouse(1, 0); 
        assertTrue("El movimiento debe ser inverso (X disminuye)", arena.getMouse().getX() < startX);
    }

    @Test
    public void testArenaCollisions() {
        ArenaModel arena = new ArenaModel();
        arena.startCombat();
        
        int mx = arena.getMouse().getX();
        int my = arena.getMouse().getY();
        arena.addProjectile(new StraightProjectile(mx, my, 10, 0, 0, 1));
        
        arena.checkCollisions();
        assertEquals("Debe haber una colisión buena", 1, arena.getGoodCollisions());
        
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
        for(int i=0; i<150; i++) rules.update(arena, input);
        
        assertFalse("La intro del minijuego debería haber terminado", rules.isIntroActive());
        
        input.setPressed(java.awt.event.KeyEvent.VK_ENTER, true);
        rules.update(arena, input);
        
        boolean bulletFound = false;
        for(ProjectileModel p : arena.getProjectiles()) {
            if(p.getType() == 99) { 
                bulletFound = true;
                break;
            }
        }
        assertTrue("El jugador debería haber disparado una bala", bulletFound);
    }

    @Test
    public void testMapConsistencyAcrossRooms() {
        int totalExpected = GameSettings.ZOMBIES_PASILLO + 
                            GameSettings.ZOMBIES_AULA_124 + 
                            GameSettings.ZOMBIES_AULA_123 + 
                            GameSettings.ZOMBIES_AULA_125;
                            
        assertTrue("Configuración de zombies inconsistente", 
            GameSettings.ZOMBIE_CANTIDAD_INICIAL >= totalExpected);
            
        assertNotNull(GameSettings.RUTA_MAPAS);
        assertNotNull(GameSettings.RUTA_PLAYER);
    }
}
