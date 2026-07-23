package com.example.battleship.controller;

import com.example.battleship.model.Session;
import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField nicknameField;

    @FXML
    void onHandleEnter(ActionEvent event) throws IOException {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor ingresa un apodo válido.");
            return;
        }

        // Guardamos el nickname en la sesión actual
        Session.setCurrentNickname(nickname);

        // Avanzamos al Menú principal
        SceneManager.changeScene(Path.menuView);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}