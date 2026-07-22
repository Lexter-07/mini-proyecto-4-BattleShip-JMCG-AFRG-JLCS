package com.example.battleship.model;

import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.PlacementResult;
import com.example.battleship.model.enums.ShipType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera automáticamente una flota válida sobre un tablero.
 */
public final class FleetGenerator {

    private static final Random RANDOM = new Random();

    private FleetGenerator() {
    }

    private static final ShipType[] FLEET = {

            ShipType.AIRCRAFT,

            ShipType.SUBMARINE,
            ShipType.SUBMARINE,

            ShipType.DESTROYER,
            ShipType.DESTROYER,
            ShipType.DESTROYER,

            ShipType.FRIGATE,
            ShipType.FRIGATE,
            ShipType.FRIGATE,
            ShipType.FRIGATE
    };


    public static List<Ship> createFleet() {

        List<Ship> ships = new ArrayList<>();

        for (ShipType type : FLEET) {
            ships.add(new Ship(type));
        }

        return ships;
    }


    /**
     * Coloca automáticamente una lista de barcos.
     */
    public static void generate(
            SeaMap seaMap,
            List<Ship> ships) {

        seaMap.reset();

        for (Ship ship : ships) {
            placeShip(seaMap, ship);
        }

    }

    /**
     * Coloca un barco aleatoriamente.
     */
    private static void placeShip(
            SeaMap seaMap,
            Ship ship) {

        PlacementResult result;

        do {

            Coordinate coordinate = randomCoordinate();

            Orientation orientation = randomOrientation();

            result = seaMap.placeShip(
                    ship,
                    coordinate,
                    orientation
            );

        } while (result != PlacementResult.SUCCESS);

    }

    /**
     * Coordenada aleatoria.
     */
    private static Coordinate randomCoordinate() {

        return new Coordinate(
                RANDOM.nextInt(SeaMap.ROWS),
                RANDOM.nextInt(SeaMap.COLUMNS)
        );

    }

    /**
     * Orientación aleatoria.
     */
    private static Orientation randomOrientation() {

        return RANDOM.nextBoolean()
                ? Orientation.HORIZONTAL
                : Orientation.VERTICAL;

    }

}