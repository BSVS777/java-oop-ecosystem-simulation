package view;

import util.SimulationAnalyzer;
import util.SimulationAnalyzer.ComparativeAnalysis;
import util.SimulationAnalyzer.ScenarioAnalysis;
import javax.swing.*;
import java.awt.*;

/**
 * Vista para análisis comparativo automático entre escenarios.
 * Ejecuta múltiples simulaciones y muestra resultados estadísticos.
 */
public class ComparisonAnalysisView extends JFrame {
    
    private static final Color COLOR_BG = new Color(176, 206, 136);
    private static final Color COLOR_DARK = new Color(4, 57, 21);
    private static final Color COLOR_PANEL = Color.WHITE;
    
    private JSpinner spinnerTurns;
    private JCheckBox chkThirdSpecies;
    private JCheckBox chkMutations;
    private JButton btnAnalyze;
    private JButton btnClose;
    private JTextArea txtResults;
    private JProgressBar progressBar;
    
    private ComparativeAnalysis currentAnalysis;
    
    public ComparisonAnalysisView() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Comparative Analysis - Ecosystem Simulator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);
        mainPanel.add(createResultsPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel lblTitle = new JLabel("AUTOMATIC COMPARATIVE ANALYSIS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle);
        
        panel.add(Box.createHorizontalStrut(20));
        
        // Max turns
        JLabel lblTurns = new JLabel("Max Turns:");
        lblTurns.setForeground(Color.WHITE);
        lblTurns.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTurns);
        
        spinnerTurns = new JSpinner(new SpinnerNumberModel(20, 10, 50, 5));
        spinnerTurns.setPreferredSize(new Dimension(70, 25));
        panel.add(spinnerTurns);
        
        panel.add(Box.createHorizontalStrut(15));
        
        // Options
        chkThirdSpecies = new JCheckBox("Include 3rd Species");
        chkThirdSpecies.setForeground(Color.WHITE);
        chkThirdSpecies.setBackground(COLOR_DARK);
        panel.add(chkThirdSpecies);
        
        chkMutations = new JCheckBox("Include Mutations");
        chkMutations.setForeground(Color.WHITE);
        chkMutations.setBackground(COLOR_DARK);
        panel.add(chkMutations);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DARK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitle = new JLabel("ANALYSIS RESULTS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_DARK);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitle, BorderLayout.NORTH);
        
        txtResults = new JTextArea();
        txtResults.setEditable(false);
        txtResults.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResults.setLineWrap(false);
        txtResults.setText("Click 'Start Analysis' to run comparative simulations.\n\n" +
                          "This will execute 10 simulations for each scenario:\n" +
                          "  • BALANCED\n" +
                          "  • PREDATORS_DOMINANT\n" +
                          "  • PREYS_DOMINANT\n\n" +
                          "Total: 30 simulations (may take 1-2 minutes)\n\n" +
                          "Results will include:\n" +
                          "  - Extinction rates and average turns\n" +
                          "  - Population statistics\n" +
                          "  - Stability metrics\n" +
                          "  - Comparative conclusions");
        
        JScrollPane scrollPane = new JScrollPane(txtResults);
        scrollPane.setPreferredSize(new Dimension(850, 500));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_DARK);
        progressBar.setString("Ready");
        panel.add(progressBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(COLOR_BG);
        
        btnAnalyze = new JButton("Start Analysis");
        btnAnalyze.setFont(new Font("Arial", Font.BOLD, 14));
        btnAnalyze.setBackground(new Color(76, 175, 80));
        btnAnalyze.setForeground(Color.BLACK);
        btnAnalyze.setFocusPainted(false);
        btnAnalyze.setPreferredSize(new Dimension(150, 35));
        btnAnalyze.addActionListener(e -> startAnalysis());
        
        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.setBackground(COLOR_DARK);
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());
        
        panel.add(btnAnalyze);
        panel.add(btnClose);
        
        return panel;
    }
    
    private void startAnalysis() {
        int maxTurns = (Integer) spinnerTurns.getValue();
        boolean withThirdSpecies = chkThirdSpecies.isSelected();
        boolean withMutations = chkMutations.isSelected();
        
        btnAnalyze.setEnabled(false);
        progressBar.setString("Running simulations...");
        progressBar.setIndeterminate(true);
        txtResults.setText("Starting comparative analysis...\n\n");
        
        SwingWorker<ComparativeAnalysis, String> worker = new SwingWorker<>() {
            @Override
            protected ComparativeAnalysis doInBackground() throws Exception {
                publish("Analyzing BALANCED scenario...\n");
                Thread.sleep(100);
                
                ComparativeAnalysis analysis = SimulationAnalyzer.executeFullAnalysis(
                    maxTurns, withThirdSpecies, withMutations
                );
                
                return analysis;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String msg : chunks) {
                    txtResults.append(msg);
                }
            }
            
            @Override
            protected void done() {
                try {
                    currentAnalysis = get();
                    displayResults(currentAnalysis);
                    
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Analysis Complete");
                    
                    JOptionPane.showMessageDialog(
                        ComparisonAnalysisView.this,
                        "Comparative analysis completed successfully!\n" +
                        "30 simulations executed.\n\n" +
                        "Review the detailed results below.",
                        "Analysis Complete",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        ComparisonAnalysisView.this,
                        "Error during analysis: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    e.printStackTrace();
                } finally {
                    btnAnalyze.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayResults(ComparativeAnalysis analysis) {
        txtResults.setText("");
        
        txtResults.append("╔═══════════════════════════════════════════════════════╗\n");
        txtResults.append("║        COMPARATIVE ANALYSIS RESULTS                   ║\n");
        txtResults.append("╚═══════════════════════════════════════════════════════╝\n\n");
        
        txtResults.append(formatScenarioResults("BALANCED SCENARIO", analysis.balanced));
        txtResults.append("\n" + "─".repeat(60) + "\n\n");
        
        txtResults.append(formatScenarioResults("PREDATORS DOMINANT SCENARIO", analysis.predatorsDominant));
        txtResults.append("\n" + "─".repeat(60) + "\n\n");
        
        txtResults.append(formatScenarioResults("PREYS DOMINANT SCENARIO", analysis.preysDominant));
        txtResults.append("\n" + "═".repeat(60) + "\n\n");
        
        txtResults.append(analysis.conclusions);
        
        txtResults.setCaretPosition(0);
    }
    
    private String formatScenarioResults(String title, ScenarioAnalysis analysis) {
        return String.format(
            "┌─ %s\n" +
            "│\n" +
            "│ Simulations Executed: %d\n" +
            "│\n" +
            "│ EXTINCTION METRICS:\n" +
            "│   • Total Extinctions: %d (%.1f%%)\n" +
            "│   • Prey Extinctions: %d\n" +
            "│   • Predator Extinctions: %d\n" +
            "│   • Avg Extinction Turn: %.1f\n" +
            "│\n" +
            "│ POPULATION METRICS:\n" +
            "│   • Avg Final Preys: %.1f\n" +
            "│   • Avg Final Predators: %.1f\n" +
            "│   • Population Stability: %.2f (lower is better)\n" +
            "│\n" +
            "│ ECOSYSTEM METRICS:\n" +
            "│   • Avg Occupation Rate: %.1f%%\n" +
            "│   • Dominant Species: %s\n" +
            "│   • Dominance Score: %.2f\n" +
            "└─",
            title,
            analysis.totalSimulations,
            analysis.extinctionsOccurred, analysis.extinctionRate * 100,
            analysis.preyExtinctions,
            analysis.predatorExtinctions,
            analysis.avgExtinctionTurn,
            analysis.avgFinalPreys,
            analysis.avgFinalPredators,
            analysis.populationStability,
            analysis.avgOccupationRate * 100,
            analysis.dominantSpecies,
            analysis.dominanceScore
        );
    }
    
    public ComparativeAnalysis getCurrentAnalysis() {
        return currentAnalysis;
    }
}