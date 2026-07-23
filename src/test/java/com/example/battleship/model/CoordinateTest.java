package com.example.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Test
    void constructorShouldStoreRowAndColumn() {

        Coordinate coordinate = new Coordinate(3,5);

        assertEquals(3, coordinate.getRow());
        assertEquals(5, coordinate.getColumn());
    }

    @Test
    void equalCoordinatesShouldBeEqual() {

        Coordinate c1 = new Coordinate(2,4);
        Coordinate c2 = new Coordinate(2,4);

        assertEquals(c1,c2);
    }

    @Test
    void differentCoordinatesShouldNotBeEqual() {

        Coordinate c1 = new Coordinate(2,4);
        Coordinate c2 = new Coordinate(4,2);

        assertNotEquals(c1,c2);
    }

    @Test
    void coordinateShouldBeEqualToItself() {

        Coordinate coordinate = new Coordinate(7,1);

        assertEquals(coordinate,coordinate);
    }

    @Test
    void coordinateShouldNotEqualNull() {

        Coordinate coordinate = new Coordinate(1,1);

        assertNotEquals(null,coordinate);
    }

    @Test
    void coordinateShouldNotEqualDifferentObject() {

        Coordinate coordinate = new Coordinate(1,1);

        assertNotEquals("hola",coordinate);
    }

    @Test
    void equalCoordinatesShouldHaveSameHashCode() {

        Coordinate c1 = new Coordinate(5,6);
        Coordinate c2 = new Coordinate(5,6);

        assertEquals(c1.hashCode(),c2.hashCode());
    }

    @Test
    void toStringShouldReturnCoordinateFormat() {

        Coordinate coordinate = new Coordinate(4,8);

        assertEquals("(4, 8)",coordinate.toString());
    }

}