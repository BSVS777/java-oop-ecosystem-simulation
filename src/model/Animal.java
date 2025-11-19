package model;

/**
 * Clase abstracta base que representa cualquier animal en el ecosistema.
 * Implementa el principio de abstracción y herencia de POO.
 */
public abstract class Animal {
    
    // Atributos protegidos para acceso desde clases hijas
    protected Position position;
    protected int turnsWithoutEating;
    protected int turnsSurvived;
    protected boolean alive;
    protected String type; // "PREY" o "PREDATOR"
    
    /**
     * Constructor base de Animal
     * @param position Posición inicial en la matriz
     * @param type Tipo de animal (PREY o PREDATOR)
     */
    public Animal(Position position, String type) {
        this.position = position;
        this.type = type;
        this.alive = true;
        this.turnsWithoutEating = 0;
        this.turnsSurvived = 0;
    }
    
    /**
     * Método abstracto para movimiento - cada tipo lo implementa diferente
     * @param ecosystem Referencia al ecosistema actual
     */
    public abstract void move(Ecosystem ecosystem);
    
    /**
     * Método abstracto para verificar si puede reproducirse
     * @return true si cumple condiciones de reproducción
     */
    public abstract boolean canReproduce();
    
    /**
     * Método abstracto para crear descendencia
     * @param position Posición donde nacerá el nuevo animal
     * @return Nuevo animal del mismo tipo
     */
    public abstract Animal reproduce(Position position);
    
    /**
     * Incrementa el contador de turnos sobrevividos
     */
    public void incrementTurnsSurvived() {
        this.turnsSurvived++;
    }
    
    /**
     * Incrementa el contador de turnos sin comer
     */
    public void incrementTurnsWithoutEating() {
        this.turnsWithoutEating++;
    }
    
    /**
     * Resetea el contador de turnos sin comer (cuando come)
     */
    public void resetTurnsWithoutEating() {
        this.turnsWithoutEating = 0;
    }
    
    /**
     * Marca el animal como muerto
     */
    public void die() {
        this.alive = false;
    }
    
    // Getters y Setters
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public int getTurnsWithoutEating() {
        return turnsWithoutEating;
    }
    
    public int getTurnsSurvived() {
        return turnsSurvived;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return String.format("%s en %s - Vivo: %s, Turnos sobrevividos: %d", 
                           type, position, alive, turnsSurvived);
    }
}