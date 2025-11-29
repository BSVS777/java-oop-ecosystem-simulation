package util;

import model.Ecosystem;
import controller.EcosystemController;
import data.StateDAO;
import java.util.*;

/**
 * Analizador automático de simulaciones para comparación entre escenarios.
 * Ejecuta múltiples simulaciones y genera métricas estadísticas.
 */
public class SimulationAnalyzer {
    
    private static final int SIMULATIONS_PER_SCENARIO = 10; // Número de simulaciones por escenario
    
    /**
     * Resultado de análisis de un escenario específico
     */
    public static class ScenarioAnalysis {
        public String scenarioName;
        public int totalSimulations;
        
        // Métricas de extinción
        public int extinctionsOccurred;
        public int preyExtinctions;
        public int predatorExtinctions;
        public double extinctionRate;
        public double avgExtinctionTurn;
        
        // Métricas de población
        public double avgFinalPreys;
        public double avgFinalPredators;
        public double avgFinalCaimans;
        public double populationStability; // Desviación estándar normalizada
        
        // Métricas de ocupación
        public double avgOccupationRate;
        public double avgTurnsToStabilize;
        
        // Análisis de dominancia
        public String dominantSpecies; // "PREYS", "PREDATORS", "BALANCED", "EXTINCT"
        public double dominanceScore;
        
        @Override
        public String toString() {
            return String.format(
                "=== %s ===\n" +
                "Simulations: %d\n" +
                "Extinction Rate: %.1f%%\n" +
                "Avg Extinction Turn: %.1f\n" +
                "Avg Final Population - Preys: %.1f | Predators: %.1f\n" +
                "Dominant Species: %s (Score: %.2f)\n" +
                "Population Stability: %.2f\n" +
                "Avg Occupation: %.1f%%\n",
                scenarioName, totalSimulations,
                extinctionRate * 100,
                avgExtinctionTurn,
                avgFinalPreys, avgFinalPredators,
                dominantSpecies, dominanceScore,
                populationStability,
                avgOccupationRate * 100
            );
        }
    }
    
    /**
     * Análisis comparativo completo entre todos los escenarios
     */
    public static class ComparativeAnalysis {
        public ScenarioAnalysis balanced;
        public ScenarioAnalysis predatorsDominant;
        public ScenarioAnalysis preysDominant;
        
        public String mostStableScenario;
        public String fastestExtinctionScenario;
        public String highestOccupationScenario;
        
        public String conclusions;
        
        public ComparativeAnalysis(ScenarioAnalysis balanced, 
                                  ScenarioAnalysis predatorsDom, 
                                  ScenarioAnalysis preysDom) {
            this.balanced = balanced;
            this.predatorsDominant = predatorsDom;
            this.preysDominant = preysDom;
            
            calculateComparativeMetrics();
            generateConclusions();
        }
        
        private void calculateComparativeMetrics() {
            // Escenario más estable (menor tasa de extinción)
            double minExtinction = Math.min(balanced.extinctionRate, 
                                   Math.min(predatorsDominant.extinctionRate, 
                                           preysDominant.extinctionRate));
            
            if (balanced.extinctionRate == minExtinction) {
                mostStableScenario = "BALANCED";
            } else if (predatorsDominant.extinctionRate == minExtinction) {
                mostStableScenario = "PREDATORS_DOM";
            } else {
                mostStableScenario = "PREYS_DOM";
            }
            
            // Escenario con extinción más rápida
            double minTurns = Math.min(balanced.avgExtinctionTurn,
                              Math.min(predatorsDominant.avgExtinctionTurn,
                                      preysDominant.avgExtinctionTurn));
            
            if (predatorsDominant.avgExtinctionTurn == minTurns) {
                fastestExtinctionScenario = "PREDATORS_DOM";
            } else if (preysDominant.avgExtinctionTurn == minTurns) {
                fastestExtinctionScenario = "PREYS_DOM";
            } else {
                fastestExtinctionScenario = "BALANCED";
            }
            
            // Escenario con mayor ocupación
            double maxOccupation = Math.max(balanced.avgOccupationRate,
                                   Math.max(predatorsDominant.avgOccupationRate,
                                           preysDominant.avgOccupationRate));
            
            if (preysDominant.avgOccupationRate == maxOccupation) {
                highestOccupationScenario = "PREYS_DOM";
            } else if (balanced.avgOccupationRate == maxOccupation) {
                highestOccupationScenario = "BALANCED";
            } else {
                highestOccupationScenario = "PREDATORS_DOM";
            }
        }
        
        private void generateConclusions() {
            StringBuilder sb = new StringBuilder();
            
            sb.append("=== COMPARATIVE ANALYSIS CONCLUSIONS ===\n\n");
            
            // 1. Equilibrio
            sb.append("1. ECOSYSTEM EQUILIBRIUM:\n");
            if (balanced.extinctionRate < 0.5) {
                sb.append("   ✓ The BALANCED scenario maintains good equilibrium with ")
                  .append(String.format("%.0f%%", (1 - balanced.extinctionRate) * 100))
                  .append(" survival rate.\n");
                sb.append("   → Both species can coexist when initial populations are similar.\n");
            } else {
                sb.append("   ✗ Even in BALANCED scenarios, extinction occurs in ")
                  .append(String.format("%.0f%%", balanced.extinctionRate * 100))
                  .append(" of cases.\n");
                sb.append("   → This indicates that reproduction rules may favor ")
                  .append(balanced.dominantSpecies).append(".\n");
            }
            
            sb.append("\n2. PREDATORS DOMINANT SCENARIO:\n");
            sb.append("   • Extinction velocity: ")
              .append(String.format("%.1f turns average", predatorsDominant.avgExtinctionTurn))
              .append("\n");
            
            if (predatorsDominant.preyExtinctions > predatorsDominant.predatorExtinctions) {
                sb.append("   → Preys die quickly due to overhunting.\n");
                sb.append("   → Subsequently, predators also die from lack of food.\n");
                sb.append("   ⚠ High predator density leads to ecosystem collapse.\n");
            } else {
                sb.append("   → Surprisingly, predators die first in some cases.\n");
                sb.append("   → This suggests preys can evade effectively when outnumbered.\n");
            }
            
            sb.append("\n3. PREYS DOMINANT SCENARIO:\n");
            sb.append("   • Matrix occupation: ")
              .append(String.format("%.0f%%", preysDominant.avgOccupationRate * 100))
              .append("\n");
            
            if (preysDominant.avgOccupationRate > 0.7) {
                sb.append("   → Preys rapidly fill the matrix (overpopulation).\n");
                sb.append("   → Overpopulation triggers natural death mechanisms.\n");
                sb.append("   ⚠ Without predators, ecosystem becomes saturated.\n");
            }
            
            if (preysDominant.extinctionRate > 0.5) {
                sb.append("   → However, predators still die from starvation in ")
                  .append(String.format("%.0f%%", preysDominant.extinctionRate * 100))
                  .append(" of cases.\n");
                sb.append("   → Too many preys doesn't guarantee predator survival.\n");
            }
            
            sb.append("\n4. TRANSVERSAL ANALYSIS:\n");
            sb.append("   • Most stable: ").append(mostStableScenario).append("\n");
            sb.append("   • Fastest collapse: ").append(fastestExtinctionScenario).append("\n");
            sb.append("   • Highest occupation: ").append(highestOccupationScenario).append("\n\n");
            
            sb.append("   KEY FACTORS:\n");
            sb.append("   1. Initial population balance is critical for survival.\n");
            sb.append("   2. Reproduction rules have more impact than initial quantities.\n");
            sb.append("   3. Predators require specific prey:predator ratios (1.5:1 to 3:1).\n");
            sb.append("   4. Overpopulation mechanisms prevent matrix saturation.\n");
            
            this.conclusions = sb.toString();
        }
        
        @Override
        public String toString() {
            return balanced.toString() + "\n" +
                   predatorsDominant.toString() + "\n" +
                   preysDominant.toString() + "\n" +
                   conclusions;
        }
    }
    
    /**
     * Ejecuta análisis completo de un escenario
     */
    public static ScenarioAnalysis analyzeScenario(String scenario, int maxTurns, 
                                                   boolean withThirdSpecies, 
                                                   boolean withMutations) {
        System.out.println("\n[ANALYZER] Starting analysis for: " + scenario);
        
        ScenarioAnalysis analysis = new ScenarioAnalysis();
        analysis.scenarioName = scenario;
        analysis.totalSimulations = SIMULATIONS_PER_SCENARIO;
        
        List<Integer> extinctionTurns = new ArrayList<>();
        List<Integer> finalPreys = new ArrayList<>();
        List<Integer> finalPredators = new ArrayList<>();
        List<Integer> finalCaimans = new ArrayList<>();
        List<Double> occupationRates = new ArrayList<>();
        
        int totalExtinctions = 0;
        int preyExtinctionCount = 0;
        int predatorExtinctionCount = 0;
        
        // Ejecutar múltiples simulaciones
        for (int i = 0; i < SIMULATIONS_PER_SCENARIO; i++) {
            EcosystemController controller = new EcosystemController();
            controller.setCurrentUser("AnalysisBot");
            controller.setTerceraEspecieActiva(withThirdSpecies);
            controller.setMutacionesActivas(withMutations);
            controller.createEcosystem(maxTurns, scenario);
            
            // Ejecutar hasta terminar
            int turnCount = 0;
            while (turnCount < maxTurns && controller.executeTurn()) {
                turnCount++;
            }
            
            Ecosystem eco = controller.getEcosystem();
            
            // Recolectar datos
            finalPreys.add(eco.countPreys());
            finalPredators.add(eco.countPredators());
            finalCaimans.add(eco.countCaimans());
            
            int occupied = 100 - eco.countEmptyCells();
            occupationRates.add(occupied / 100.0);
            
            if (eco.hasExtinction()) {
                totalExtinctions++;
                extinctionTurns.add(eco.getCurrentTurn());
                
                if (eco.countPreys() == 0) {
                    preyExtinctionCount++;
                } else {
                    predatorExtinctionCount++;
                }
            } else {
                extinctionTurns.add(maxTurns); // No extinción
            }
            
            System.out.println("  Simulation " + (i+1) + "/" + SIMULATIONS_PER_SCENARIO + 
                             " completed. Extinction: " + eco.hasExtinction());
        }
        
        // Calcular métricas
        analysis.extinctionsOccurred = totalExtinctions;
        analysis.preyExtinctions = preyExtinctionCount;
        analysis.predatorExtinctions = predatorExtinctionCount;
        analysis.extinctionRate = totalExtinctions / (double) SIMULATIONS_PER_SCENARIO;
        
        analysis.avgExtinctionTurn = extinctionTurns.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(maxTurns);
        
        analysis.avgFinalPreys = finalPreys.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        analysis.avgFinalPredators = finalPredators.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        analysis.avgFinalCaimans = finalCaimans.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        analysis.avgOccupationRate = occupationRates.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
        
        // Calcular estabilidad (desviación estándar de población final)
        double preyStdDev = calculateStdDev(finalPreys);
        double predatorStdDev = calculateStdDev(finalPredators);
        analysis.populationStability = (preyStdDev + predatorStdDev) / 2.0;
        
        // Determinar especie dominante
        if (analysis.avgFinalPreys == 0 || analysis.avgFinalPredators == 0) {
            analysis.dominantSpecies = "EXTINCT";
            analysis.dominanceScore = 0;
        } else {
            double ratio = analysis.avgFinalPreys / analysis.avgFinalPredators;
            if (ratio >= 1.5 && ratio <= 3.0) {
                analysis.dominantSpecies = "BALANCED";
                analysis.dominanceScore = 1.0 - Math.abs(ratio - 2.25) / 2.25;
            } else if (ratio > 3.0) {
                analysis.dominantSpecies = "PREYS";
                analysis.dominanceScore = Math.min(ratio / 10.0, 1.0);
            } else {
                analysis.dominantSpecies = "PREDATORS";
                analysis.dominanceScore = Math.min(3.0 / ratio / 10.0, 1.0);
            }
        }
        
        System.out.println("[ANALYZER] Analysis complete for: " + scenario);
        return analysis;
    }
    
    /**
     * Ejecuta análisis comparativo completo de todos los escenarios
     */
    public static ComparativeAnalysis executeFullAnalysis(int maxTurns, 
                                                         boolean withThirdSpecies,
                                                         boolean withMutations) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   STARTING FULL COMPARATIVE ANALYSIS     ║");
        System.out.println("╚══════════════════════════════════════════╝");
        
        ScenarioAnalysis balanced = analyzeScenario("BALANCED", maxTurns, 
                                                    withThirdSpecies, withMutations);
        ScenarioAnalysis predatorsDom = analyzeScenario("PREDATORS_DOM", maxTurns, 
                                                        withThirdSpecies, withMutations);
        ScenarioAnalysis preysDom = analyzeScenario("PREYS_DOM", maxTurns, 
                                                    withThirdSpecies, withMutations);
        
        ComparativeAnalysis comparative = new ComparativeAnalysis(balanced, predatorsDom, preysDom);
        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   COMPARATIVE ANALYSIS COMPLETED         ║");
        System.out.println("╚══════════════════════════════════════════╝");
        
        return comparative;
    }
    
    /**
     * Calcula desviación estándar de una lista de enteros
     */
    private static double calculateStdDev(List<Integer> values) {
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
        return Math.sqrt(variance);
    }
}