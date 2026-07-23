package com.example.battleship.model.threads;

import com.example.battleship.model.Coordinate;
import com.example.battleship.model.GameModel;
import com.example.battleship.model.enums.AttackResult;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;

/**
 * Thread responsible for executing the AI's turns concurrently.
 * Implementa un algoritmo "Hunt and Target" para ser más eficiente.
 */
public class IAThread extends Thread {

    private final GameModel gameModel;
    private volatile boolean running;

    // Lista de disparos aleatorios (Modo Búsqueda)
    private final List<Coordinate> availableShots;

    // Pila de disparos prioritarios cercanos a un impacto (Modo Apuntar)
    private final Stack<Coordinate> huntingTargets;

    private BiConsumer<Coordinate, AttackResult> onUIAttackUpdate;

    public IAThread(GameModel gameModel) {
        this.gameModel = gameModel;
        this.running = true;
        this.availableShots = new ArrayList<>();
        this.huntingTargets = new Stack<>();
        initializeShots();
    }

    public void setOnUIAttackUpdate(BiConsumer<Coordinate, AttackResult> onUIAttackUpdate) {
        this.onUIAttackUpdate = onUIAttackUpdate;
    }

    private void initializeShots() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                availableShots.add(new Coordinate(r, c));
            }
        }
        Collections.shuffle(availableShots);
    }

    public void stopThread() {
        this.running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running && !gameModel.isGameFinished()) {
            if (!gameModel.isHumanTurn()) {
                try {
                    Thread.sleep(1200); // Tiempo de "pensamiento"
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!running || gameModel.isGameFinished() || (availableShots.isEmpty() && huntingTargets.isEmpty())) break;

                // ==========================================
                // LÓGICA DE SELECCIÓN DE DISPARO
                // ==========================================
                Coordinate shot;

                // Si hay objetivos prioritarios, atacamos ahí primero
                if (!huntingTargets.isEmpty()) {
                    shot = huntingTargets.pop();
                } else {
                    // Si no, volvemos a buscar aleatoriamente
                    shot = availableShots.remove(0);
                }

                AttackResult result = gameModel.machineAttack(shot);

                // ==========================================
                // EVALUACIÓN POST-DISPARO
                // ==========================================
                if (result == AttackResult.HIT) {
                    // Si impactamos, buscamos a su alrededor
                    addAdjacentTargets(shot);
                } else if (result == AttackResult.SUNK) {
                    // Si hundimos el barco, cancelamos la búsqueda focalizada
                    huntingTargets.clear();
                }

                if (onUIAttackUpdate != null) {
                    Platform.runLater(() -> onUIAttackUpdate.accept(shot, result));
                }

            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Calcula los 4 puntos cardinales y los añade a la pila si son válidos.
     */
    private void addAdjacentTargets(Coordinate center) {
        int r = center.getRow();
        int c = center.getColumn();

        // Direcciones: Norte, Sur, Oeste, Este
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];

            // Verificamos límites del tablero (10x10)
            if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10) {

                // Extraemos la coordenada de los tiros disponibles (si aún no fue atacada)
                Coordinate validTarget = extractFromAvailable(nr, nc);

                if (validTarget != null) {
                    huntingTargets.push(validTarget);
                }
            }
        }
    }

    /**
     * Busca y elimina una coordenada de la lista de tiros aleatorios.
     * Retorna la coordenada si existía (lo que asegura que no disparamos dos veces al mismo sitio).
     */
    private Coordinate extractFromAvailable(int r, int c) {
        for (int i = 0; i < availableShots.size(); i++) {
            if (availableShots.get(i).getRow() == r && availableShots.get(i).getColumn() == c) {
                return availableShots.remove(i);
            }
        }
        return null;
    }
}