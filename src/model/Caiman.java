package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase Caiman - Tercera especie depredadora.
 * Come tanto presas como depredadores.
 * Más resistente al hambre que los depredadores normales.
 */
public class Caiman extends Animal {
    
    private static final Random random = new Random();
    private static final int MAX_TURNS_WITHOUT_EATING = 4; // Más resistente que depredadores
    private int lastTurnAte;
    
    public Caiman(Position position) {
        super(position, "CAIMAN");
        this.lastTurnAte = 0;
    }
    
    @Override
    public void move(Ecosystem ecosystem) {
        if (!alive) return;
        
        // Prioridad 1: Buscar presas o depredadores adyacentes
        List<Position> potentialPrey = getAdjacentAnimals(ecosystem);
        
        if (!potentialPrey.isEmpty()) {
            Position preyPosition = potentialPrey.get(random.nextInt(potentialPrey.size()));
            hunt(ecosystem, preyPosition);
            return;
        }
        
        // Prioridad 2: Moverse a celda vacía
        List<Position> emptyCells = getAdjacentEmptyCells(ecosystem);
        
        if (!emptyCells.isEmpty()) {
            Position newPosition = emptyCells.get(random.nextInt(emptyCells.size()));
            ecosystem.moveAnimal(this, newPosition);
            System.out.println("[CAIMAN] Moved from " + this.position + " to " + newPosition);
            this.position = newPosition;
        } else {
            System.out.println("[CAIMAN] At " + this.position + " has no available cells");
        }
    }
    
    private void hunt(Ecosystem ecosystem, Position preyPosition) {
        Animal prey = ecosystem.getAnimal(preyPosition);
        
        if (prey != null && prey.isAlive()) {
            prey.die();
            ecosystem.removeAnimal(preyPosition);
            
            ecosystem.moveAnimal(this, preyPosition);
            System.out.println("[CAIMAN] Hunted " + prey.getType() + " at " + preyPosition);
            this.position = preyPosition;
            
            resetTurnsWithoutEating();
            this.lastTurnAte = ecosystem.getCurrentTurn();
        }
    }
    
    @Override
    public boolean canReproduce() {
        if (!alive) return false;
        return turnsWithoutEating == 0 && turnsSurvived >= 4;
    }
    
    public boolean shouldDieFromHunger() {
        return turnsWithoutEating >= MAX_TURNS_WITHOUT_EATING;
    }
    
    @Override
    public Animal reproduce(Position position) {
        System.out.println("[CAIMAN] Reproduced at " + position);
        return new Caiman(position);
    }
    
    private List<Position> getAdjacentAnimals(Ecosystem ecosystem) {
        List<Position> animals = new ArrayList<>();
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newColumn = position.getColumn() + dir[1];
            Position newPos = new Position(newRow, newColumn);
            
            if (newPos.isValid()) {
                Animal animal = ecosystem.getAnimal(newPos);
                // Puede comer tanto presas como depredadores
                if (animal != null && animal.isAlive() && !(animal instanceof Caiman)) {
                    animals.add(newPos);
                }
            }
        }
        
        return animals;
    }
    
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
        return "CAIMAN " + super.toString() + ", Turns without eating: " + turnsWithoutEating;
    }
}