package com.tetris.view;

import com.tetris.model.Board;
import com.tetris.model.GameState;
import com.tetris.model.Tetromino;
import com.tetris.model.TetrominoType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Vista de Tetris (MVC - View).
 * Construye la interfaz gráfica con JavaFX y se encarga del renderizado
 * del tablero principal (Canvas), la pieza previa, y los paneles de estadísticas y controles.
 */
public class TetrisView {
    public static final int CELL_SIZE = 30; // Tamaño en píxeles de cada bloque en el tablero
    public static final int PREVIEW_CELL_SIZE = 24;

    private final BorderPane rootPane;
    private final Canvas boardCanvas;
    private final Canvas previewCanvas;

    private final Label scoreValueLabel;
    private final Label levelValueLabel;
    private final Label linesValueLabel;

    private final Button pauseButton;
    private final Button restartButton;

    public TetrisView() {
        rootPane = new BorderPane();
        rootPane.getStyleClass().add("main-container");

        // --- Tablero de Juego (Canvas) ---
        boardCanvas = new Canvas(Board.COLUMNS * CELL_SIZE, Board.ROWS * CELL_SIZE);
        StackPane boardWrapper = new StackPane(boardCanvas);
        boardWrapper.getStyleClass().add("board-container");
        boardWrapper.setFocusTraversable(false);

        // --- Panel Lateral (Estadísticas, Vista Previa, Controles) ---
        VBox sidePanel = new VBox();
        sidePanel.getStyleClass().add("side-panel");
        sidePanel.setAlignment(Pos.TOP_CENTER);

        // Título estilizado
        Label titleLabel = new Label("TETRIS");
        titleLabel.getStyleClass().add("title-label");

        // Tarjeta de Vista Previa (Next Piece)
        VBox previewCard = new VBox();
        previewCard.getStyleClass().add("card");
        Label previewTitle = new Label("SIGUIENTE PIEZA");
        previewTitle.getStyleClass().add("card-title");
        previewCanvas = new Canvas(4 * PREVIEW_CELL_SIZE + 10, 4 * PREVIEW_CELL_SIZE + 10);
        StackPane previewWrapper = new StackPane(previewCanvas);
        previewWrapper.getStyleClass().add("preview-container");
        previewCard.getChildren().addAll(previewTitle, previewWrapper);

        // Tarjeta de Estadísticas (Score, Nivel, Líneas)
        VBox statsCard = new VBox();
        statsCard.getStyleClass().add("card");

        Label scoreTitle = new Label("PUNTUACIÓN");
        scoreTitle.getStyleClass().add("card-title");
        scoreValueLabel = new Label("0");
        scoreValueLabel.getStyleClass().add("stat-value");

        Label levelTitle = new Label("NIVEL");
        levelTitle.getStyleClass().add("card-title");
        levelValueLabel = new Label("1");
        levelValueLabel.getStyleClass().add("stat-value");

        Label linesTitle = new Label("LÍNEAS");
        linesTitle.getStyleClass().add("card-title");
        linesValueLabel = new Label("0");
        linesValueLabel.getStyleClass().add("stat-value");

        statsCard.getChildren().addAll(
            scoreTitle, scoreValueLabel,
            levelTitle, levelValueLabel,
            linesTitle, linesValueLabel
        );

        // Tarjeta de Botones
        VBox buttonsCard = new VBox();
        buttonsCard.getStyleClass().add("card");
        buttonsCard.setSpacing(10);

        pauseButton = new Button("⏸ Pausar");
        pauseButton.getStyleClass().addAll("action-btn", "btn-pause");
        pauseButton.setMaxWidth(Double.MAX_VALUE);
        pauseButton.setFocusTraversable(false);

        restartButton = new Button("🔄 Reiniciar");
        restartButton.getStyleClass().addAll("action-btn", "btn-restart");
        restartButton.setMaxWidth(Double.MAX_VALUE);
        restartButton.setFocusTraversable(false);

        buttonsCard.getChildren().addAll(pauseButton, restartButton);

        // Tarjeta de Ayuda de Controles
        VBox controlsCard = new VBox();
        controlsCard.getStyleClass().add("card");
        Label controlsTitle = new Label("CONTROLES");
        controlsTitle.getStyleClass().add("card-title");

        Label controlsGuide = new Label(
            "◄ / ► : Mover lateral\n" +
            "▼ : Caída rápida (Soft)\n" +
            "▲ / W : Rotar pieza\n" +
            "Espacio : Caída instantánea\n" +
            "P : Pausar / Reanudar\n" +
            "R : Reiniciar partida"
        );
        controlsGuide.getStyleClass().add("controls-hint");
        controlsCard.getChildren().addAll(controlsTitle, controlsGuide);

        sidePanel.getChildren().addAll(titleLabel, previewCard, statsCard, buttonsCard, controlsCard);

        // Armado del contenedor principal
        HBox centerLayout = new HBox(20);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().addAll(boardWrapper, sidePanel);

        rootPane.setCenter(centerLayout);
    }

    public Parent getRoot() {
        return rootPane;
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public void updateStats(int score, int level, int lines) {
        scoreValueLabel.setText(String.format("%,d", score));
        levelValueLabel.setText(String.valueOf(level));
        linesValueLabel.setText(String.valueOf(lines));
    }

    public void updatePauseButton(boolean isPaused) {
        if (isPaused) {
            pauseButton.setText("▶ Reanudar");
        } else {
            pauseButton.setText("⏸ Pausar");
        }
    }

    /**
     * Renderizado completo del estado del juego: tablero, piezas y overlay.
     */
    public void render(Board board, Tetromino currentPiece, Tetromino ghostPiece,
                       Tetromino nextPiece, GameState state, int score) {
        renderBoard(board, currentPiece, ghostPiece, state, score);
        renderPreview(nextPiece);
    }

    /**
     * Dibuja el tablero principal, los bloques fijos, la pieza activa y la proyección fantasma.
     */
    private void renderBoard(Board board, Tetromino currentPiece, Tetromino ghostPiece,
                             GameState state, int score) {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        double w = boardCanvas.getWidth();
        double h = boardCanvas.getHeight();

        // Fondo del tablero
        gc.setFill(Color.web("#0e1424"));
        gc.fillRect(0, 0, w, h);

        // Cuadrícula sutil
        gc.setStroke(Color.web("#18223c"));
        gc.setLineWidth(1.0);
        for (int c = 0; c <= Board.COLUMNS; c++) {
            gc.strokeLine(c * CELL_SIZE, 0, c * CELL_SIZE, h);
        }
        for (int r = 0; r <= Board.ROWS; r++) {
            gc.strokeLine(0, r * CELL_SIZE, w, r * CELL_SIZE);
        }

        // 1. Dibujar bloques fijados en el tablero
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLUMNS; c++) {
                String colorHex = board.getCell(r, c);
                if (colorHex != null) {
                    drawBlock(gc, c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, Color.web(colorHex), false);
                }
            }
        }

        // 2. Dibujar pieza fantasma (Ghost Piece) si el juego está en marcha
        if (state == GameState.RUNNING && ghostPiece != null && currentPiece != null) {
            Color ghostColor = Color.web(ghostPiece.getType().getColorHex());
            for (int[] block : ghostPiece.getAbsoluteBlocks()) {
                int r = block[0];
                int c = block[1];
                if (r >= 0 && r < Board.ROWS && c >= 0 && c < Board.COLUMNS) {
                    drawGhostBlock(gc, c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, ghostColor);
                }
            }
        }

        // 3. Dibujar pieza activa en movimiento
        if (currentPiece != null) {
            Color pieceColor = Color.web(currentPiece.getType().getColorHex());
            for (int[] block : currentPiece.getAbsoluteBlocks()) {
                int r = block[0];
                int c = block[1];
                if (r >= 0 && r < Board.ROWS && c >= 0 && c < Board.COLUMNS) {
                    drawBlock(gc, c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, pieceColor, false);
                }
            }
        }

        // 4. Overlays informativos (PAUSA y GAME OVER)
        if (state == GameState.PAUSED) {
            drawPauseOverlay(gc, w, h);
        } else if (state == GameState.GAME_OVER) {
            drawGameOverOverlay(gc, w, h, score);
        }
    }

    /**
     * Dibuja la siguiente pieza en el recuadro de previsualización.
     */
    private void renderPreview(Tetromino nextPiece) {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        double w = previewCanvas.getWidth();
        double h = previewCanvas.getHeight();

        gc.setFill(Color.web("#0e1424"));
        gc.fillRect(0, 0, w, h);

        if (nextPiece == null) {
            return;
        }

        TetrominoType type = nextPiece.getType();
        int[][] shape = type.getShape(0);
        Color color = Color.web(type.getColorHex());

        // Centrado de la pieza en el canvas de vista previa
        int minR = Integer.MAX_VALUE, maxR = Integer.MIN_VALUE;
        int minC = Integer.MAX_VALUE, maxC = Integer.MIN_VALUE;
        for (int[] b : shape) {
            minR = Math.min(minR, b[0]);
            maxR = Math.max(maxR, b[0]);
            minC = Math.min(minC, b[1]);
            maxC = Math.max(maxC, b[1]);
        }

        double pieceW = (maxC - minC + 1) * PREVIEW_CELL_SIZE;
        double pieceH = (maxR - minR + 1) * PREVIEW_CELL_SIZE;
        double startX = (w - pieceW) / 2.0 - minC * PREVIEW_CELL_SIZE;
        double startY = (h - pieceH) / 2.0 - minR * PREVIEW_CELL_SIZE;

        for (int[] b : shape) {
            double x = startX + b[1] * PREVIEW_CELL_SIZE;
            double y = startY + b[0] * PREVIEW_CELL_SIZE;
            drawBlock(gc, x, y, PREVIEW_CELL_SIZE, color, true);
        }
    }

    /**
     * Dibuja un bloque con efecto biselado / arcade moderno (luz superior-izquierda, sombra inferior-derecha).
     */
    private void drawBlock(GraphicsContext gc, double x, double y, double size, Color color, boolean isPreview) {
        double inset = 1.0;
        double blockSize = size - inset * 2;

        // Base de color
        gc.setFill(color);
        gc.fillRect(x + inset, y + inset, blockSize, blockSize);

        // Bisel de brillo superior e izquierdo
        gc.setFill(Color.color(1, 1, 1, 0.35));
        gc.beginPath();
        gc.moveTo(x + inset, y + inset);
        gc.lineTo(x + inset + blockSize, y + inset);
        gc.lineTo(x + inset + blockSize - 3, y + inset + 3);
        gc.lineTo(x + inset + 3, y + inset + 3);
        gc.lineTo(x + inset + 3, y + inset + blockSize - 3);
        gc.lineTo(x + inset, y + inset + blockSize);
        gc.closePath();
        gc.fill();

        // Bisel de sombra inferior y derecho
        gc.setFill(Color.color(0, 0, 0, 0.35));
        gc.beginPath();
        gc.moveTo(x + inset + blockSize, y + inset);
        gc.lineTo(x + inset + blockSize, y + inset + blockSize);
        gc.lineTo(x + inset, y + inset + blockSize);
        gc.lineTo(x + inset + 3, y + inset + blockSize - 3);
        gc.lineTo(x + inset + blockSize - 3, y + inset + blockSize - 3);
        gc.lineTo(x + inset + blockSize - 3, y + inset + 3);
        gc.closePath();
        gc.fill();

        // Borde oscuro exterior fino
        gc.setStroke(Color.color(0, 0, 0, 0.4));
        gc.setLineWidth(1.0);
        gc.strokeRect(x + inset, y + inset, blockSize, blockSize);
    }

    /**
     * Dibuja la pieza fantasma proyectada (Ghost Piece) con borde semitransparente.
     */
    private void drawGhostBlock(GraphicsContext gc, double x, double y, double size, Color color) {
        double inset = 2.0;
        double blockSize = size - inset * 2;

        // Relleno suave translúcido
        gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.18));
        gc.fillRect(x + inset, y + inset, blockSize, blockSize);

        // Contorno punteado/brillante
        gc.setStroke(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.85));
        gc.setLineWidth(1.5);
        gc.strokeRect(x + inset, y + inset, blockSize, blockSize);
    }

    private void drawPauseOverlay(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.color(0.04, 0.06, 0.1, 0.75));
        gc.fillRect(0, 0, w, h);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        gc.setFill(Color.web("#38bdf8"));
        gc.fillText("JUEGO PAUSADO", w / 2, h / 2 - 15);

        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        gc.setFill(Color.web("#94a3b8"));
        gc.fillText("Presiona [P] o Reanudar", w / 2, h / 2 + 20);
    }

    private void drawGameOverOverlay(GraphicsContext gc, double w, double h, int score) {
        gc.setFill(Color.color(0.12, 0.02, 0.04, 0.88));
        gc.fillRect(0, 0, w, h);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 32));
        gc.setFill(Color.web("#ff1744"));
        gc.fillText("GAME OVER", w / 2, h / 2 - 35);

        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#ffffff"));
        gc.fillText("Puntuación Final: " + String.format("%,d", score), w / 2, h / 2 + 5);

        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        gc.setFill(Color.web("#cbd5e1"));
        gc.fillText("Presiona [R] o Reiniciar para jugar", w / 2, h / 2 + 40);
    }
}
