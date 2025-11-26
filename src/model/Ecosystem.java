package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Ecosystem extendida con soporte para:
 * - Tercera especie (Caimán)
 * - Mutaciones genéticas
 */
public class Ecosystem {
    
    private static final int SIZE = 10;
    private Animal[][] matrix;
    private int currentTurn;
    private int maxTurns;
    private String scenario;
    private List<Animal> aliveAnimals;
    private Random random;
    
    // Nuevas propiedades
    private boolean terceraEspecieActiva = false;
    private boolean mutacionesActivas = false;
    
    public Ecosystem(int maxTurns, String scenario) {
        this.matrix = new Animal[SIZE][SIZE];
        this.currentTurn = 0;
        this.maxTurns = maxTurns;
        this.scenario = scenario;
        this.aliveAnimals = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Configura si la tercera especie está activa
     */
    public void setTerceraEspecieActiva(boolean activa) {
        this.terceraEspecieActiva = activa;
    }
    
    /**
     * Configura si las mutaciones están activas
     */
    public void setMutacionesActivas(boolean activas) {
        this.mutacionesActivas = activas;
    }
    
    public void initialize() {
        int numPreys = 0;
        int numPredators = 0;
        int numCaimans = 0;
        
        switch (scenario) {
            case "BALANCED":
                numPreys = 30;
                numPredators = 20;
                numCaimans = terceraEspecieActiva ? 3 : 0;
                break;
            case "PREDATORS_DOM":
                numPreys = 15;
                numPredators = 35;
                numCaimans = terceraEspecieActiva ? 5 : 0;
                break;
            case "PREYS_DOM":
                numPreys = 35;
                numPredators = 15;
                numCaimans = terceraEspecieActiva ? 2 : 0;
                break;
            default:
                numPreys = 30;
                numPredators = 20;
                numCaimans = terceraEspecieActiva ? 3 : 0;
        }
        
        placeAnimalsRandomly(numPreys, "PREY");
        placeAnimalsRandomly(numPredators, "PREDATOR");
        
        if (terceraEspecieActiva) {
            placeAnimalsRandomly(numCaimans, "CAIMAN");
        }
        
        // Aplicar mutaciones iniciales si están activas
        if (mutacionesActivas) {
            applyInitialMutations();
        }
        
        System.out.println(">>> Ecosystem initialized - Scenario: " + scenario);
        System.out.println("    Preys: " + numPreys + " | Predators: " + numPredators + 
                          (terceraEspecieActiva ? " | Caimans: " + numCaimans : ""));
        if (mutacionesActivas) {
            System.out.println("    Genetic mutations: ENABLED");
        }
    }
    
    private void placeAnimalsRandomly(int quantity, String type) {
        int placed = 0;
        
        while (placed < quantity) {
            int row = random.nextInt(SIZE);
            int column = random.nextInt(SIZE);
            Position pos = new Position(row, column);
            
            if (isEmpty(pos)) {
                Animal animal;
                switch (type) {
                    case "PREY":
                        animal = new Prey(pos);
                        break;
                    case "PREDATOR":
                        animal = new Predator(pos);
                        break;
                    case "CAIMAN":
                        animal = new Caiman(pos);
                        break;
                    default:
                        continue;
                }
                
                matrix[row][column] = animal;
                aliveAnimals.add(animal);
                placed++;
            }
        }
    }
    
    /**
     * Aplica mutaciones genéticas iniciales a todos los animales
     */
    private void applyInitialMutations() {
        for (Animal animal : aliveAnimals) {
            applyMutation(animal);
        }
    }
    
    /**
     * Aplica una mutación a un animal individual
     * La mutación afecta la velocidad de reproducción
     */
    private void applyMutation(Animal animal) {
        // Mutación aleatoria: -1, 0, o +1 en el tiempo de reproducción
        int mutation = random.nextInt(3) - 1;
        
        // Aquí podrías agregar un campo mutationFactor en Animal
        // Por ahora solo lo registramos
        System.out.println("[MUTATION] Applied to " + animal.getType() + 
                          " at " + animal.getPosition() + ": " + mutation);
    }
    
    public String executeTurn() {
        currentTurn++;
        System.out.println("\n--- Executing Turn " + currentTurn + " ---");
        
        // 1. MOVIMIENTO
        List<Animal> animalsToMove = new ArrayList<>(aliveAnimals);
        for (Animal animal : animalsToMove) {
            if (animal.isAlive()) {
                animal.move(this);
                
                // Aplicar mutaciones ocasionales (5% de probabilidad)
                if (mutacionesActivas && random.nextDouble() < 0.05) {
                    applyMutation(animal);
                }
            }
        }
        
        // 2. VERIFICAR MUERTES POR HAMBRE
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
            } else if (animal instanceof Caiman) {
                Caiman caiman = (Caiman) animal;
                caiman.incrementTurnsWithoutEating();
                
                if (caiman.shouldDieFromHunger()) {
                    System.out.println("[DEATH] Caiman died of hunger at " + caiman.getPosition());
                    caiman.die();
                    matrix[caiman.getPosition().getRow()][caiman.getPosition().getColumn()] = null;
                    animalsToRemove.add(caiman);
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
                        
                        // Aplicar mutación al descendiente si está activo
                        if (mutacionesActivas) {
                            applyMutation(offspring);
                        }
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
        int caimans = countCaimans();
        int emptyCells = countEmptyCells();
        
        String state = String.format(
            "Turn %d | Preys: %d | Predators: %d" + 
            (terceraEspecieActiva ? " | Caimans: %d" : "") + " | Empty: %d",
            currentTurn, preys, predators, 
            terceraEspecieActiva ? caimans : null, emptyCells
        );
        
        System.out.println("[STATS] " + state);
        return state;
    }
    
    // Métodos auxiliares
    
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
    
    public int countCaimans() {
        return (int) aliveAnimals.stream()
            .filter(a -> a instanceof Caiman && a.isAlive())
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
        int preys = countPreys();
        int predators = countPredators();
        int caimans = countCaimans();
        
        // Extinción si desaparecen las presas O todos los depredadores (incluyendo caimanes)
        return preys == 0 || (predators == 0 && caimans == 0);
    }
    
    // Getters
    
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
    
    public boolean isTerceraEspecieActiva() {
        return terceraEspecieActiva;
    }
    
    public boolean isMutacionesActivas() {
        return mutacionesActivas;
    }
}