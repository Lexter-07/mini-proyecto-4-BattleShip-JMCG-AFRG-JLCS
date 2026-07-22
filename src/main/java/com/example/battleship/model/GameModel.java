package com.example.battleship.model;

import com.example.battleship.model.enums.AttackResult;
import com.example.battleship.model.enums.Orientation;
import com.example.battleship.model.enums.PlacementResult;
import com.example.battleship.model.enums.ShipType;

import java.io.Serializable;
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
        createMachineFleet();

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


    // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4


    // Management of ships

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
     * aca se puede implementar o reemplazar despues con una clase IA o algo asi
     * if the machine fails, eseasl turn pasa al humano.
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

    private void createMachineFleet() {

        placeMachineShip(ShipType.AIRCRAFT);

        placeMachineShip(ShipType.SUBMARINE);
        placeMachineShip(ShipType.SUBMARINE);

        placeMachineShip(ShipType.DESTROYER);
        placeMachineShip(ShipType.DESTROYER);
        placeMachineShip(ShipType.DESTROYER);

        placeMachineShip(ShipType.FRIGATE);
        placeMachineShip(ShipType.FRIGATE);
        placeMachineShip(ShipType.FRIGATE);
        placeMachineShip(ShipType.FRIGATE);

    }


    private void placeMachineShip(ShipType type) {

        Ship ship = new Ship(type);
        PlacementResult result;

        do {

            int row = random.nextInt(SeaMap.ROWS);
            int column = random.nextInt(SeaMap.COLUMNS);

            Orientation orientation;

            if (random.nextBoolean()) {
                orientation = Orientation.HORIZONTAL;
            } else {
                orientation = Orientation.VERTICAL;
            }

            result = gameStatus
                    .getMachinePlayer()
                    .getSeaMap()
                    .placeShip(
                            ship,
                            new Coordinate(row, column),
                            orientation
                    );

        } while (result != PlacementResult.SUCCESS);

    }


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
        if (gameStatus.getMachinePlayer()
                .getSeaMap()
                .allShipsSunk()) {

            finishGame();
            return gameStatus.getHumanPlayer();
        }

        if (gameStatus.getHumanPlayer()
                .getSeaMap()
                .allShipsSunk()) {

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
     * Guardar la partidq.
     */
    public void saveGame() {
        // Se implementa despues con una clase savegame
    }

    /**
     * Cargar la partida.
     */
    public void loadGame() {
        // Se implementa despues con una clase Savegame.
    }


}
