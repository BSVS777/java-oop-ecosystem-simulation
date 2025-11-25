package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Ecosystem con CONFIGURACIONES BALANCEADAS.
 * Ajustadas las proporciones iniciales de cada escenario.
 */
public class Ecosystem {
    
    private static final int SIZE = 10;
    private Animal[][] matrix;
    private int currentTurn;
    private int maxTurns;
    private String scenario;
    private List<Animal> aliveAnimals;
    private Random random;
    
    public Ecosystem(int maxTurns, String scenario) {
        this.matrix = new Animal[SIZE][SIZE];
        this.currentTurn = 0;
        this.maxTurns = maxTurns;
        this.scenario = scenario;
        this.aliveAnimals = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * MODIFICADO: Configuraciones iniciales balanceadas
     */
    public void initialize() {
        int numPreys = 0;
        int numPredators = 0;
        
        switch (scenario) {
            case "BALANCED":
                // Más depredadores para compensar su dificultad de reproducción
                numPreys = 30;
                numPredators = 20;
                break;
            case "PREDATORS_DOM":
                // Depredadores dominan claramente
                numPreys = 15;
                numPredators = 35;
                break;
            case "PREYS_DOM":
                // Presas dominan pero no abruman
                numPreys = 35;
                numPredators = 15;
                break;
            default:
                numPreys = 30;
                numPredators = 20;
        }
        
        placeAnimalsRandomly(numPreys, "PREY");
        placeAnimalsRandomly(numPredators, "PREDATOR");
        
        System.out.println(">>> Ecosystem initialized - Scenario: " + scenario);
        System.out.println("    Preys: " + numPreys + " | Predators: " + numPredators);
    }
    
    private void placeAnimalsRandomly(int quantity, String type) {
        int placed = 0;
        
        while (placed < quantity) {
            int row = random.nextInt(SIZE);
            int column = random.nextInt(SIZE);
            Position pos = new Position(row, column);
            
            if (isEmpty(pos)) {
                Animal animal;
                if (type.equals("PREY")) {
                    animal = new Prey(pos);
                } else {
                    animal = new Predator(pos);
                }
                
                matrix[row][column] = animal;
                aliveAnimals.add(animal);
                placed++;
            }
        }
    }
    
    /**
     * Ejecuta un turno completo de simulación
     */
    public String executeTurn() {
        currentTurn++;
        System.out.println("\n--- Executing Turn " + currentTurn + " ---");
        
        // 1. MOVIMIENTO
        List<Animal> animalsToMove = new ArrayList<>(aliveAnimals);
        for (Animal animal : animalsToMove) {
            if (animal.isAlive()) {
                animal.move(this);
            }
        }
        
        // 2. VERIFICAR MUERTES POR HAMBRE (Depredadores)
        List<Animal> animalsToRemove = new ArrayList<>();
        for (Animal animal : aliveAnimals) {
            if (animal instanceof Predator) {
                Predator predator = (Predator) animal;
                predator.incrementTurnsWithoutEating();
                
                if (predator.shouldDieFromHunger()) {
                    System.out.println("[DEATH] Predator died of hunger at " + predator.getPosition());
                    predator.die();
                    matrix[predator.getPosition().getRow()][predator.getPosition().getColumn()] = null;
                    animalsToRemove.add(predator);
                }
            }
        }
        aliveAnimals.removeAll(animalsToRemove);
        
        // 3. REPRODUCCIÓN
        List<Animal> newAnimals = new ArrayList<>();
        for (Animal animal : aliveAnimals) {
            if (animal.isAlive()) {
                animal.incrementTurnsSurvived();
                
                if (animal.canReproduce()) {
                    Position emptyCell = findAdjacentEmptyCell(animal.getPosition());
                    if (emptyCell != null) {
                        Animal offspring = animal.reproduce(emptyCell);
                        matrix[emptyCell.getRow()][emptyCell.getColumn()] = offspring;
                        newAnimals.add(offspring);
                    }
                }
            }
        }
        aliveAnimals.addAll(newAnimals);
        
        return generateTurnState();
    }
    
    private Position findAdjacentEmptyCell(Position pos) {
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        List<Position> emptyPositions = new ArrayList<>();
        
        for (int[] dir : directions) {
            int newRow = pos.getRow() + dir[0];
            int newColumn = pos.getColumn() + dir[1];
            Position newPos = new Position(newRow, newColumn);
            
            if (newPos.isValid() && isEmpty(newPos)) {
                emptyPositions.add(newPos);
            }
        }
        
        return emptyPositions.isEmpty() ? null : emptyPositions.get(random.nextInt(emptyPositions.size()));
    }
    
    private String generateTurnState() {
        int preys = countPreys();
        int predators = countPredators();
        int emptyCells = countEmptyCells();
        
        String state = String.format(
            "Turn %d | Preys: %d | Predators: %d | Empty: %d",
            currentTurn, preys, predators, emptyCells
        );
        
        System.out.println("[STATS] " + state);
        return state;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    public boolean isEmpty(Position pos) {
        return matrix[pos.getRow()][pos.getColumn()] == null;
    }
    
    public Animal getAnimal(Position pos) {
        return matrix[pos.getRow()][pos.getColumn()];
    }
    
    public void removeAnimal(Position pos) {
        matrix[pos.getRow()][pos.getColumn()] = null;
    }
    
    public void moveAnimal(Animal animal, Position newPos) {
        matrix[animal.getPosition().getRow()][animal.getPosition().getColumn()] = null;
        matrix[newPos.getRow()][newPos.getColumn()] = animal;
    }
    
    public int countPreys() {
        return (int) aliveAnimals.stream()
            .filter(a -> a instanceof Prey && a.isAlive())
            .count();
    }
    
    public int countPredators() {
        return (int) aliveAnimals.stream()
            .filter(a -> a instanceof Predator && a.isAlive())
            .count();
    }
    
    public int countEmptyCells() {
        int empty = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (matrix[i][j] == null) empty++;
            }
        }
        return empty;
    }
    
    public boolean hasExtinction() {
        return countPreys() == 0 || countPredators() == 0;
    }
    
    // ========== GETTERS ==========
    
    public Animal[][] getMatrix() {
        return matrix;
    }
    
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    public int getMaxTurns() {
        return maxTurns;
    }
    
    public String getScenario() {
        return scenario;
    }
    
    public List<Animal> getAliveAnimals() {
        return new ArrayList<>(aliveAnimals);
    }
}