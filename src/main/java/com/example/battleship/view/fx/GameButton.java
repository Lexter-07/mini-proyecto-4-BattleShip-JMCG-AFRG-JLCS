package com.example.battleship.view.fx;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * A {@link Button} styled and animated like a modern game UI button.
 * Purely a graphical component: usable straight from FXML in place of a
 * plain Button. onAction / fx:id wiring in controllers is unaffected.
 */
public class GameButton extends Button {

    public GameButton() {
        getStyleClass().add("game-button");

        ScaleTransition grow = new ScaleTransition(Duration.millis(140), this);
        grow.setToX(1.045);
        grow.setToY(1.045);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(140), this);
        shrink.setToX(1.0);
        shrink.setToY(1.0);

        setOnMouseEntered(e -> {
            shrink.stop();
            grow.playFromStart();
        });
        setOnMouseExited(e -> {
            grow.stop();
            shrink.playFromStart();
        });

        ScaleTransition press = new ScaleTransition(Duration.millis(70), this);
        press.setToX(0.96);
        press.setToY(0.96);
        setOnMousePressed(e -> press.playFromStart());
        setOnMouseReleased(e -> {
            press.stop();
            grow.playFromStart();
        });
    }
}
