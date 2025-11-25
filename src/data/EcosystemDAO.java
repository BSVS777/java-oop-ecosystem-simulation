package data;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object para configuraciones del ecosistema.
 * Maneja lectura y escritura del archivo ecosistema.txt
 */
public class EcosystemDAO {
    
    private static final String CONFIG_FILE = "ecosistema.txt";
    
    /**
     * Guarda la configuracion inicial de un ecosistema
     * @param scenario Tipo de escenario
     * @param maxTurns Numero maximo de turnos
     * @param numPreys Cantidad inicial de presas
     * @param numPredators Cantidad inicial de depredadores
     * @param username Usuario que creo la simulacion
     * @return true si se guardo exitosamente
     */
    public boolean saveConfiguration(String scenario, int maxTurns, 
                                     int numPreys, int numPredators, String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE, true))) {
            
            // Formato: timestamp|username|scenario|maxTurns|preys|predators
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String line = String.format("%s|%s|%s|%d|%d|%d",
                timestamp, username, scenario, maxTurns, numPreys, numPredators);
            
            writer.write(line);
            writer.newLine();
            
            System.out.println("[DAO] Configuration saved to " + CONFIG_FILE);
            return true;
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save configuration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga la ultima configuracion guardada
     * @return Map con la configuracion o null si no existe
     */
    public Map<String, String> loadLastConfiguration() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println("[INFO] No configuration file found");
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String lastLine = null;
            String line;
            
            // Leer hasta la ultima linea
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
            
            if (lastLine != null) {
                return parseConfiguration(lastLine);
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load configuration: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Carga todas las configuraciones guardadas
     * @return Array de Maps con todas las configuraciones
     */
    public Map<String, String>[] loadAllConfigurations() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return new Map[0];
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            java.util.List<Map<String, String>> configs = new java.util.ArrayList<>();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Map<String, String> config = parseConfiguration(line);
                    if (config != null) {
                        configs.add(config);
                    }
                }
            }
            
            System.out.println("[DAO] Loaded " + configs.size() + " configurations");
            return configs.toArray(new Map[0]);
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load configurations: " + e.getMessage());
            return new Map[0];
        }
    }
    
    /**
     * Parsea una linea de configuracion
     * @param line Linea del archivo
     * @return Map con los datos parseados
     */
    private Map<String, String> parseConfiguration(String line) {
        try {
            String[] parts = line.split("\\|");
            
            if (parts.length != 6) {
                System.err.println("[WARNING] Invalid configuration format: " + line);
                return null;
            }
            
            Map<String, String> config = new HashMap<>();
            config.put("timestamp", parts[0]);
            config.put("username", parts[1]);
            config.put("scenario", parts[2]);
            config.put("maxTurns", parts[3]);
            config.put("numPreys", parts[4]);
            config.put("numPredators", parts[5]);
            
            return config;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse configuration: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene configuraciones por usuario
     * @param username Nombre de usuario
     * @return Array de configuraciones del usuario
     */
    public Map<String, String>[] getConfigurationsByUser(String username) {
        Map<String, String>[] allConfigs = loadAllConfigurations();
        java.util.List<Map<String, String>> userConfigs = new java.util.ArrayList<>();
        
        for (Map<String, String> config : allConfigs) {
            if (config.get("username").equals(username)) {
                userConfigs.add(config);
            }
        }
        
        return userConfigs.toArray(new Map[0]);
    }
    
    /**
     * Obtiene configuraciones por escenario
     * @param scenario Tipo de escenario
     * @return Array de configuraciones del escenario
     */
    public Map<String, String>[] getConfigurationsByScenario(String scenario) {
        Map<String, String>[] allConfigs = loadAllConfigurations();
        java.util.List<Map<String, String>> scenarioConfigs = new java.util.ArrayList<>();
        
        for (Map<String, String> config : allConfigs) {
            if (config.get("scenario").equals(scenario)) {
                scenarioConfigs.add(config);
            }
        }
        
        return scenarioConfigs.toArray(new Map[0]);
    }
    
    /**
     * Limpia el archivo de configuraciones
     * @return true si se limpio exitosamente
     */
    public boolean clearConfigurations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write("");
            System.out.println("[INFO] Configuration file cleared");
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to clear configuration file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene estadisticas de configuraciones guardadas
     * @return String con estadisticas formateadas
     */
    public String getStatistics() {
        Map<String, String>[] configs = loadAllConfigurations();
        
        if (configs.length == 0) {
            return "No configurations saved yet";
        }
        
        // Contar por escenario
        int balanced = 0, predatorsDom = 0, preysDom = 0;
        
        for (Map<String, String> config : configs) {
            String scenario = config.get("scenario");
            switch (scenario) {
                case "BALANCED":
                    balanced++;
                    break;
                case "PREDATORS_DOM":
                    predatorsDom++;
                    break;
                case "PREYS_DOM":
                    preysDom++;
                    break;
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== CONFIGURATION STATISTICS ===\n");
        stats.append("Total simulations: ").append(configs.length).append("\n");
        stats.append("Balanced: ").append(balanced).append("\n");
        stats.append("Predators Dominant: ").append(predatorsDom).append("\n");
        stats.append("Preys Dominant: ").append(preysDom).append("\n");
        
        return stats.toString();
    }
}