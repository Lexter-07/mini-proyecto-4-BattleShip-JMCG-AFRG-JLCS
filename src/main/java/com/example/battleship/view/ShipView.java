package com.example.battleship.view;

import com.example.battleship.model.Ship;
import com.example.battleship.model.enums.Orientation;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;

public class ShipView {

    private final StackPane view;
    private final Ship ship;

    private Point2D originalPosition;

    public ShipView(StackPane view, Ship ship) {

        this.view = view;
        this.ship = ship;


    }

    public StackPane getView() {
        return view;
    }

    public Ship getShip() {
        return ship;
    }

    public Point2D getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(Point2D originalPosition) {
        this.originalPosition = originalPosition;
    }

    public void rotate() {

        Rectangle rectangle = (Rectangle) view.getChildren().get(0);

        int size = ship.getSize();
        double cellSize = 40;

        if (ship.getOrientation() == Orientation.HORIZONTAL) {

            rectangle.setWidth(size * cellSize);
            rectangle.setHeight(cellSize);

            view.setPrefWidth(size * cellSize);
            view.setPrefHeight(cellSize);

        } else {

            rectangle.setWidth(cellSize);
            rectangle.setHeight(size * cellSize);

            view.setPrefWidth(cellSize);
            view.setPrefHeight(size * cellSize);
        }
    }

}