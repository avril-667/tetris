package com.tetris.controller;

import com.tetris.model.GameListener;
import com.tetris.model.GameModel;
import com.tetris.model.GameState;
import com.tetris.view.TetrisView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controlador de Tetris (MVC - Controller).
 * Conecta el modelo y la vista, maneja los eventos de teclado y ratón,
 * y controla el bucle de actualización en tiempo real (Game Loop) mediante AnimationTimer.
 */
public class TetrisController implements GameListener {
    private final GameModel model;
    private final TetrisView view;

    private AnimationTimer gameTimer;
    private long lastTickTime = 0;

    public TetrisController(GameModel model, TetrisView view) {
        this.model = model;
        this.view = view;

        // Suscribir el controlador como listener del modelo (Patrón MVC / Observer)
        this.model.addListener(this);

        initEventHandlers();
        initGameLoop();
    }

    private void initEventHandlers() {
        // Enlazar botones de la vista
        view.getPauseButton().setOnAction(e -> model.togglePause());
        view.getRestartButton().setOnAction(e -> {
            model.startNewGame();
            lastTickTime = 0;
        });
    }

    /**
     * Vincula los controles de teclado a la escena JavaFX.
     */
    public void setupKeyHandling(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPressed);
    }

    /**
     * Manejador central de teclas según las especificaciones del juego:
     * - Flechas Izquierda/Derecha: Desplazamiento lateral
     * - Flecha Abajo: Soft Drop
     * - Flecha Arriba / Tecla 'W': Rotar pieza
     * - Barra Espaciadora: Hard Drop
     * - Tecla 'P': Pausar / Reanudar
     * - Tecla 'R': Reiniciar
     */
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        // Si el juego está en Game Over, permitir reiniciar con R o Espacio
        if (model.getGameState() == GameState.GAME_OVER) {
            if (code == KeyCode.R || code == KeyCode.SPACE) {
                model.startNewGame();
                lastTickTime = 0;
            }
            return;
        }

        switch (code) {
            case LEFT:
                model.moveLeft();
                break;
            case RIGHT:
                model.moveRight();
                break;
            case DOWN:
                model.softDrop();
                // Reiniciar el temporizador para dar ritmo consistente al jugador
                lastTickTime = System.nanoTime();
                break;
            case UP:
            case W:
                model.rotate();
                break;
            case SPACE:
                model.hardDrop();
                lastTickTime = System.nanoTime();
                break;
            case P:
                model.togglePause();
                break;
            case R:
                model.startNewGame();
                lastTickTime = 0;
                break;
            default:
                break;
        }
    }

    /**
     * Bucle de juego basado en AnimationTimer de JavaFX.
     * Consulta dinámicamente la velocidad según el nivel actual del modelo.
     */
    private void initGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (model.getGameState() != GameState.RUNNING) {
                    return;
                }

                long intervalNanos = model.getDropIntervalMillis() * 1_000_000L;
                if (lastTickTime == 0) {
                    lastTickTime = now;
                    return;
                }

                if (now - lastTickTime >= intervalNanos) {
                    model.stepDown();
                    lastTickTime = now;
                }
            }
        };
    }

    /**
     * Inicia una nueva partida y arranca el temporizador del bucle de juego.
     */
    public void startGame() {
        model.startNewGame();
        lastTickTime = 0;
        gameTimer.start();
        renderCurrentState();
    }

    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void renderCurrentState() {
        view.render(
            model.getBoard(),
            model.getCurrentPiece(),
            model.calculateGhostPiece(),
            model.getNextPiece(),
            model.getGameState(),
            model.getScore()
        );
    }

    // --- Implementación de GameListener (Reacciones a cambios del Modelo) ---

    @Override
    public void onGameStateChanged(GameState newState) {
        view.updatePauseButton(newState == GameState.PAUSED);
        renderCurrentState();
    }

    @Override
    public void onScoreChanged(int newScore, int newLevel, int linesCleared) {
        view.updateStats(newScore, newLevel, linesCleared);
    }

    @Override
    public void onBoardUpdated() {
        renderCurrentState();
    }

    @Override
    public void onLinesCleared(int count) {
        // En caso de querer añadir efectos sonoros o animaciones en el futuro
    }
}
