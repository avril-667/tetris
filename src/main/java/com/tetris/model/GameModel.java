package com.tetris.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo central del juego (MVC - Model).
 * Encapsula las reglas del juego, puntuación, generación de piezas (7-Bag),
 * detección de colisiones, niveles y estado del juego.
 */
public class GameModel {
    private final Board board;
    private final List<GameListener> listeners;
    private final List<TetrominoType> pieceBag;

    private Tetromino currentPiece;
    private Tetromino nextPiece;

    private int score;
    private int level;
    private int linesCleared;
    private GameState gameState;

    public GameModel() {
        this.board = new Board();
        this.listeners = new ArrayList<>();
        this.pieceBag = new ArrayList<>();
        this.gameState = GameState.READY;
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
    }

    public void addListener(GameListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Inicia una nueva partida desde cero.
     */
    public void startNewGame() {
        board.clear();
        pieceBag.clear();
        score = 0;
        level = 1;
        linesCleared = 0;

        currentPiece = createNextTetromino();
        nextPiece = createNextTetromino();

        setGameState(GameState.RUNNING);
        notifyScoreChanged();
        notifyBoardUpdated();
    }

    /**
     * Alterna entre PAUSADO y EN EJECUCIÓN.
     */
    public void togglePause() {
        if (gameState == GameState.RUNNING) {
            setGameState(GameState.PAUSED);
        } else if (gameState == GameState.PAUSED) {
            setGameState(GameState.RUNNING);
        }
    }

    /**
     * Avance regular por gravedad (invocado por el bucle de juego del Controlador).
     */
    public boolean stepDown() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return false;
        }

        Tetromino test = currentPiece.copy();
        test.move(1, 0);

        if (board.isValidPosition(test)) {
            currentPiece.move(1, 0);
            notifyBoardUpdated();
            return true;
        } else {
            lockAndAdvance();
            return false;
        }
    }

    /**
     * Desplaza la pieza a la izquierda.
     */
    public boolean moveLeft() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return false;
        }

        Tetromino test = currentPiece.copy();
        test.move(0, -1);

        if (board.isValidPosition(test)) {
            currentPiece.move(0, -1);
            notifyBoardUpdated();
            return true;
        }
        return false;
    }

    /**
     * Desplaza la pieza a la derecha.
     */
    public boolean moveRight() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return false;
        }

        Tetromino test = currentPiece.copy();
        test.move(0, 1);

        if (board.isValidPosition(test)) {
            currentPiece.move(0, 1);
            notifyBoardUpdated();
            return true;
        }
        return false;
    }

    /**
     * Caída rápida (Soft Drop). Suma 1 punto si avanza.
     */
    public boolean softDrop() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return false;
        }

        Tetromino test = currentPiece.copy();
        test.move(1, 0);

        if (board.isValidPosition(test)) {
            currentPiece.move(1, 0);
            score += 1;
            notifyScoreChanged();
            notifyBoardUpdated();
            return true;
        } else {
            lockAndAdvance();
            return false;
        }
    }

    /**
     * Caída instantánea (Hard Drop).
     * Deja caer la pieza al fondo inmediatamente, sumando 2 puntos por fila descendida.
     */
    public void hardDrop() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return;
        }

        int dropDistance = 0;
        Tetromino test = currentPiece.copy();

        while (true) {
            test.move(1, 0);
            if (board.isValidPosition(test)) {
                dropDistance++;
            } else {
                break;
            }
        }

        currentPiece.move(dropDistance, 0);
        score += dropDistance * 2;
        lockAndAdvance();
    }

    /**
     * Rotación de pieza con Wall-Kicks básicos para evitar bloqueos en bordes y esquinas.
     */
    public boolean rotate() {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return false;
        }

        Tetromino test = currentPiece.copy();
        test.rotateClockwise();

        // Pruebas de desplazamiento (Wall Kicks) si la rotación básica colisiona
        int[][] kickOffsets = {
            {0, 0},   // Posición estándar
            {0, -1},  // Desplazar izquierda
            {0, 1},   // Desplazar derecha
            {0, -2},  // Desplazar 2 izquierda (para la I)
            {0, 2},   // Desplazar 2 derecha (para la I)
            {-1, 0}   // Desplazar hacia arriba (piso)
        };

        for (int[] offset : kickOffsets) {
            Tetromino kickTest = test.copy();
            kickTest.move(offset[0], offset[1]);
            if (board.isValidPosition(kickTest)) {
                currentPiece.rotateClockwise();
                currentPiece.move(offset[0], offset[1]);
                notifyBoardUpdated();
                return true;
            }
        }

        return false;
    }

    /**
     * Fija la pieza activa, elimina líneas completas, actualiza puntuación y genera la siguiente.
     */
    private void lockAndAdvance() {
        boolean placedOk = board.lockPiece(currentPiece);
        if (!placedOk) {
            setGameState(GameState.GAME_OVER);
            return;
        }

        int cleared = board.clearLines();
        if (cleared > 0) {
            linesCleared += cleared;
            updateScoreForLines(cleared);
            updateLevel();
            notifyLinesCleared(cleared);
        }

        // Spawn de la siguiente pieza
        currentPiece = nextPiece;
        nextPiece = createNextTetromino();

        // Comprobación de Game Over si la nueva pieza colisiona al nacer
        if (!board.isValidPosition(currentPiece)) {
            setGameState(GameState.GAME_OVER);
        } else {
            notifyBoardUpdated();
        }
    }

    /**
     * Calcula la posición proyectada (Ghost Piece) de la pieza activa.
     */
    public Tetromino calculateGhostPiece() {
        if (currentPiece == null) {
            return null;
        }
        Tetromino ghost = currentPiece.copy();
        while (true) {
            Tetromino next = ghost.copy();
            next.move(1, 0);
            if (board.isValidPosition(next)) {
                ghost = next;
            } else {
                break;
            }
        }
        return ghost;
    }

    /**
     * Generador 7-Bag clásico de Tetris (Garantiza una distribución justa y sin sequías de piezas).
     */
    private Tetromino createNextTetromino() {
        if (pieceBag.isEmpty()) {
            Collections.addAll(pieceBag, TetrominoType.values());
            Collections.shuffle(pieceBag);
        }
        TetrominoType nextType = pieceBag.remove(pieceBag.size() - 1);
        return new Tetromino(nextType);
    }

    private void updateScoreForLines(int count) {
        int basePoints;
        switch (count) {
            case 1:
                basePoints = 100;
                break;
            case 2:
                basePoints = 300;
                break;
            case 3:
                basePoints = 500;
                break;
            case 4:
                basePoints = 800; // ¡Tetris!
                break;
            default:
                basePoints = count * 200;
                break;
        }
        this.score += basePoints * level;
        notifyScoreChanged();
    }

    private void updateLevel() {
        int calculatedLevel = (linesCleared / 10) + 1;
        if (calculatedLevel != this.level) {
            this.level = calculatedLevel;
            notifyScoreChanged();
        }
    }

    /**
     * Calcula la velocidad de caída (intervalo en milisegundos) según el nivel actual.
     * Cuanto mayor el nivel, menor el intervalo de tiempo (mayor velocidad).
     */
    public long getDropIntervalMillis() {
        // Nivel 1: 750ms, Nivel 10: ~180ms, mínimo 90ms
        long interval = (long) (750 * Math.pow(0.86, level - 1));
        return Math.max(90, interval);
    }

    private void setGameState(GameState newState) {
        this.gameState = newState;
        for (GameListener listener : listeners) {
            listener.onGameStateChanged(newState);
        }
    }

    private void notifyScoreChanged() {
        for (GameListener listener : listeners) {
            listener.onScoreChanged(score, level, linesCleared);
        }
    }

    private void notifyBoardUpdated() {
        for (GameListener listener : listeners) {
            listener.onBoardUpdated();
        }
    }

    private void notifyLinesCleared(int count) {
        for (GameListener listener : listeners) {
            listener.onLinesCleared(count);
        }
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getNextPiece() {
        return nextPiece;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public GameState getGameState() {
        return gameState;
    }
}
