package com.example.battleship;

import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Entry point for the Battleship JavaFX application. <p>
 *
 * This class initializes the primary application window (stage) and sets up the initial
 * scene navigation by launching the login view using {@link SceneManager}.
 * </p>
 *
 * @author Jorge Luis Castro
 * @author Andres Felipe Rodriguez
 * @author Jose Manuel Cardona Gil
 * @version 1.2
 */
public class Main extends Application {

    /**
     * Main entry point for the application launch.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Initializes and starts the primary JavaFX stage. <p>
     *
     * Sets up the {@link SceneManager} context with the primary stage, navigates directly
     * to the login view, and sets the window title to "BattleShip".  </p>
     *
     * @param primaryStage the primary window stage for this application
     * @throws IOException if an error occurs while loading the initial layout or FXML resource
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        new SceneManager(primaryStage);

        // Cambiamos menuView por loginView para iniciar directamente en el Login
        SceneManager.changeScene(Path.loginView);
        primaryStage.setTitle("BattleShip");
    }
}