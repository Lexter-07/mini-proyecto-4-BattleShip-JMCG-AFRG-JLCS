package com.example.battleship.controller;

import com.example.battleship.model.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartGameController {

    @FXML private AnchorPane rootPane;
    @FXML private StackPane Aircraft;
    @FXML private StackPane Destroyer1;
    @FXML private StackPane Destroyer2;
    @FXML private StackPane Destroyer3;
    @FXML private StackPane Frigate1;
    @FXML private StackPane Frigate2;
    @FXML private StackPane Frigate3;
    @FXML private StackPane Frigate4;
    @FXML private StackPane Submarine1;
    @FXML private StackPane Submarine2;
    @FXML private GridPane seaGrid;
    @FXML private Button confirmButton;

    private double xOffset = 0;
    private double yOffset = 0;

    private GameModel gameModel;

    // Almacena las instancias de ShipView proporcionadas en tu paquete View
    private final HashMap<StackPane, ShipView> shipViews = new HashMap<>();
    private ShipView selectedShipView;

    @FXML
    public void initialize() {
        createDefaultShipViews();
        confirmButton.setDisable(true);
    }

    /**
     * Inyecta el modelo del juego.
     * Si la partida tiene barcos ya colocados (cargada del disco), los restaura.
     */
    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;

        List<Ship> placedShips = gameModel.getHumanPlayer().getSeaMap().getShips();
        if (!placedShips.isEmpty()) {
            restorePlacedShips(placedShips);
        }

        enableConfirmButton();
    }

    /**
     * Crea los ShipViews iniciales para una partida nueva usando constructores limpios.
     */
    private void createDefaultShipViews() {
        registerShipView(Aircraft, ShipType.AIRCRAFT);
        registerShipView(Submarine1, ShipType.SUBMARINE);
        registerShipView(Submarine2, ShipType.SUBMARINE);
        registerShipView(Destroyer1, ShipType.DESTROYER);
        registerShipView(Destroyer2, ShipType.DESTROYER);
        registerShipView(Destroyer3, ShipType.DESTROYER);
        registerShipView(Frigate1, ShipType.FRIGATE);
        registerShipView(Frigate2, ShipType.FRIGATE);
        registerShipView(Frigate3, ShipType.FRIGATE);
        registerShipView(Frigate4, ShipType.FRIGATE);
    }

    private void registerShipView(StackPane pane, ShipType type) {
        Ship newShip = new Ship(type);
        ShipView view = new ShipView(pane, newShip);
        view.setOriginalPosition(new Point2D(pane.getLayoutX(), pane.getLayoutY()));
        shipViews.put(pane, view);
    }

    /**
     * Si la partida fue cargada, recrea los ShipViews usando los barcos reales del modelo
     * en lugar de intentar usar un "setShip" que no existe en ShipView.
     */
    private void restorePlacedShips(List<Ship> loadedShips) {

        List<Ship> unassignedShips = new ArrayList<>(loadedShips);

        List<StackPane> panes =
                new ArrayList<>(shipViews.keySet());

        for (StackPane pane : panes) {

            ShipView currentView = shipViews.get(pane);

            Ship matchingShip = null;

            for (Ship loadedShip : unassignedShips) {

                if (loadedShip.getType()
                        == currentView.getShip().getType()) {

                    matchingShip = loadedShip;
                    break;
                }
            }

            if (matchingShip != null) {

                unassignedShips.remove(matchingShip);

                ShipView restored =
                        new ShipView(pane, matchingShip);

                restored.setOriginalPosition(
                        currentView.getOriginalPosition());

                shipViews.put(pane, restored);
            }
        }
        refreshFleetView();
    }

    @FXML
    void onMousePressedShip(MouseEvent event) {
        StackPane pane = (StackPane) event.getSource();
        selectedShipView = shipViews.get(pane);

        Point2D point = rootPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        xOffset = point.getX() - pane.getLayoutX();
        yOffset = point.getY() - pane.getLayoutY();
    }

    @FXML
    void onMouseDragged(MouseEvent event) {
        if (selectedShipView == null) return;
        StackPane pane = (StackPane) event.getSource();

        Point2D point = rootPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        pane.setLayoutX(point.getX() - xOffset);
        pane.setLayoutY(point.getY() - yOffset);
    }

    @FXML
    void onMouseReleased(MouseEvent event) {
        if (selectedShipView == null) return;

        if (!isInsideGrid(event)) {
            returnToOrigin();
            return;
        }

        Coordinate coordinate = calculateCoordinate();
        PlacementResult result = gameModel.placeShip(
                selectedShipView.getShip(),
                coordinate,
                selectedShipView.getShip().getOrientation()
        );

        if (result != PlacementResult.SUCCESS) {
            returnToOrigin();
            return;
        }

        snapShipVisual(selectedShipView, coordinate);

        gameModel.saveGame();
        enableConfirmButton();
    }

    private void snapShipVisual(ShipView shipView, Coordinate coordinate) {
        StackPane pane = shipView.getView();
        double cellWidth = seaGrid.getWidth() / SeaMap.COLUMNS;
        double cellHeight = seaGrid.getHeight() / SeaMap.ROWS;

        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());
        double sceneX = gridBounds.getMinX() + coordinate.getColumn() * cellWidth;
        double sceneY = gridBounds.getMinY() + coordinate.getRow() * cellHeight;

        Point2D point = rootPane.sceneToLocal(sceneX, sceneY);
        pane.setLayoutX(point.getX());
        pane.setLayoutY(point.getY());
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
        StackPane pane = selectedShipView.getView();
        Point2D origin = selectedShipView.getOriginalPosition();

        gameModel.unplaceShip(selectedShipView.getShip());
        enableConfirmButton();
        gameModel.saveGame();

        selectedShipView.getShip().setOrientation(Orientation.HORIZONTAL);

        // Deshacer rotación visual si estaba en vertical
        if (pane.getPrefHeight() > pane.getPrefWidth()) {
            selectedShipView.rotate();
        }

        pane.setLayoutX(origin.getX());
        pane.setLayoutY(origin.getY());
    }

    private boolean isInsideGrid(MouseEvent event) {
        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        return mouseX >= gridBounds.getMinX()
                && mouseX <= gridBounds.getMaxX()
                && mouseY >= gridBounds.getMinY()
                && mouseY <= gridBounds.getMaxY();
    }


    private void refreshFleetView() {

        for (ShipView shipView : shipViews.values()) {
            Ship ship = shipView.getShip();

            if (!ship.isPlaced()) {
                continue;
            }

            shipView.rotate();

            snapShipVisual(
                    shipView,
                    ship.getStartCoordinate()
            );
        }
        enableConfirmButton();
    }


    @FXML
    void onHandleRotate(ActionEvent event) {
        if (selectedShipView == null) return;

        Ship ship = selectedShipView.getShip();
        Orientation newOrientation = (ship.getOrientation() == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;

        Coordinate coordinate = calculateCoordinate();
        PlacementResult result = gameModel.placeShip(ship, coordinate, newOrientation);

        if (result != PlacementResult.SUCCESS) return;

        ship.setOrientation(newOrientation);
        selectedShipView.rotate(); // Llama al método de tu clase ShipView

        snapShipVisual(selectedShipView, coordinate);

        gameModel.saveGame();
    }



    @FXML
    private void onHandleAutoPlace(ActionEvent event) {
        List<Ship> ships = new ArrayList<>();

        for (ShipView shipView : shipViews.values()) {
            ships.add(shipView.getShip());
        }
        gameModel.autoPlaceHumanFleet(ships);
        refreshFleetView();
    }


    @FXML
    void onHandleConfirm(ActionEvent event) throws IOException {
        if (!gameModel.getHumanPlayer().getSeaMap().hasAllShipsPlaced()) return;

        gameModel.startGame();
        gameModel.saveGame();

        // Usa la ruta definida en tu archivo Path
        FXMLLoader loader = SceneManager.changeScene(Path.attackView);
        AttackController controller = loader.getController();
        controller.setGameModel(gameModel);
    }

    private void enableConfirmButton() {
        if (gameModel == null) return;
        confirmButton.setDisable(!gameModel.getHumanPlayer().getSeaMap().hasAllShipsPlaced());
    }
}