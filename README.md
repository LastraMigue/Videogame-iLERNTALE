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

## 👥 Créditos
Desarrollado con ❤️ por el equipo (Miguel A. Lastra, Antonio Alejandro Duarte y Bakunin Sosa) de **1º DAW de iLERNA**.

---

© 2026 iLERNTALE Project. Todos los derechos reservados.