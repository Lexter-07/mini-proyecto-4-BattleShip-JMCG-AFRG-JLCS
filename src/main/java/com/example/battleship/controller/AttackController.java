package com.example.battleship.controller;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.SeaMap;
import com.example.battleship.model.Ship;
import com.example.battleship.model.Shot;
import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.exceptions.InvalidTurnException;
import com.example.battleship.model.threads.IAThread;
import com.example.battleship.model.threads.TurnThread;
import com.example.battleship.view.ShipView;
import com.example.battleship.view.fx.CombatEffects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttackController {

    @FXML private AnchorPane rootPane;

    @FXML private Pane playerFleetPane;
    @FXML private GridPane enemyGrid;
    @FXML private GridPane playerGrid;
    @FXML private Pane enemyFleetPane;

    private final Button[][] enemyButtons = new Button[10][10];
    private final Button[][] playerButtons = new Button[10][10];
    private final HashMap<Ship, ShipView> enemyShipViews = new HashMap<>();

    private GameModel gameModel;

    // ELIMINADO: AutoSaveThread
    private IAThread iaThread;
    private TurnThread turnThread;

    @FXML
    public void initialize() {
        createEnemyGrid();
        createPlayerGrid();
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;

        drawPlayerShips();
        createEnemyFleetViews();
        restoreBoardState();
        initializeThreads();
        updateBoardInteractivity();
    }

    // ==========================================
    // Sincronización de Partidas Guardadas
    // ==========================================
    private void restoreBoardState() {
        // Restaurar los disparos que el Humano le hizo a la Máquina
        for (Shot shot : gameModel.getMachinePlayer().getSeaMap().getShots()) {
            Coordinate coord = shot.getCoordinate();
            Button btn = enemyButtons[coord.getRow()][coord.getColumn()];
            updateCellVisuals(btn, shot.getResult());

            if (shot.getResult() == AttackResult.SUNK) {
                paintSunkShip(gameModel.getMachineShipAt(coord), enemyButtons);
            }
        }

        // Restaurar los disparos que la Máquina le hizo al Humano
        for (Shot shot : gameModel.getHumanPlayer().getSeaMap().getShots()) {
            Coordinate coord = shot.getCoordinate();
            Button btn = playerButtons[coord.getRow()][coord.getColumn()];
            updateCellVisuals(btn, shot.getResult());
        }
    }




    private void createEnemyFleetViews() {
        enemyFleetPane.getChildren().clear();
        enemyShipViews.clear();

        for (Ship ship : gameModel.getMachinePlayer().getSeaMap().getShips()) {

            StackPane pane = new StackPane();
            ShipView shipView = new ShipView(pane, ship);

            pane.setMouseTransparent(true);
            pane.setVisible(false);

            enemyFleetPane.getChildren().add(pane);
            enemyShipViews.put(ship, shipView);

            positionEnemyShip(shipView);
        }
    }




    private void positionEnemyShip(ShipView shipView) {

        Ship ship = shipView.getShip();

        Coordinate start = ship.getStartCoordinate();

        double cell = 39;

        double x = start.getColumn() * cell;
        double y = start.getRow() * cell;

        shipView.getView().setLayoutX(x);
        shipView.getView().setLayoutY(y);

        if (ship.getOrientation() == Orientation.VERTICAL) {
            shipView.rotate();
        }
    }



    @FXML
    void onShowEnemyFleet(MouseEvent event) {
        for (ShipView shipView : enemyShipViews.values()) {
            shipView.getView().setVisible(true);
        }
    }


    @FXML
    void onHideEnemyFleet(MouseEvent event) {
        for (ShipView shipView : enemyShipViews.values()) {
            shipView.getView().setVisible(false);
        }
    }



    // ==========================================
    // Inicialización y Gestión de Hilos
    // ==========================================
    private void initializeThreads() {
        turnThread = new TurnThread(gameModel);
        turnThread.setDaemon(true); // ¡CRUCIAL! Permite que el programa se cierre al darle a la X
        turnThread.setOnTurnChange(this::updateBoardInteractivity);
        turnThread.start();

        iaThread = new IAThread(gameModel);
        iaThread.setDaemon(true); // ¡CRUCIAL!
        iaThread.setOnUIAttackUpdate(this::onMachineAttackComplete);
        iaThread.start();
    }

    private void stopThreads() {
        if (iaThread != null) iaThread.stopThread();
        if (turnThread != null) turnThread.stopThread();
    }

    private void updateBoardInteractivity() {
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
                button.setPrefSize(39, 39);
                button.setStyle("-fx-background-color: rgba(30, 144, 255, 0.3); -fx-border-color: #33b5e5; -fx-border-width: 0.5;");

                playerButtons[row][column] = button;
                playerGrid.add(button, column, row);
            }
        }
    }

    private void drawPlayerShips() {

        playerFleetPane.getChildren().clear();

        for (Ship ship : gameModel.getHumanPlayer().getSeaMap().getShips()) {

            StackPane pane = new StackPane();

            ShipView shipView = new ShipView(pane, ship);

            pane.setMouseTransparent(true);

            playerFleetPane.getChildren().add(pane);

            positionPlayerShip(shipView);
        }
    }

    private void positionPlayerShip(ShipView shipView) {

        Ship ship = shipView.getShip();

        Coordinate start = ship.getStartCoordinate();

        double cell = 39;

        double x = start.getColumn() * cell;
        double y = start.getRow() * cell;

        shipView.getView().setLayoutX(x);
        shipView.getView().setLayoutY(y);

        if (ship.getOrientation() == Orientation.VERTICAL) {
            shipView.rotate();
        }
    }

    // ==========================================
    // Lógica de Ataques y Guardado
    // ==========================================
    private void onEnemyCellPressed(int row, int column) {
        if (gameModel.isGameFinished() || !gameModel.isHumanTurn()) return;

        Coordinate coordinate = new Coordinate(row, column);


        try {
            AttackResult result = gameModel.humanAttack(coordinate);
            if (result == AttackResult.ALREADY_ATTACKED) return;

            updateCellVisuals(enemyButtons[row][column], result);

            if (result == AttackResult.SUNK) {
                paintSunkShip(gameModel.getMachineShipAt(coordinate), enemyButtons);
            }

        } catch (InvalidTurnException e) {

            System.err.println(e.getMessage());

        }


        // NUEVO: Guardado directo, instantáneo y seguro
        gameModel.saveGame();
        checkIfGameEnded();
    }

    private void onMachineAttackComplete(Coordinate coordinate, AttackResult result) {
        int r = coordinate.getRow();
        int c = coordinate.getColumn();

        updateCellVisuals(playerButtons[r][c], result);

        // NUEVO: Guardado directo tras el turno de la IA
        gameModel.saveGame();
        checkIfGameEnded();
    }

    private void updateCellVisuals(Button button, AttackResult result) {
        switch (result) {
            case MISS:
                button.setText("•");
                button.setStyle("-fx-background-color: #0b5986; -fx-text-fill: white;");
                CombatEffects.playMiss(button);
                break;
            case HIT:
            case SUNK:
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
            button.setStyle("-fx-background-color: #610d0d;");
            shipCells.add(button);
        }
        CombatEffects.playSunk(shipCells);
    }

    private void checkIfGameEnded() {
        if (gameModel.hasWinner()) {
            stopThreads();
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