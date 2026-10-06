package com.tetris.model;

/**
 * Representa la matriz del tablero de Tetris (10 columnas x 20 filas).
 * Gestiona el almacenamiento de los bloques fijados, la detección de colisiones
 * y la eliminación automática de líneas completas.
 */
public class Board {
    public static final int ROWS = 20;
    public static final int COLUMNS = 10;

    // Almacena el código de color hexadecimal del bloque fijado en cada casilla, o null si está vacía
    private final String[][] grid;

    public Board() {
        this.grid = new String[ROWS][COLUMNS];
    }

    /**
     * Limpia completamente el tablero.
     */
    public void clear() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                grid[r][c] = null;
            }
        }
    }

    /**
     * Verifica si una pieza en su posición y rotación actuales es válida dentro del tablero.
     * Retorna false si colisiona con los bordes o con bloques ya fijados.
     */
    public boolean isValidPosition(Tetromino piece) {
        int[][] blocks = piece.getAbsoluteBlocks();
        for (int[] block : blocks) {
            int r = block[0];
            int c = block[1];

            // Comprobación de límites del tablero
            if (c < 0 || c >= COLUMNS || r >= ROWS) {
                return false;
            }

            // Permitimos filas negativas (por encima del tablero) durante spawn,
            // pero si está dentro del tablero visible, comprobamos colisión con bloques fijados
            if (r >= 0 && grid[r][c] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fija la pieza activa en la matriz del tablero.
     * Retorna false si algún bloque quedó por encima de la fila 0 (señal de desbordamiento/Game Over).
     */
    public boolean lockPiece(Tetromino piece) {
        int[][] blocks = piece.getAbsoluteBlocks();
        boolean validPlacement = true;
        for (int[] block : blocks) {
            int r = block[0];
            int c = block[1];
            if (r >= 0 && r < ROWS && c >= 0 && c < COLUMNS) {
                grid[r][c] = piece.getType().getColorHex();
            } else {
                validPlacement = false;
            }
        }
        return validPlacement;
    }

    /**
     * Detecta y elimina todas las filas completas.
     * Desplaza las filas superiores hacia abajo.
     * @return El número de líneas eliminadas en esta llamada.
     */
    public int clearLines() {
        int linesCleared = 0;

        for (int r = ROWS - 1; r >= 0; r--) {
            if (isLineFull(r)) {
                linesCleared++;
                removeLine(r);
                // Como las filas cayeron una posición, volvemos a evaluar la misma fila r
                r++;
            }
        }

        return linesCleared;
    }

    private boolean isLineFull(int row) {
        for (int c = 0; c < COLUMNS; c++) {
            if (grid[row][c] == null) {
                return false;
            }
        }
        return true;
    }

    private void removeLine(int targetRow) {
        // Desplazar todas las filas superiores hacia abajo
        for (int r = targetRow; r > 0; r--) {
            System.arraycopy(grid[r - 1], 0, grid[r], 0, COLUMNS);
        }
        // Vaciar la fila superior (fila 0)
        for (int c = 0; c < COLUMNS; c++) {
            grid[0][c] = null;
        }
    }

    public String getCell(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLUMNS) {
            return grid[row][col];
        }
        return null;
    }

    public String[][] getGridCopy() {
        String[][] copy = new String[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(grid[r], 0, copy[r], 0, COLUMNS);
        }
        return copy;
    }
}
