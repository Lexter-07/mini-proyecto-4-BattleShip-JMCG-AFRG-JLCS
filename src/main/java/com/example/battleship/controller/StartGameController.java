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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartGameController {

    @FXML private AnchorPane rootPane;
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

        System.out.println("CLICK SHIP");

        StackPane pane = (StackPane) event.getSource();
        selectedShipView = shipViews.get(pane);

        Point2D point = rootPane.sceneToLocal(event.getSceneX(), event.getSceneY());

        xOffset = point.getX() - pane.getLayoutX();
        yOffset = point.getY() - pane.getLayoutY();

        pane.toFront();

        pane.setMouseTransparent(false);
    }

    private StackPane createShipPane(
            double x,
            double y,
            ShipType type) {

        StackPane pane = new StackPane();

        pane.setLayoutX(x);
        pane.setLayoutY(y);

        pane.setPickOnBounds(true);

        pane.setOnMousePressed(this::onMousePressedShip);
        pane.setOnMouseDragged(this::onMouseDragged);
        pane.setOnMouseReleased(this::onMouseReleased);

        rootPane.getChildren().add(pane);

        ShipView shipView =
                new ShipView(pane, new Ship(type));

        shipViews.put(pane, shipView);

        return pane;
    }

    private void createShips() {

        createShipPane(47,150, ShipType.AIRCRAFT);

        createShipPane(47,315, ShipType.SUBMARINE);
        createShipPane(47,366, ShipType.SUBMARINE);

        createShipPane(47,205, ShipType.DESTROYER);
        createShipPane(140,205, ShipType.DESTROYER);
        createShipPane(47,256, ShipType.DESTROYER);

        createShipPane(47,425, ShipType.FRIGATE);
        createShipPane(97,425, ShipType.FRIGATE);
        createShipPane(147,425, ShipType.FRIGATE);
        createShipPane(197,425, ShipType.FRIGATE);

    }

    private void saveOriginalPositions() {
        for(ShipView shipView : shipViews.values()) {
            shipView.setOriginalPosition(new Point2D(shipView.getView().getLayoutX(), shipView.getView().getLayoutY()));
        }
    }

    @FXML void onMouseDragged(MouseEvent event) { moveShip(event); }
    @FXML void onMousePressedShip(MouseEvent event) {pressedShip(event);}

    @FXML
    void onMouseReleased(MouseEvent event) {
        if (!isInsideGrid(event)) {
            returnToOrigin();
            return;
        }

        Coordinate coordinate = calculateCoordinate();
        PlacementResult result = gameModel.placeShip(selectedShipView.getShip(),
                coordinate, selectedShipView.getShip().getOrientation());

        if (result != PlacementResult.SUCCESS) {
            returnToOrigin();
            return;
        }

        snapShip(selectedShipView, coordinate);
        selectedShipView.playSnapEffect();
        enableConfirmButton();
    }



    private void snapShip(ShipView shipView, Coordinate coordinate) {
        StackPane pane = shipView.getView();

        double cellWidth = seaGrid.getWidth() / SeaMap.COLUMNS;
        double cellHeight = seaGrid.getHeight() / SeaMap.ROWS;

        Bounds gridBounds = seaGrid.localToScene(seaGrid.getBoundsInLocal());

        double sceneX = gridBounds.getMinX() + coordinate.getColumn() * cellWidth;
        double sceneY = gridBounds.getMinY() + coordinate.getRow() * cellHeight;

        Point2D point = rootPane.sceneToLocal(sceneX, sceneY);

        shipView.animateMoveTo(point.getX(), point.getY());
        pane.toFront();
        pane.setMouseTransparent(false);
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
    void onHandleAutoPlace(ActionEvent event) {

        if (gameModel == null) return;

        List<Ship> ships = new ArrayList<>();

        for (ShipView shipView : shipViews.values()) {
            ships.add(shipView.getShip());
        }

        gameModel.autoPlaceHumanFleet(ships);

        for (ShipView shipView : shipViews.values()) {
            Ship ship = shipView.getShip();

            if (ship.getOrientation() != shipView.getDisplayedOrientation()) {
                shipView.rotate();
            }

            snapShip(shipView, ship.getStartCoordinate());
            rootPane.applyCss();
            rootPane.layout();
            shipView.playSnapEffect();
        }
        enableConfirmButton();
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

        rootPane.applyCss();
        rootPane.layout();

        snapShip(selectedShipView, coordinate);
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