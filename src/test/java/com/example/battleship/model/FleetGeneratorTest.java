package com.example.battleship.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetGeneratorTest {

    @Test
    void createFleetShouldCreateTenShips() {
        List<Ship> fleet = FleetGenerator.createFleet();

        assertEquals(10, fleet.size());
    }

    @Test
    void generatedFleetShouldPlaceAllShips() {
        SeaMap seaMap = new SeaMap();

        FleetGenerator.generate(
                seaMap,
                FleetGenerator.createFleet()
        );

        assertEquals(
                10,
                seaMap.getShips().size()
        );
    }

    @Test
    void generatedFleetShouldBeReady() {
        SeaMap seaMap = new SeaMap();

        FleetGenerator.generate(
                seaMap,
                FleetGenerator.createFleet()
        );

        assertTrue(seaMap.hasAllShipsPlaced());
    }

    @Test
    void generatedShipsShouldBeInsideBoard() {
        SeaMap seaMap = new SeaMap();

        FleetGenerator.generate(
                seaMap,
                FleetGenerator.createFleet()
        );

        for (Ship ship : seaMap.getShips()) {

            for (Coordinate coordinate : ship.getPositions()) {

                assertTrue(
                        seaMap.isInsideBoard(coordinate)
                );
            }
        }
    }

    @Test
    void generatedShipsShouldNotOverlap() {
        SeaMap seaMap = new SeaMap();

        FleetGenerator.generate(
                seaMap,
                FleetGenerator.createFleet()
        );

        int occupiedCells = 0;

        for (Ship ship : seaMap.getShips()) {
            occupiedCells += ship.getPositions().size();
        }

        int uniqueCells = 0;

        for (int row = 0; row < SeaMap.ROWS; row++) {
            for (int column = 0; column < SeaMap.COLUMNS; column++) {
                if (seaMap.isOccupied(new Coordinate(row, column))) {
                    uniqueCells++;
                }
            }
        }

        assertEquals(occupiedCells, uniqueCells);
    }

}