package com.example.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void shouldStoreNickname() {
        Session.setCurrentNickname("Ferran");

        assertEquals("Ferran", Session.getCurrentNickname()
        );
    }

    @Test
    void shouldReplaceNickname() {

        Session.setCurrentNickname("Ana");
        Session.setCurrentNickname("Carlos");

        assertEquals(
                "Carlos",
                Session.getCurrentNickname()
        );
    }

    @Test
    void shouldAcceptNullNickname() {

        Session.setCurrentNickname(null);

        assertNull(
                Session.getCurrentNickname()
        );
    }

}