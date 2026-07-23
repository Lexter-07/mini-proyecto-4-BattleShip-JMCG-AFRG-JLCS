package com.example.battleship.model.exceptions;

import com.example.battleship.model.enums.PlacementResult;

/**
 * Exception thrown when a ship cannot be placed on the board.
 */
public class InvalidShipPlacementException extends Exception {

  public InvalidShipPlacementException(PlacementResult result) {

    super(createMessage(result));

  }

  private static String createMessage(PlacementResult result) {

    return switch (result) {

      case OVERLAP ->
              "The ship overlaps another ship.";

      case OUT_OF_BOUNDS ->
              "The ship is outside the board limits.";

      default ->
              "Invalid ship placement.";

    };

  }

}