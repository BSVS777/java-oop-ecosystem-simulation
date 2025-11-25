package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Prey con BALANCE MEJORADO.
 * Cambios clave:
 * - Reproducción más lenta: cada 3 turnos (antes: 2)
 * - Muerte por sobrepoblación: si >70% de celdas ocupadas
 */
public class Prey extends Animal {
    
    private static final Random random = new Random();
    private static final int REPRODUCTION_COOLDOWN = 3; // Aumentado de 2 a 3
    private static final double OVERPOPULATION_THRESHOLD = 0.7; // 70% ocupación
    
    /**
     * Constructor de Prey
     * @param position Posición inicial
     */
    public Prey(Position position) {
        super(position, "PREY");
    }
    
    /**
     * Implementa el movimiento de la presa.
     * Se mueve a una celda adyacente vacía aleatoriamente.
     * @param ecosystem Referencia al ecosistema
     */
    @Override
    public void move(Ecosystem ecosystem) {
        if (!alive) return;
        
        // NUEVO: Verificar sobrepoblación
        if (shouldDieFromOverpopulation(ecosystem)) {
            die();
            ecosystem.removeAnimal(this.position);
            System.out.println("[PREY] Died from overpopulation at " + position);
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
        }
    }
    
    /**
     * Verifica si debe morir por sobrepoblación
     */
    private boolean shouldDieFromOverpopulation(Ecosystem ecosystem) {
        int totalCells = 100;
        int occupiedCells = totalCells - ecosystem.countEmptyCells();
        double occupationRate = occupiedCells / (double) totalCells;
        
        // Si hay sobrepoblación, 30% de chance de morir por estrés/recursos
        if (occupationRate > OVERPOPULATION_THRESHOLD) {
            return random.nextDouble() < 0.3;
        }
        return false;
    }
    
    /**
     * Verifica si la presa puede reproducirse.
     * MODIFICADO: Ahora requiere 3 turnos sobrevividos (antes: 2)
     * @return true si puede reproducirse
     */
    @Override
    public boolean canReproduce() {
        return alive && turnsSurvived >= REPRODUCTION_COOLDOWN;
    }
    
    /**
     * Crea una nueva presa en la posición indicada.
     * @param position Posición del nuevo animal
     * @return Nueva instancia de Prey
     */
    @Override
    public Animal reproduce(Position position) {
        System.out.println("[PREY] Reproduced at " + position);
        return new Prey(position);
    }
    
    /**
     * Obtiene lista de posiciones adyacentes vacías
     * @param ecosystem Referencia al ecosistema
     * @return Lista de posiciones disponibles
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