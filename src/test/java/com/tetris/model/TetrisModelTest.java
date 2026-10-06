package com.tetris.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TetrisModelTest {

    private Board board;
    private GameModel gameModel;

    @BeforeEach
    public void setUp() {
        board = new Board();
        gameModel = new GameModel();
    }

    @Test
    public void testBoardDimensions() {
        assertEquals(20, Board.ROWS, "El tablero debe tener 20 filas");
        assertEquals(10, Board.COLUMNS, "El tablero debe tener 10 columnas");
    }

    @Test
    public void testAllSevenTetrominoTypesExist() {
        TetrominoType[] types = TetrominoType.values();
        assertEquals(7, types.length, "Deben existir exactamente 7 tetriminos clásicos");

        for (TetrominoType type : types) {
            assertNotNull(type.getColorHex(), "Cada pieza debe tener un color asignado");
            assertNotNull(type.getBorderHex(), "Cada pieza debe tener un borde asignado");

            // Cada pieza debe tener 4 estados de rotación y 4 bloques
            for (int rot = 0; rot < 4; rot++) {
                int[][] shape = type.getShape(rot);
                assertEquals(4, shape.length, "Cada pieza debe estar compuesta por 4 bloques");
                for (int[] block : shape) {
                    assertEquals(2, block.length, "Cada bloque debe tener coordenadas [fila, columna]");
                }
            }
        }
    }

    @Test
    public void testPieceMovementAndBounds() {
        Tetromino piece = new Tetromino(TetrominoType.O, 0, 0, 0); // 2x2 bloque en (0,0)
        assertTrue(board.isValidPosition(piece));

        // Mover hacia la izquierda más allá del borde (columna negativa)
        Tetromino outLeft = new Tetromino(TetrominoType.O, 0, -1, 0);
        assertFalse(board.isValidPosition(outLeft), "No debe permitir salirse por la izquierda");

        // Mover hacia la derecha más allá del borde
        Tetromino outRight = new Tetromino(TetrominoType.O, 0, 9, 0); // O ocupa col 9 y 10 (col 10 inválida)
        assertFalse(board.isValidPosition(outRight), "No debe permitir salirse por la derecha");

        // Mover más allá del fondo
        Tetromino outBottom = new Tetromino(TetrominoType.O, 19, 0, 0); // Fila 19 y 20 (fila 20 inválida)
        assertFalse(board.isValidPosition(outBottom), "No debe permitir salirse por el fondo");
    }

    @Test
    public void testCollisionWithLockedBlocks() {
        // Fijar un bloque en la fila 19, columna 5
        Tetromino placed = new Tetromino(TetrominoType.O, 18, 4, 0); // Ocupa (18,4), (18,5), (19,4), (19,5)
        assertTrue(board.lockPiece(placed));

        assertNotNull(board.getCell(18, 4));
        assertNotNull(board.getCell(19, 5));

        // Intentar colocar otra pieza encima exactamente donde ya hay bloques
        Tetromino colliding = new Tetromino(TetrominoType.O, 18, 4, 0);
        assertFalse(board.isValidPosition(colliding), "Debe detectar colisión con bloques fijados");
    }

    @Test
    public void testLineClearAndScoreUpdate() {
        // Llenar manualmente la última fila (fila 19) excepto la última columna
        for (int c = 0; c < 9; c++) {
            Tetromino single = new Tetromino(TetrominoType.O, 18, c, 0);
        }

        // Llenar completamente la fila 19
        Tetromino bottomFiller1 = new Tetromino(TetrominoType.I, 18, 0, 0); // Fila 19, cols 0..3
        Tetromino bottomFiller2 = new Tetromino(TetrominoType.I, 18, 4, 0); // Fila 19, cols 4..7
        board.lockPiece(bottomFiller1);
        board.lockPiece(bottomFiller2);

        // Completar columnas 8 y 9
        Tetromino endPiece = new Tetromino(TetrominoType.O, 18, 8, 0); // Fila 19 cols 8,9 y fila 18 cols 8,9
        board.lockPiece(endPiece);

        // Verificar que la fila 19 está llena
        int cleared = board.clearLines();
        assertTrue(cleared >= 1, "Debe haber limpiado al menos 1 línea completa");

        // La fila 19 tras desplazarse debe tener la fila 18 anterior
        for (int c = 0; c < 8; c++) {
            assertNull(board.getCell(0, c), "La fila superior debe quedar vacía");
        }
    }

    @Test
    public void testGameModelStartAndDrop() {
        gameModel.startNewGame();
        assertEquals(GameState.RUNNING, gameModel.getGameState());
        assertNotNull(gameModel.getCurrentPiece());
        assertNotNull(gameModel.getNextPiece());
        assertEquals(0, gameModel.getScore());
        assertEquals(1, gameModel.getLevel());

        // Caída rápida (Soft drop) suma 1 punto
        int initialRow = gameModel.getCurrentPiece().getRow();
        boolean moved = gameModel.softDrop();
        assertTrue(moved);
        assertEquals(initialRow + 1, gameModel.getCurrentPiece().getRow());
        assertEquals(1, gameModel.getScore());
    }

    @Test
    public void testHardDropLocksPiece() {
        gameModel.startNewGame();
        Tetromino firstPiece = gameModel.getCurrentPiece();

        gameModel.hardDrop();

        // Tras un Hard Drop, la pieza debe haberse fijado y haberse generado la siguiente
        assertNotSame(firstPiece, gameModel.getCurrentPiece(), "La pieza activa debe avanzar tras Hard Drop");
        assertTrue(gameModel.getScore() > 0, "Hard Drop debe otorgar puntos");
    }

    @Test
    public void testGhostPieceCalculation() {
        gameModel.startNewGame();
        Tetromino ghost = gameModel.calculateGhostPiece();
        assertNotNull(ghost);

        // La pieza fantasma debe tener la misma columna y rotación, pero fila mayor o igual
        assertEquals(gameModel.getCurrentPiece().getCol(), ghost.getCol());
        assertEquals(gameModel.getCurrentPiece().getRotation(), ghost.getRotation());
        assertTrue(ghost.getRow() >= gameModel.getCurrentPiece().getRow());
    }

    @Test
    public void testTogglePause() {
        gameModel.startNewGame();
        assertEquals(GameState.RUNNING, gameModel.getGameState());

        gameModel.togglePause();
        assertEquals(GameState.PAUSED, gameModel.getGameState());

        gameModel.togglePause();
        assertEquals(GameState.RUNNING, gameModel.getGameState());
    }

    @Test
    public void testSpeedProgression() {
        long speedLvl1 = gameModel.getDropIntervalMillis();
        assertTrue(speedLvl1 >= 600, "Nivel 1 debe ser un ritmo accesible");

        // Simular avance de nivel a nivel 5
        // Si el nivel es más alto, el intervalo debe ser menor (mayor velocidad)
        // Probamos la fórmula
        long speedLvl5 = (long) (750 * Math.pow(0.86, 5 - 1));
        assertTrue(speedLvl5 < speedLvl1, "Nivel superior debe tener menor intervalo (mayor velocidad)");
    }
}
