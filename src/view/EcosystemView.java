package view;

import model.*;
import controller.EcosystemController;
import util.AnimalIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal para visualizar y controlar la simulacion del ecosistema.
 * Muestra la matriz 10x10 con representacion grafica de animales.
 */
public class EcosystemView extends JFrame {
    
    // Colores ecologicos
    private static final Color COLOR_BG_LIGHT = new Color(176, 206, 136); // Verde claro
    private static final Color COLOR_BG_DARK = new Color(76, 118, 59);    // Verde medio
    private static final Color COLOR_TEXT_DARK = new Color(4, 57, 21);     // Verde oscuro
    private static final Color COLOR_HIGHLIGHT = new Color(255, 253, 143); // Amarillo suave
    private static final Color COLOR_EMPTY_CELL = new Color(245, 245, 220); // Beige claro
    private static final Color COLOR_PREY = new Color(100, 200, 100);      // Verde claro para presas
    private static final Color COLOR_PREDATOR = new Color(180, 50, 50);    // Rojo oscuro para depredadores
    
    // Componentes principales
    private User currentUser;
    private EcosystemController controller;
    private JPanel panelMatrix;
    private JPanel panelControls;
    private JPanel panelStats;
    private JLabel[][] cellLabels;
    
    // Controles
    private JComboBox<String> comboScenario;
    private JSpinner spinnerTurns;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnStop;
    private JButton btnReport;
    private JButton btnLogout;
    
    // Estadisticas
    private JLabel lblCurrentTurn;
    private JLabel lblPreyCount;
    private JLabel lblPredatorCount;
    private JLabel lblEmptyCount;
    private JProgressBar progressBar;
    private JTextArea txtLog;
    
    // Estado
    private Timer simulationTimer;
    private boolean isRunning;
    
    /**
     * Constructor
     * @param user Usuario que inicio sesion
     */
    public EcosystemView(User user) {
        this.currentUser = user;
        this.controller = new EcosystemController();
        this.cellLabels = new JLabel[10][10];
        this.isRunning = false;
        
        initComponents();
        setLocationRelativeTo(null);
    }
    
    /**
     * Inicializa todos los componentes de la interfaz
     */
    private void initComponents() {
        setTitle("Ecosystem Simulator - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1900, 1000);
        setResizable(true);
        
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior: Controles
        createControlPanel();
        mainPanel.add(panelControls, BorderLayout.NORTH);
        
        // Panel central: Matriz del ecosistema
        createMatrixPanel();
        mainPanel.add(panelMatrix, BorderLayout.CENTER);
        
        // Panel derecho: Estadisticas y log
        createStatsPanel();
        mainPanel.add(panelStats, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    /**
     * Crea el panel de controles superior
     */
    private void createControlPanel() {
        panelControls = new JPanel();
        panelControls.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelControls.setBackground(COLOR_BG_DARK);
        panelControls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Etiqueta de escenario
        JLabel lblScenario = new JLabel("Scenario:");
        lblScenario.setForeground(Color.WHITE);
        lblScenario.setFont(new Font("Arial", Font.BOLD, 14));
        panelControls.add(lblScenario);
        
        // ComboBox de escenarios
        String[] scenarios = {"BALANCED", "PREDATORS_DOM", "PREYS_DOM"};
        comboScenario = new JComboBox<>(scenarios);
        comboScenario.setFont(new Font("Arial", Font.PLAIN, 13));
        comboScenario.setPreferredSize(new Dimension(180, 30));
        panelControls.add(comboScenario);
        
        // Etiqueta de turnos
        JLabel lblTurns = new JLabel("Max Turns:");
        lblTurns.setForeground(Color.WHITE);
        lblTurns.setFont(new Font("Arial", Font.BOLD, 14));
        panelControls.add(lblTurns);
        
        // Spinner de turnos
        spinnerTurns = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
        spinnerTurns.setFont(new Font("Arial", Font.PLAIN, 13));
        spinnerTurns.setPreferredSize(new Dimension(80, 30));
        panelControls.add(spinnerTurns);
        
        // Separador
        panelControls.add(Box.createHorizontalStrut(20));
        
        // Boton Start
        btnStart = new JButton("Start Simulation");
        btnStart.setFont(new Font("Arial", Font.BOLD, 13));
        btnStart.setBackground(COLOR_HIGHLIGHT);
        btnStart.setForeground(COLOR_TEXT_DARK);
        btnStart.setFocusPainted(false);
        btnStart.setBorderPainted(false);
        btnStart.setPreferredSize(new Dimension(150, 35));
        btnStart.addActionListener(e -> startSimulation());
        panelControls.add(btnStart);
        
        // Boton Pause
        btnPause = new JButton("Pause");
        btnPause.setFont(new Font("Arial", Font.BOLD, 13));
        btnPause.setBackground(Color.ORANGE);
        btnPause.setForeground(Color.WHITE);
        btnPause.setFocusPainted(false);
        btnPause.setBorderPainted(false);
        btnPause.setPreferredSize(new Dimension(100, 35));
        btnPause.setEnabled(false);
        btnPause.addActionListener(e -> pauseSimulation());
        panelControls.add(btnPause);
        
        // Boton Stop
        btnStop = new JButton("Stop");
        btnStop.setFont(new Font("Arial", Font.BOLD, 13));
        btnStop.setBackground(new Color(200, 50, 50));
        btnStop.setForeground(Color.WHITE);
        btnStop.setFocusPainted(false);
        btnStop.setBorderPainted(false);
        btnStop.setPreferredSize(new Dimension(100, 35));
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> stopSimulation());
        panelControls.add(btnStop);
        
        // Separador
        panelControls.add(Box.createHorizontalStrut(20));
        
        // Boton Report
        btnReport = new JButton("Generate Report");
        btnReport.setFont(new Font("Arial", Font.BOLD, 13));
        btnReport.setBackground(COLOR_BG_DARK);
        btnReport.setForeground(Color.WHITE);
        btnReport.setFocusPainted(false);
        btnReport.setBorderPainted(false);
        btnReport.setPreferredSize(new Dimension(150, 35));
        btnReport.setEnabled(false);
        btnReport.addActionListener(e -> generateReport());
        panelControls.add(btnReport);
        
        // Boton Logout
        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogout.setBackground(COLOR_TEXT_DARK);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.addActionListener(e -> logout());
        panelControls.add(btnLogout);
    }
    
    /**
     * Crea el panel de la matriz del ecosistema
     */
    private void createMatrixPanel() {
        panelMatrix = new JPanel();
        panelMatrix.setLayout(new GridLayout(10, 10, 2, 2));
        panelMatrix.setBackground(COLOR_TEXT_DARK);
        panelMatrix.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TEXT_DARK, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Crear celdas de la matriz
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(COLOR_EMPTY_CELL);
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                cell.setPreferredSize(new Dimension(60, 60));
                
                cellLabels[i][j] = cell;
                panelMatrix.add(cell);
            }
        }
    }
    
    /**
     * Crea el panel de estadisticas y log
     */
    private void createStatsPanel() {
        panelStats = new JPanel();
        panelStats.setLayout(new BoxLayout(panelStats, BoxLayout.Y_AXIS));
        panelStats.setBackground(Color.WHITE);
        panelStats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TEXT_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelStats.setPreferredSize(new Dimension(300, 0));
        
        // Titulo
        JLabel lblTitle = new JLabel("STATISTICS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelStats.add(lblTitle);
        panelStats.add(Box.createVerticalStrut(15));
        
        // Turno actual
        lblCurrentTurn = createStatLabel("Current Turn: 0");
        panelStats.add(lblCurrentTurn);
        panelStats.add(Box.createVerticalStrut(10));
        
        // Contador de presas
        lblPreyCount = createStatLabel("Preys: 0");
        lblPreyCount.setForeground(COLOR_PREY);
        panelStats.add(lblPreyCount);
        panelStats.add(Box.createVerticalStrut(5));
        
        // Contador de depredadores
        lblPredatorCount = createStatLabel("Predators: 0");
        lblPredatorCount.setForeground(COLOR_PREDATOR);
        panelStats.add(lblPredatorCount);
        panelStats.add(Box.createVerticalStrut(5));
        
        // Celdas vacias
        lblEmptyCount = createStatLabel("Empty Cells: 100");
        panelStats.add(lblEmptyCount);
        panelStats.add(Box.createVerticalStrut(15));
        
        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_BG_DARK);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panelStats.add(progressBar);
        panelStats.add(Box.createVerticalStrut(20));
        
        // Log de eventos
        JLabel lblLog = new JLabel("EVENT LOG");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));
        lblLog.setForeground(COLOR_TEXT_DARK);
        lblLog.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelStats.add(lblLog);
        panelStats.add(Box.createVerticalStrut(5));
        
        txtLog = new JTextArea(15, 25);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        panelStats.add(scrollLog);
        
        panelStats.add(Box.createVerticalGlue());
    }
    
    /**
     * Helper para crear etiquetas de estadisticas
     */
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(COLOR_TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    /**
     * Inicia la simulacion
     */
    private void startSimulation() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this, 
                "Simulation is already running", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener configuracion
        String scenario = (String) comboScenario.getSelectedItem();
        int maxTurns = (Integer) spinnerTurns.getValue();
        
        // Crear ecosistema
        controller.createEcosystem(maxTurns, scenario);
        
        // Actualizar UI
        btnStart.setEnabled(false);
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
        comboScenario.setEnabled(false);
        spinnerTurns.setEnabled(false);
        
        isRunning = true;
        addLog("Simulation started - Scenario: " + scenario);
        
        // Actualizar vista inicial
        updateMatrixView();
        updateStats();
        
        // Iniciar timer para turnos automaticos
        simulationTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeTurn();
            }
        });
        simulationTimer.start();
    }
    
    /**
     * Pausa/Resume la simulacion
     */
    private void pauseSimulation() {
        if (simulationTimer != null) {
            if (simulationTimer.isRunning()) {
                simulationTimer.stop();
                btnPause.setText("Resume");
                addLog("Simulation paused");
            } else {
                simulationTimer.start();
                btnPause.setText("Pause");
                addLog("Simulation resumed");
            }
        }
    }
    
    /**
     * Detiene la simulacion
     */
    private void stopSimulation() {
        if (simulationTimer != null) {
            simulationTimer.stop();
        }
        
        isRunning = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnReport.setEnabled(true);
        comboScenario.setEnabled(true);
        spinnerTurns.setEnabled(true);
        btnPause.setText("Pause");
        
        addLog("Simulation stopped");
    }
    
    /**
     * Ejecuta un turno de simulacion
     */
    private void executeTurn() {
        boolean continueSimulation = controller.executeTurn();
        
        updateMatrixView();
        updateStats();
        
        if (!continueSimulation) {
            stopSimulation();
            showSimulationEndDialog();
        }
    }
    
    /**
     * Actualiza la vista de la matriz
     */
    private void updateMatrixView() {
        Ecosystem ecosystem = controller.getEcosystem();
        if (ecosystem == null) return;
        
        Animal[][] matrix = ecosystem.getMatrix();
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Animal animal = matrix[i][j];
                JLabel cell = cellLabels[i][j];
                
                if (animal == null) {
                    cell.setIcon(AnimalIcon.createEmptyIcon(50));
                    cell.setText("");
                    cell.setBackground(COLOR_EMPTY_CELL);
                } else if (animal instanceof Prey) {
                    cell.setIcon(AnimalIcon.createPreyIcon(50));
                    cell.setText("");
                    cell.setBackground(COLOR_PREY);
                } else if (animal instanceof Predator) {
                    cell.setIcon(AnimalIcon.createPredatorIcon(50));
                    cell.setText("");
                    cell.setBackground(COLOR_PREDATOR);
                }
            }
        }
    }
    
    /**
     * Actualiza las estadisticas
     */
    private void updateStats() {
        Ecosystem ecosystem = controller.getEcosystem();
        if (ecosystem == null) return;
        
        int currentTurn = ecosystem.getCurrentTurn();
        int maxTurns = ecosystem.getMaxTurns();
        int preys = ecosystem.countPreys();
        int predators = ecosystem.countPredators();
        int empty = ecosystem.countEmptyCells();
        
        lblCurrentTurn.setText("Current Turn: " + currentTurn + "/" + maxTurns);
        lblPreyCount.setText("Preys: " + preys);
        lblPredatorCount.setText("Predators: " + predators);
        lblEmptyCount.setText("Empty Cells: " + empty);
        
        int progress = (int) ((currentTurn / (double) maxTurns) * 100);
        progressBar.setValue(progress);
        
        // Agregar evento al log
        String event = String.format("Turn %d: P=%d D=%d E=%d", 
            currentTurn, preys, predators, empty);
        addLog(event);
    }
    
    /**
     * Agrega un mensaje al log
     */
    private void addLog(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    /**
     * Muestra dialogo al finalizar la simulacion
     */
    private void showSimulationEndDialog() {
        Ecosystem ecosystem = controller.getEcosystem();
        
        String message = "Simulation completed!\n\n";
        message += "Total turns executed: " + ecosystem.getCurrentTurn() + "\n";
        message += "Final preys: " + ecosystem.countPreys() + "\n";
        message += "Final predators: " + ecosystem.countPredators() + "\n\n";
        
        if (ecosystem.hasExtinction()) {
            if (ecosystem.countPreys() == 0) {
                message += "Result: Preys went extinct!";
            } else {
                message += "Result: Predators went extinct!";
            }
        } else {
            message += "Result: Ecosystem survived!";
        }
        
        JOptionPane.showMessageDialog(this, message, 
            "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Genera reporte de la simulacion
     */
    private void generateReport() {
        JOptionPane.showMessageDialog(this,
            "Report generation will be implemented in Phase 7",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Cierra sesion y regresa al login
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (simulationTimer != null) {
                simulationTimer.stop();
            }
            
            this.dispose();
            new LoginView().setVisible(true);
        }
    }
}