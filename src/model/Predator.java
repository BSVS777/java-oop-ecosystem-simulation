package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Predator BALANCEADA.
 * Cambios críticos:
 * - Muerte por hambre más gradual (4 turnos en lugar de 3)
 * - Reproducción más restrictiva
 * - Comportamiento más inteligente
 */
public class Predator extends Animal {
    
    private static final Random random = new Random();
    private static final int MAX_TURNS_WITHOUT_EATING = 4; // Aumentado de 3 a 4
    private int lastTurnAte;
    private int totalPreysEaten; // Nuevo: contador de presas comidas
    
    /**
     * Constructor de Predator
     */
    public Predator(Position position) {
        super(position, "PREDATOR");
        this.lastTurnAte = 0;
        this.totalPreysEaten = 0;
    }
    
    /**
     * Movimiento mejorado con priorización inteligente.
     */
    @Override
    public void move(Ecosystem ecosystem) {
        if (!alive) return;
        
        // Busca presas adyacentes
        List<Position> adjacentPreys = getAdjacentPreys(ecosystem);
        
        if (!adjacentPreys.isEmpty()) {
            Position preyPosition = adjacentPreys.get(random.nextInt(adjacentPreys.size()));
            hunt(ecosystem, preyPosition);
            return;
        }
        
        // NUEVO: Si tiene mucha hambre (2+ turnos), busca presas cercanas (radio 2)
        if (turnsWithoutEating >= 2) {
            Position nearbyPrey = findNearbyPrey(ecosystem, 2);
            if (nearbyPrey != null) {
                moveTowards(ecosystem, nearbyPrey);
                return;
            }
        }
        
        // Si no hay presas, se mueve a celda vacía
        List<Position> emptyCells = getAdjacentEmptyCells(ecosystem);
        
        if (!emptyCells.isEmpty()) {
            Position newPosition = emptyCells.get(random.nextInt(emptyCells.size()));
            ecosystem.moveAnimal(this, newPosition);
            System.out.println("[PREDATOR] Moved from " + this.position + " to " + newPosition);
            this.position = newPosition;
        } else {
            System.out.println("[PREDATOR] At " + this.position + " has no available cells");
        }
    }
    
    /**
     * Caza una presa.
     */
    private void hunt(Ecosystem ecosystem, Position preyPosition) {
        Animal prey = ecosystem.getAnimal(preyPosition);
        
        if (prey != null && prey instanceof Prey && prey.isAlive()) {
            prey.die();
            ecosystem.removeAnimal(preyPosition);
            
            ecosystem.moveAnimal(this, preyPosition);
            System.out.println("[PREDATOR] Hunted prey at " + preyPosition);
            this.position = preyPosition;
            
            resetTurnsWithoutEating();
            this.lastTurnAte = ecosystem.getCurrentTurn();
            this.totalPreysEaten++;
        }
    }
    
    /**
     * NUEVO: Busca presas en un radio específico.
     */
    private Position findNearbyPrey(Ecosystem ecosystem, int radius) {
        List<Position> nearbyPreys = new ArrayList<>();
        
        for (int i = Math.max(0, position.getRow() - radius); 
             i <= Math.min(9, position.getRow() + radius); i++) {
            for (int j = Math.max(0, position.getColumn() - radius); 
                 j <= Math.min(9, position.getColumn() + radius); j++) {
                
                Position pos = new Position(i, j);
                if (!pos.equals(position)) {
                    Animal animal = ecosystem.getAnimal(pos);
                    if (animal instanceof Prey && animal.isAlive()) {
                        nearbyPreys.add(pos);
                    }
                }
            }
        }
        
        return nearbyPreys.isEmpty() ? null : nearbyPreys.get(random.nextInt(nearbyPreys.size()));
    }
    
    /**
     * NUEVO: Se mueve hacia una presa lejana.
     */
    private void moveTowards(Ecosystem ecosystem, Position target) {
        int rowDiff = target.getRow() - position.getRow();
        int colDiff = target.getColumn() - position.getColumn();
        
        List<Position> candidates = new ArrayList<>();
        
        // Prioriza movimiento vertical
        if (rowDiff != 0) {
            int newRow = position.getRow() + (rowDiff > 0 ? 1 : -1);
            Position candidate = new Position(newRow, position.getColumn());
            if (candidate.isValid() && ecosystem.isEmpty(candidate)) {
                candidates.add(candidate);
            }
        }
        
        // Prioriza movimiento horizontal
        if (colDiff != 0) {
            int newCol = position.getColumn() + (colDiff > 0 ? 1 : -1);
            Position candidate = new Position(position.getRow(), newCol);
            if (candidate.isValid() && ecosystem.isEmpty(candidate)) {
                candidates.add(candidate);
            }
        }
        
        if (!candidates.isEmpty()) {
            Position newPosition = candidates.get(random.nextInt(candidates.size()));
            ecosystem.moveAnimal(this, newPosition);
            System.out.println("[PREDATOR] Moving towards prey from " + this.position + " to " + newPosition);
            this.position = newPosition;
        }
    }
    
    /**
     * Reproducción MÁS RESTRICTIVA.
     * Ahora requiere: haber comido recientemente Y haber comido al menos 2 presas en total.
     */
    @Override
    public boolean canReproduce() {
        if (!alive) return false;

        int adjustedSurvivalRequirement = 5 + mutationFactor;
        adjustedSurvivalRequirement = Math.max(3, adjustedSurvivalRequirement);

        boolean ateRecently = turnsWithoutEating <= 1;
        boolean hasEatenEnough = totalPreysEaten >= 2;
        boolean hasSurvivedEnough = turnsSurvived >= adjustedSurvivalRequirement;

        return ateRecently && hasEatenEnough && hasSurvivedEnough;
    }
    
    /**
     * Verificación de muerte por hambre (ahora 4 turnos).
     */
    public boolean shouldDieFromHunger() {
        return turnsWithoutEating >= MAX_TURNS_WITHOUT_EATING;
    }
    
    /**
     * Crea un nuevo depredador.
     */
    @Override
    public Animal reproduce(Position position) {
        System.out.println("[PREDATOR] Reproduced at " + position);
        return new Predator(position);
    }
    
    /**
     * Obtiene presas adyacentes.
     */
    private List<Position> getAdjacentPreys(Ecosystem ecosystem) {
        List<Position> preys = new ArrayList<>();
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newColumn = position.getColumn() + dir[1];
            Position newPos = new Position(newRow, newColumn);
            
            if (newPos.isValid()) {
                Animal animal = ecosystem.getAnimal(newPos);
                if (animal instanceof Prey && animal.isAlive()) {
                    preys.add(newPos);
                }
            }
        }
        
        return preys;
    }
    
    /**
     * Obtiene celdas vacías adyacentes.
     */
    private List<Position> getAdjacentEmptyCells(Ecosystem ecosystem) {
        List<Position> emptyCells = new ArrayList<>();
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newColumn = position.getColumn() + dir[1];
            Position newPos = new Position(newRow, newColumn);
            
            if (newPos.isValid() && ecosystem.isEmpty(newPos)) {
                emptyCells.add(newPos);
            }
        }
        
        return emptyCells;
    }
    
    public int getTotalPreysEaten() {
        return totalPreysEaten;
    }
    
    @Override
    public String toString() {
        return "PREDATOR " + super.toString() + 
               ", Turns without eating: " + turnsWithoutEating + 
               ", Total eaten: " + totalPreysEaten;
    }
}