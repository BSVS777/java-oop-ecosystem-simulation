package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Predator que hereda de Animal.
 * Implementa comportamiento específico de los depredadores:
 * - Movimiento hacia presas o celdas vacías
 * - Caza de presas
 * - Muerte por hambre después de 3 turnos sin comer
 * - Reproducción si comió al menos una vez en los últimos 3 turnos
 */
public class Predator extends Animal {
    
    private static final Random random = new Random();
    private static final int MAX_TURNS_WITHOUT_EATING = 3;
    private int lastTurnAte; // Guarda el turno en que comió por última vez
    
    /**
     * Constructor de Predator
     * @param position Posición inicial
     */
    public Predator(Position position) {
        super(position, "PREDATOR");
        this.lastTurnAte = 0;
    }
    
    /**
     * Implementa el movimiento del depredador.
     * Prioridad: 1) Moverse hacia una presa adyacente (come)
     *            2) Moverse a una celda vacía
     * @param ecosystem Referencia al ecosistema
     */
    @Override
    public void move(Ecosystem ecosystem) {
        if (!alive) return;
        
        // Busca presas adyacentes
        List<Position> adjacentPreys = getAdjacentPreys(ecosystem);
        
        if (!adjacentPreys.isEmpty()) {
            // Caza una presa aleatoria de las disponibles
            Position preyPosition = adjacentPreys.get(random.nextInt(adjacentPreys.size()));
            hunt(ecosystem, preyPosition);
            return;
        }
        
        // Si no hay presas, se mueve a una celda vacía
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
     * Caza una presa en la posición indicada
     * @param ecosystem Referencia al ecosistema
     * @param preyPosition Posición de la presa a cazar
     */
    private void hunt(Ecosystem ecosystem, Position preyPosition) {
        Animal prey = ecosystem.getAnimal(preyPosition);
        
        if (prey != null && prey instanceof Prey) {
            // Mata la presa
            prey.die();
            ecosystem.removeAnimal(preyPosition);
            
            // Mueve el depredador a esa posicion
            ecosystem.moveAnimal(this, preyPosition);
            System.out.println("[PREDATOR] Hunted prey at " + preyPosition);
            this.position = preyPosition;
            
            // Resetea contador de hambre
            resetTurnsWithoutEating();
            this.lastTurnAte = ecosystem.getCurrentTurn();
        }
    }
    
    /**
     * Verifica si el depredador puede reproducirse.
     * Condición: haber comido al menos una vez en los últimos 3 turnos Y
     *            haber sobrevivido al menos 3 turnos.
     * @return true si puede reproducirse
     */
    @Override
    public boolean canReproduce() {
        if (!alive) return false;
        // Más restrictivo: necesita haber comido Y haber sobrevivido suficiente
        return turnsWithoutEating == 0 && turnsSurvived >= 3;
    }
    
    /**
     * Verifica si el depredador debe morir por hambre
     * @return true si debe morir
     */
    public boolean shouldDieFromHunger() {
        return turnsWithoutEating >= MAX_TURNS_WITHOUT_EATING;
    }
    
    /**
     * Crea un nuevo depredador en la posición indicada.
     * @param position Posición del nuevo animal
     * @return Nueva instancia de Predator
     */
    @Override
    public Animal reproduce(Position position) {
        System.out.println("[PREDATOR] Reproduced at " + position);
        return new Predator(position);
    }
    
    /**
     * Obtiene lista de posiciones adyacentes con presas
     * @param ecosystem Referencia al ecosistema
     * @return Lista de posiciones con presas
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
                if (animal != null && animal instanceof Prey && animal.isAlive()) {
                    preys.add(newPos);
                }
            }
        }
        
        return preys;
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
        return "PREDATOR " + super.toString() + ", Turns without eating: " + turnsWithoutEating;
    }
}