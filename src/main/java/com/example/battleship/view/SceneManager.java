package com.example.battleship.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage stage;

    public SceneManager(Stage primaryStage){
        stage =  primaryStage;
    }

    public static void changeScene(String fxmlFileName) throws IOException {
        if (stage == null) {
            throw new IllegalStateException("SceneManager no inicializado.");
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(
                com.example.battleship.view.SceneManager.class.getResource(fxmlFileName)
        ));
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
