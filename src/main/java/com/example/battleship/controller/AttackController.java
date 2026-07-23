package com.example.battleship.controller;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.SeaMap;
import com.example.battleship.model.Ship;
import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.threads.AutoSaveThread;
import com.example.battleship.model.threads.IAThread;
import com.example.battleship.model.threads.TurnThread;
import com.example.battleship.view.fx.CombatEffects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class AttackController {

    @FXML private GridPane enemyGrid;
    @FXML private GridPane playerGrid;

    private final Button[][] enemyButtons = new Button[10][10];
    private final Button[][] playerButtons = new Button[10][10];

    private GameModel gameModel;

    // Hilos de ejecución
    private IAThread iaThread;
    private TurnThread turnThread;
    private AutoSaveThread autoSaveThread;

    @FXML
    public void initialize() {
        createEnemyGrid();
        createPlayerGrid();
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;

        drawPlayerShips();
        initializeThreads();
        updateBoardInteractivity();
    }

    // ==========================================
    // Inicialización y Gestión de Hilos
    // ==========================================

    private void initializeThreads() {
        // Hilo de Auto-guardado
        autoSaveThread = new AutoSaveThread(gameModel);
        autoSaveThread.start();

        // Hilo de control de Turnos
        turnThread = new TurnThread(gameModel);
        turnThread.setOnTurnChange(this::updateBoardInteractivity);
        turnThread.start();

        // Hilo de Inteligencia Artificial
        iaThread = new IAThread(gameModel);
        iaThread.setOnUIAttackUpdate(this::onMachineAttackComplete);
        iaThread.start();
    }

    private void stopThreads() {
        if (iaThread != null) iaThread.stopThread();
        if (turnThread != null) turnThread.stopThread();
        if (autoSaveThread != null) autoSaveThread.stopThread();
    }

    private void updateBoardInteractivity() {
        // Bloquea el tablero enemigo si no es tu turno o el juego terminó
        boolean isInteractive = gameModel.isHumanTurn() && !gameModel.isGameFinished();
        enemyGrid.setDisable(!isInteractive);
    }

    // ==========================================
    // Creación visual de los tableros
    // ==========================================

    private void createEnemyGrid() {
        for (int row = 0; row < SeaMap.ROWS; row++) {
            for (int column = 0; column < SeaMap.COLUMNS; column++) {
                Button button = new Button();
                int currentRow = row;
                int currentColumn = column;

                button.setOnAction(e -> onEnemyCellPressed(currentRow, currentColumn));
                button.setPrefSize(39, 39);
                CombatEffects.applyBaseStyle(button);

                enemyButtons[row][column] = button;
                enemyGrid.add(button, column, row);
            }
        }
    }

    private void createPlayerGrid() {
        for (int row = 0; row < SeaMap.ROWS; row++) {
            for (int column = 0; column < SeaMap.COLUMNS; column++) {
                Button button = new Button();
                // El tablero del jugador es solo de visualización (sin setOnAction)
                button.setPrefSize(39, 39);
                button.setStyle("-fx-background-color: rgba(30, 144, 255, 0.3); -fx-border-color: #33b5e5; -fx-border-width: 0.5;");

                playerButtons[row][column] = button;
                playerGrid.add(button, column, row);
            }
        }
    }

    private void drawPlayerShips() {
        // Recuperamos la lista de barcos del jugador humano y los pintamos
        List<Ship> humanShips = gameModel.getHumanPlayer().getSeaMap().getShips();
        for (Ship ship : humanShips) {
            for (Coordinate coord : ship.getPositions()) {
                Button btn = playerButtons[coord.getRow()][coord.getColumn()];
                // Color para identificar visualmente dónde están tus barcos
                btn.setStyle("-fx-background-color: #7f8fa6; -fx-border-color: #2f3640; -fx-border-width: 1;");
            }
        }
    }

    // ==========================================
    // Lógica de Ataques (Humano y Máquina)
    // ==========================================

    private void onEnemyCellPressed(int row, int column) {
        if (gameModel.isGameFinished() || !gameModel.isHumanTurn()) return;

        Coordinate coordinate = new Coordinate(row, column);
        AttackResult result = gameModel.humanAttack(coordinate);

        updateCellVisuals(enemyButtons[row][column], result);

        if (result == AttackResult.SUNK) {
            paintSunkShip(gameModel.getMachineShipAt(coordinate), enemyButtons);
        }

        autoSaveThread.requestSave(); // Solicitamos un guardado en segundo plano
        checkIfGameEnded();
    }

    private void onMachineAttackComplete(Coordinate coordinate, AttackResult result) {
        int r = coordinate.getRow();
        int c = coordinate.getColumn();

        updateCellVisuals(playerButtons[r][c], result);

        // Si la máquina nos hundió un barco, lo identificamos y lo pintamos (asumiendo que tienes un getter similar para el humano, si no, bastará con el updateCellVisuals)
        // Nota: Si necesitas el efecto completo de "SUNK" para el jugador, se implementa igual que el del enemigo.

        autoSaveThread.requestSave();
        checkIfGameEnded();
    }

    private void updateCellVisuals(Button button, AttackResult result) {
        switch (result) {
            case MISS:
                button.setText("O");
                button.setStyle("-fx-background-color: #353b48; -fx-text-fill: white;");
                CombatEffects.playMiss(button);
                break;
            case HIT:
            case SUNK: // Los impactos visuales iniciales son los mismos
                button.setStyle("-fx-background-color: #e84118;");
                CombatEffects.playHit(button);
                break;
            case ALREADY_ATTACKED:
                return;
        }
        button.setDisable(true);
    }

    private void paintSunkShip(Ship ship, Button[][] gridButtons) {
        if (ship == null) return;
        List<Button> shipCells = new ArrayList<>();
        for (Coordinate coordinate : ship.getPositions()) {
            Button button = gridButtons[coordinate.getRow()][coordinate.getColumn()];
            button.setStyle("-fx-background-color: #c23616;"); // Rojo más oscuro
            shipCells.add(button);
        }
        CombatEffects.playSunk(shipCells);
    }

    // ==========================================
    // Fin de Partida
    // ==========================================

    private void checkIfGameEnded() {
        if (gameModel.hasWinner()) {
            stopThreads(); // ¡Crucial detener los hilos antes de salir de la escena!
            gameModel.finishGame();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("¡Fin de la Partida!");
            alert.setHeaderText(null);
            alert.setContentText("El ganador es: " + gameModel.checkWinner().getNickname() + "\n\nLa partida guardada ha sido eliminada.");
            alert.showAndWait();

            try {
                com.example.battleship.view.SceneManager.changeScene(com.example.battleship.view.Path.menuView);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}