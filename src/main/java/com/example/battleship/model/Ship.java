package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.ShipType;

import java.io.Serializable;
import java.util.*;

public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    // ID of the Ship (this never change)
    private final ShipType type;

    // Estado del barco
    private Orientation orientation;
    private final ArrayList<Coordinate> positions;
    private final HashSet<Coordinate> hitPositions;

    public Ship(ShipType type) {
        this.type = type;
        this.orientation = Orientation.HORIZONTAL;
        this.positions = new ArrayList<>();
        this.hitPositions = new HashSet<>();
    }

    // ===========================
    // Getters
    // ===========================

    public ShipType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public List<Coordinate> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public Set<Coordinate> getHitPositions() {
        return Collections.unmodifiableSet(hitPositions);
    }

    public int getSize() {
        return type.getSize();
    }

    // ===========================
    // Ship State
    // ===========================

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setPositions(ArrayList<Coordinate> newPositions) {
        positions.clear();
        positions.addAll(newPositions);
    }

    public void clearPositions() {
        positions.clear();
    }


    // ===========================
    // Combat
    // ===========================
    public AttackResult receiveShot(Coordinate coordinate) {

        if (!positions.contains(coordinate)) {return AttackResult.MISS;}
        if (hitPositions.contains(coordinate)) {return AttackResult.ALREADY_ATTACKED;}

        hitPositions.add(coordinate);

        if (isSunk()) {return AttackResult.SUNK;}

        return AttackResult.HIT;
    }

    public boolean isSunk() {
        return hitPositions.containsAll(positions);
    }

    public void resetHits() {
        hitPositions.clear();
    }

    public boolean isPlaced() {
        return !positions.isEmpty();
    }

    public Coordinate getStartCoordinate() {

        if (positions.isEmpty()) {
            return null;
        }

        return positions.get(0);

    }

    public boolean occupies(Coordinate coordinate) {
        return positions.contains(coordinate);
    }

    // ===========================
    // Reset ship
    // ===========================

    public void reset() {
        orientation = Orientation.HORIZONTAL;
        positions.clear();
        hitPositions.clear();
    }

    @Override
    public String toString() {

        return type +
                " | Size: " + getSize() +
                " | Orientation: " + orientation +
                " | Positions: " + positions +
                " | Hits: " + hitPositions.size();

    }

}