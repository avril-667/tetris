package com.tetris.model;

/**
 * Interfaz de observador para el patrón MVC.
 * Permite a la Vista y al Controlador reaccionar ante cambios en el modelo de Tetris.
 */
public interface GameListener {
    void onGameStateChanged(GameState newState);
    void onScoreChanged(int newScore, int newLevel, int linesCleared);
    void onBoardUpdated();
    void onLinesCleared(int count);
}
