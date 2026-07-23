package com.example.battleship.view.fx;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

/**
 * Reusable, stateless effect presets. Pure presentation helpers -
 * no references to game state.
 */
public final class UIEffects {

    private UIEffects() { }

    public static DropShadow cyanGlow(double radius) {
        DropShadow d = new DropShadow(BlurType.GAUSSIAN, Color.rgb(33, 216, 255, .8), radius, 0.4, 0, 0);
        return d;
    }

    public static DropShadow fireGlow(double radius) {
        return new DropShadow(BlurType.GAUSSIAN, Color.rgb(255, 138, 43, .85), radius, 0.45, 0, 0);
    }

    public static DropShadow goldGlow(double radius) {
        return new DropShadow(BlurType.GAUSSIAN, Color.rgb(232, 184, 75, .8), radius, 0.4, 0, 0);
    }

    public static DropShadow softShadow() {
        return new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, .55), 18, .3, 0, 8);
    }

    public static InnerShadow hullShading() {
        return new InnerShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, .45), 8, .2, 2, 2);
    }

    public static Glow gentleGlow(double level) {
        return new Glow(level);
    }
}