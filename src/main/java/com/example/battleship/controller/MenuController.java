package com.example.battleship.controller;

import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MenuController {

    @FXML
    void onHandleNewGame(ActionEvent event) throws IOException {
        SceneManager.changeScene(Path.startGameView);
    }
}
