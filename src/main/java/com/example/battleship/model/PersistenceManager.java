package com.example.battleship.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages the persistence of the game, including serialization of the game state
 * and flat file storage for simple user statistics.
 */
public class PersistenceManager {

    private static final String SAVES_DIRECTORY = "saves/";
    private static final String SERIALIZED_EXTENSION = "_save.dat";
    private static final String FLAT_FILE_EXTENSION = "_info.txt";

    /**
     * Ensures the save directory exists.
     */
    private static void ensureDirectoryExists() {
        File directory = new File(SAVES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Checks if a save file exists for a given nickname.
     *
     * @param nickname The player's nickname.
     * @return true if the save file exists, false otherwise.
     */
    public static boolean saveExists(String nickname) {
        File file = new File(SAVES_DIRECTORY + nickname + SERIALIZED_EXTENSION);
        return file.exists();
    }

    /**
     * Deletes the saved game and associated flat files for a specific user.
     *
     * @param nickname The player's nickname.
     */
    public static void deleteSave(String nickname) {
        File serializedFile = new File(SAVES_DIRECTORY + nickname + SERIALIZED_EXTENSION);
        File flatFile = new File(SAVES_DIRECTORY + nickname + FLAT_FILE_EXTENSION);

        if (serializedFile.exists()) serializedFile.delete();
        if (flatFile.exists()) flatFile.delete();
    }

    /**
     * Saves the game status using serialization and simple info using flat files.
     *
     * @param gameStatus The current state of the game.
     * @param nickname The player's nickname.
     */
    public static void saveGame(GameStatus gameStatus, String nickname) {
        ensureDirectoryExists();

        // 1. Serialize game state and boards
        String serializedPath = SAVES_DIRECTORY + nickname + SERIALIZED_EXTENSION;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serializedPath))) {
            out.writeObject(gameStatus);
        } catch (IOException e) {
            System.err.println("Error serializing game state: " + e.getMessage());
        }

        // 2. Save flat file with basic information
        String flatFilePath = SAVES_DIRECTORY + nickname + FLAT_FILE_EXTENSION;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(flatFilePath))) {
            writer.write("Nickname: " + nickname);
            writer.newLine();

            int humanSunkShips = countSunkShips(gameStatus.getHumanPlayer().getSeaMap());
            int machineSunkShips = countSunkShips(gameStatus.getMachinePlayer().getSeaMap());

            writer.write("Human Sunk Ships: " + humanSunkShips);
            writer.newLine();
            writer.write("Machine Sunk Ships: " + machineSunkShips);
            writer.newLine();
            writer.write("Human Turn: " + gameStatus.isHumanTurn());
        } catch (IOException e) {
            System.err.println("Error writing flat file: " + e.getMessage());
        }
    }

    /**
     * Loads a serialized game status.
     *
     * @param nickname The player's nickname.
     * @return The restored GameStatus object, or null if it fails.
     */
    public static GameStatus loadGame(String nickname) {
        String serializedPath = SAVES_DIRECTORY + nickname + SERIALIZED_EXTENSION;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializedPath))) {
            return (GameStatus) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to count how many ships are sunk on a given board.
     *
     * @param map The SeaMap to evaluate.
     * @return The number of sunk ships.
     */
    private static int countSunkShips(SeaMap map) {
        int count = 0;
        for (Ship ship : map.getShips()) {
            if (ship.isSunk()) {
                count++;
            }
        }
        return count;
    }
}