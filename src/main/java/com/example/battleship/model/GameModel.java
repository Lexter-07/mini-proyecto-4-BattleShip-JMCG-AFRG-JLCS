package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.PlacementResult;
import com.example.battleship.model.enums.ShipType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Coordina toda la lógica del juego.
 */
public class GameModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Random random = new Random();
    private GameStatus gameStatus;

    /**
     * Constructor vacío.
     */
    public GameModel() {
    }

    /**
     * Create a new game.
     *
     * @param nickname Nombre del jugador.
     */
    public void newGame(String nickname) {
        gameStatus = new GameStatus(nickname);

        FleetGenerator.generate(
                gameStatus.getMachinePlayer().getSeaMap(), FleetGenerator.createFleet());
    }

    // ===========================
    // Getters
    // ===========================

    public GameStatus getGame() {
        return gameStatus;
    }

    public Player getHumanPlayer() {
        return gameStatus.getHumanPlayer();
    }

    public Player getMachinePlayer() {
        return gameStatus.getMachinePlayer();
    }

    public boolean isHumanTurn() {
        return gameStatus.isHumanTurn();
    }

    public boolean isGameStarted() {
        return gameStatus.isGameStarted();
    }

    public boolean isGameFinished() {
        return gameStatus.isGameFinished();
    }

    // ===========================
    // Gestión de partida
    // ===========================

    /**
     * Reinicia la partida actual.
     */
    public void reset() {
        if (gameStatus != null) gameStatus.reset();
    }

    /**
     * Start the game.
     */
    public void startGame() {
        gameStatus.setGameStarted(true);
    }

    /**
     * Ends Game.
     */
    public void finishGame() {
        gameStatus.setGameFinished(true);
    }


    // ===========================
    // Management of ships
    // ===========================

    /**
     * Coloca o mueve un barco del jugador humano.
     */
    public PlacementResult placeShip(
            Ship ship,
            Coordinate start,
            Orientation orientation) {

        return gameStatus.getHumanPlayer()
                .getSeaMap()
                .placeShip(ship, start, orientation);
    }


    /**
     * Coloca automáticamente la flota del jugador humano.
     */
    public void autoPlaceHumanFleet(List<Ship> ships) {
        FleetGenerator.generate(
                gameStatus.getHumanPlayer().getSeaMap(),
                ships);
    }

    /**
     * Retira un barco del tablero.
     */
    public void unplaceShip(Ship ship) {
        gameStatus.getHumanPlayer()
                .getSeaMap()
                .unplaceShip(ship);
    }


    // ===========================
    // Attacks
    // ===========================

    /**
     * The human shots to machine's Board.
     * Si atina, the turns continue.
     * if fails, the turn change.
     */
    public AttackResult humanAttack(Coordinate coordinate) {

        AttackResult result = gameStatus
                .getMachinePlayer()
                .getSeaMap()
                .attack(coordinate);

        if (result == AttackResult.MISS) {
            gameStatus.changeTurn();
        }
        return result;
    }


    /**
     * La Maquina shoots aleatoariamente to Player Board.
     */
    public AttackResult machineAttack(Coordinate coordinate) {
        AttackResult result = gameStatus
                .getHumanPlayer()
                .getSeaMap()
                .attack(coordinate);

        if (result == AttackResult.MISS) {
            gameStatus.changeTurn();
        }

        return result;
    }


    /**
     * Cambia el turno manualmente.
     */
    public void changeTurn() {
        gameStatus.changeTurn();
    }


    // ===========================
    // Validations & Setup
    // ===========================

    /**
     * Verifica si el jugador humano ya colocó todos sus barcos.
     */
    public boolean humanReady() {
        return gameStatus.getHumanPlayer()
                .getSeaMap()
                .getShips()
                .size() == 10;
    }


    // h
    // h
    // h
    // h




    /**
     * Validate if machine ya colocó todos sus barcos.
     */
    public boolean machineReady() {
        return gameStatus.getMachinePlayer()
                .getSeaMap()
                .getShips()
                .size() == 10;
    }

    public Ship getMachineShipAt(Coordinate coordinate) {
        return gameStatus
                .getMachinePlayer()
                .getSeaMap()
                .getShipAt(coordinate);
    }

    /**
     * Validates if the game, can start.
     */
    public boolean canStartGame() {
        return humanReady() && machineReady();
    }

    /**
     * Comprueba si existe un ganador.
     *
     * @return El jugador ganador o null si la partida continúa.
     */
    public Player checkWinner() {
        if (gameStatus.getMachinePlayer().getSeaMap().allShipsSunk()) {
            finishGame();
            return gameStatus.getHumanPlayer();
        }

        if (gameStatus.getHumanPlayer().getSeaMap().allShipsSunk()) {
            finishGame();
            return gameStatus.getMachinePlayer();
        }
        return null;
    }

    /**
     * Indica si la partida terminó.
     */
    public boolean hasWinner() {
        return checkWinner() != null;
    }

    // ===========================
    // SAVE THE GAME
    // ===========================

    /**
     * Saves the current game state to the disk using the player's nickname.
     */
    public void saveGame() {
        if (gameStatus != null && gameStatus.getHumanPlayer() != null) {
            String nickname = gameStatus.getHumanPlayer().getNickname();
            PersistenceManager.saveGame(this.gameStatus, nickname);
        }
    }

    /**
     * Loads a game state from the disk for a given nickname.
     *
     * @param nickname The identifier of the player.
     */
    public void loadGame(String nickname) {
        GameStatus loadedStatus = PersistenceManager.loadGame(nickname);
        if (loadedStatus != null) {
            this.gameStatus = loadedStatus;
        }
    }

    /**
     * Injects an already loaded GameStatus directly.
     *
     * @param status The restored GameStatus.
     */
    public void setGameStatus(GameStatus status) {
        this.gameStatus = status;
    }
}