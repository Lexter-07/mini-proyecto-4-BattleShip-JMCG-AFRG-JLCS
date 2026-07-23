package com.example.battleship.model;

import com.example.battleship.model.*;
import com.example.battleship.model.enums.*;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {


    @Test
    void constructorShouldInitializeShipCorrectly() {
        Ship ship = new Ship(ShipType.DESTROYER);

        assertEquals(ShipType.DESTROYER, ship.getType());
        assertEquals(Orientation.HORIZONTAL, ship.getOrientation());

        assertTrue(ship.getPositions().isEmpty());
        assertTrue(ship.getHitPositions().isEmpty());
    }


    @Test
    void shouldChangeOrientation() {
        Ship ship = new Ship(ShipType.DESTROYER);
        ship.setOrientation(Orientation.VERTICAL);

        assertEquals(Orientation.VERTICAL, ship.getOrientation());
    }


    @Test
    void shouldStorePositions() {
        Ship ship = new Ship(ShipType.DESTROYER);

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        coordinates.add(new Coordinate(1,1));
        coordinates.add(new Coordinate(1,2));

        ship.setPositions(coordinates);
        assertEquals(2, ship.getPositions().size());
    }


    @Test
    void shipShouldNotBePlacedInitially() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(ship.isPlaced());
    }

    @Test
    void shipShouldBePlacedAfterSettingPositions() {

        Ship ship = new Ship(ShipType.DESTROYER);

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        coordinates.add(new Coordinate(0,0));
        coordinates.add(new Coordinate(0,1));

        ship.setPositions(coordinates);

        assertTrue(ship.isPlaced());
    }

    @Test
    void receiveShotOutsideShipShouldReturnMiss() {

        Ship ship = createShip();

        AttackResult result = ship.receiveShot(new Coordinate(5,5));

        assertEquals(AttackResult.MISS,result);
    }


    @Test
    void receiveShotShouldReturnHit() {

        Ship ship = createShip();

        AttackResult result = ship.receiveShot(new Coordinate(0,0));
        assertEquals(AttackResult.HIT,result);
    }


    @Test
    void secondShotSameCoordinateShouldReturnAlreadyAttacked() {

        Ship ship = createShip();
        ship.receiveShot(new Coordinate(0,0));

        AttackResult result =
                ship.receiveShot(new Coordinate(0,0));

        assertEquals(AttackResult.ALREADY_ATTACKED,result);
    }


    @Test
    void lastHitShouldSinkShip() {

        Ship ship = createShip();
        ship.receiveShot(new Coordinate(0,0));

        AttackResult result =
                ship.receiveShot(new Coordinate(0,1));

        assertEquals(AttackResult.SUNK,result);
    }


    @Test
    void shipShouldNotBeSunkInitially() {
        Ship ship = createShip();
        assertFalse(ship.isSunk());
    }


    @Test
    void shipShouldBeSunkAfterAllHits() {

        Ship ship = createShip();

        ship.receiveShot(new Coordinate(0,0));
        ship.receiveShot(new Coordinate(0,1));

        assertTrue(ship.isSunk());
    }


    @Test
    void occupiesShouldReturnTrueForShipPosition() {
        Ship ship = createShip();
        assertTrue(ship.occupies(new Coordinate(0,0)));
    }


    @Test
    void occupiesShouldReturnFalseForOtherPosition() {
        Ship ship = createShip();
        assertFalse(ship.occupies(new Coordinate(8,8)));
    }


    @Test
    void resetShouldRestoreInitialState() {

        Ship ship = createShip();

        ship.receiveShot(new Coordinate(0,0));
        ship.setOrientation(Orientation.VERTICAL);

        ship.reset();

        assertEquals(Orientation.HORIZONTAL, ship.getOrientation());
        assertTrue(ship.getPositions().isEmpty());
        assertTrue(ship.getHitPositions().isEmpty());
    }


    @Test
    void getStartCoordinateShouldReturnFirstCoordinate() {
        Ship ship = createShip();
        assertEquals(new Coordinate(0,0), ship.getStartCoordinate());
    }


    @Test
    void getStartCoordinateShouldReturnNullWhenNotPlaced() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertNull(ship.getStartCoordinate());
    }



    private Ship createShip() {

        Ship ship = new Ship(ShipType.DESTROYER);

        ArrayList<Coordinate> positions = new ArrayList<>();
        positions.add(new Coordinate(0,0));
        positions.add(new Coordinate(0,1));

        ship.setPositions(positions);

        return ship;
    }


}
