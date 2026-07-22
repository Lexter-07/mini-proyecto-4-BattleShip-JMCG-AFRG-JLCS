package com.example.battleship.controller;

import com.example.battleship.model.*;
import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.threads.AutoSaveThread;
import com.example.battleship.model.threads.IAThread;
import com.example.battleship.model.threads.TurnThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class AttackController {

    @FXML private GridPane enemyGrid;
    @FXML private GridPane playerGrid;

    private final Button[][] enemyButtons = new Button[SeaMap.ROWS][SeaMap.COLUMNS];
    private final Button[][] playerButtons = new Button[SeaMap.ROWS][SeaMap.COLUMNS];

    private GameModel gameModel;
    private IAThread iaThread;
    private AutoSaveThread autoSaveThread;
    private TurnThread turnThread;

    @FXML
    public void initialize() {
        createGrids();
    }

    /**
     * Injects the GameModel and initializes threads and UI states.
     */
    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;

        drawPlayerShips();
        restorePreviousShots();

        // Initialize and start background threads
        autoSaveThread = new AutoSaveThread(gameModel);
        autoSaveThread.setDaemon(true);
        autoSaveThread.start();

        iaThread = new IAThread(gameModel);
        iaThread.setDaemon(true);
        iaThread.setOnUIAttackUpdate(this::handleMachineAttackUpdate);
        iaThread.start();

        turnThread = new TurnThread(gameModel);
        turnThread.setDaemon(true);
        turnThread.setOnTurnChange(this::handleTurnChange);
        turnThread.start();

        // Start game if not already started (useful for new games)
        if (!gameModel.isGameStarted()) {
            gameModel.startGame();
        }
    }

    private void createGrids() {
        for (int row = 0; row < SeaMap.ROWS; row++) {
            for (int column = 0; column < SeaMap.COLUMNS; column++) {

                // Enemy Button setup
                Button enemyBtn = new Button();
                int currentRow = row;
                int currentColumn = column;
                enemyBtn.setOnAction(e -> onEnemyCellPressed(currentRow, currentColumn));
                enemyBtn.setPrefSize(39, 39);
                enemyBtn.setOpacity(0.60);
                enemyBtn.setStyle("-fx-background-radius: 0; -fx-background-color: #d2dae2; -fx-border-color: #718093;");
                enemyButtons[row][column] = enemyBtn;
                enemyGrid.add(enemyBtn, column, row);

                // Player Button setup (View only)
                Button playerBtn = new Button();
                playerBtn.setPrefSize(39, 39);
                playerBtn.setOpacity(0.80);
                playerBtn.setDisable(true); // User cannot click their own grid
                playerBtn.setStyle("-fx-background-radius: 0; -fx-background-color: #0abde3; -fx-border-color: #0984e3;");
                playerButtons[row][column] = playerBtn;
                playerGrid.add(playerBtn, column, row);
            }
        }
    }

    /**
     * Renders the human player's fleet on their grid.
     */
    private void drawPlayerShips() {
        SeaMap humanMap = gameModel.getHumanPlayer().getSeaMap();
        for (Ship ship : humanMap.getShips()) {
            for (Coordinate coord : ship.getPositions()) {
                Button btn = playerButtons[coord.getRow()][coord.getColumn()];
                btn.setStyle("-fx-background-radius: 0; -fx-background-color: #2f3640; -fx-border-color: #718093;");
            }
        }
    }

    /**
     * Restores visual state if a game was loaded from disk.
     */
    private void restorePreviousShots() {
        SeaMap humanMap = gameModel.getHumanPlayer().getSeaMap();
        SeaMap machineMap = gameModel.getMachinePlayer().getSeaMap();

        for (Shot shot : machineMap.getShots()) {
            updateEnemyCell(shot.getCoordinate().getRow(), shot.getCoordinate().getColumn(), shot.getResult());
            if (shot.getResult() == AttackResult.SUNK) {
                paintSunkShip(gameModel.getMachineShipAt(shot.getCoordinate()), enemyButtons);
            }
        }

        for (Shot shot : humanMap.getShots()) {
            updatePlayerCell(shot.getCoordinate().getRow(), shot.getCoordinate().getColumn(), shot.getResult());
            if (shot.getResult() == AttackResult.SUNK) {
                paintSunkShip(humanMap.getShipAt(shot.getCoordinate()), playerButtons);
            }
        }
    }

    /**
     * Handles human attack action.
     */
    private void onEnemyCellPressed(int row, int column) {
        if (!gameModel.isHumanTurn() || gameModel.isGameFinished()) return;

        Coordinate coordinate = new Coordinate(row, column);
        AttackResult result = gameModel.humanAttack(coordinate);

        if (result == AttackResult.ALREADY_ATTACKED) return;

        updateEnemyCell(row, column, result);

        if (result == AttackResult.SUNK) {
            Ship ship = gameModel.getMachineShipAt(coordinate);
            paintSunkShip(ship, enemyButtons);
        }

        // Trigger AutoSave immediately after the human action
        if (autoSaveThread != null) autoSaveThread.requestSave();

        checkGameEnd();
    }

    /**
     * Callback triggered by TurnThread whenever turn state flips.
     */
    private void handleTurnChange() {
        // Interacción visual si se desea habilitar/deshabilitar la grilla según de quién sea el turno
        boolean isHuman = gameModel.isHumanTurn();
        enemyGrid.setDisable(!isHuman);
    }

    /**
     * Callback triggered by IAThread to update the UI after a machine attack.
     */
    private void handleMachineAttackUpdate() {
        // Find the last shot fired by the machine to update the specific cell
        SeaMap humanMap = gameModel.getHumanPlayer().getSeaMap();
        if (humanMap.getShots().isEmpty()) return;

        Shot lastShot = humanMap.getShots().getLast();
        Coordinate coord = lastShot.getCoordinate();
        AttackResult result = lastShot.getResult();

        updatePlayerCell(coord.getRow(), coord.getColumn(), result);

        if (result == AttackResult.SUNK) {
            Ship ship = humanMap.getShipAt(coord);
            paintSunkShip(ship, playerButtons);
        }

        // Trigger AutoSave immediately after the IA action
        if (autoSaveThread != null) autoSaveThread.requestSave();

        checkGameEnd();
    }

    private void paintSunkShip(Ship ship, Button[][] gridButtons) {
        if (ship == null) return;
        for (Coordinate coordinate : ship.getPositions()) {
            Button button = gridButtons[coordinate.getRow()][coordinate.getColumn()];
            button.setStyle("-fx-background-color: #2d3436; -fx-background-radius: 0; -fx-border-color: #636e72;");
            button.setText("S");
        }
    }

    private void updateEnemyCell(int row, int column, AttackResult result) {
        Button button = enemyButtons[row][column];
        applyCellVisuals(button, result);
    }

    private void updatePlayerCell(int row, int column, AttackResult result) {
        Button button = playerButtons[row][column];
        applyCellVisuals(button, result);
    }

    private void applyCellVisuals(Button button, AttackResult result) {
        switch (result) {
            case MISS:
                button.setText("O");
                button.setStyle("-fx-background-color: #0ae4fc; -fx-background-radius: 0; -fx-text-fill: white;");
                break;
            case HIT:
                button.setText("X");
                button.setStyle("-fx-background-color: #e84118; -fx-background-radius: 0; -fx-text-fill: white;");
                break;
            case SUNK:
            case ALREADY_ATTACKED:
                break;
        }
        button.setDisable(true);
    }

    private void checkGameEnd() {
        if (gameModel.hasWinner()) {
            gameModel.finishGame();
            Player winner = gameModel.checkWinner();

            // Final save to record the finished state
            if (autoSaveThread != null) autoSaveThread.requestSave();

            stopThreads();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("The war has ended!");
            alert.setContentText("Winner: " + winner.getNickname());
            alert.showAndWait();

            Platform.exit();
        }
    }

    /**
     * Ensures background threads are safely terminated.
     */
    public void stopThreads() {
        if (turnThread != null) turnThread.stopThread();
        if (iaThread != null) iaThread.stopThread();
        if (autoSaveThread != null) autoSaveThread.stopThread();
    }
}