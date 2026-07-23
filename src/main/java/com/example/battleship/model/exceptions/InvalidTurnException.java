package com.example.battleship.model.exceptions;

/**
 * Exception thrown when a player tries to perform
 * an action outside of their turn.
 */
public class InvalidTurnException extends Exception {

    public InvalidTurnException() {
        super("It is not the current player's turn.");
    }

}