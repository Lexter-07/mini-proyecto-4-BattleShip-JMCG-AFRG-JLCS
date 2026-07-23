package com.example.battleship.model;

import java.io.Serializable;

/**
 * Representa el estado de una partida.
 * Contiene los dos jugadores y el estado general del juego.
 */
public class GameStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Player humanPlayer;
    private final Player machinePlayer;

    // EL ARREGLO ESTÁ AQUÍ: Añadimos 'volatile' a las banderas de estado
    private volatile boolean humanTurn;
    private volatile boolean gameStarted;
    private volatile boolean gameFinished;

    /**
     * Constructor.
     *
     * @param nickname Name of the human player.
     */
    public GameStatus(String nickname) {
        this.humanPlayer = new Player(nickname, true);
        this.machinePlayer = new Player("Machine", false);

        this.humanTurn = true;
        this.gameStarted = false;
        this.gameFinished = false;
    }

    // ===========================
    // Getters y Setters se mantienen exactamente igual
    // ===========================

    public Player getHumanPlayer() { return humanPlayer; }
    public Player getMachinePlayer() { return machinePlayer; }
    public boolean isHumanTurn() { return humanTurn; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isGameFinished() { return gameFinished; }

    public void setHumanTurn(boolean humanTurn) { this.humanTurn = humanTurn; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }
    public void setGameFinished(boolean gameFinished) { this.gameFinished = gameFinished; }

    public void changeTurn() {
        humanTurn = !humanTurn;
    }

    public void reset() {
        humanPlayer.reset();
        machinePlayer.reset();
        humanTurn = true;
        gameStarted = false;
        gameFinished = false;
    }
}