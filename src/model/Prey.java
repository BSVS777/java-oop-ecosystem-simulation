package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Prey con BALANCE CRÍTICO MEJORADO.
 * Cambios para evitar explosión demográfica:
 * - Reproducción cada 4 turnos (antes: 3)
 * - Muerte por sobrepoblación más agresiva
 * - Muerte por estrés en condiciones adversas
 */
public class Prey extends Animal {
    
    private static final Random random = new Random();
    private static final int REPRODUCTION_COOLDOWN = 4; // Aumentado de 3 a 4
    private static final double OVERPOPULATION_THRESHOLD = 0.65; // Reducido de 0.7 a 0.65
    private static final double STRESS_DEATH_CHANCE = 0.15; // Nueva: muerte por estrés
    
    /**
     * Constructor de Prey
     * @param position Posición inicial
     */
    public Prey(Position position) {
        super(position, "PREY");
    }
    
    /**
     * Implementa el movimiento de la presa con controles adicionales.
     */
    @Override
    public void move(Ecosystem ecosystem) {
        if (!alive) return;
        
        // CRÍTICO 1: Verificar sobrepoblación
        if (shouldDieFromOverpopulation(ecosystem)) {
            die();
            ecosystem.removeAnimal(this.position);
            System.out.println("[PREY] Died from overpopulation at " + position);
            return;
        }
        
        // CRÍTICO 2: Verificar muerte por estrés ambiental
        if (shouldDieFromStress(ecosystem)) {
            die();
            ecosystem.removeAnimal(this.position);
            System.out.println("[PREY] Died from environmental stress at " + position);
            return;
        }
        
        List<Position> emptyCells = getAdjacentEmptyCells(ecosystem);
        
        if (!emptyCells.isEmpty()) {
            Position newPosition = emptyCells.get(random.nextInt(emptyCells.size()));
            ecosystem.moveAnimal(this, newPosition);
            System.out.println("[PREY] Moved from " + this.position + " to " + newPosition);
            this.position = newPosition;
        } else {
            System.out.println("[PREY] At " + this.position + " has no empty cells to move");
            
            // NUEVO: Si no puede moverse, 10% chance de muerte por encierro
            if (random.nextDouble() < 0.10) {
                die();
                ecosystem.removeAnimal(this.position);
                System.out.println("[PREY] Died from confinement at " + position);
            }
        }
    }
    
    /**
     * Verifica si debe morir por sobrepoblación.
     * Probabilidad aumenta con la densidad.
     */
    private boolean shouldDieFromOverpopulation(Ecosystem ecosystem) {
        int totalCells = 100;
        int occupiedCells = totalCells - ecosystem.countEmptyCells();
        double occupationRate = occupiedCells / (double) totalCells;
        
        if (occupationRate > OVERPOPULATION_THRESHOLD) {
            // Probabilidad escala con la sobrepoblación
            double deathChance = 0.25 + (occupationRate - OVERPOPULATION_THRESHOLD) * 2;
            deathChance = Math.min(deathChance, 0.60); // Máximo 60%
            
            return random.nextDouble() < deathChance;
        }
        return false;
    }
    
    /**
     * NUEVO: Muerte por estrés cuando hay muchos depredadores cerca.
     */
    private boolean shouldDieFromStress(Ecosystem ecosystem) {
        int nearbyPredators = countNearbyPredators(ecosystem);
        
        // Si hay 3+ depredadores cerca, 15% chance de muerte por estrés
        if (nearbyPredators >= 3) {
            return random.nextDouble() < STRESS_DEATH_CHANCE;
        }
        
        return false;
    }
    
    /**
     * Cuenta depredadores en un radio de 2 celdas.
     */
    private int countNearbyPredators(Ecosystem ecosystem) {
        int count = 0;
        
        for (int i = Math.max(0, position.getRow() - 2); 
             i <= Math.min(9, position.getRow() + 2); i++) {
            for (int j = Math.max(0, position.getColumn() - 2); 
                 j <= Math.min(9, position.getColumn() + 2); j++) {
                Animal animal = ecosystem.getAnimal(new Position(i, j));
                if (animal instanceof Predator && animal.isAlive()) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Reproducción más lenta: cada 4 turnos.
     */
    @Override
    public boolean canReproduce() {
        // Ajustar cooldown basado en mutación genética
        int adjustedCooldown = REPRODUCTION_COOLDOWN + mutationFactor;
        adjustedCooldown = Math.max(2, adjustedCooldown); // Mínimo 2 turnos

        return alive && turnsSurvived >= adjustedCooldown && 
               turnsSurvived % adjustedCooldown == 0;
    }
    
    /**
     * Crea una nueva presa.
     */
    @Override
    public Animal reproduce(Position position) {
        System.out.println("[PREY] Reproduced at " + position);
        return new Prey(position);
    }
    
    /**
     * Obtiene celdas adyacentes vacías.
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
    
    @Override
    public String toString() {
        return "PREY " + super.toString();
    }
}