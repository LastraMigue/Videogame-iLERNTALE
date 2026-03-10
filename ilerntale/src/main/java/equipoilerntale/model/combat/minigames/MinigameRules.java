package equipoilerntale.model.combat.minigames;

import java.awt.Graphics2D;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.controller.InputHandler;

public interface MinigameRules {
    void start(ArenaModel arena);

    void update(ArenaModel arena, InputHandler input);

    void render(Graphics2D g2d, ArenaModel arena);

    boolean isIntroActive();

    boolean isFinished(ArenaModel arena);

    int getDurationInSeconds();
}
