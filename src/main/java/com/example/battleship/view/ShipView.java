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
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Graphical wrapper around a {@link Ship}. Keeps the exact public API the
 * controllers already depend on (view, ship, original position, rotate())
 * and only changes HOW the ship looks and moves: a stylized hull with
 * bevel/shadow/turrets instead of a flat rectangle, plus animated
 * movement instead of instant teleporting.
 */
public class ShipView {

    private static final double CELL_SIZE = 40;

    private final StackPane view;
    private final Ship ship;

    private Point2D originalPosition;

    public ShipView(StackPane view, Ship ship) {
        this.view = view;
        this.ship = ship;

        view.setPickOnBounds(true);
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

    public void setOriginalPosition(Point2D originalPosition) {
        this.originalPosition = originalPosition;
    }

    /**
     * Rebuilds the hull for the ship's current orientation with a quick
     * "turn" flourish (squash + fade) instead of an instant size swap.
     */
    public void rotate() {
        ScaleTransition squash = new ScaleTransition(Duration.millis(90), view);
        squash.setToX(0.15);
        squash.setOnFinished(e -> {
            rebuildHull();
            ScaleTransition restore = new ScaleTransition(Duration.millis(120), view);
            restore.setToX(1.0);
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
        boolean horizontal = ship.getOrientation() == Orientation.HORIZONTAL;
        int size = ship.getSize();

        double length = size * CELL_SIZE;
        double width = CELL_SIZE;

        double w = horizontal ? length : width;
        double h = horizontal ? width : length;

        view.setPrefWidth(w);
        view.setPrefHeight(h);
        view.setMinWidth(w);
        view.setMinHeight(h);

        Group hull = horizontal ? buildHorizontalHull(length, width) : buildVerticalHull(width, length);

        DropShadow shadow = UIEffects.softShadow();
        view.setEffect(shadow);

        view.getChildren().setAll(hull);
    }

    private Group buildHorizontalHull(double length, double width) {
        Group group = new Group();

        double bow = Math.min(width * 0.9, length * 0.25);

        Polygon body = new Polygon(
                0, width * 0.15,
                length - bow, 0,
                length, width * 0.5,
                length - bow, width,
                0, width * 0.85
        );
        body.setFill(hullGradient(true));
        body.setStroke(Color.rgb(10, 20, 30, .8));
        body.setStrokeWidth(1.2);
        body.setEffect(UIEffects.hullShading());

        Rectangle deckStripe = new Rectangle(width * 0.3, width * 0.32, length - width * 0.9, width * 0.14);
        deckStripe.setArcWidth(6);
        deckStripe.setArcHeight(6);
        deckStripe.setFill(Color.rgb(255, 255, 255, .18));

        group.getChildren().addAll(body, deckStripe);
        addTurrets(group, length, width, true);
        return group;
    }

    private Group buildVerticalHull(double width, double length) {
        Group group = new Group();

        double bow = Math.min(width * 0.9, length * 0.25);

        Polygon body = new Polygon(
                width * 0.15, 0,
                0, length - bow,
                width * 0.5, length,
                width, length - bow,
                width * 0.85, 0
        );
        body.setFill(hullGradient(false));
        body.setStroke(Color.rgb(10, 20, 30, .8));
        body.setStrokeWidth(1.2);
        body.setEffect(UIEffects.hullShading());

        Rectangle deckStripe = new Rectangle(width * 0.32, width * 0.3, width * 0.14, length - width * 0.9);
        deckStripe.setArcWidth(6);
        deckStripe.setArcHeight(6);
        deckStripe.setFill(Color.rgb(255, 255, 255, .18));

        group.getChildren().addAll(body, deckStripe);
        addTurrets(group, width, length, false);
        return group;
    }

    private void addTurrets(Group group, double length, double width, boolean horizontal) {
        int turretCount = Math.max(1, ship.getSize() - 1);
        double radius = width * 0.13;

        for (int i = 0; i < turretCount; i++) {
            double t = (i + 1) / (double) (turretCount + 1);
            Circle turret = new Circle(radius);
            turret.setFill(Color.rgb(40, 55, 65, .95));
            turret.setStroke(Color.rgb(200, 220, 235, .5));
            turret.setStrokeWidth(0.8);

            if (horizontal) {
                turret.setCenterX(length * t);
                turret.setCenterY(width * 0.5);
            } else {
                turret.setCenterX(width * 0.5);
                turret.setCenterY(length * t);
            }
            group.getChildren().add(turret);
        }
    }

    private LinearGradient hullGradient(boolean horizontal) {
        if (horizontal) {
            return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#7FB8DD")),
                    new Stop(0.45, Color.web("#2E6E9E")),
                    new Stop(1, Color.web("#123B57")));
        }
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7FB8DD")),
                new Stop(0.45, Color.web("#2E6E9E")),
                new Stop(1, Color.web("#123B57")));
    }
}
