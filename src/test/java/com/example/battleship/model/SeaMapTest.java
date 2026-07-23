package com.example.battleship.model;

import com.example.battleship.model.enums.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeaMapTest {

    private Ship createDestroyer() {
        return new Ship(ShipType.DESTROYER);
    }

    @Test
    void constructorShouldInitializeEmptySeaMap() {

        SeaMap seaMap = new SeaMap();

        assertTrue(seaMap.getShips().isEmpty());
        assertTrue(seaMap.getShots().isEmpty());

    }

    @Test
    void shouldPlaceShipSuccessfully() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        PlacementResult result = seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        assertEquals(PlacementResult.SUCCESS, result);
        assertEquals(1, seaMap.getShips().size());

    }

    @Test
    void placedShipShouldOccupyCells() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        assertTrue(seaMap.isOccupied(new Coordinate(0,0)));
        assertTrue(seaMap.isOccupied(new Coordinate(0,1)));

    }

    @Test
    void shouldNotPlaceShipOutsideBoard() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        PlacementResult result = seaMap.placeShip(
                ship,
                new Coordinate(9,9),
                Orientation.HORIZONTAL);

        assertEquals(PlacementResult.OUT_OF_BOUNDS, result);
        assertTrue(seaMap.getShips().isEmpty());

    }

    @Test
    void shouldDetectOverlap() {

        SeaMap seaMap = new SeaMap();

        Ship ship1 = createDestroyer();
        Ship ship2 = createDestroyer();

        seaMap.placeShip(
                ship1,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        PlacementResult result =
                seaMap.placeShip(
                        ship2,
                        new Coordinate(0,1),
                        Orientation.HORIZONTAL);

        assertEquals(PlacementResult.OVERLAP, result);

    }

    @Test
    void shouldRepositionShipSuccessfully() {

        SeaMap seaMap = new SeaMap();

        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.placeShip(
                ship,
                new Coordinate(5,5),
                Orientation.VERTICAL);

        assertTrue(seaMap.isOccupied(new Coordinate(5,5)));
        assertTrue(seaMap.isOccupied(new Coordinate(6,5)));

        assertFalse(seaMap.isOccupied(new Coordinate(0,0)));

    }

    @Test
    void failedRepositionShouldRestorePreviousPosition() {

        SeaMap seaMap = new SeaMap();

        Ship ship1 = createDestroyer();
        Ship ship2 = createDestroyer();

        seaMap.placeShip(
                ship1,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.placeShip(
                ship2,
                new Coordinate(5,5),
                Orientation.HORIZONTAL);

        PlacementResult result =
                seaMap.placeShip(
                        ship1,
                        new Coordinate(5,5),
                        Orientation.HORIZONTAL);

        assertEquals(PlacementResult.OVERLAP, result);

        assertTrue(seaMap.isOccupied(new Coordinate(0,0)));
        assertTrue(seaMap.isOccupied(new Coordinate(0,1)));

    }

    @Test
    void shouldUnplaceShip() {

        SeaMap seaMap = new SeaMap();

        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.unplaceShip(ship);

        assertTrue(seaMap.getShips().isEmpty());
        assertFalse(seaMap.isOccupied(new Coordinate(0,0)));

    }

    @Test
    void shouldDetectCoordinateInsideBoard() {

        SeaMap seaMap = new SeaMap();

        assertTrue(seaMap.isInsideBoard(new Coordinate(0,0)));
        assertTrue(seaMap.isInsideBoard(new Coordinate(9,9)));

    }

    @Test
    void shouldDetectCoordinateOutsideBoard() {

        SeaMap seaMap = new SeaMap();

        assertFalse(seaMap.isInsideBoard(new Coordinate(-1,0)));
        assertFalse(seaMap.isInsideBoard(new Coordinate(0,-1)));
        assertFalse(seaMap.isInsideBoard(new Coordinate(10,0)));
        assertFalse(seaMap.isInsideBoard(new Coordinate(0,10)));

    }


    @Test
    void attackShouldReturnMissWhenNoShipIsPresent() {

        SeaMap seaMap = new SeaMap();

        assertEquals(
                AttackResult.MISS,
                seaMap.attack(new Coordinate(4,4))
        );

        assertEquals(1, seaMap.getShots().size());

    }

    @Test
    void attackShouldReturnHitWhenShipIsPresent() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        assertEquals(
                AttackResult.HIT,
                seaMap.attack(new Coordinate(0,0))
        );

    }

    @Test
    void attackShouldReturnAlreadyAttackedWhenRepeated() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.attack(new Coordinate(0,0));

        assertEquals(
                AttackResult.ALREADY_ATTACKED,
                seaMap.attack(new Coordinate(0,0))
        );

    }

    @Test
    void lastHitShouldSinkShip() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.attack(new Coordinate(0,0));

        assertEquals(
                AttackResult.SUNK,
                seaMap.attack(new Coordinate(0,1))
        );

    }

    @Test
    void getShipAtShouldReturnShip() {

        SeaMap seaMap = new SeaMap();
        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(2,2),
                Orientation.HORIZONTAL);

        assertEquals(
                ship,
                seaMap.getShipAt(new Coordinate(2,2))
        );

    }

    @Test
    void getShipAtShouldReturnNullWhenCellIsEmpty() {

        SeaMap seaMap = new SeaMap();

        assertNull(
                seaMap.getShipAt(new Coordinate(8,8))
        );

    }

    @Test
    void allShipsSunkShouldReturnFalseWhenNoShipsExist() {

        SeaMap seaMap = new SeaMap();

        assertFalse(seaMap.allShipsSunk());

    }

    @Test
    void allShipsSunkShouldReturnFalseWhenShipStillAlive() {

        SeaMap seaMap = new SeaMap();

        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.attack(new Coordinate(0,0));

        assertFalse(seaMap.allShipsSunk());

    }

    @Test
    void allShipsSunkShouldReturnTrueWhenAllShipsDestroyed() {

        SeaMap seaMap = new SeaMap();

        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.attack(new Coordinate(0,0));
        seaMap.attack(new Coordinate(0,1));

        assertTrue(seaMap.allShipsSunk());

    }

    @Test
    void resetShouldClearBoard() {

        SeaMap seaMap = new SeaMap();

        Ship ship = createDestroyer();

        seaMap.placeShip(
                ship,
                new Coordinate(0,0),
                Orientation.HORIZONTAL);

        seaMap.attack(new Coordinate(0,0));

        seaMap.reset();

        assertTrue(seaMap.getShips().isEmpty());
        assertTrue(seaMap.getShots().isEmpty());

    }

    @Test
    void hasAllShipsPlacedShouldReturnFalseInitially() {

        SeaMap seaMap = new SeaMap();

        assertFalse(seaMap.hasAllShipsPlaced());

    }


}