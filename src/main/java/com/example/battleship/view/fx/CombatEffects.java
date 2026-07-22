package com.example.battleship.view.fx;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.util.List;

/**
 * Animations for the enemy-grid combat cells. Every method only ever
 * touches styleClass / visual transforms of the given, already existing
 * {@link Button} instances created by AttackController — it never reads
 * or writes any game state.
 */
public final class CombatEffects {

    private CombatEffects() { }

    /** Base idle look applied once, at cell-creation time. */
    public static void applyBaseStyle(Button cell) {
        cell.getStyleClass().add("cell-water");
    }

    /** Splash animation for a MISS: cell flashes and settles. */
    public static void playMiss(Button cell) {
        cell.getStyleClass().removeAll("cell-hit", "cell-sunk");
        cell.getStyleClass().add("cell-miss");

        ScaleTransition pop = new ScaleTransition(Duration.millis(220), cell);
        pop.setFromX(0.4);
        pop.setFromY(0.4);
        pop.setToX(1);
        pop.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(260), cell);
        fade.setFromValue(0.3);
        fade.setToValue(1);

        new ParallelTransition(pop, fade).play();
    }

    /** Explosion-style feedback for a HIT: glow flash + short shake. */
    public static void playHit(Button cell) {
        cell.getStyleClass().removeAll("cell-miss", "cell-sunk");
        cell.getStyleClass().add("cell-hit");

        ScaleTransition burst = new ScaleTransition(Duration.millis(160), cell);
        burst.setFromX(1.35);
        burst.setFromY(1.35);
        burst.setToX(1);
        burst.setToY(1);

        TranslateTransition shake = new TranslateTransition(Duration.millis(45), cell);
        shake.setByX(4);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);

        ParallelTransition combo = new ParallelTransition(burst, shake);
        combo.setOnFinished(e -> cell.setTranslateX(0));
        combo.play();
    }

    /** Ship-goes-down feedback applied to every cell of a sunk ship. */
    public static void playSunk(List<Button> shipCells) {
        for (int i = 0; i < shipCells.size(); i++) {
            Button cell = shipCells.get(i);
            cell.getStyleClass().removeAll("cell-water", "cell-miss", "cell-hit");

            PauseTransition delay = new PauseTransition(Duration.millis(i * 60));

            RotateTransition tilt = new RotateTransition(Duration.millis(500), cell);
            tilt.setToAngle(8);

            TranslateTransition sink = new TranslateTransition(Duration.millis(650), cell);
            sink.setByY(6);

            FadeTransition dim = new FadeTransition(Duration.millis(650), cell);
            dim.setToValue(0.85);

            ParallelTransition sinkCombo = new ParallelTransition(tilt, sink, dim);
            sinkCombo.setOnFinished(e -> cell.getStyleClass().add("cell-sunk"));

            new SequentialTransition(delay, sinkCombo).play();
        }
    }
}
