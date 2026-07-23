package com.example.battleship.model.threads;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.enums.AttackResult;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Thread responsible for executing the AI's turns concurrently.
 */
public class IAThread extends Thread {

    private final GameModel gameModel;
    private volatile boolean running;
    private final List<Coordinate> availableShots;

    // NUEVO: BiConsumer para enviar la coordenada y el resultado al controlador
    private BiConsumer<Coordinate, AttackResult> onUIAttackUpdate;

    public IAThread(GameModel gameModel) {
        this.gameModel = gameModel;
        this.running = true;
        this.availableShots = new ArrayList<>();
        initializeShots();
    }

    // NUEVO: Recibimos un BiConsumer en lugar de un Runnable
    public void setOnUIAttackUpdate(BiConsumer<Coordinate, AttackResult> onUIAttackUpdate) {
        this.onUIAttackUpdate = onUIAttackUpdate;
    }

    private void initializeShots() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                availableShots.add(new Coordinate(r, c));
            }
        }
        Collections.shuffle(availableShots);
    }

    public void stopThread() {
        this.running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running && !gameModel.isGameFinished()) {
            if (!gameModel.isHumanTurn()) {
                try {
                    Thread.sleep(1200); // Tiempo de "pensamiento" de la máquina
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!running || gameModel.isGameFinished() || availableShots.isEmpty()) break;

                Coordinate shot = availableShots.remove(0);
                AttackResult result = gameModel.machineAttack(shot);

                // NUEVO: Enviamos el disparo y el resultado a la interfaz gráfica
                if (onUIAttackUpdate != null) {
                    Platform.runLater(() -> onUIAttackUpdate.accept(shot, result));
                }

            } else {
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