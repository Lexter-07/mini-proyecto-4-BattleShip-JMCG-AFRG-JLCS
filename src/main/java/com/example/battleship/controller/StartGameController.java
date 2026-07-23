package com.example.battleship.controller;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.SeaMap;
import com.example.battleship.model.Ship;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.PlacementResult;
import com.example.battleship.model.enums.ShipType;
import com.example.battleship.view.Path;
import com.example.battleship.view.SceneManager;
import com.example.battleship.view.ShipView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;

public class StartGameController {

    @FXML private AnchorPane rootPane;
    @FXML private StackPane Aircraft, Destroyer1, Destroyer2, Destroyer3, Frigate1, Frigate2, Frigate3, Frigate4, Submarine1, Submarine2;
    @FXML private GridPane seaGrid;
    @FXML private Button confirmButton;

    private double xOffset = 0;
    private double yOffset = 0;

    private GameModel gameModel;
    private final HashMap<StackPane, ShipView> shipViews = new HashMap<>();
    private ShipView selectedShipView;

    @FXML
    public void initialize() {
        createShips();
        saveOriginalPositions();
    }

    // Método clave inyectado correctamente para evitar sobrescribir con "Paco"
    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
        enableConfirmButton();
    }

    private void moveShip(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        Point2D point = rootPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        pane.setLayoutX(point.getX() - xOffset);
        pane.setLayoutY(point.getY() - yOffset);
    }

    private void pressedShip(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        selectedShipView = shipViews.get(pane);
        Point2D point = rootPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        xOffset = point.getX() - pane.getLayoutX();
        yOffset = point.getY() - pane.getLayoutY();
    }

    private void createShips() {
        shipViews.put(Aircraft, new ShipView(Aircraft, new Ship(ShipType.AIRCRAFT)));
        shipViews.put(Submarine1, new ShipView(Submarine1, new Ship(ShipType.SUBMARINE)));
        shipViews.put(Submarine2, new ShipView(Submarine2, new Ship(ShipType.SUBMARINE)));
        shipViews.put(Destroyer1, new ShipView(Destroyer1, new Ship(ShipType.DESTROYER)));
        shipViews.put(Destroyer2, new ShipView(Destroyer2, new Ship(ShipType.DESTROYER)));
        shipViews.put(Destroyer3, new ShipView(Destroyer3, new Ship(ShipType.DESTROYER)));
        shipViews.put(Frigate1, new ShipView(Frigate1, new Ship(ShipType.FRIGATE)));
        shipViews.put(Frigate2, new ShipView(Frigate2, new Ship(ShipType.FRIGATE)));
        shipViews.put(Frigate3, new ShipView(Frigate3, new Ship(ShipType.FRIGATE)));
        shipViews.put(Frigate4, new ShipView(Frigate4, new Ship(ShipType.FRIGATE)));
    }

    private void saveOriginalPositions() {
        for(ShipView shipView : shipViews.values()) {
            shipView.setOriginalPosition(new Point2D(shipView.getView().getLayoutX(), shipView.getView().getLayoutY()));
        }
    }

    @FXML void onMouseDragged(MouseEvent event) { moveShip(event); }
    @FXML void onMousePressedShip(MouseEvent event) { pressedShip(event); }

    @FXML
    void onMouseReleased(MouseEvent event) {
        if (!isInsideGrid(event)) {
            returnToOrigin();
            return;
        }

        Coordinate coordinate = calculateCoordinate();
        PlacementResult result = gameModel.placeShip(selectedShipView.getShip(), coordinate, selectedShipView.getShip().getOrientation());

        if (result != PlacementResult.SUCCESS) {
            returnToOrigin();
            return;
        }

        snapShip(coordinate);
        selectedShipView.playSnapEffect();
        enableConfirmButton();
    }

    private void snapShip(Coordinate coordinate) {
        StackPane pane = selectedShipView.getView();
        double cellWidth = seaGrid.getWidth() / SeaMap.COLUMNS;
        double cellHeight = seaGrid.getHeight() / SeaMap.ROWS;
        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());
        double sceneX = gridBounds.getMinX() + coordinate.getColumn() * cellWidth;
        double sceneY = gridBounds.getMinY() + coordinate.getRow() * cellHeight;
        Point2D point = rootPane.sceneToLocal(sceneX, sceneY);
        selectedShipView.animateMoveTo(point.getX(), point.getY());
    }

    private Coordinate calculateCoordinate() {
        StackPane pane = selectedShipView.getView();
        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());
        Point2D paneScene = rootPane.localToScene(pane.getLayoutX(), pane.getLayoutY());
        double localX = paneScene.getX() - gridBounds.getMinX();
        double localY = paneScene.getY() - gridBounds.getMinY();
        double cellWidth = seaGrid.getWidth() / SeaMap.COLUMNS;
        double cellHeight = seaGrid.getHeight() / SeaMap.ROWS;
        int column = (int)(localX / cellWidth);
        int row = (int)(localY / cellHeight);
        return new Coordinate(row, column);
    }

    private void returnToOrigin() {
        Point2D origin = selectedShipView.getOriginalPosition();
        gameModel.unplaceShip(selectedShipView.getShip());
        enableConfirmButton();
        selectedShipView.getShip().setOrientation(Orientation.HORIZONTAL);
        selectedShipView.rotate();
        selectedShipView.animateMoveTo(origin.getX(), origin.getY());
    }

    private boolean isInsideGrid(MouseEvent event) {
        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        return mouseX >= gridBounds.getMinX() && mouseX <= gridBounds.getMaxX() &&
                mouseY >= gridBounds.getMinY() && mouseY <= gridBounds.getMaxY();
    }

    @FXML
    void onHandleRotate(ActionEvent event) {
        if (selectedShipView == null) return;
        Ship ship = selectedShipView.getShip();
        Orientation newOrientation = (ship.getOrientation() == Orientation.HORIZONTAL) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        Coordinate coordinate = calculateCoordinate();
        PlacementResult result = gameModel.placeShip(ship, coordinate, newOrientation);
        if (result != PlacementResult.SUCCESS) return;
        ship.setOrientation(newOrientation);
        selectedShipView.rotate();
        snapShip(coordinate);
    }

    private void enableConfirmButton() {
        if (gameModel != null && gameModel.getHumanPlayer() != null) {
            confirmButton.setDisable(!gameModel.getHumanPlayer().getSeaMap().hasAllShipsPlaced());
        }
    }

    @FXML
    void onHandleConfirm(ActionEvent event) throws IOException {
        if (gameModel == null || !gameModel.getHumanPlayer().getSeaMap().hasAllShipsPlaced()) return;

        gameModel.startGame(); // Marca el juego como iniciado
        gameModel.saveGame();

        FXMLLoader loader = SceneManager.changeScene(Path.attackView);
        AttackController controller = loader.getController();
        controller.setGameModel(gameModel);
    }
}