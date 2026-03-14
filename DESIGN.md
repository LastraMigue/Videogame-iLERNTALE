# iLERNTALE - Ingeniería Directa (RA5.d)

Este documento define la arquitectura técnica del videojuego **iLERNTALE**, estructurada bajo el patrón de diseño **Modelo-Vista-Controlador (MVC)** para garantizar una separación de responsabilidades clara, escalabilidad y facilidad de mantenimiento.

## 1. Diagrama de Clases (MVC)

El siguiente diagrama representa las entidades, interfaces de usuario y controladores, así como sus relaciones fundamentales.

```mermaid
classDiagram
    direction TB

    %% CAPA MODELO (Entities & State)
    namespace Modelo {
        class Entity {
            <<abstract>>
            -int x
            -int y
            -int size
            -Direction direction
            +getHitbox() Rectangle
            +moveIfNoCollision() void
        }
        class Player {
            -int health
            -int maxHealth
            +takeDamage(int) void
            +heal(int) void
        }
        class Zombie {
            -int type
            -int detectionRadius
            +updateMovement() void
            +takeDamage(int) void
        }
        class Boss {
            -int health
            -boolean isAlive
            +takeDamage(int) void
        }
        class ItemModel {
            -String nombre
            -int cantidad
            -boolean usableEnCombate
            +consumir() void
        }
        class ArenaModel {
            -List projectiles
            -MouseModel mouse
            +update() void
            +checkCollisions() void
        }
        class AbstractRoom {
            <<abstract>>
            -String name
            -List walls
            -List doors
            +getZombiesToSpawn() int
        }
    }

    %% CAPA VISTA (Rendering & UI)
    namespace Vista {
        class MainFrame {
            -JPanel currentPanel
            +cambiarPantalla(String) void
        }
        class ExplorationPanel {
            -MapRenderer mapRenderer
            +paintComponent(Graphics) void
        }
        class CombatPanel {
            -BarraVida healthBar
            -BulletRenderer bulletRenderer
            +paintComponent(Graphics) void
        }
        class AssetService {
            <<singleton>>
            -Map imageCache
            +getCharacterSprite() Image
            +loadBackground() Image
        }
    }

    %% CAPA CONTROLADOR (Logic & Handlers)
    namespace Controlador {
        class MainController {
            -GameState currentState
            +update() void
        }
        class ExplorationManager {
            -Player player
            -AbstractRoom currentRoom
            +handlePlayerMovement() void
            +checkInteractions() void
        }
        class CombatController {
            -ArenaModel arena
            -MinigameRules rules
            +update() void
            +isMinigameFinished() boolean
        }
        class InputHandler {
            -boolean upPressed
            -boolean enterPressed
            +keyPressed(KeyEvent) void
        }
    }

    %% RELACIONES
    Entity <|-- Player : Herencia
    Entity <|-- Zombie : Herencia
    Entity <|-- Boss : Herencia
    
    ExplorationManager *-- Player : Composición
    ExplorationManager *-- AbstractRoom : Composición
    CombatController *-- ArenaModel : Composición
    
    ExplorationPanel --> ExplorationManager : Consulta datos
    CombatPanel --> CombatController : Consulta datos
    
    ExplorationManager --> InputHandler : Usa
    CombatController --> InputHandler : Usa
    
    MainFrame *-- ExplorationPanel : Contiene
    MainFrame *-- CombatPanel : Contiene
```

## 2. Definición de Responsabilidades

Siguiendo el principio de **Responsabilidad Única (SRP)**, el sistema se divide de la siguiente manera:

### A. Modelo (Model)
Contiene la lógica de negocio y los datos puros. No conoce la interfaz gráfica.
*   **Entity / Player / Zombie**: Gestionan sus propios atributos (vida, posición) y reglas internas (recibir daño, cálculo de colisiones).
*   **ArenaModel**: Mantiene el estado físico del combate (posiciones de proyectiles y colisiones detectadas).
*   **ItemModel**: Define las propiedades de los objetos y su lógica de consumo.

### B. Vista (View)
Se encarga exclusivamente de la representación visual del Modelo.
*   **MainFrame**: El contenedor principal que orquestra el cambio entre paneles (Pantalla de Selección, Exploración, Combate).
*   **Panels (Combat/Exploration)**: Interpretan los datos del controlador para dibujarlos mediante `Graphics2D`.
*   **AssetService**: Centraliza la carga de recursos (imágenes, sonidos) para evitar redundancia en memoria.

### C. Controlador (Controller)
Actúa como puente entre el Modelo y la Vista, gestionando el flujo del juego.
*   **MainController**: Controla el ciclo de vida del juego (estados, pausas, transiciones).
*   **ExplorationManager / CombatController**: Traducen las entradas del usuario (`InputHandler`) en cambios del estado del Modelo y coordinan la lógica temporal (frames, actualizaciones).
*   **InputHandler**: Captura los eventos de hardware (teclado/ratón) y los expone de forma desacoplada.

## 3. Beneficios del Diseño
1.  **Ingeniería Directa / Inversa**: La estructura modular permite mapear directamente estas clases con los archivos `.java`, facilitando la actualización del diagrama si el código cambia.
2.  **Desacoplamiento**: Es posible cambiar el motor gráfico (Vista) sin alterar la lógica de los personajes (Modelo).
3.  **Testeo**: La lógica del Modelo puede probarse mediante tests unitarios sin necesidad de levantar una interfaz gráfica.

## 4. Diagrama de Casos de Uso 

Los siguientes diagramas exponen tres casos de uso de iLERNTALE

Caso de Uso 1: Iniciar Partida

``` mermaid

graph LR
%% DIAGRAMA DE CASOS DE USO - INICIAR PARTIDA

%% Definir Actores
Player((Player))

%% Definir límite del Sistema y Acciones
subgraph "iLERNTALE"
CU1([Start Game])
CU2([Select Play In Main Menu])
CU3([Select Character])
CU4([Skip Prologue])

%% Definir relaciones especiales (include y extend)

%% Relación include (obligatoria). Es obligatorio seleccionar Personaje para Iniciar Partida
CU1 -.->|&lt;&lt;include&gt;&gt;| CU3

%% Relación include (obligatoria). Es obligatorio pulsar Play para Iniciar Partida (y seleccionar personaje)
CU3 -.->|&lt;&lt;include&gt;&gt;| CU2

%% Relación extend (opcional). Es opcional saltar el prólogo al Iniciar Partida
CU4 -.->|&lt;&lt;extend&gt;&gt;| CU1
end

%% Definir relaciones Actor/Casos de Uso
Player --- CU1

```

Caso de Uso 2: Atacar

``` mermaid

graph LR
%% DIAGRAMA DE CASOS DE USO - ATACAR

%% Definir Actores
Player((Player))
Foe((Foe))

%% Definir límite del Sistema y Acciones
subgraph "iLERNTALE Combat"
CU1([Attack])
CU2([Select Fight Option])
CU3([Engage In Battle])
CU4([Inflict Damage])
CU5([Dodge Enemy Attack])
CU6([Foe Attack])

%% Definir relaciones especiales (include y extend)

%% Relación include (obligatoria). Hay que entrar en combate para Atacar
CU1 -.->|&lt;&lt;include&gt;&gt;| CU2

%% Relación include (obligatoria). Hay que seleccionar opción Luchar (Fight) para Atacar
CU2 -.->|&lt;&lt;include&gt;&gt;| CU3

%% Relación extend (opcional). Aunque para infligir daño hay que atacar, en la pantalla de atacar es opcional infligir daño, ya que se puede fallar el minijuego que daña al enemigo (no recoger los puños verdes), aunque recomendable
CU1 -.->|&lt;&lt;extend&gt;&gt;| CU4

%% Relación extend (opcional). Es opcional esquivar los ataques del enemigo para Atacar (aunque recomendable)
CU1 -.->|&lt;&lt;extend&gt;&gt;| CU5
end

%% Definir relaciones Actor/Casos de Uso
Player --- CU1
Foe --- CU6

```

Caso de Uso 3: Utilizar objeto en Combate

``` mermaid

graph LR
%% DIAGRAMA DE CASOS DE USO - USAR OBJETO EN COMBATE

%% Definir Actores
Player((Player))
Foe((Foe))

%% Definir límite del Sistema y Acciones
subgraph "iLERNTALE Combat"
CU1([Use Battle Item])
CU2([Select Item Option])
CU3([Select Item])
CU4([Engage In Battle])
CU5([Pick Up Item])
CU6([Await Player's Decision])

%% Definir relaciones especiales (include y extend)

%% Relación include (obligatoria). Hay que seleccionar un objeto para usarlo
CU1 -.->|&lt;&lt;include&gt;&gt;| CU3

%% Relación include (obligatoria). Para seleccionar un objeto, hay que seleccionar la opción Item
CU3 -.->|&lt;&lt;include&gt;&gt;| CU2

%% Relación include (obligatoria). Para seleccionar la opción item
CU2 -.->|&lt;&lt;include&gt;&gt;| CU4

%% Para usar un objeto, es obligatorio haberlo recogido del mapa antes de entrar en combate
CU4 -.->|&lt;&lt;include&gt;&gt;| CU5
end

%% Definir relaciones Actor/Casos de Uso
Player --- CU1
%% Mientras el jugador utiliza un objeto, el enemigo no ataca, espera que tome la decisión de usar objeto, atacar u otra
Foe --- CU6

```

## 5. Diagrama de Secuencia

El siguiente diagrama representa de manera detallada una interacción crítica y concreta de iLERNTALE, en este caso, el flujo desde que el jugador pulsa el botón de luchar (Fight) hasta que se resta vida al enemigo (o al propio jugador)

``` mermaid

sequenceDiagram
autonumber
%% Diagrama de Secuencia para el proceso de atacar y restar vida en iLERNTALE

%% Actores y participantes
actor Player
%% Paneles participantes

%%  Fight Button activa el minijuego de combate
participant FightButton
%% El minijuego es donde transcurre el combate
participant BattleMinigame
%% En el minijuego se restará vida según condiciones
%% Enemigo
participant Foe

%% Secuencia
%% El Jugador entra en combate con el enemigo y selecciona el botón de Luchar (Fight)
Player->>FightButton: selectFightButton()
activate FightButton
%% Al activarse el botón Fight, se genera el Minijuego y se activa éste y después la pelea con el enemigo
FightButton->>BattleMinigame: generateMinigame()
activate BattleMinigame
BattleMinigame->>Foe: triggerFoeCombat()
activate Foe
%% Dentro del minijuego se resta vida según las siguientes condiciones
%% Si el jugador contacta con los puños verdes, inflige daño al enemigo y le resta vida
alt player collects green fists
BattleMinigame->>Foe: FoeTakesDamage
Foe-->>BattleMinigame: retrieveFoeRemainingHP()
%% Si el jugador contacta con las calaveras rojas, será el enemigo quien le inflija daño
else player collects red skulls
BattleMinigame->>Foe: FoeInflictsDamage
Foe-->>BattleMinigame: retrievePlayerRemainingHP()
end

deactivate Foe
BattleMinigame-->> FightButton: resetMinigame()
deactivate BattleMinigame
FightButton-->> Player: endFightTurn()
deactivate FightButton

```




