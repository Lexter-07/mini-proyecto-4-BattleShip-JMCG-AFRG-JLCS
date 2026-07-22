package com.example.battleship.controller;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.SeaMap;
import com.example.battleship.model.Ship;
import com.example.battleship.model.enums.AttackResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class AttackController {

    @FXML private GridPane enemyGrid;
    @FXML private GridPane playerGrid;

    private final Button[][] enemyButtons = new Button[10][10];
    private final Button[][] playerButtons = new Button[10][10];

    private GameModel gameModel;

    @FXML
    public void initialize() {

        createEnemyGrid();

    }

    private void createEnemyGrid() {

        for (int row = 0; row < SeaMap.ROWS; row++) {

            for (int column = 0; column < SeaMap.COLUMNS; column++) {

                Button button = new Button();

                int currentRow = row;
                int currentColumn = column;

                button.setOnAction(e ->
                        onEnemyCellPressed(currentRow, currentColumn));

                button.setPrefSize(39, 39);
                button.setOpacity(0.40);
                button.setStyle("-fx-background-radius: 0;");

                enemyButtons[row][column] = button;

                enemyGrid.add(button, column, row);

            }
        }
    }

    private void onEnemyCellPressed(int row, int column) {

        Coordinate coordinate = new Coordinate(row, column);

        AttackResult result =
                gameModel.humanAttack(coordinate);

        System.out.println(result);
        System.out.println("Disparo: " + row + ", " + column);

        updateEnemyCell(row, column, result);

        if (result == AttackResult.SUNK) {
            Ship ship = gameModel.getMachineShipAt(coordinate);
            paintSunkShip(ship);
        }
    }

    private void paintSunkShip(Ship ship) {

        for (Coordinate coordinate : ship.getPositions()) {
            Button button =
                    enemyButtons[coordinate.getRow()]
                            [coordinate.getColumn()];

            button.setStyle("-fx-background-color: gray;" + "-fx-background-radius: 0;");
        }
    }

    private void updateEnemyCell(
            int row,
            int column,
            AttackResult result) {

        Button button = enemyButtons[row][column];

        switch (result) {

            case MISS:
                button.setText("x");
                button.setStyle("-fx-background-color: #0ae4fc; -fx-background-radius: 0;");
                break;

            case HIT:
                button.setStyle(" -fx-background-color: red; -fx-background-radius: 0; ");
                break;

            case SUNK:
                break;

            case ALREADY_ATTACKED:
                return;
        }

        button.setDisable(true);

    }


    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
        //drawPlayerShips();
    }

}
