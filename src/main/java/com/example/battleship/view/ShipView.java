package com.example.battleship.view;

import com.example.battleship.model.Ship;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.view.fx.UIEffects;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Graphical wrapper around a {@link Ship}. Keeps the exact public API the
 * controllers already depend on (view, ship, original position, rotate())
 * and only changes HOW the ship looks and moves: a stylized hull with
 * bevel/shadow/turrets instead of a flat rectangle, plus animated
 * movement instead of instant teleporting.
 */
public class ShipView {

    private static final double CELL_SIZE = 40;

    private static final String SHIPS_PATH =
            "/com/example/battleship/FXML/ships/";

    private final StackPane view;
    private final Ship ship;

    private Point2D originalPosition;
    private Orientation displayedOrientation;

    public ShipView(StackPane view, Ship ship) {
        this.view = view;
        this.ship = ship;

        view.setPickOnBounds(true);

        view.getStyleClass().add("ship-node");

        displayedOrientation = ship.getOrientation();

        rebuildHull();
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

    public Orientation getDisplayedOrientation() {
        return displayedOrientation;
    }

    public void setOriginalPosition(Point2D originalPosition) {
        this.originalPosition = originalPosition;
    }

    /**
     * Rebuilds the hull for the ship's current orientation with a quick
     * "turn" flourish (squash + fade) instead of an instant size swap.
     */
    public void rotate() {

        displayedOrientation = ship.getOrientation();

        ScaleTransition squash = new ScaleTransition(Duration.millis(90), view);

        squash.setToX(0.15);

        squash.setOnFinished(e -> {

            rebuildHull();

            view.applyCss();
            view.layout();

            ScaleTransition restore =
                    new ScaleTransition(Duration.millis(120), view);

            restore.setToX(1);

            restore.play();

        });

        squash.play();

    }

    /** Animated move (used instead of an instant setLayoutX/Y). */
    public void animateMoveTo(double x, double y) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(160), view);
        view.setLayoutX(x);
        view.setLayoutY(y);
        // Start slightly offset upward, like the ship easing onto the water.
        view.setTranslateY(-14);
        view.setOpacity(0.85);
        tt.setFromY(-14);
        tt.setToY(0);

        FadeTransition ft = new FadeTransition(Duration.millis(160), view);
        ft.setFromValue(0.85);
        ft.setToValue(1);

        new ParallelTransition(tt, ft).play();
    }

    /** Snap-confirmation pulse played after a successful placement. */
    public void playSnapEffect() {
        ScaleTransition bounce1 = new ScaleTransition(Duration.millis(90), view);
        bounce1.setToX(1.08);
        bounce1.setToY(0.92);

        ScaleTransition bounce2 = new ScaleTransition(Duration.millis(120), view);
        bounce2.setToX(1.0);
        bounce2.setToY(1.0);

        new SequentialTransition(bounce1, bounce2).play();
    }

    // ===========================
    // Hull construction
    // ===========================

    private void rebuildHull() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(
                            SHIPS_PATH + getFXMLName()));

            Parent shipGraphic = loader.load();

            view.getChildren().clear();

            view.getChildren().add(shipGraphic);
            StackPane.setAlignment(shipGraphic, Pos.CENTER);

            view.setEffect(UIEffects.softShadow());

            displayedOrientation = ship.getOrientation();

            if (ship.getOrientation() == Orientation.VERTICAL) {

                view.setPrefWidth(CELL_SIZE);
                view.setPrefHeight(ship.getSize() * CELL_SIZE);
                view.setMinWidth(CELL_SIZE);
                view.setMinHeight(ship.getSize()*CELL_SIZE);

                view.setMaxWidth(CELL_SIZE);
                view.setMaxHeight(ship.getSize()*CELL_SIZE);

            } else {

                view.setPrefWidth(ship.getSize() * CELL_SIZE);
                view.setPrefHeight(CELL_SIZE);
                view.setMinWidth(ship.getSize()*CELL_SIZE);
                view.setMinHeight(CELL_SIZE);

                view.setMaxWidth(ship.getSize()*CELL_SIZE);
                view.setMaxHeight(CELL_SIZE);


            }

        }

        catch (IOException e) {

            e.printStackTrace();

        }

    }

    private String getFXMLName() {

        boolean vertical = ship.getOrientation() == Orientation.VERTICAL;

        switch (ship.getType()) {

            case AIRCRAFT:
                return vertical ? "AircraftVertical.fxml"
                        : "Aircraft.fxml";

            case SUBMARINE:
                return vertical ? "SubmarineVertical.fxml"
                        : "Submarine.fxml";

            case DESTROYER:
                return vertical ? "DestroyerVertical.fxml"
                        : "Destroyer.fxml";

            case FRIGATE:
                return vertical ? "FrigateVertical.fxml"
                        : "Frigate.fxml";

            default:
                throw new IllegalStateException("Unknown ship");
        }
    }
}