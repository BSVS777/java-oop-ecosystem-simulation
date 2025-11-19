package model;

/**
 * Clase que representa una posición (x,y) en la matriz del ecosistema.
 * Encapsula las coordenadas y validaciones.
 */
public class Position {
    
    private int row;
    private int column;
    
    /**
     * Constructor de Position
     * @param row Coordenada Y (0-9)
     * @param column Coordenada X (0-9)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
    
    /**
     * Verifica si la posición es válida en una matriz 10x10
     * @return true si está dentro de los límites
     */
    public boolean isValid() {
        return row >= 0 && row < 10 && column >= 0 && column < 10;
    }
    
    /**
     * Calcula la distancia Manhattan entre dos posiciones
     * @param other Otra posición
     * @return Distancia en pasos ortogonales
     */
    public int distance(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.column - other.column);
    }
    
    /**
     * Verifica si dos posiciones son adyacentes (arriba, abajo, izq, der)
     * @param other Otra posición
     * @return true si son adyacentes
     */
    public boolean isAdjacent(Position other) {
        return distance(other) == 1;
    }
    
    /**
     * Crea una copia de la posición
     * @return Nueva instancia con mismas coordenadas
     */
    public Position copy() {
        return new Position(this.row, this.column);
    }
    
    // Getters y Setters
    public int getRow() {
        return row;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }
    
    @Override
    public int hashCode() {
        return 31 * row + column;
    }
    
    @Override
    public String toString() {
        return String.format("(%d,%d)", row, column);
    }
}