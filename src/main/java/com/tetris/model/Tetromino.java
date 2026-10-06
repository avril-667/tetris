package com.tetris.model;

/**
 * Representa una pieza activa en movimiento en el tablero.
 * Maneja la posición (fila, columna) y el estado de rotación de la pieza.
 */
public class Tetromino {
    private final TetrominoType type;
    private int row;
    private int col;
    private int rotation;

    public Tetromino(TetrominoType type) {
        this.type = type;
        this.rotation = 0;
        // Posicionamiento de spawn clásico centrado en un tablero de 10 columnas
        this.row = 0;
        this.col = (10 - type.getDimension()) / 2;
    }

    public Tetromino(TetrominoType type, int row, int col, int rotation) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.rotation = Math.floorMod(rotation, 4);
    }

    /**
     * Retorna una copia de esta pieza para pruebas y cálculos de proyección (Ghost piece).
     */
    public Tetromino copy() {
        return new Tetromino(type, row, col, rotation);
    }

    /**
     * Retorna las coordenadas absolutas en el tablero [4 bloques][0=fila, 1=columna]
     * según la rotación y posición actuales.
     */
    public int[][] getAbsoluteBlocks() {
        int[][] relative = type.getShape(rotation);
        int[][] absolute = new int[4][2];
        for (int i = 0; i < 4; i++) {
            absolute[i][0] = row + relative[i][0];
            absolute[i][1] = col + relative[i][1];
        }
        return absolute;
    }

    public void move(int deltaRow, int deltaCol) {
        this.row += deltaRow;
        this.col += deltaCol;
    }

    public void rotateClockwise() {
        this.rotation = (this.rotation + 1) % 4;
    }

    public void rotateCounterClockwise() {
        this.rotation = (this.rotation + 3) % 4;
    }

    public TetrominoType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = Math.floorMod(rotation, 4);
    }
}
