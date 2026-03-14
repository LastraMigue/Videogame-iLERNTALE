# iLERNTALE - Diseño del Videojugo

---

## 1. Resumen Ejecutivo
**iLERNTALE** es un RPG 2D de misterio y supervivencia ambientado en el Instituto "iLERNA", asolado por un brote zombie repentino. El jugador, un estudiante atrapado, debe explorar los pasillos, esquivar a sus compañeros infectados y descubrir el origen de la pandemia. El combate combina la toma de decisiones por turnos con mecánicas de *bullet-hell* (esquivar proyectiles) en tiempo real.

## 2. Bucle de Juego (Core Loop)
El juego alterna continuamente entre dos estados principales:

1. **Exploración (Overworld):** Navegar por el instituto, buscar llaves/pistas y evitar el contacto visual con los zombies.
2. **Combate (Encuentro):** Si un zombie atrapa al jugador, se entra en la fase de batalla por turnos.

---

## 3. Mecánicas Principales

### A. Fase de Exploración
* **Cámara y Movimiento:** Vista cenital (Top-down) en 2D. Movimiento en 4 direcciones.
* **Sigilo y Persecución:** Los zombies patrullan los pasillos y correrán hacia él si lo detectan.
* **Transición:** El contacto con el sprite de un zombie inicia el combate.

### B. Sistema de Combate
Interfaz dividida: arriba el enemigo y las estadísticas, abajo el menú de acciones y la "Caja de Evasión".

**Turno del Jugador:**
* **Atacar:** Requiere acertar un minijuego de ritmo para maximizar el daño.
* **Interactuar:** Mantienes una conversacion si pudieses (LORE).
* **Objeto:** Resturar HP o aumentar ataque base.
* **Perdonar / Huir:** Probabilidad de éxito para finalizar el combate sin matar al zombie.

**Turno del Enemigo (Fase de Defensa):**
* El jugador controla un pequeño icono (un cuaderno) dentro de una caja inferior.
* Debe superar diversos **minijuegos temáticos** (ej. esquivar proyectiles clásicos, moverse por laberintos, usar escudos protectores o disparar de vuelta) durante 15-20 segundos. Cada enemigo puede presentar patrones de ataque y minijuegos distintos.

---

## 4. El Mundo y la Historia
* **El Escenario:** El Instituto iLERNA.
    * *Zonas clave:* Entrada, Secretaría, Pasillo Principal y las Aulas (123, 124, 125). Algunas zonas más peligrosas o importantes (como el Aula 124) están bloqueadas y requieren encontrar una Llave durante la exploración.
* **Personajes Seleccionables:** El jugador puede elegir entre tres estudiantes, cada uno con su propio estilo visual:
    * **Antonio**
    * **Baku**
    * **Migue**
* **Lore:** A través de notas y conversaciones, se descubre que el virus no es mágico, sino un experimento.
* **Objetos e Inventario:** El jugador puede recoger y utilizar objetos estratégicos:
    * **Botella Vida:** Recupera puntos de salud (PS +30).
    * **Patito Aguante:** Aumenta la defensa temporalmente.
    * **Pelota Ataque:** Duplica el daño infligido durante una ronda.
    * **Llave:** Objeto de progresión crucial para abrir aulas bloqueadas.

---

## 5. Enemigos

Las características de los enemigos no están predefinidas por categorías, sino que se generan de forma **aleatoria** al aparecer para aportar mayor variedad:
* **Tipos Visuales:** Existen 8 variaciones distintas de zombies.
* **Atributos Aleatorios:** Al instanciarse, cada zombie recibe valores diferentes de salud, velocidad de movimiento en el mapa (más rápidos o más lentos), radio de visión (para detectar al jugador) y precisión de persecución.
* **Comportamiento en Grupo:** Cuentan con lógica de separación automática para evitar apilarse y amontonarse gráficamente al perseguir al jugador.
* **Proyectiles (Combate):** Los proyectiles no están vinculados al zombie, sino al tipo de minijuego que se inicie aleatoriamente en el momento del combate.

### Jefe Final: "Sergio"
Existe un único jefe final que representa el mayor reto del juego, divido en dos fases de combate:
* **Fase 1:** El combate estándar con una vida mayor a la habitual (100 HP) y tamaño ampliado.
* **Fase Final:** Transformación que resetea la vida a 200 HP, invierte los controles del jugador (confusión), duplica el daño recibido e incluye ataques psicológicos que rompen la cuarta pared (como generar ventanas emergentes o "scare popups" directamente en la pantalla).

---

## 6. Arte y Audio
* **Estilo Visual:** Pixel art retro con sprites dinámicamente escalados y variaciones de color para cada tipo de zombie o personaje (como la transformación de la fase final).
* **Audio:**
  * Implementación centralizada mediante `SoundService` que soporta BGM en bucle continuo y SFX.
  * *Combate:* Diferentes temas para el combate general (`combate.wav`) y la fase final del jefe (`combatefinal.wav`).
  * *Efectos de sonido (SFX):* Interacciones en los menús (`mouse_click.wav`), retroalimentación sonora al acertar ataques (`hitbueno.wav`) y recibir daño (`hitmalo.wav`).

---

## 7. Especificaciones Técnicas
* **Motor:** Desarrollo nativo en Java utilizando las librerías **Swing** y **AWT** para el renderizado 2D.
* **Resolución:** 1000x600 píxeles.
* **Rendimiento:** Objetivo de 60 FPS constantes con lógica de *Delta Time*.
* **Arquitectura:** Modelo-Vista-Controlador (MVC) para separar la lógica de combate y exploración de la interfaz visual.