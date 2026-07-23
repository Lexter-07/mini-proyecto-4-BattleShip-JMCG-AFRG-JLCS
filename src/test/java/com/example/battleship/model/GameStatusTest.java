package com.example.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStatusTest {

    @Test
    void constructorShouldInitializeGameStatusCorrectly() {

        GameStatus status = new GameStatus("Ferran");

        assertEquals("Ferran", status.getHumanPlayer().getNickname());
        assertEquals("Machine", status.getMachinePlayer().getNickname());

        assertTrue(status.isHumanTurn());
        assertFalse(status.isGameStarted());
        assertFalse(status.isGameFinished());

    }

    @Test
    void changeTurnShouldToggleTurn() {

        GameStatus status = new GameStatus("Ferran");

        boolean previousTurn = status.isHumanTurn();

        status.changeTurn();

        assertNotEquals(previousTurn, status.isHumanTurn());

    }

    @Test
    void settersShouldUpdateFlags() {

        GameStatus status = new GameStatus("Ferran");

        status.setHumanTurn(false);
        status.setGameStarted(true);
        status.setGameFinished(true);

        assertFalse(status.isHumanTurn());
        assertTrue(status.isGameStarted());
        assertTrue(status.isGameFinished());

    }

    @Test
    void resetShouldRestoreInitialState() {

        GameStatus status = new GameStatus("Ferran");

        status.setHumanTurn(false);
        status.setGameStarted(true);
        status.setGameFinished(true);

        status.reset();

        assertTrue(status.isHumanTurn());
        assertFalse(status.isGameStarted());
        assertFalse(status.isGameFinished());

    }

}