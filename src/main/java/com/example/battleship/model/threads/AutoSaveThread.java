package com.example.battleship.model.threads;

import com.example.battleship.model.GameModel;

/**
 * Thread responsible for auto-saving the game state seamlessly in the background.
 * It waits for save requests triggered after every player/IA shot.
 */
public class AutoSaveThread extends Thread {

    private final GameModel gameModel;
    private volatile boolean running;
    private volatile boolean saveRequested;

    /**
     * Constructor.
     *
     * @param gameModel The current GameModel to persist.
     */
    public AutoSaveThread(GameModel gameModel) {
        this.gameModel = gameModel;
        this.running = true;
        this.saveRequested = false;
    }

    /**
     * Safely stops the thread.
     */
    public void stopThread() {
        this.running = false;
    }

    /**
     * Flags the thread to perform a disk save on its next cycle.
     * Called by Controllers after any attack.
     */
    public void requestSave() {
        this.saveRequested = true;
    }

    @Override
    public void run() {
        while (running) {
            if (saveRequested) {
                // Disk operation done in background to avoid freezing JavaFX
                gameModel.saveGame();
                saveRequested = false;
            }

            try {
                // Polling interval for save requests
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}