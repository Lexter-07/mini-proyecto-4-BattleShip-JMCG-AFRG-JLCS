package com.example.battleship.model.threads;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.enums.AttackResult;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread responsible for executing the AI's turns concurrently.
 * Ensures rules are respected, no duplicated shots are fired,
 * and JavaFX is not blocked.
 */
public class IAThread extends Thread {

    private final GameModel gameModel;
    private volatile boolean running;
    private final List<Coordinate> availableShots;

    // Callback to strictly update the UI safely
    private Runnable onUIAttackUpdate;

    /**
     * Constructor initializes the pool of available shots.
     *
     * @param gameModel The current GameModel.
     */
    public IAThread(GameModel gameModel) {
        this.gameModel = gameModel;
        this.running = true;
        this.availableShots = new ArrayList<>();
        initializeShots();
    }

    /**
     * Sets the UI callback to refresh the grids after the AI attacks.
     *
     * @param onUIAttackUpdate The Runnable containing UI update logic.
     */
    public void setOnUIAttackUpdate(Runnable onUIAttackUpdate) {
        this.onUIAttackUpdate = onUIAttackUpdate;
    }

    /**
     * Generates a shuffled list of all 100 possible coordinates
     * to guarantee O(1) selection and zero repetitions.
     */
    private void initializeShots() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                availableShots.add(new Coordinate(r, c));
            }
        }
        Collections.shuffle(availableShots);
    }

    /**
     * Safely stops the thread.
     */
    public void stopThread() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running && !gameModel.isGameFinished()) {

            // Check if it's the machine's turn
            if (!gameModel.isHumanTurn()) {
                try {
                    // Simulate "thinking" time for realistic pacing
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!running || gameModel.isGameFinished() || availableShots.isEmpty()) break;

                // Select a guaranteed valid, non-repeated shot
                Coordinate shot = availableShots.remove(0);
                AttackResult result = gameModel.machineAttack(shot);

                // Update UI using strictly Platform.runLater
                if (onUIAttackUpdate != null) {
                    Platform.runLater(onUIAttackUpdate);
                }

            } else {
                // If it is human turn, sleep shortly (safe polling)
                // Ensures synchronization with TurnThread and GameModel state
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}