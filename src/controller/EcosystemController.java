package controller;

import model.Ecosystem;
import data.EcosystemDAO;
import data.StateDAO;

/**
 * Controlador extendido con soporte para tercera especie y mutaciones
 */
public class EcosystemController {
    
    private Ecosystem ecosystem;
    private EcosystemDAO ecosystemDAO;
    private StateDAO stateDAO;
    private String currentUsername;
    
    // Flags para extensiones
    private boolean terceraEspecieActiva = false;
    private boolean mutacionesActivas = false;
    
    public EcosystemController() {
        this.ecosystem = null;
        this.ecosystemDAO = new EcosystemDAO();
        this.stateDAO = new StateDAO();
        this.currentUsername = "Guest";
    }
    
    public void setCurrentUser(String username) {
        this.currentUsername = username;
    }
    
    /**
     * Configura la tercera especie antes de crear el ecosistema
     */
    public void setTerceraEspecieActiva(boolean activa) {
        this.terceraEspecieActiva = activa;
    }
    
    /**
     * Configura las mutaciones antes de crear el ecosistema
     */
    public void setMutacionesActivas(boolean activas) {
        this.mutacionesActivas = activas;
    }
    
    public void createEcosystem(int maxTurns, String scenario) {
        this.ecosystem = new Ecosystem(maxTurns, scenario);
        
        // Configurar extensiones ANTES de inicializar
        this.ecosystem.setTerceraEspecieActiva(terceraEspecieActiva);
        this.ecosystem.setMutacionesActivas(mutacionesActivas);
        
        this.ecosystem.initialize();
        
        // Guardar configuración inicial
        int numPreys = ecosystem.countPreys();
        int numPredators = ecosystem.countPredators();
        ecosystemDAO.saveConfiguration(scenario, maxTurns, numPreys, numPredators, currentUsername);
        
        // Iniciar registro de estados
        stateDAO.startNewSimulation(scenario, currentUsername);
        stateDAO.saveTurnState(ecosystem);
        
        System.out.println("[CONTROLLER] Ecosystem created:");
        System.out.println("  Scenario: " + scenario);
        System.out.println("  Max turns: " + maxTurns);
        System.out.println("  Initial preys: " + numPreys);
        System.out.println("  Initial predators: " + numPredators);
        if (terceraEspecieActiva) {
            System.out.println("  Initial caimans: " + ecosystem.countCaimans());
        }
        if (mutacionesActivas) {
            System.out.println("  Mutations: ACTIVE");
        }
    }
    
    public boolean executeTurn() {
        if (ecosystem == null) {
            System.err.println("[ERROR] No ecosystem initialized");
            return false;
        }
        
        ecosystem.executeTurn();
        stateDAO.saveTurnState(ecosystem);
        
        int currentTurn = ecosystem.getCurrentTurn();
        int maxTurns = ecosystem.getMaxTurns();
        boolean hasExtinction = ecosystem.hasExtinction();
        
        boolean shouldContinue = (currentTurn < maxTurns) && !hasExtinction;
        
        if (!shouldContinue) {
            stateDAO.saveFinalState(ecosystem, currentTurn);
            
            System.out.println("[CONTROLLER] Simulation ended:");
            System.out.println("  Turns executed: " + currentTurn);
            System.out.println("  Extinction: " + hasExtinction);
            System.out.println("  Final preys: " + ecosystem.countPreys());
            System.out.println("  Final predators: " + ecosystem.countPredators());
            if (terceraEspecieActiva) {
                System.out.println("  Final caimans: " + ecosystem.countCaimans());
            }
        }
        
        return shouldContinue;
    }
    
    public Ecosystem getEcosystem() {
        return ecosystem;
    }
    
    public void resetEcosystem() {
        if (ecosystem != null) {
            String scenario = ecosystem.getScenario();
            int maxTurns = ecosystem.getMaxTurns();
            createEcosystem(maxTurns, scenario);
        }
    }
    
    public boolean hasEcosystem() {
        return ecosystem != null;
    }
    
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
        
        if (ecosystem.isTerceraEspecieActiva()) {
            stats.append("Caimans: ").append(ecosystem.countCaimans()).append("\n");
        }
        
        stats.append("Empty Cells: ").append(ecosystem.countEmptyCells()).append("\n");
        stats.append("Extinction: ").append(ecosystem.hasExtinction()).append("\n");
        
        if (ecosystem.isMutacionesActivas()) {
            stats.append("Mutations: ACTIVE\n");
        }
        
        return stats.toString();
    }
    
    public EcosystemDAO getEcosystemDAO() {
        return ecosystemDAO;
    }
    
    public StateDAO getStateDAO() {
        return stateDAO;
    }
    
    public boolean isTerceraEspecieActiva() {
        return terceraEspecieActiva;
    }
    
    public boolean isMutacionesActivas() {
        return mutacionesActivas;
    }
}