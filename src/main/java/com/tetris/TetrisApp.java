package com.tetris;

import com.tetris.controller.TetrisController;
import com.tetris.model.GameModel;
import com.tetris.view.TetrisView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Punto de entrada principal de la aplicación JavaFX.
 * Ensambla los componentes del patrón MVC (Modelo, Vista y Controlador)
 * y configura la ventana del juego.
 */
public class TetrisApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Instanciación del Modelo (MVC - Model)
        GameModel model = new GameModel();

        // 2. Instanciación de la Vista (MVC - View)
        TetrisView view = new TetrisView();

        // 3. Instanciación del Controlador (MVC - Controller)
        TetrisController controller = new TetrisController(model, view);

        // 4. Configuración de la Escena
        Scene scene = new Scene(view.getRoot(), 600, 680);

        // Cargar hoja de estilos CSS
        URL cssResource = getClass().getResource("/com/tetris/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        // Configuración de controles de teclado en la escena
        controller.setupKeyHandling(scene);

        // 5. Configuración de la Ventana Principal
        primaryStage.setTitle("Tetris Clásico - JavaFX MVC");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Detener bucles y recursos al cerrar la ventana
        primaryStage.setOnCloseRequest(event -> controller.stopGame());

        primaryStage.show();

        // Iniciar la partida
        controller.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
