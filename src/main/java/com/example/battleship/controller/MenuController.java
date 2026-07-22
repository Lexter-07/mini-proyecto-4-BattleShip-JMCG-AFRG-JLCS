package com.example.battleship.controller;

import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MenuController {

    @FXML
    void onHandleNewGame(ActionEvent event) throws IOException {
        // Redirects to Login instead of jumping straight into StartGame
        SceneManager.changeScene(Path.loginView);
    }

    @FXML
    public void onHandleContinue(ActionEvent actionEvent) throws IOException {
        // Redirects to Login where the user can input their nickname to load
        SceneManager.changeScene(Path.loginView);
    }

    @FXML
    public void onHandleExit(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}