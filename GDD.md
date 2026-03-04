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
* Debe esquivar los ataques temáticos del zombie (libros, dentaduras, babas) que caen como proyectiles durante 15-20 segundos.

---

## 4. El Mundo y la Historia
* **El Escenario:** El Instituto iLERNA.
    * *Zonas clave:* Pasillos de taquillas, Cafetería (zona de alto riesgo), Gimnasio, y el Laboratorio de Ciencias (zona final).
* **El Protagonista:** Un estudiante antiguo que visita el centro despues de mucho tiempo.
* **Lore:** A través de notas y conversaciones, se descubre que el virus no es mágico, sino un experimento.

---

## 5. Enemigos

| Tipo de Enemigo | Velocidad (Mapa) | Proyectiles (Combate) |
| :--- | :--- | :--- |
| **Zombie Fácil** | Lenta | Libros, aviones de papel |
| **Zombie Medio** | Media | Escobas, llaves inglesas |
| **Zombie Rápido** | Rápida | Balones prisioneros |

### Jefes Finales: El Científico y Su Creación
* **El Científico:** Cuenta el motivo por el cual ha creado el virus y sus ataques son jeringuillas para intentar infectarnos.
* **Su Creacion:** Tiene doble transformacion y rompe la cuarta pared (creando ventanas emergentes con texto), sus ataques son mas variados y dificiles de esquivar.

---

## 6. Arte y Audio
* **Estilo Visual:** Pixel art retro. Exploración en tonos apagados (grises). Combate en un fuerte contraste de blanco y negro para la legibilidad de los proyectiles.
* **Audio:** * *Mapa:* Silencio tenso, pasos, gruñidos distantes.
    * *Persecución:* Música 8-bits acelerada.
    * *Combate:* Tema chiptune dinámico y rítmico. Sonidos de "blips" clásicos para los textos de los diálogos.