# 🕹️ Tetris Clásico en Java y JavaFX (Patrón MVC)

Una implementación completa, funcional y moderna del clásico juego **Tetris**, desarrollada en **Java 17+** y **JavaFX 21** aplicando estrictamente el patrón de diseño arquitectónico **Modelo-Vista-Controlador (MVC)**, sin dependencias externas de motores de juegos.

---

## 🏛️ Arquitectura de Software (Patrón MVC)

El proyecto está diseñado bajo una estricta separación de responsabilidades:

```
src/main/java/com/tetris/
├── TetrisApp.java           # Punto de entrada de la aplicación JavaFX
├── Launcher.java            # Lanzador para empaquetado y ejecución segura
│
├── model/                   # [MODELO] Lógica de negocio y reglas del juego
│   ├── Board.java           # Matriz 10x20, colisiones y limpieza de líneas
│   ├── TetrominoType.java   # Definición de las 7 piezas, matrices de rotación y colores
│   ├── Tetromino.java       # Pieza activa, posición absoluta, traslación y rotación
│   ├── GameState.java       # Estados del juego (READY, RUNNING, PAUSED, GAME_OVER)
│   ├── GameModel.java       # Reglas, puntuación, niveles, velocidad y generador 7-Bag
│   └── GameListener.java    # Interfaz observador para notificar a la vista/controlador
│
├── view/                    # [VISTA] Interfaz gráfica de usuario en JavaFX
│   └── TetrisView.java      # Canvas para tablero, pieza fantasma, vista previa, UI y estilos
│
└── controller/              # [CONTROLADOR] Manejo de eventos y bucle de juego
    └── TetrisController.java # Manejador de teclado, botones y bucle temporal (AnimationTimer)
```

### 1. Modelo (`com.tetris.model`)
- **`Board`**: Representa la matriz clásica de 10 columnas por 20 filas. Almacena las celdas ocupadas, verifica colisiones de bordes y piezas previas, y realiza la eliminación automática y desplazamiento de filas completas.
- **`TetrominoType`**: Enumerador con las 7 piezas clásicas ($I, J, L, O, S, T, Z$), sus 4 estados de rotación (sistema SRS estándar) y sus códigos de color oficiales.
- **`Tetromino`**: Instancia viva de una pieza con coordenadas de fila/columna y rotación actual, con soporte para clonación para pruebas de colisión y proyección fantasma.
- **`GameModel`**: Orquesta el flujo:
  - Generador **7-Bag Randomizer** (distribución equitativa y sin sequías prolongadas).
  - Rotaciones con **Wall Kicks** (permite rotar pegado a los bordes sin bloquearse).
  - Cálculo de la **Ghost Piece** (proyección traslúcida que indica dónde caerá la pieza).
  - Sistema de puntuación y aumento dinámico de velocidad según el nivel.
- **`GameListener`**: Patrón observador para desacoplar el modelo de la vista.

### 2. Vista (`com.tetris.view`)
- **`TetrisView`**: Construida con JavaFX puro y renderizado mediante `Canvas` de alta precisión:
  - Tablero central de 10x20 con bloques en relieve tridimensional tipo arcade moderno.
  - Proyección de **Pieza Fantasma (Ghost Piece)** semitransparente.
  - Panel lateral con:
    - Tarjeta de **Siguiente Pieza** (vista previa centrada).
    - Tarjeta de **Puntuación**, **Nivel** y **Líneas despejadas**.
    - Botones de acción: **Pausar / Reanudar** y **Reiniciar**.
    - Panel de recordatorio de controles.
  - Overlays visuales elegantes para estados de **Pausa** y **Game Over**.
- **`styles.css`**: Hoja de estilos con estética moderna, tipografía nítida y modo oscuro (*Dark Modern Theme*).

### 3. Controlador (`com.tetris.controller`)
- **`TetrisController`**:
  - Captura los eventos del teclado (`KeyPressed`) y clicks en los botones.
  - Administra el bucle de actualización en tiempo real (*Game Loop*) mediante `AnimationTimer` de JavaFX.
  - Sincroniza dinámicamente el intervalo de caída por gravedad según el nivel del modelo.

---

## 🎮 Controles del Juego

| Tecla | Acción | Descripción |
| :--- | :--- | :--- |
| ◀ **Flecha Izquierda** | Mover izquierda | Desplaza la pieza lateralmente a la izquierda |
| ▶ **Flecha Derecha** | Mover derecha | Desplaza la pieza lateralmente a la derecha |
| ▼ **Flecha Abajo** | **Soft Drop** | Caída acelerada (otorga +1 punto por celda) |
| ▲ **Flecha Arriba** / **W** | **Rotar** | Rota la pieza en sentido horario (con wall kicks) |
| ␣ **Barra Espaciadora** | **Hard Drop** | Caída instantánea al fondo (otorga +2 pts por celda) |
| **P** | **Pausar / Reanudar** | Alterna el estado de pausa |
| **R** | **Reiniciar** | Comienza una nueva partida |

---

## 🧩 Piezas Clásicas (Tetriminos) y Colores

| Pieza | Nombre | Color Característico | Dimensiones |
| :---: | :---: | :---: | :---: |
| **I** | Tetrimino I | Cian (`#00E5FF`) | 4 × 4 |
| **J** | Tetrimino J | Azul (`#2979FF`) | 3 × 3 |
| **L** | Tetrimino L | Naranja (`#FF9100`) | 3 × 3 |
| **O** | Tetrimino O | Amarillo (`#FFD600`) | 2 × 2 |
| **S** | Tetrimino S | Verde (`#00E676`) | 3 × 3 |
| **T** | Tetrimino T | Púrpura / Magenta (`#D500F9`) | 3 × 3 |
| **Z** | Tetrimino Z | Rojo (`#FF1744`) | 3 × 3 |

---

## 📈 Sistema de Puntuación y Progresión de Velocidad

### Puntuación:
- **1 línea despejada**: $100 \times \text{Nivel}$
- **2 líneas despejadas**: $300 \times \text{Nivel}$
- **3 líneas despejadas**: $500 \times \text{Nivel}$
- **4 líneas despejadas (¡Tetris!)**: $800 \times \text{Nivel}$
- **Soft Drop**: $+1$ punto por cada casilla descendida manualmente
- **Hard Drop**: $+2$ puntos por cada casilla descendida instantáneamente

### Progresión de Nivel y Dificultad:
- Cada **10 líneas eliminadas**, el jugador avanza de nivel.
- La velocidad de caída se incrementa progresivamente en cada nivel mediante la fórmula:
  $$\text{Intervalo (ms)} = \max\left(90,\; 750 \times 0.86^{(\text{nivel} - 1)}\right)$$

---

## 🚀 Requisitos y Ejecución

### Requisitos:
- **Java 17 o superior** (Java 17, 21 o 25).
- **Apache Maven 3.6+**.
- **JavaFX 21+** (gestionado automáticamente vía dependencias de Maven).

### Cómo Ejecutar:

#### Opción 1: Con los scripts incluidos (Windows)
Haz doble clic o ejecuta en la terminal:
```powershell
.\run.ps1
```
o
```cmd
run.bat
```

#### Opción 2: Mediante Maven directamente
```bash
mvn javafx:run
```

#### Opción 3: Compilar y ejecutar pruebas unitarias
```bash
mvn test
```
*(Incluye 10 pruebas unitarias automatizadas con JUnit 5 que verifican colisiones, rotaciones, eliminación de líneas, sistema 7-bag y puntuación).*

---

## 📦 Estructura del Proyecto

```
practica-cuartaunidad/
├── pom.xml                     # Configuración de compilación Maven y JavaFX
├── run.bat                     # Script de ejecución para Windows CMD
├── run.ps1                     # Script de ejecución para PowerShell
├── README.md                   # Documentación completa del proyecto
├── .gitignore                  # Exclusiones de control de versiones
├── src/
│   ├── main/
│   │   ├── java/com/tetris/
│   │   │   ├── TetrisApp.java
│   │   │   ├── Launcher.java
│   │   │   ├── controller/TetrisController.java
│   │   │   ├── model/
│   │   │   │   ├── Board.java
│   │   │   │   ├── GameListener.java
│   │   │   │   ├── GameModel.java
│   │   │   │   ├── GameState.java
│   │   │   │   ├── Tetromino.java
│   │   │   │   └── TetrominoType.java
│   │   │   └── view/TetrisView.java
│   │   └── resources/com/tetris/
│   │       └── styles.css
│   └── test/
│       └── java/com/tetris/model/
│           └── TetrisModelTest.java
```
