package com.tetris.model;

/**
 * Representa los 7 tipos de piezas clásicas (Tetriminos) de Tetris
 * con sus respectivas formas en 4 estados de rotación y su paleta de colores oficial.
 */
public enum TetrominoType {
    // I - Cyan (Matriz 4x4)
    I(new int[][][] {
        { {1, 0}, {1, 1}, {1, 2}, {1, 3} }, // Horizontal
        { {0, 2}, {1, 2}, {2, 2}, {3, 2} }, // Vertical
        { {2, 0}, {2, 1}, {2, 2}, {2, 3} }, // Horizontal invertido
        { {0, 1}, {1, 1}, {2, 1}, {3, 1} }  // Vertical invertido
    }, "#00E5FF", "#00B0FF", 4),

    // J - Azul (Matriz 3x3)
    J(new int[][][] {
        { {0, 0}, {1, 0}, {1, 1}, {1, 2} },
        { {0, 1}, {0, 2}, {1, 1}, {2, 1} },
        { {1, 0}, {1, 1}, {1, 2}, {2, 2} },
        { {0, 1}, {1, 1}, {2, 0}, {2, 1} }
    }, "#2979FF", "#1565C0", 3),

    // L - Naranja (Matriz 3x3)
    L(new int[][][] {
        { {0, 2}, {1, 0}, {1, 1}, {1, 2} },
        { {0, 1}, {1, 1}, {2, 1}, {2, 2} },
        { {1, 0}, {1, 1}, {1, 2}, {2, 0} },
        { {0, 0}, {0, 1}, {1, 1}, {2, 1} }
    }, "#FF9100", "#EF6C00", 3),

    // O - Amarillo (Matriz 2x2)
    O(new int[][][] {
        { {0, 0}, {0, 1}, {1, 0}, {1, 1} },
        { {0, 0}, {0, 1}, {1, 0}, {1, 1} },
        { {0, 0}, {0, 1}, {1, 0}, {1, 1} },
        { {0, 0}, {0, 1}, {1, 0}, {1, 1} }
    }, "#FFD600", "#FBC02D", 2),

    // S - Verde (Matriz 3x3)
    S(new int[][][] {
        { {0, 1}, {0, 2}, {1, 0}, {1, 1} },
        { {0, 1}, {1, 1}, {1, 2}, {2, 2} },
        { {1, 1}, {1, 2}, {2, 0}, {2, 1} },
        { {0, 0}, {1, 0}, {1, 1}, {2, 1} }
    }, "#00E676", "#00C853", 3),

    // T - Púrpura (Matriz 3x3)
    T(new int[][][] {
        { {0, 1}, {1, 0}, {1, 1}, {1, 2} },
        { {0, 1}, {1, 1}, {1, 2}, {2, 1} },
        { {1, 0}, {1, 1}, {1, 2}, {2, 1} },
        { {0, 1}, {1, 0}, {1, 1}, {2, 1} }
    }, "#D500F9", "#AA00FF", 3),

    // Z - Rojo (Matriz 3x3)
    Z(new int[][][] {
        { {0, 0}, {0, 1}, {1, 1}, {1, 2} },
        { {0, 2}, {1, 1}, {1, 2}, {2, 1} },
        { {1, 0}, {1, 1}, {2, 1}, {2, 2} },
        { {0, 1}, {1, 0}, {1, 1}, {2, 0} }
    }, "#FF1744", "#D50000", 3);

    private final int[][][] shapes; // [rotación][bloque][0=fila, 1=columna]
    private final String colorHex;
    private final String borderHex;
    private final int dimension;

    TetrominoType(int[][][] shapes, String colorHex, String borderHex, int dimension) {
        this.shapes = shapes;
        this.colorHex = colorHex;
        this.borderHex = borderHex;
        this.dimension = dimension;
    }

    public int[][] getShape(int rotation) {
        int index = Math.floorMod(rotation, 4);
        return shapes[index];
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getBorderHex() {
        return borderHex;
    }

    public int getDimension() {
        return dimension;
    }
}
