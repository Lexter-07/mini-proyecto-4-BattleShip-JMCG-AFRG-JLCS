package com.example.battleship.model;

import java.io.Serializable;

/**
 * Representa un jugador de la partida.
 * Cada jugador posee un tablero y puede ser humano o máquina.
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nickname;
    private final boolean human;
    private final SeaMap seaMap;


    /**
     * Constructor.
     *
     * @param nickname Nombre del jugador.
     * @param human true si es un jugador humano.
     */
    public Player(String nickname, boolean human) {

        this.nickname = nickname;
        this.human = human;
        this.seaMap = new SeaMap();

    }

    // ===========================
    // Getters
    // ===========================

    public String getNickname() {
        return nickname;
    }

    public boolean isHuman() {
        return human;
    }

    public SeaMap getSeaMap() {
        return seaMap;
    }

    // ===========================
    // ===========================

    /**
     * Restore the initial status from playerr.
     */
    public void reset() {
        seaMap.reset();
    }

    @Override
    public String toString() {
        return nickname + (human ? " (Human)" : " (Machine)");
    }

}