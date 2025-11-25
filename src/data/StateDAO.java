package data;

import model.Ecosystem;
import model.Animal;
import model.Prey;
import model.Predator;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para estados de la simulacion.
 * Maneja lectura y escritura del archivo estado_turnos.txt
 */
public class StateDAO {
    
    private static final String STATE_FILE = "estado_turnos.txt";
    private String currentSimulationId;
    
    /**
     * Constructor
     */
    public StateDAO() {
        this.currentSimulationId = null;
    }
    
    /**
     * Inicia una nueva simulacion y genera un ID unico
     * @param scenario Escenario de la simulacion
     * @param username Usuario que ejecuta la simulacion
     */
    public void startNewSimulation(String scenario, String username) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        this.currentSimulationId = scenario + "_" + username + "_" + timestamp;
        
        System.out.println("[DAO] Started simulation: " + currentSimulationId);
    }
    
    /**
     * Guarda el estado de un turno
     * @param ecosystem Ecosistema actual
     * @return true si se guardo exitosamente
     */
    public boolean saveTurnState(Ecosystem ecosystem) {
        if (currentSimulationId == null) {
            System.err.println("[ERROR] No active simulation. Call startNewSimulation() first");
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, true))) {
            
            // Linea de separacion de turno
            writer.write("=== SIMULATION: " + currentSimulationId + " ===");
            writer.newLine();
            
            // Informacion del turno
            writer.write("TURN: " + ecosystem.getCurrentTurn());
            writer.newLine();
            writer.write("SCENARIO: " + ecosystem.getScenario());
            writer.newLine();
            writer.write("TIMESTAMP: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            
            // Estadisticas
            writer.write("PREYS: " + ecosystem.countPreys());
            writer.newLine();
            writer.write("PREDATORS: " + ecosystem.countPredators());
            writer.newLine();
            writer.write("EMPTY: " + ecosystem.countEmptyCells());
            writer.newLine();
            writer.write("EXTINCTION: " + ecosystem.hasExtinction());
            writer.newLine();
            
            // Matriz completa
            writer.write("MATRIX:");
            writer.newLine();
            Animal[][] matrix = ecosystem.getMatrix();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    Animal animal = matrix[i][j];
                    if (animal == null) {
                        writer.write("E"); // Empty
                    } else if (animal instanceof Prey) {
                        writer.write("P");
                    } else if (animal instanceof Predator) {
                        writer.write("D");
                    }
                    
                    if (j < 9) writer.write(",");
                }
                writer.newLine();
            }
            
            writer.write("--- END TURN ---");
            writer.newLine();
            writer.newLine();
            
            return true;
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save turn state: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Guarda el estado final de la simulacion
     * @param ecosystem Ecosistema final
     * @param totalTurns Total de turnos ejecutados
     */
    public void saveFinalState(Ecosystem ecosystem, int totalTurns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE, true))) {
            
            writer.write("╔═══════════════════════════════════════════╗");
            writer.newLine();
            writer.write("║         SIMULATION COMPLETED              ║");
            writer.newLine();
            writer.write("╚═══════════════════════════════════════════╝");
            writer.newLine();
            writer.write("SIMULATION ID: " + currentSimulationId);
            writer.newLine();
            writer.write("TOTAL TURNS: " + totalTurns);
            writer.newLine();
            writer.write("FINAL PREYS: " + ecosystem.countPreys());
            writer.newLine();
            writer.write("FINAL PREDATORS: " + ecosystem.countPredators());
            writer.newLine();
            writer.write("EXTINCTION OCCURRED: " + ecosystem.hasExtinction());
            writer.newLine();
            
            if (ecosystem.hasExtinction()) {
                if (ecosystem.countPreys() == 0) {
                    writer.write("RESULT: Preys went extinct");
                } else {
                    writer.write("RESULT: Predators went extinct");
                }
            } else {
                writer.write("RESULT: Both species survived");
            }
            writer.newLine();
            
            writer.write("════════════════════════════════════════════");
            writer.newLine();
            writer.newLine();
            
            System.out.println("[DAO] Final state saved for simulation: " + currentSimulationId);
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save final state: " + e.getMessage());
        }
    }
    
    /**
     * Lee todos los estados de una simulacion especifica
     * @param simulationId ID de la simulacion
     * @return Lista de estados por turno
     */
    public List<TurnState> loadSimulationStates(String simulationId) {
        List<TurnState> states = new ArrayList<>();
        
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            return states;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(STATE_FILE))) {
            String line;
            TurnState currentState = null;
            boolean inSimulation = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("SIMULATION: " + simulationId)) {
                    inSimulation = true;
                    currentState = new TurnState();
                } else if (inSimulation && line.startsWith("TURN:")) {
                    currentState.turn = Integer.parseInt(line.split(":")[1].trim());
                } else if (inSimulation && line.startsWith("PREYS:")) {
                    currentState.preys = Integer.parseInt(line.split(":")[1].trim());
                } else if (inSimulation && line.startsWith("PREDATORS:")) {
                    currentState.predators = Integer.parseInt(line.split(":")[1].trim());
                } else if (inSimulation && line.startsWith("EMPTY:")) {
                    currentState.empty = Integer.parseInt(line.split(":")[1].trim());
                } else if (inSimulation && line.contains("END TURN")) {
                    states.add(currentState);
                    currentState = null;
                } else if (line.contains("SIMULATION COMPLETED")) {
                    inSimulation = false;
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load simulation states: " + e.getMessage());
        }
        
        return states;
    }
    
    /**
     * Obtiene la lista de IDs de todas las simulaciones guardadas
     * @return Lista de IDs de simulaciones
     */
    public List<String> getAllSimulationIds() {
        List<String> ids = new ArrayList<>();
        
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            return ids;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(STATE_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("SIMULATION:")) {
                    String id = line.substring(line.indexOf(":") + 1)
                        .replace("===", "").trim();
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load simulation IDs: " + e.getMessage());
        }
        
        return ids;
    }
    
    /**
     * Obtiene estadisticas generales del archivo de estados
     * @return String con estadisticas
     */
    public String getGeneralStatistics() {
        List<String> simulations = getAllSimulationIds();
        
        if (simulations.isEmpty()) {
            return "No simulations recorded yet";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATE FILE STATISTICS ===\n");
        stats.append("Total simulations: ").append(simulations.size()).append("\n");
        stats.append("\nSimulation IDs:\n");
        
        for (String id : simulations) {
            List<TurnState> states = loadSimulationStates(id);
            stats.append("  - ").append(id)
                 .append(" (").append(states.size()).append(" turns)\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Limpia el archivo de estados
     * @return true si se limpio exitosamente
     */
    public boolean clearStates() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE))) {
            writer.write("");
            System.out.println("[INFO] State file cleared");
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to clear state file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clase interna para representar el estado de un turno
     */
    public static class TurnState {
        public int turn;
        public int preys;
        public int predators;
        public int empty;
        
        @Override
        public String toString() {
            return String.format("Turn %d: P=%d, D=%d, E=%d", 
                turn, preys, predators, empty);
        }
    }
}