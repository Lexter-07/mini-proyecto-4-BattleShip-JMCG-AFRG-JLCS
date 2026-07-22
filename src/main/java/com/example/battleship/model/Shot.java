package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a shot realizado sobre un SeaMap.
 * Guarda la coordenada atacada, el resultado del disparo
 * y el instante en el que ocurrió.
 */
public class Shot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Coordinate coordinate;

    private final AttackResult result;

    private final LocalDateTime timestamp;

    public Shot(Coordinate coordinate, AttackResult result) {

        this.coordinate = coordinate;
        this.result = result;
        this.timestamp = LocalDateTime.now();

    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public AttackResult getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {

        return coordinate +
                " -> " +
                result +
                " (" +
                timestamp +
                ")";

    }

}