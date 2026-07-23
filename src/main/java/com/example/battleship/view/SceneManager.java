package com.example.battleship.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static Stage stage;

    public SceneManager(Stage primaryStage) {
        stage = primaryStage;
    }

    public static FXMLLoader changeScene(String fxmlFileName) throws IOException {
        if (stage == null) {
            throw new IllegalStateException("SceneManager no inicializado.");
        }

        // 1. Intentar cargar directamente con la ruta proporcionada
        URL resource = SceneManager.class.getResource(fxmlFileName);

        // 2. Si no lo encuentra y la ruta no empieza con '/', intentar desde la raíz del Classpath
        if (resource == null && !fxmlFileName.startsWith("/")) {
            resource = SceneManager.class.getResource("/" + fxmlFileName);
        }

        // 3. Si aún no lo encuentra, buscar usando el ClassLoader
        if (resource == null) {
            String cleanPath = fxmlFileName.startsWith("/") ? fxmlFileName.substring(1) : fxmlFileName;
            resource = SceneManager.class.getClassLoader().getResource(cleanPath);
        }

        // 4. Si después de todos los intentos sigue siendo null, lanzar una excepción informativa
        if (resource == null) {
            throw new IOException("No se pudo encontrar el archivo FXML: '" + fxmlFileName + "'. " +
                    "Verifica que el archivo exista en 'src/main/resources' y que el nombre sea correcto.");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        return loader;
    }
}