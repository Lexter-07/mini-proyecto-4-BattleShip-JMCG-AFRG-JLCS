package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.PlacementResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Representa el tablero de un jugador.
 * Administra la colocación de barcos y el historial de disparos.
 */
public class SeaMap implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int ROWS = 10;
    public static final int COLUMNS = 10;

    private final HashMap<Coordinate, Ship> occupiedCells;
    private final ArrayList<Ship> ships;
    private final LinkedList<Shot> shots;

    /**
     * Constructor.
     */
    public SeaMap() {
        occupiedCells = new HashMap<>();
        ships = new ArrayList<>();
        shots = new LinkedList<>();
    }

    /**
     * Retorna una copia de la lista de barcos.
     */
    public ArrayList<Ship> getShips() {
        return new ArrayList<>(ships);
    }

    /**
     * Retorna una copia del historial de disparos.
     */
    public LinkedList<Shot> getShots() {
        return new LinkedList<>(shots);
    }

    /**
     * Coloca o recoloca un barco.
     * <p>
     * Si el barco ya estaba colocado, se eliminan temporalmente sus casillas.
     * Si la nueva posición no es válida, el barco vuelve automáticamente
     * a su posición anterior.
     */
    public PlacementResult placeShip(
            Ship ship,
            Coordinate start,
            Orientation orientation) {

        // Guardar estado anterior
        ArrayList<Coordinate> previousPositions =
                new ArrayList<>(ship.getPositions());

        Orientation previousOrientation = ship.getOrientation();

        // Liberar casillas anteriores
        freeCells(ship);

        PlacementResult result =
                canPlaceShip(ship, start, orientation);

        // !!
        if (result != PlacementResult.SUCCESS) {

            // Restore the last ships state
            ship.setOrientation(previousOrientation);
            ship.setPositions(previousPositions);

            occupyCells(ship);

            return result;
        }

        // Calcular posiciones nuevas
        ArrayList<Coordinate> newPositions =
                calculatePositions(ship.getSize(), start, orientation);

        // Actualizar barco
        ship.setOrientation(orientation);
        ship.setPositions(newPositions);

        // Actualizar mapa
        occupyCells(ship);

        if (!ships.contains(ship)) {
            ships.add(ship);
        }

        return PlacementResult.SUCCESS;
    }

    /**
     * Retira un barco del tablero.
     * El barco continúa existiendo, pero ya no ocupa casillas.
     */
    public void unplaceShip(Ship ship) {

        freeCells(ship);
        ship.clearPositions();
        ship.resetHits();
        ships.remove(ship);
    }

    /**
     * Verifica si un barco puede colocarse.
     */
    public PlacementResult canPlaceShip(
            Ship ship,
            Coordinate start,
            Orientation orientation) {

        ArrayList<Coordinate> positions =
                calculatePositions(ship.getSize(), start, orientation);

        if (positions == null) {
            return PlacementResult.OUT_OF_BOUNDS;
        }

        for (Coordinate coordinate : positions) {
            Ship occupyingShip = occupiedCells.get(coordinate);

            if (occupyingShip != null &&
                    occupyingShip != ship) {

                return PlacementResult.OVERLAP;
            }
        }

        return PlacementResult.SUCCESS;
    }


    /**
     * Calcula las posiciones que ocuparía un barco.
     *
     * @param shipSize Tamaño del barco.
     * @param start Casilla inicial.
     * @param orientation Orientación del barco.
     * @return Lista de coordenadas o null si el barco sale del tablero.
     */
    private ArrayList<Coordinate> calculatePositions(
            int shipSize,
            Coordinate start,
            Orientation orientation) {

        ArrayList<Coordinate> positions = new ArrayList<>();

        for (int i = 0; i < shipSize; i++) {
            Coordinate coordinate;

            if (orientation == Orientation.HORIZONTAL) {

                coordinate = new Coordinate(
                        start.getRow(),
                        start.getColumn() + i
                );

            } else {
                coordinate = new Coordinate(
                        start.getRow() + i,
                        start.getColumn()
                );
            }

            if (!isInsideBoard(coordinate)) return null;


            positions.add(coordinate);
        }

        return positions;

    }

    /**
     * Marca las casillas ocupadas por un barco.
     */
    private void occupyCells(Ship ship) {

        for (Coordinate coordinate : ship.getPositions()) {
            occupiedCells.put(coordinate, ship);
        }
    }

    /**
     * Libera todas las casillas ocupadas por un barco.
     */
    private void freeCells(Ship ship) {

        for (Coordinate coordinate : ship.getPositions()) {
            occupiedCells.remove(coordinate);
        }
    }

    /**
     * Verifica si una coordenada pertenece al tablero.
     */
    public boolean isInsideBoard(Coordinate coordinate) {

        return coordinate.getRow() >= 0 &&
                coordinate.getRow() < ROWS &&
                coordinate.getColumn() >= 0 &&
                coordinate.getColumn() < COLUMNS;

    }

    /**
     * Indica si una casilla está ocupada.
     */
    public boolean isOccupied(Coordinate coordinate) {
        return occupiedCells.containsKey(coordinate);
    }

// ########

    /**
     * Realiza un ataque sobre una coordenada.
     *
     * @param coordinate Coordenada atacada.
     * @return Resultado del ataque.
     */
    public AttackResult attack(Coordinate coordinate) {

        // Verificar si ya se disparó anteriormente
        for (Shot shot : shots) {
            if (shot.getCoordinate().equals(coordinate)) {
                return AttackResult.ALREADY_ATTACKED;
            }
        }

        Ship ship = occupiedCells.get(coordinate);
        AttackResult result;

        if (ship == null) {
            result = AttackResult.MISS;
        } else {
            result = ship.receiveShot(coordinate);
        }

        shots.add(new Shot(coordinate, result));
        return result;
    }


    public Ship getShipAt(Coordinate coordinate) {
        return occupiedCells.get(coordinate);
    }


    /**
     * Verifica si todos los barcos han sido hundidos.
     *
     * @return true si todos están hundidos.
     */
    public boolean allShipsSunk() {

        if (ships.isEmpty()) return false;

        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }

        return true;
    }

    /**
     * Reinicia completamente el tablero.
     */
    public void reset() {

        occupiedCells.clear();
        shots.clear();

        for (Ship ship : ships) {
            ship.reset();
        }

        ships.clear();
    }

    public boolean hasAllShipsPlaced() {
        return ships.size() == 10;
    }

}