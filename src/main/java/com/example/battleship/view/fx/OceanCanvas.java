package com.example.battleship.view.fx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Purely decorative animated ocean background used behind every screen.
 * Not part of the game model/controllers: it just paints a moving sea,
 * soft fog particles and (optionally) a silhouette to give the menu depth.
 * <p>
 * Usage: create it, bind its width/height to the parent pane, add it as
 * the first (bottom-most) child of a StackPane/AnchorPane.
 */
public class OceanCanvas extends Canvas {

    private final List<Wave> waves = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final boolean darkVariant;

    private AnimationTimer timer;
    private double t = 0;

    public OceanCanvas(boolean darkVariant) {
        this.darkVariant = darkVariant;

        for (int i = 0; i < 4; i++) {
            waves.add(new Wave(
                    30 + random.nextDouble() * 40,
                    0.6 + random.nextDouble() * 0.8,
                    random.nextDouble() * Math.PI * 2,
                    0.15 + i * 0.18
            ));
        }
        for (int i = 0; i < 40; i++) {
            particles.add(new Particle(random));
        }

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                startAnimating();
            } else {
                stopAnimating();
            }
        });

        widthProperty().addListener((o, a, b) -> draw());
        heightProperty().addListener((o, a, b) -> draw());

        setMouseTransparent(true);
        setManaged(false);

        parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent instanceof Region region) {
                widthProperty().bind(region.widthProperty());
                heightProperty().bind(region.heightProperty());
            }
        });
    }

    public OceanCanvas() {
        this(false);
    }

    private void startAnimating() {
        if (timer != null) return;
        timer = new AnimationTimer() {
            private long last = -1;

            @Override
            public void handle(long now) {
                if (last < 0) { last = now; return; }
                double dt = (now - last) / 1_000_000_000.0;
                last = now;
                t += dt;
                draw();
            }
        };
        timer.start();
    }

    private void stopAnimating() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void draw() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        LinearGradient sky = darkVariant
                ? new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#04070d")),
                new Stop(.55, Color.web("#0a1826")),
                new Stop(1, Color.web("#050b12")))
                : new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#041525")),
                new Stop(.45, Color.web("#0a3b60")),
                new Stop(1, Color.web("#07233d")));

        gc.setFill(sky);
        gc.fillRect(0, 0, w, h);

        // Layered wave silhouettes for parallax depth.
        for (Wave wave : waves) {
            drawWave(gc, wave, w, h);
        }

        // Soft drifting fog/foam particles.
        gc.setGlobalAlpha(1);
        for (Particle p : particles) {
            p.advance(t, w, h);
            double alpha = p.alpha * (0.5 + 0.5 * Math.sin(t * 0.5 + p.seed));
            gc.setFill(Color.rgb(180, 225, 255, Math.max(0, alpha)));
            gc.fillOval(p.x, p.y, p.size, p.size);
        }
    }

    private void drawWave(GraphicsContext gc, Wave wave, double w, double h) {
        double baseY = h * (0.55 + wave.depth * 0.4);
        gc.beginPath();
        gc.moveTo(0, h);
        gc.lineTo(0, baseY);

        int steps = 40;
        for (int i = 0; i <= steps; i++) {
            double x = w * i / (double) steps;
            double y = baseY + Math.sin((x / w) * Math.PI * 2 * 1.5 + t * wave.speed + wave.phase) * wave.amplitude;
            gc.lineTo(x, y);
        }
        gc.lineTo(w, h);
        gc.closePath();

        double shade = 0.10 + wave.depth * 0.25;
        Color c = darkVariant
                ? Color.rgb(10, 25, 40, shade + 0.35)
                : Color.rgb(20, 90, 140, shade + 0.25);
        gc.setFill(c);
        gc.fill();
    }

    private static final class Wave {
        final double amplitude, speed, phase, depth;
        Wave(double amplitude, double speed, double phase, double depth) {
            this.amplitude = amplitude;
            this.speed = speed;
            this.phase = phase;
            this.depth = depth;
        }
    }

    private static final class Particle {
        final double baseX, baseY, driftSpeed;
        double x, y, size, alpha, seed;

        Particle(Random random) {
            baseX = random.nextDouble();
            baseY = random.nextDouble();
            size = 1 + random.nextDouble() * 2.2;
            alpha = 0.15 + random.nextDouble() * 0.35;
            seed = random.nextDouble() * 10;
            driftSpeed = 0.005 + random.nextDouble() * 0.01;
        }

        void advance(double t, double w, double h) {
            x = ((baseX + t * driftSpeed) % 1.0) * w;
            y = baseY * h;
        }
    }
}