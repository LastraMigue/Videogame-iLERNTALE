# iLERNTALE - RPG de Supervivencia y Misterio

Bienvenido al repositorio oficial de **iLERNTALE**, un videojuego desarrollado íntegramente por nuestro equipo para el 1er curso de DAW. Este proyecto combina mecánicas de RPG clásico con desafíos de habilidad en un entorno de supervivencia escolar.

---

## 🧟 Sinopsis
Eres un estudiante que regresa al Instituto **iLERNA** después de mucho tiempo, solo para encontrarte con un brote zombie repentino. Debes explorar los pasillos, recolectar suministros y enfrentarte a tus antiguos compañeros infectados para descubrir el origen del experimento que asola el centro.

---

## 🎮 Características Principales

### 1. Exploración y Sigilo (Overworld)
- **Navegación Táctica:** Explora escenarios detallados como la Entrada, Secretaría y las Aulas.
- **Sistema de Alerta:** Los zombies patrullan y te perseguirán si entras en su radio de visión.
- **Progresión:** Encuentra llaves y objetos estratégicos para desbloquear nuevas zonas y sobrevivir.

### 2. Combate por Turnos Dinámico
- **Fase de Acción:** Utiliza objetos o interactúa para avanzar en la historia.
- **Fase de Defensa (Minijuegos):** Un giro al estilo *bullet-hell* donde debes superar retos de habilidad:
  - **Dodge:** Esquiva proyectiles clásicos.
  - **Maze:** Escapa de laberintos en tiempo real.
  - **Shield:** Protege tu núcleo con escudos rotatorios.
  - **Shooter:** Contraataca eliminando amenazas.

### 3. Selección de Personajes
Elige tu avatar al inicio de la partida entre tres estudiantes con estilos únicos: **Antonio**, **Baku** o **Migue**.

### 4. Desafío Final
Enfréntate a **Sergio**, el jefe final, en un combate épico de dos fases que pondrá a prueba tus reflejos e incluso romperá la cuarta pared para confundirte.

---

## 🛠️ Tecnologías Utilizadas
- **Lenguaje:** Java 21.
- **Gráficos:** Java Swing & AWT (Renderizado 2D nativo).
- **Gestión de Recursos:** Sistema centralizado para audio (BGM/SFX) y sprites pixel art.
- **Arquitectura:** Patrón Modelo-Vista-Controlador (MVC).
- **Herramientas:** Maven para la gestión de dependencias y construcción.

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- Java JDK 21 o superior.
- Maven instalado en el sistema.

### Instalación y Ejecución
1. Clona el repositorio:
   ```bash
   git clone https://github.com/LastraMigue/Videogame-iLERNTALE.git
   ```
2. Navega al directorio del proyecto:
   ```bash
   cd Videogame-iLERNTALE/ilerntale
   ```
3. Compila el proyecto:
   ```bash
   mvn clean install
   ```
4. Ejecuta el juego:
   ```bash
   mvn exec:java -Dexec.mainClass="equipoilerntale.Main"
   ```

---

## 📂 Estructura del Proyecto
- [`GDD.md`](GDD.md): Documento de Diseño de Juego (Mecánicas, Lore, Enemigos).
- [`DESIGN.md`](DESIGN.md): Especificaciones técnicas y diagramas de clases (UML).
- `src/main/java/equipoilerntale/model`: Lógica del juego, entidades y estados.
- `src/main/java/equipoilerntale/view`: Interfaz gráfica y renderizado.
- `src/main/java/equipoilerntale/controller`: Controladores de exploración y combate.

---

## 🧠 Reflexión sobre el Modelado (RA5.d)
En el desarrollo de **iLERNTALE**, el modelado previo ha sido fundamental para el éxito del proyecto. La transición entre la **Ingeniería Directa** (diseño inicial) y la **Ingeniería Inversa** (análisis del código final) nos ha permitido valorar los siguientes puntos:

1.  **Reducción de la Complejidad**: El uso del patrón **MVC** permitió desacoplar la lógica de los minijuegos de la interfaz visual, evitando que el código se volviera inmanejable. Adecuar el código a este patrón de diseño agilizó el proceso de refactorización, simplificándolo y haciéndolo más directo al eliminar redundancias. Asimismo, incorporar durante el desarrollo el patrón Singleton también favoreció la eficiencia del código, al permitir crear una sola instancia de una clase que permite un punto de acceso global a ella. Esto fue especialmente conveniente en el tratamiento de las clases destinadas a recursos (sonidos e imágenes), para así gestionarlos de manera más eficaz.
2.  **Detección de Brechas**: Al contrastar el diseño original con el código real en `DESIGN.md`, observamos cómo surgieron necesidades técnicas imprevistas (como el `EnemySystem`) que fueron integradas armónicamente gracias a que la base arquitectónica era sólida.
3.  **Valor Académico y Profesional**: Documentar mediante diagramas de Casos de Uso, Secuencia y Estados no es solo un requisito; es una hoja de ruta que minimiza errores de lógica y facilita el trabajo en equipo.

La importancia del modelado radica en su capacidad para servir como "puente" entre la idea creativa y la implementación técnica, garantizando que el producto final sea robusto y mantenible.

---

## 📋 Gestión del Proyecto (GitHub Projects)

El desarrollo de **iLERNTALE** se ha organizado en cuatro fases clave, utilizando **GitHub Projects** para la gestión de tareas (issues) y la coordinación del equipo. El reparto de responsabilidades se detalla a continuación:

### 🛠️ Fases del Proyecto y Reparto de Tareas

#### **Fase 1: Cimentación y Estructura Base**
- **Objetivo:** Configuración del entorno Maven, diseño inicial (GDD/DESIGN) y esqueleto del Menú Principal.
- **Tareas Clave:**
  - Creación del proyecto Maven e infraestructura básica. (#1, #2, #3) - **Miguel A. / Baku**
  - Implementación del Menú Principal y gestión de recursos iniciales. (#4, #5, #6) - **Antonio A.**

#### **Fase 2: Mundo y Navegación (Overworld)**
- **Objetivo:** Implementación del movimiento del jugador, transiciones entre salas y sistema de colisiones.
- **Tareas Clave:**
  - Sistema de movimiento y transiciones de pantalla. (#9, #10, #11, #16) - **Baku**
  - Implementación de diálogos e interacciones básicas. (#8) - **Baku / Antonio A.**
  - Creación del sistema de enemigos y lógica de patrullaje. (#17) - **Baku**

#### **Fase 3: Núcleo de Combate**
- **Objetivo:** Desarrollo del motor de combate por turnos y los minijuegos de defensa.
- **Tareas Clave:**
  - Lógica central del sistema de combate y acciones. (#12) - **Miguel A.**
  - Diseño y decoración de la interfaz de combate (CombatPanel). (#13) - **Miguel A. / Antonio A.**
  - Gestión de estados de victoria/derrota y cierre de flujos de combate. (#14, #15) - **Miguel A.**

#### **Fase 4: Pulido, Mecánicas Avanzadas y Balanceo**
- **Objetivo:** Implementación de dificultad dinámica, efectos visuales y refinamiento de la experiencia de usuario.
- **Tareas Clave:**
  - Implementación de dificultad dinámica y efectos de vibración. (#30) - **Baku**
  - Refinamiento de botones, menús de diálogo y saltos de escena. (#30) - **Antonio A.**
  - Corrección de errores críticos y optimización de minijuegos. - **Miguel A.**

---

## 👥 Créditos
Desarrollado con ❤️ por el equipo (Miguel A. Lastra, Antonio Alejandro Duarte y Bakunin Sosa) de **1º DAW de iLERNA**.

---

© 2026 iLERNTALE Project. Todos los derechos reservados.
