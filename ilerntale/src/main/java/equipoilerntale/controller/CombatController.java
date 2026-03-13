package equipoilerntale.controller;

import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.model.combat.minigames.MinigameRules;

/**
 * CONTROLADOR DE LA LÓGICA DE COMBATE.
 * COORDINA LAS REGLAS DEL MINIJUEGO ACTUAL CON EL MODELO DE LA ARENA.
 */
public class CombatController {

    private ArenaModel arenaModel;
    private InputHandler inputHandler;
    private MinigameRules currentRules;

    /**
     * CONSTRUCTOR DEL CONTROLADOR DE COMBATE.
     */
    public CombatController(ArenaModel arenaModel, InputHandler inputHandler) {
        this.arenaModel = arenaModel;
        this.inputHandler = inputHandler;
    }

    public void setRules(MinigameRules rules) {
        this.currentRules = rules;
    }

    public void startMinigame() {
        if (currentRules != null && arenaModel != null) {
            currentRules.start(arenaModel);
        }
    }

    public void update() {
        if (arenaModel != null && currentRules != null) {
            currentRules.update(arenaModel, inputHandler);
        }
    }

    public boolean isMinigameFinished() {
        if (currentRules != null && arenaModel != null) {
            return currentRules.isFinished(arenaModel);
        }
        return false;
    }
}