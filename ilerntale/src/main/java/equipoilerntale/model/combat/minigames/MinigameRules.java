package equipoilerntale.model.combat.minigames;

import java.awt.Graphics2D;
import equipoilerntale.model.combat.ArenaModel;
import equipoilerntale.controller.InputHandler;

/**
 * Interfaz que define las reglas y el comportamiento de un minijuego de combate.
 * Cada minijuego debe implementar su lógica de inicio, actualización, renderizado y fin.
 */
public interface MinigameRules {
    /**
     * Inicializa el estado del minijuego.
     * 
     * @param arena El modelo de la arena donde se desarrolla el juego.
     */
    void start(ArenaModel arena);

    /**
     * Actualiza la lógica del minijuego en cada frame.
     * 
     * @param arena Modelo de la arena.
     * @param input Manejador de entrada de teclado.
     */
    void update(ArenaModel arena, InputHandler input);

    /**
     * Renderiza los elementos específicos del minijuego.
     * 
     * @param g2d Contexto gráfico para dibujar.
     * @param arena Modelo de la arena.
     */
    void render(Graphics2D g2d, ArenaModel arena);

    /**
     * Indica si la pantalla de introducción del minijuego está activa.
     * 
     * @return true si se está mostrando la intro, false en caso contrario.
     */
    boolean isIntroActive();

    /**
     * Determina si el minijuego ha finalizado.
     * 
     * @param arena Modelo de la arena.
     * @return true si se cumple la condición de victoria o derrota.
     */
    boolean isFinished(ArenaModel arena);

    /**
     * Obtiene la duración máxima configurada para el minijuego.
     * 
     * @return Duración en segundos.
     */
    int getDurationInSeconds();
}
