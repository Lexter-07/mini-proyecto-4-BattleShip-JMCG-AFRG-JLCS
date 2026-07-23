package com.example.battleship.model;

import com.example.battleship.model.enums.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    private Ship createDestroyer() {
        return new Ship(ShipType.DESTROYER);
    }

    @Test
    void newGameShouldCreateGameStatus() {
        GameModel model = new GameModel();

        model.newGame("Jorge");

        assertNotNull(model.getGame());
        assertNotNull(model.getHumanPlayer());
        assertNotNull(model.getMachinePlayer());
    }

    @Test
    void newGameShouldStoreNickname() {
        GameModel model = new GameModel();
        model.newGame("Andres");

        assertEquals(
                "Andres",
                model.getHumanPlayer().getNickname()
        );
    }

    @Test
    void gameShouldNotStartAutomatically() {
        GameModel model = new GameModel();

        model.newGame("Jose");

        assertFalse(model.isGameStarted());
    }

    @Test
    void startGameShouldMarkGameAsStarted() {
        GameModel model = new GameModel();

        model.newGame("Jose");
        model.startGame();

        assertTrue(model.isGameStarted());
    }

    @Test
    void finishGameShouldMarkGameAsFinished() {
        GameModel model = new GameModel();

        model.newGame("Jorge");
        model.finishGame();

        assertTrue(model.isGameFinished());
    }

    @Test
    void shouldPlaceHumanShip() {

        GameModel model = new GameModel();

        model.newGame("Eusebio");

        Ship ship = createDestroyer();

        PlacementResult result =
                model.placeShip(
                        ship,
                        new Coordinate(0,0),
                        Orientation.HORIZONTAL);

        assertEquals(
                PlacementResult.SUCCESS,
                result
        );

    }

    @Test
    void shouldUnplaceHumanShip() {

        GameModel model = new GameModel();
        model.newGame("Marta lucia");

        Ship ship = createDestroyer();

        model.placeShip(ship, new Coordinate(0,0), Orientation.HORIZONTAL);
        model.unplaceShip(ship);

        assertTrue(
                model.getHumanPlayer()
                        .getSeaMap()
                        .getShips()
                        .isEmpty()
        );

    }

    @Test
    void autoPlaceHumanFleetShouldPlaceAllShips() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        List<Ship> fleet = FleetGenerator.createFleet();

        model.autoPlaceHumanFleet(fleet);

        assertEquals(
                10,
                model.getHumanPlayer()
                        .getSeaMap()
                        .getShips()
                        .size()
        );

    }



    @Test
    void humanAttackShouldReturnMissWhenNoShipExists() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        AttackResult result = model.humanAttack(new Coordinate(9, 9));

        assertEquals(AttackResult.MISS, result);

    }

    @Test
    void machineAttackShouldReturnMissWhenNoShipExists() {

        GameModel model = new GameModel();
        model.newGame("Vozinha");

        AttackResult result = model.machineAttack(new Coordinate(9, 9));

        assertEquals(AttackResult.MISS, result);

    }

    @Test
    void humanAttackShouldHitPlacedShip() {

        GameModel model = new GameModel();
        model.newGame("Jamilton Campaz");

        Ship ship = createDestroyer();

        model.getMachinePlayer()
                .getSeaMap()
                .placeShip(
                        ship,
                        new Coordinate(0, 0),
                        Orientation.HORIZONTAL);

        AttackResult result =
                model.humanAttack(new Coordinate(0, 0));

        assertEquals(AttackResult.HIT, result);

    }

    @Test
    void machineAttackShouldHitPlacedShip() {

        GameModel model = new GameModel();
        model.newGame("Andres");

        Ship ship = createDestroyer();

        model.getHumanPlayer()
                .getSeaMap()
                .placeShip(
                        ship,
                        new Coordinate(0, 0),
                        Orientation.HORIZONTAL);

        AttackResult result =
                model.machineAttack(new Coordinate(0, 0));

        assertEquals(AttackResult.HIT, result);

    }

    @Test
    void humanMissShouldChangeTurn() {

        GameModel model = new GameModel();
        model.newGame("Jorge");

        boolean previousTurn = model.isHumanTurn();

        model.humanAttack(new Coordinate(9, 9));

        assertNotEquals(previousTurn, model.isHumanTurn());

    }

    @Test
    void machineMissShouldChangeTurn() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        boolean previousTurn = model.isHumanTurn();

        model.machineAttack(new Coordinate(9, 9));

        assertNotEquals(previousTurn, model.isHumanTurn());

    }

    @Test
    void manualChangeTurnShouldToggleCurrentPlayer() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        boolean previousTurn = model.isHumanTurn();

        model.changeTurn();

        assertNotEquals(previousTurn, model.isHumanTurn());

    }

    @Test
    void humanReadyShouldReturnFalseInitially() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        assertFalse(model.humanReady());

    }

    @Test
    void machineReadyShouldReturnTrueAfterNewGame() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        assertTrue(model.machineReady());

    }

    @Test
    void canStartGameShouldReturnFalseWhenHumanFleetMissing() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        assertFalse(model.canStartGame());

    }

    @Test
    void canStartGameShouldReturnTrueWhenBothPlayersAreReady() {

        GameModel model = new GameModel();
        model.newGame("Ferran");

        model.autoPlaceHumanFleet(FleetGenerator.createFleet());

        assertTrue(model.canStartGame());

    }


}