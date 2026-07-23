package com.example.battleship.controller;

import com.example.battleship.model.GameModel;
import com.example.battleship.model.GameStatus;
import com.example.battleship.model.PersistenceManager;
import com.example.battleship.model.Session;
import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

public class MenuController {

    @FXML
    void onHandleNewGame(ActionEvent event) throws IOException {
        String nickname = Session.getCurrentNickname();
        if (nickname == null || nickname.isEmpty()) {
            SceneManager.changeScene(Path.loginView);
            return;
        }

        if (PersistenceManager.saveExists(nickname)) {
            boolean overwrite = confirmOverwrite();
            if (!overwrite) return;
            PersistenceManager.deleteSave(nickname);
        }

        GameModel newGameModel = new GameModel();
        newGameModel.newGame(nickname);
        newGameModel.saveGame();

        navigateToStartGame(newGameModel);
    }


    @FXML
    public void onHandleContinue(ActionEvent actionEvent) throws IOException {
        String nickname = Session.getCurrentNickname();
        if (nickname == null || nickname.isEmpty()) {
            SceneManager.changeScene(Path.loginView);
            return;
        }

        if (!PersistenceManager.saveExists(nickname)) {
            showAlert(Alert.AlertType.INFORMATION, "Información", "No se encontró una partida guardada para: " + nickname);
            return;
        }

        GameStatus restoredStatus = PersistenceManager.loadGame(nickname);
        if (restoredStatus == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Fallo al cargar la partida guardada.");
            return;
        }

        // NUEVO: Bloquear el acceso a una partida que ya había concluido y borrarla
        if (restoredStatus.isGameFinished()) {
            PersistenceManager.deleteSave(nickname);
            showAlert(Alert.AlertType.INFORMATION, "Partida Finalizada", "La partida guardada ya había terminado. El archivo se ha limpiado, por favor inicia una partida nueva.");
            return;
        }

        GameModel restoredGameModel = new GameModel();
        restoredGameModel.setGameStatus(restoredStatus);

        if (restoredStatus.isGameStarted()) {
            navigateToAttackView(restoredGameModel);
        } else {
            navigateToStartGame(restoredGameModel);
        }
    }

    @FXML
    public void onHandleExit(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    private boolean confirmOverwrite() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Partida existente");
        alert.setHeaderText("Ya existe una partida guardada.");
        alert.setContentText("Si continúas, la partida anterior se sobrescribirá.\n\n¿Deseas continuar?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToStartGame(GameModel model) throws IOException {
        javafx.fxml.FXMLLoader loader = SceneManager.changeScene(Path.startGameView);
        StartGameController controller = loader.getController();
        controller.setGameModel(model);
    }

    private void navigateToAttackView(GameModel model) throws IOException {
        javafx.fxml.FXMLLoader loader = SceneManager.changeScene(Path.attackView);
        AttackController controller = loader.getController();
        controller.setGameModel(model);
    }
}