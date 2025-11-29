package view;

import model.*;
import controller.EcosystemController;
import javax.swing.*;
import java.awt.*;
import util.ImagePanel;

/**
 * Vista mejorada del ecosistema con imágenes, fondo de bosque y funcionalidades completas.
 * Incluye soporte para tercera especie (Caimán), mutaciones genéticas y reportes PDF.
 */
public class EcosystemView extends JFrame {
    
    // Tu paleta de colores original
    private static final Color COLOR_BG_LIGHT = new Color(176, 206, 136);
    private static final Color COLOR_BG_DARK = new Color(76, 118, 59);
    private static final Color COLOR_TEXT_DARK = new Color(4, 57, 21);
    private static final Color COLOR_HIGHLIGHT = new Color(255, 253, 143);
    
    // Componentes principales
    private User currentUser;
    private EcosystemController controller;
    private JPanel panelMatrixContainer;
    private JPanel panelMatrix;
    private JLabel[][] cellLabels;
    private JLabel backgroundLabel; // Para el fondo de bosque
    
    // Controles
    private JComboBox<String> comboScenario;
    private JSpinner spinnerTurns;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnStop;
    private JButton btnReport;
    private JButton btnLogout;
    private JButton btnToggleTerceraEspecie;
    private JButton btnToggleMutaciones;
    
    // Estadísticas
    private JLabel lblCurrentTurn;
    private JLabel lblPreyCount;
    private JLabel lblPredatorCount;
    private JLabel lblCaimanCount; // Nuevo: contador de caimanes
    private JLabel lblEmptyCount;
    private JProgressBar progressBar;
    private JTextArea txtLog;
    
    // Estado
    private Timer simulationTimer;
    private boolean isRunning;
    private boolean terceraEspecieActiva = false;
    private boolean mutacionesActivas = false;
    
    public EcosystemView(User user) {
        this.currentUser = user;
        this.controller = new EcosystemController();
        this.controller.setCurrentUser(user.getName());
        this.cellLabels = new JLabel[10][10];
        this.isRunning = false;
        
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Ecosystem Simulator - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1650, 950);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);
        mainPanel.add(createMatrixPanelWithBackground(), BorderLayout.CENTER);
        mainPanel.add(createStatsPanel(), BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Scenario
        panel.add(createLabel("Scenario:"));
        comboScenario = new JComboBox<>(new String[]{"BALANCED", "PREDATORS_DOM", "PREYS_DOM"});
        comboScenario.setPreferredSize(new Dimension(150, 28));
        panel.add(comboScenario);
        
        // Max Turns
        panel.add(createLabel("Max Turns:"));
        spinnerTurns = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
        spinnerTurns.setPreferredSize(new Dimension(70, 28));
        panel.add(spinnerTurns);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // Toggle buttons
        btnToggleTerceraEspecie = createToggleButton("3rd Species: OFF", new Color(100, 149, 237));
        btnToggleTerceraEspecie.addActionListener(e -> toggleTerceraEspecie());
        panel.add(btnToggleTerceraEspecie);
        
        btnToggleMutaciones = createToggleButton("Mutations: OFF", new Color(147, 112, 219));
        btnToggleMutaciones.addActionListener(e -> toggleMutaciones());
        panel.add(btnToggleMutaciones);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // Control buttons
        btnStart = createButton("Start", COLOR_HIGHLIGHT, COLOR_TEXT_DARK, 100);
        btnStart.addActionListener(e -> startSimulation());
        panel.add(btnStart);
        
        btnPause = createButton("Pause", Color.ORANGE, Color.BLACK, 90);
        btnPause.setEnabled(false);
        btnPause.addActionListener(e -> pauseSimulation());
        panel.add(btnPause);
        
        btnStop = createButton("Stop", new Color(200, 50, 50), Color.BLACK, 80);
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> stopSimulation());
        panel.add(btnStop);
        
        panel.add(Box.createHorizontalStrut(10));
        
        btnReport = createButton("Report", COLOR_BG_DARK, Color.BLACK, 90);
        btnReport.setEnabled(false);
        btnReport.addActionListener(e -> generateReport());
        panel.add(btnReport);
        
        btnLogout = createButton("Logout", COLOR_TEXT_DARK, Color.BLACK, 90);
        btnLogout.addActionListener(e -> logout());
        panel.add(btnLogout);
        
        return panel;
    }
    
    private JPanel createMatrixPanelWithBackground() {
        panelMatrixContainer = new JPanel(new BorderLayout());
        panelMatrixContainer.setBackground(COLOR_BG_LIGHT);
        panelMatrixContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // 1. Instanciar tu ImagePanel con la ruta de la imagen
        ImagePanel imagePanel = new ImagePanel("/imagenes/bosque.png");
        
        // 2. Crear la matriz (Grid)
        panelMatrix = createMatrixGrid();
        
        // 3. Añadir la matriz al ImagePanel. 
        // Al tener GridBagLayout (definido en ImagePanel), se centrará automáticamente.
        imagePanel.add(panelMatrix);
        
        // Guardamos referencia para el fondo (opcional, por si quieres cambiarlo luego)
        // backgroundLabel ya no es necesario como JLabel, ahora el contenedor es imagePanel
        
        panelMatrixContainer.add(imagePanel, BorderLayout.CENTER);
        
        return panelMatrixContainer;
    }
    
    // Método para cuando falla la carga (puedes mantenerlo igual o simplificarlo)
    private void createMatrixWithoutBackground() {
        JPanel centerPanel = new JPanel(new GridBagLayout()); // Centrado mejorado
        centerPanel.setBackground(COLOR_BG_LIGHT);
        
        panelMatrix = createMatrixGrid();
        centerPanel.add(panelMatrix);
        
        panelMatrixContainer.add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createMatrixGrid() {
        // Espacio entre celdas (hgap, vgap) reducido a 1 o 2 para ver líneas finas
        JPanel grid = new JPanel(new GridLayout(10, 10, 2, 2));
        
        // --- CAMBIO CLAVE 1: El contenedor de la rejilla debe ser TRANSPARENTE ---
        grid.setOpaque(false); 
        
        // Borde exterior sutil para contener la matriz visualmente
        grid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 40, 20, 180), 5), // Marco madera oscuro
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Tamaño fijo para asegurar que cuadra bien en pantalla
        grid.setPreferredSize(new Dimension(650, 650));
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                
                // --- CAMBIO CLAVE 2: Manejo de opacidad de celdas ---
                // Para que el color semi-transparente funcione sobre el fondo, 
                // la celda debe ser opaca, pero usaremos un color con Alpha.
                cell.setOpaque(true);
                
                // Color base: Blanco hueso muy transparente (Alpha 120 de 255)
                // Esto permite ver el bosque a través de las celdas vacías
                cell.setBackground(new Color(255, 253, 208, 120)); 
                
                cell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                
                // Borde de celda más sutil
                cell.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33, 100), 1));
                
                cell.setPreferredSize(new Dimension(60, 60));
                
                cellLabels[i][j] = cell;
                grid.add(cell);
            }
        }
        
        return grid;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TEXT_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(300, 0));
        
        // Title
        JLabel lblTitle = new JLabel("STATISTICS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));
        
        // Stats labels
        lblCurrentTurn = createStatLabel("Current Turn: 0");
        panel.add(lblCurrentTurn);
        panel.add(Box.createVerticalStrut(10));
        
        lblPreyCount = createStatLabel("Preys: 0");
        lblPreyCount.setForeground(new Color(100, 200, 100));
        panel.add(lblPreyCount);
        panel.add(Box.createVerticalStrut(5));
        
        lblPredatorCount = createStatLabel("Predators: 0");
        lblPredatorCount.setForeground(new Color(180, 50, 50));
        panel.add(lblPredatorCount);
        panel.add(Box.createVerticalStrut(5));
        
        // Contador de Caimanes (oculto inicialmente)
        lblCaimanCount = createStatLabel("🐊 Caimans: 0");
        lblCaimanCount.setForeground(new Color(70, 130, 180));
        lblCaimanCount.setVisible(false); // Oculto hasta que se active
        panel.add(lblCaimanCount);
        panel.add(Box.createVerticalStrut(5));
        
        lblEmptyCount = createStatLabel("Empty Cells: 100");
        panel.add(lblEmptyCount);
        panel.add(Box.createVerticalStrut(15));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_BG_DARK);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(20));
        
        // Event log
        JLabel lblLog = new JLabel("EVENT LOG");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));
        lblLog.setForeground(COLOR_TEXT_DARK);
        lblLog.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblLog);
        panel.add(Box.createVerticalStrut(5));
        
        txtLog = new JTextArea(15, 25);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        panel.add(scrollLog);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    // Helper methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }
    
    private JButton createButton(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, 30));
        return btn;
    }
    
    private JButton createToggleButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 30));
        return btn;
    }
    
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(COLOR_TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // Simulation control methods
    private void startSimulation() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this, "Simulation is already running", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String scenario = (String) comboScenario.getSelectedItem();
        int maxTurns = (Integer) spinnerTurns.getValue();
        
        // Configurar extensiones en el controlador ANTES de crear el ecosistema
        controller.setTerceraEspecieActiva(terceraEspecieActiva);
        controller.setMutacionesActivas(mutacionesActivas);
        
        controller.createEcosystem(maxTurns, scenario);
        
        btnStart.setEnabled(false);
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
        btnToggleTerceraEspecie.setEnabled(false);
        btnToggleMutaciones.setEnabled(false);
        comboScenario.setEnabled(false);
        spinnerTurns.setEnabled(false);
        
        isRunning = true;
        addLog("✓ Simulation started - Scenario: " + scenario);
        if (terceraEspecieActiva) {
            addLog("  • Third species (Caiman) enabled");
            lblCaimanCount.setVisible(true);
        }
        if (mutacionesActivas) addLog("  • Genetic mutations enabled");
        
        updateMatrixView();
        updateStats();
        
        simulationTimer = new Timer(1500, e -> executeTurn());
        simulationTimer.start();
    }
    
    private void pauseSimulation() {
        if (simulationTimer != null) {
            if (simulationTimer.isRunning()) {
                simulationTimer.stop();
                btnPause.setText("Resume");
                addLog("⏸ Simulation paused");
            } else {
                simulationTimer.start();
                btnPause.setText("Pause");
                addLog("▶ Simulation resumed");
            }
        }
    }
    
    private void stopSimulation() {
        if (simulationTimer != null) {
            simulationTimer.stop();
        }
        
        isRunning = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnReport.setEnabled(true);
        btnToggleTerceraEspecie.setEnabled(true);
        btnToggleMutaciones.setEnabled(true);
        comboScenario.setEnabled(true);
        spinnerTurns.setEnabled(true);
        btnPause.setText("Pause");
        
        addLog("■ Simulation stopped");
    }
    
    private void executeTurn() {
        boolean continueSimulation = controller.executeTurn();
        
        updateMatrixView();
        updateStats();
        
        if (!continueSimulation) {
            stopSimulation();
            showSimulationEndDialog();
        }
    }
    
    private void updateMatrixView() {
        Ecosystem ecosystem = controller.getEcosystem();
        if (ecosystem == null) return;
        
        Animal[][] matrix = ecosystem.getMatrix();
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Animal animal = matrix[i][j];
                JLabel cell = cellLabels[i][j];
                
                if (animal == null) {
                    cell.setIcon(null);
                    cell.setText("");
                    // Vuelve al color base semi-transparente para ver el bosque
                    cell.setBackground(new Color(255, 253, 208, 120)); 
                } else if (animal instanceof Prey) {
                    loadAnimalIcon(cell, "/imagenes/conejo.png", "🐰");
                    // Verde claro con transparencia (Alpha 180) para resaltar pero integrar
                    cell.setBackground(new Color(144, 238, 144, 180)); 
                } else if (animal instanceof Caiman) {
                    loadAnimalIcon(cell, "/imagenes/caiman.png", "🐊");
                    // Azul/Cian oscuro transparente
                    cell.setBackground(new Color(70, 130, 180, 180)); 
                } else if (animal instanceof Predator) {
                    loadAnimalIcon(cell, "/imagenes/hiena.png", "🐺");
                    // Rojo/Salmón transparente
                    cell.setBackground(new Color(255, 160, 122, 180)); 
                }
            }
        }
        
        // IMPORTANTE: Repintar el contenedor padre para procesar transparencias correctamente
        if (panelMatrix != null) {
            panelMatrix.revalidate();
            panelMatrix.repaint();
        }
    }
    
    private void loadAnimalIcon(JLabel cell, String imagePath, String emojiBackup) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
            if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                cell.setIcon(new ImageIcon(scaledImg));
                cell.setText("");
            } else {
                cell.setIcon(null);
                cell.setText(emojiBackup);
            }
        } catch (Exception e) {
            cell.setIcon(null);
            cell.setText(emojiBackup);
        }
    }
    
    private void updateStats() {
        Ecosystem ecosystem = controller.getEcosystem();
        if (ecosystem == null) return;
        
        int currentTurn = ecosystem.getCurrentTurn();
        int maxTurns = ecosystem.getMaxTurns();
        int preys = ecosystem.countPreys();
        int predators = ecosystem.countPredators();
        int caimans = ecosystem.countCaimans();
        int empty = ecosystem.countEmptyCells();
        
        lblCurrentTurn.setText("Current Turn: " + currentTurn + "/" + maxTurns);
        lblPreyCount.setText("Preys: " + preys);
        lblPredatorCount.setText("Predators: " + predators);
        
        if (terceraEspecieActiva) {
            lblCaimanCount.setText("Caimans: " + caimans);
        }
        
        lblEmptyCount.setText("Empty Cells: " + empty);
        
        int progress = (int) ((currentTurn / (double) maxTurns) * 100);
        progressBar.setValue(progress);
        
        String logMsg = String.format("Turn %d: P=%d D=%d", currentTurn, preys, predators);
        if (terceraEspecieActiva) {
            logMsg += String.format(" C=%d", caimans);
        }
        logMsg += String.format(" E=%d", empty);
        addLog(logMsg);
    }
    
    private void addLog(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    private void showSimulationEndDialog() {
        Ecosystem ecosystem = controller.getEcosystem();
        
        String message = "Simulation completed!\n\n" +
            "Total turns: " + ecosystem.getCurrentTurn() + "\n" +
            "Final preys: " + ecosystem.countPreys() + "\n" +
            "Final predators: " + ecosystem.countPredators() + "\n\n";
        
        if (ecosystem.hasExtinction()) {
            message += ecosystem.countPreys() == 0 ? 
                "Result: 💀 Preys extinct!" : "Result: 💀 Predators extinct!";
        } else {
            message += "Result: Ecosystem survived!";
        }
        
        JOptionPane.showMessageDialog(this, message, 
            "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * IMPLEMENTACIÓN COMPLETA: Genera reporte PDF y envía por email
     * LÍNEAS CRÍTICAS: 543-636
     */
    private void generateReport() {
        // VALIDACIÓN: Verificar que hay datos de simulación
        if (controller.getEcosystem() == null) {
            JOptionPane.showMessageDialog(this,
                "No simulation data available. Please run a simulation first.",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // CONFIRMACIÓN: Mostrar diálogo con email del usuario
        int confirm = JOptionPane.showConfirmDialog(this,
            "Generate PDF report and send to email:\n" + currentUser.getEmail() + "?",
            "Confirm Report Generation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // INTERFAZ: Mostrar progress bar indeterminado
        JDialog progressDialog = new JDialog(this, "Generating Report", true);
        JProgressBar progressBarDialog = new JProgressBar();
        progressBarDialog.setIndeterminate(true);
        progressBarDialog.setString("Generating PDF report...");
        progressBarDialog.setStringPainted(true);
        progressDialog.add(progressBarDialog);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(this);
        
        // WORKER THREAD: Generar reporte en segundo plano
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // LLAMADA CRÍTICA: ReportGenerator.generateReport()
                return util.ReportGenerator.generateReport(
                    controller.getEcosystem(),
                    currentUser.getName(),
                    controller.getStateDAO()
                );
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                
                try {
                    String pdfPath = get();
                    
                    if (pdfPath != null) {
                        addLog("✓ PDF report generated: " + pdfPath);
                        
                        // ENVÍO DE EMAIL: EmailService.sendEmailWithAttachment()
                        boolean emailSent = new data.EmailService().sendEmailWithAttachment(
                            currentUser.getEmail(),
                            "Ecosystem Simulation Report - " + controller.getEcosystem().getScenario(),
                            generateEmailBody(),
                            pdfPath
                        );
                        
                        if (emailSent) {
                            JOptionPane.showMessageDialog(EcosystemView.this,
                                "Report generated successfully!\n" +
                                "PDF: " + pdfPath + "\n" +
                                "Email sent to: " + currentUser.getEmail(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            addLog("✓ Email sent to " + currentUser.getEmail());
                        } else {
                            JOptionPane.showMessageDialog(EcosystemView.this,
                                "Report generated but email failed.\n" +
                                "PDF saved at: " + pdfPath,
                                "Partial Success",
                                JOptionPane.WARNING_MESSAGE);
                            addLog("✗ Email failed");
                        }
                    } else {
                        JOptionPane.showMessageDialog(EcosystemView.this,
                            "Failed to generate report. Check console for errors.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        addLog("✗ Report generation failed");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EcosystemView.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }
    
    /**
     * Genera el cuerpo del email con estadísticas básicas
     */
    private String generateEmailBody() {
        Ecosystem eco = controller.getEcosystem();
        return String.format(
            "Simulation completed!\n\n" +
            "Scenario: %s\n" +
            "Total Turns: %d\n" +
            "Final Preys: %d\n" +
            "Final Predators: %d\n" +
            "Extinction: %s\n\n" +
            "Please find the detailed report in the attached PDF.",
            eco.getScenario(),
            eco.getCurrentTurn(),
            eco.countPreys(),
            eco.countPredators(),
            eco.hasExtinction() ? "Yes" : "No"
        );
    }
    
    private void toggleTerceraEspecie() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this,
                "Cannot toggle species during simulation",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        terceraEspecieActiva = !terceraEspecieActiva;
        
        if (terceraEspecieActiva) {
            btnToggleTerceraEspecie.setText("3rd Species: ON");
            btnToggleTerceraEspecie.setBackground(new Color(34, 139, 34));
            addLog("✓ Third species (Caiman) enabled");
        } else {
            btnToggleTerceraEspecie.setText("3rd Species: OFF");
            btnToggleTerceraEspecie.setBackground(new Color(100, 149, 237));
            lblCaimanCount.setVisible(false); // Ocultar contador
            addLog("✗ Third species (Caiman) disabled");
        }
    }
    
    private void toggleMutaciones() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this,
                "Cannot toggle mutations during simulation",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mutacionesActivas = !mutacionesActivas;
        
        if (mutacionesActivas) {
            btnToggleMutaciones.setText("Mutations: ON");
            btnToggleMutaciones.setBackground(new Color(138, 43, 226));
            addLog("✓ Genetic mutations enabled");
        } else {
            btnToggleMutaciones.setText("Mutations: OFF");
            btnToggleMutaciones.setBackground(new Color(147, 112, 219));
            addLog("✗ Genetic mutations disabled");
        }
    }
    
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