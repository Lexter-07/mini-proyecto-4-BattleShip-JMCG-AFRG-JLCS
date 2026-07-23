package com.example.battleship.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceManagerTest {

    private static final String NICKNAME = "JUnitPlayer";

    @AfterEach
    void cleanUp() {
        PersistenceManager.deleteSave(NICKNAME);
    }

    @Test
    void saveShouldNotExistInitially() {
        PersistenceManager.deleteSave(NICKNAME);

        assertFalse(
                PersistenceManager.saveExists(NICKNAME)
        );
    }

    @Test
    void saveGameShouldCreateSaveFile() {
        GameModel model = new GameModel();
        model.newGame(NICKNAME);

        PersistenceManager.saveGame(
                model.getGame(),
                NICKNAME
        );

        assertTrue(
                PersistenceManager.saveExists(NICKNAME)
        );
    }

    @Test
    void loadGameShouldRestoreGameStatus() {
        GameModel model = new GameModel();
        model.newGame(NICKNAME);

        PersistenceManager.saveGame(
                model.getGame(),
                NICKNAME
        );

        GameStatus loaded =
                PersistenceManager.loadGame(NICKNAME);

        assertNotNull(loaded);
        assertEquals(
                NICKNAME,
                loaded.getHumanPlayer().getNickname()
        );
    }

    @Test
    void deleteSaveShouldRemoveSaveFiles() {
        GameModel model = new GameModel();
        model.newGame(NICKNAME);

        PersistenceManager.saveGame(
                model.getGame(),
                NICKNAME
        );

        PersistenceManager.deleteSave(NICKNAME);

        assertFalse(
                PersistenceManager.saveExists(NICKNAME)
        );
    }

    @Test
    void loadNonExistingSaveShouldReturnNull() {
        PersistenceManager.deleteSave(NICKNAME);

        GameStatus loaded =
                PersistenceManager.loadGame(NICKNAME);

        assertNull(loaded);
    }

    @Test
    void saveGameShouldCreateFlatFile() {
        GameModel model = new GameModel();
        model.newGame(NICKNAME);

        PersistenceManager.saveGame(
                model.getGame(),
                NICKNAME
        );

        File file = new File(
                "saves/" + NICKNAME + "_info.txt"
        );

        assertTrue(file.exists());
    }


}