package com.example.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void constructorShouldInitializePlayerCorrectly() {

        Player player = new Player("Jorge", true);

        assertEquals("Jorge", player.getNickname());
        assertTrue(player.isHuman());
        assertNotNull(player.getSeaMap());

    }

    @Test
    void machinePlayerShouldNotBeHuman() {

        Player player = new Player("CPU", false);

        assertFalse(player.isHuman());

    }

    @Test
    void seaMapShouldAlwaysExist() {

        Player player = new Player("Jorge", true);

        assertNotNull(player.getSeaMap());

    }

    @Test
    void toStringShouldIdentifyHumanPlayer() {

        Player player = new Player("Jorge", true);

        assertEquals("Jorge (Human)", player.toString());

    }

    @Test
    void toStringShouldIdentifyMachinePlayer() {

        Player player = new Player("CPU", false);

        assertEquals("CPU (Machine)", player.toString());

    }

}