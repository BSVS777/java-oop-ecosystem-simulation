package controller;

import model.Ecosystem;
import data.EcosystemDAO;
import data.StateDAO;

/**
 * Controlador para la logica del ecosistema.
 * Maneja la creacion y ejecucion de simulaciones.
 */
public class EcosystemController {
    
    private Ecosystem ecosystem;
    private EcosystemDAO ecosystemDAO;
    private StateDAO stateDAO;
    private String currentUsername;
    
    /**
     * Constructor del controlador
     */
    public EcosystemController() {
        this.ecosystem = null;
        this.ecosystemDAO = new EcosystemDAO();
        this.stateDAO = new StateDAO();
        this.currentUsername = "Guest";
    }
    
    /**
     * Establece el usuario actual
     * @param username Nombre del usuario
     */
    public void setCurrentUser(String username) {
        this.currentUsername = username;
    }
    
    /**
     * Crea un nuevo ecosistema con la configuracion especificada
     * @param maxTurns Numero maximo de turnos
     * @param scenario Tipo de escenario (BALANCED, PREDATORS_DOM, PREYS_DOM)
     */
    public void createEcosystem(int maxTurns, String scenario) {
        this.ecosystem = new Ecosystem(maxTurns, scenario);
        this.ecosystem.initialize();
        
        // Guardar configuracion inicial
        int numPreys = ecosystem.countPreys();
        int numPredators = ecosystem.countPredators();
        ecosystemDAO.saveConfiguration(scenario, maxTurns, numPreys, numPredators, currentUsername);
        
        // Iniciar registro de estados
        stateDAO.startNewSimulation(scenario, currentUsername);
        
        // Guardar estado inicial (turno 0)
        stateDAO.saveTurnState(ecosystem);
        
        System.out.println("[CONTROLLER] Ecosystem created:");
        System.out.println("  Scenario: " + scenario);
        System.out.println("  Max turns: " + maxTurns);
        System.out.println("  Initial preys: " + numPreys);
        System.out.println("  Initial predators: " + numPredators);
    }
    
    /**
     * Ejecuta un turno de simulacion
     * @return true si la simulacion debe continuar, false si termino
     */
    public boolean executeTurn() {
        if (ecosystem == null) {
            System.err.println("[ERROR] No ecosystem initialized");
            return false;
        }
        
        // Ejecutar turno
        ecosystem.executeTurn();
        
        // Guardar estado del turno
        stateDAO.saveTurnState(ecosystem);
        
        // Verificar si debe continuar
        int currentTurn = ecosystem.getCurrentTurn();
        int maxTurns = ecosystem.getMaxTurns();
        boolean hasExtinction = ecosystem.hasExtinction();
        
        // Continua si no ha llegado al maximo y no hay extincion
        boolean shouldContinue = (currentTurn < maxTurns) && !hasExtinction;
        
        if (!shouldContinue) {
            // Guardar estado final
            stateDAO.saveFinalState(ecosystem, currentTurn);
            
            System.out.println("[CONTROLLER] Simulation ended:");
            System.out.println("  Turns executed: " + currentTurn);
            System.out.println("  Extinction: " + hasExtinction);
            System.out.println("  Final preys: " + ecosystem.countPreys());
            System.out.println("  Final predators: " + ecosystem.countPredators());
        }
        
        return shouldContinue;
    }
    
    /**
     * Obtiene el ecosistema actual
     * @return Ecosistema actual
     */
    public Ecosystem getEcosystem() {
        return ecosystem;
    }
    
    /**
     * Reinicia el ecosistema actual
     */
    public void resetEcosystem() {
        if (ecosystem != null) {
            String scenario = ecosystem.getScenario();
            int maxTurns = ecosystem.getMaxTurns();
            createEcosystem(maxTurns, scenario);
        }
    }
    
    /**
     * Verifica si hay un ecosistema activo
     * @return true si hay un ecosistema inicializado
     */
    public boolean hasEcosystem() {
        return ecosystem != null;
    }
    
    /**
     * Obtiene estadisticas del ecosistema actual
     * @return String con estadisticas formateadas
     */
    public String getStats() {
        if (ecosystem == null) {
            return "No ecosystem initialized";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== ECOSYSTEM STATISTICS ===\n");
        stats.append("Scenario: ").append(ecosystem.getScenario()).append("\n");
        stats.append("Current Turn: ").append(ecosystem.getCurrentTurn())
             .append("/").append(ecosystem.getMaxTurns()).append("\n");
        stats.append("Preys: ").append(ecosystem.countPreys()).append("\n");
        stats.append("Predators: ").append(ecosystem.countPredators()).append("\n");
        stats.append("Empty Cells: ").append(ecosystem.countEmptyCells()).append("\n");
        stats.append("Extinction: ").append(ecosystem.hasExtinction()).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Obtiene el DAO de ecosistema
     * @return EcosystemDAO
     */
    public EcosystemDAO getEcosystemDAO() {
        return ecosystemDAO;
    }
    
    /**
     * Obtiene el DAO de estados
     * @return StateDAO
     */
    public StateDAO getStateDAO() {
        return stateDAO;
    }
}