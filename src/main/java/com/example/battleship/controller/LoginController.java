package com.example.battleship.controller;

import com.example.battleship.model.GameModel;
import com.example.battleship.model.GameStatus;
import com.example.battleship.model.PersistenceManager;
import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField nicknameField;

    /**
     * Triggered when the user wants to start a completely new game.
     */
    @FXML
    void onHandleNewGame(ActionEvent event) throws IOException {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid nickname.");
            return;
        }

        if (PersistenceManager.saveExists(nickname)) {
            boolean overwrite = confirmOverwrite();
            if (!overwrite) {
                return; // User cancelled
            }
            PersistenceManager.deleteSave(nickname);
        }

        GameModel newGameModel = new GameModel();
        newGameModel.newGame(nickname);

        // Save initial state to claim the save slot immediately
        newGameModel.saveGame();

        navigateToStartGame(newGameModel);
    }

    /**
     * Triggered when the user wants to continue a previously saved game.
     */
    @FXML
    void onHandleContinue(ActionEvent event) throws IOException {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid nickname.");
            return;
        }

        if (!PersistenceManager.saveExists(nickname)) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "No saved game found for this nickname.");
            return;
        }

        GameStatus restoredStatus = PersistenceManager.loadGame(nickname);
        if (restoredStatus == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the saved game.");
            return;
        }

        GameModel restoredGameModel = new GameModel();
        restoredGameModel.setGameStatus(restoredStatus);

        if (restoredStatus.isGameStarted()) {
            // If the game was already started, jump directly to the Attack View
            navigateToAttackView(restoredGameModel);
        } else {
            // Otherwise, they were still placing ships
            navigateToStartGame(restoredGameModel);
        }
    }

    /**
     * Shows a confirmation alert for overwriting an existing save file.
     *
     * @return true if the user clicks YES, false if NO.
     */
    private boolean confirmOverwrite() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Exists");
        alert.setHeaderText("There is already a saved game.");
        alert.setContentText("If you continue, the previous saved game will be deleted permanently.\n\nDo you want to continue?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToStartGame(GameModel model) throws IOException {
        // Obtenemos el loader para acceder al controlador de destino
        javafx.fxml.FXMLLoader loader = SceneManager.changeScene(Path.startGameView);
        StartGameController controller = loader.getController();
        // Inyectamos el modelo que acabamos de crear/cargar
        controller.setGameModel(model);
    }

    private void navigateToAttackView(GameModel model) throws IOException {
        javafx.fxml.FXMLLoader loader = SceneManager.changeScene(Path.attackView);
        AttackController controller = loader.getController();
        // Inyectamos el modelo restaurado
        controller.setGameModel(model);
    }}