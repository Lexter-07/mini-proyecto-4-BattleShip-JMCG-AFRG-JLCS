package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ShotTest {

    @Test
    void constructorShouldStoreCoordinateAndResult() {

        Coordinate coordinate = new Coordinate(3, 4);

        Shot shot = new Shot(coordinate, AttackResult.HIT);

        assertEquals(coordinate, shot.getCoordinate());
        assertEquals(AttackResult.HIT, shot.getResult());

    }

    @Test
    void timestampShouldBeGeneratedAutomatically() {

        Shot shot = new Shot(
                new Coordinate(0, 0),
                AttackResult.MISS);

        assertNotNull(shot.getTimestamp());

    }

    @Test
    void timestampShouldBeCreatedDuringConstruction() {

        LocalDateTime before = LocalDateTime.now();

        Shot shot = new Shot(
                new Coordinate(1, 1),
                AttackResult.HIT);

        LocalDateTime after = LocalDateTime.now();

        assertFalse(shot.getTimestamp().isBefore(before));
        assertFalse(shot.getTimestamp().isAfter(after));

    }

    @Test
    void toStringShouldContainCoordinate() {

        Shot shot = new Shot(
                new Coordinate(5, 6),
                AttackResult.SUNK);

        assertTrue(shot.toString().contains("(5, 6)"));

    }

    @Test
    void toStringShouldContainAttackResult() {

        Shot shot = new Shot(
                new Coordinate(5, 6),
                AttackResult.SUNK);

        assertTrue(shot.toString().contains("SUNK"));

    }

}