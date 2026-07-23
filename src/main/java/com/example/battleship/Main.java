package com.example.battleship;

import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        new SceneManager(primaryStage);

        // Cambiamos menuView por loginView para iniciar directamente en el Login
        SceneManager.changeScene(Path.loginView);
        primaryStage.setTitle("BattleShip");
    }
}