package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Prey que hereda de Animal.
 * Implementa comportamiento específico de las presas:
 * - Movimiento aleatorio a celdas vacías
 * - Reproducción cada 2 turnos sobrevividos
 */
public class Prey extends Animal {
    
    private static final Random random = new Random();
    
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
        
        List<Position> emptyCells = getAdjacentEmptyCells(ecosystem);
        
        if (!emptyCells.isEmpty()) {
            // Selecciona aleatoriamente una celda vacía
            Position newPosition = emptyCells.get(random.nextInt(emptyCells.size()));
            
            // Actualiza posición en el ecosistema
            ecosystem.moveAnimal(this, newPosition);
            
            System.out.println("[PREY] Moved from " + this.position + " to " + newPosition);
            this.position = newPosition;
        } else {
            System.out.println("[PREY] At " + this.position + " has no empty cells to move");
        }
    }
    
    /**
     * Verifica si la presa puede reproducirse.
     * Condición: haber sobrevivido 2 turnos consecutivos.
     * @return true si puede reproducirse
     */
    @Override
    public boolean canReproduce() {
        return alive && turnsSurvived >= 2;
    }
    
    /**
     * Crea una nueva presa en la posición indicada.
     * @param position Posición del nuevo animal
     * @return Nueva instancia de Prey
     */
    @Override
    public Animal reproduce(Position position) {
        System.out.println("🐰🐰 Presa se reproduce en " + position);
        return new Prey(position);
    }
    
    /**
     * Obtiene lista de posiciones adyacentes vacías
     * @param ecosystem Referencia al ecosistema
     * @return Lista de posiciones disponibles
     */
    private List<Position> getAdjacentEmptyCells(Ecosystem ecosystem) {
        List<Position> emptyCells = new ArrayList<>();
        
        // Direcciones: arriba, abajo, izquierda, derecha
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newColumn = position.getColumn() + dir[1];
            
            Position newPos = new Position(newRow, newColumn);
            
            // Verifica si la posición es válida y está vacía
            if (newPos.isValid() && ecosystem.isEmpty(newPos)) {
                emptyCells.add(newPos);
            }
        }
        
        return emptyCells;
    }
    
    @Override
    public String toString() {
        return "🐰 PREY " + super.toString();
    }
}