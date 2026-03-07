package equipoilerntale.controller;

import equipoilerntale.model.combat.ArenaModel;

public class CombatController {

    private ArenaModel arenaModel;
    private InputHandler inputHandler;
    private int tickCounter = 0;

    public CombatController(ArenaModel arenaModel, InputHandler inputHandler) {
        this.arenaModel = arenaModel;
        this.inputHandler = inputHandler;
    }

    public void update() {
        if (arenaModel != null) {

            // 1. Mover al jugador
            if (arenaModel.getMouse() != null) {
                int dx = 0;
                int dy = 0;
                if (inputHandler.upPressed)
                    dy = -1;
                if (inputHandler.downPressed)
                    dy = 1;
                if (inputHandler.leftPressed)
                    dx = -1;
                if (inputHandler.rightPressed)
                    dx = 1;

                if (dx != 0 || dy != 0) {
                    arenaModel.intentarMoverMouse(dx, dy);
                }
            }

            // 2. Mover proyectiles y generar nuevos
            if (arenaModel.getProjectiles() != null) {
                arenaModel.actualizarProjectiles();

                // Dispara un proyectil nuevo cada ~0.5 segundos (30 frames a 60fps)
                tickCounter++;
                if (tickCounter >= 30) {
                    arenaModel.spawnProjectile();
                    tickCounter = 0;
                }
            }
        }
    }
}