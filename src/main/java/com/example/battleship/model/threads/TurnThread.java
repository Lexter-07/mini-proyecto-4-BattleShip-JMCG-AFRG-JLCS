package com.example.battleship.model.threads;

import com.example.battleship.model.GameModel;
import javafx.application.Platform;

public class TurnThread extends Thread {

    private final GameModel gameModel;
    private volatile boolean running = true;
    private Runnable onTurnChangeCallback;

    public TurnThread(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void setOnTurnChange(Runnable onTurnChangeCallback) {
        this.onTurnChangeCallback = onTurnChangeCallback;
    }

    @Override
    public void run() {
        boolean previousTurnState = gameModel.isHumanTurn();

        while (running && !gameModel.isGameFinished()) {
            try {
                boolean currentTurnState = gameModel.isHumanTurn();

                // Detecta cuando el turno cambia entre el Humano y la IA
                if (currentTurnState != previousTurnState) {
                    previousTurnState = currentTurnState;
                    if (onTurnChangeCallback != null) {
                        Platform.runLater(onTurnChangeCallback);
                    }
                }

                Thread.sleep(200); // Sondeo liviano para verificar el turno
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopThread() {
        this.running = false;
        this.interrupt();
    }
}