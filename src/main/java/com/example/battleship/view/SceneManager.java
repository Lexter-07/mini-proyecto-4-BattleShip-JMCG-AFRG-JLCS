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


    public static FXMLLoader changeScene(String fxmlFileName) throws IOException {
        if (stage == null) {
            throw new IllegalStateException("SceneManager no inicializado.");
        }

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        SceneManager.class.getResource(fxmlFileName)
                )
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        return loader;
    }
}
