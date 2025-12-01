package util;

import model.Ecosystem;
import data.StateDAO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Color;

/**
 * Generador de reportes en PDF con gráficos y estadísticas.
 * VERSIÓN CORREGIDA: Tabla de turnos ahora muestra datos correctamente.
 */
public class ReportGenerator {
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    
    /**
     * Genera un reporte completo de la simulación.
     */
    public static String generateReport(Ecosystem ecosystem, String username, StateDAO stateDAO) {
        try {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "report_" + ecosystem.getScenario() + "_" + username + "_" + timestamp + ".pdf";
            
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            document.open();
            
            // Header
            addHeader(document, ecosystem, username);
            document.add(Chunk.NEWLINE);
            
            // Resumen ejecutivo
            addExecutiveSummary(document, ecosystem);
            document.add(Chunk.NEWLINE);
            
            // Estadísticas finales
            addFinalStatistics(document, ecosystem);
            document.add(Chunk.NEWLINE);
            
            // Gráfico de pastel - Distribución final
            addPieChart(document, writer, ecosystem);
            document.add(Chunk.NEWLINE);
            
            // Gráfico de ocupación
            addOccupationPieChart(document, writer, ecosystem);
            document.add(Chunk.NEWLINE);
            
            // Análisis de extinción
            addExtinctionAnalysis(document, ecosystem, stateDAO);
            document.add(Chunk.NEWLINE);
            
            // Evolución por turnos (tabla) - CORREGIDO
            addTurnEvolutionTable(document, stateDAO, ecosystem);
            
            // Footer
            addFooter(document);
            
            document.close();
            
            System.out.println("[REPORT] PDF generated: " + filename);
            return filename;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to generate report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Agrega el encabezado del reporte.
     */
    private static void addHeader(Document document, Ecosystem ecosystem, String username) throws DocumentException {
        Paragraph title = new Paragraph("ECOSYSTEM SIMULATION REPORT", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addTableRow(table, "Scenario:", ecosystem.getScenario());
        addTableRow(table, "User:", username);
        addTableRow(table, "Date:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addTableRow(table, "Total Turns:", String.valueOf(ecosystem.getCurrentTurn()));
        addTableRow(table, "Max Turns:", String.valueOf(ecosystem.getMaxTurns()));
        
        document.add(table);
    }
    
    /**
     * Agrega resumen ejecutivo.
     */
    private static void addExecutiveSummary(Document document, Ecosystem ecosystem) throws DocumentException {
        Paragraph subtitle = new Paragraph("EXECUTIVE SUMMARY", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        String status = ecosystem.hasExtinction() ? "EXTINCTION OCCURRED" : "ECOSYSTEM SURVIVED";
        BaseColor statusColor = ecosystem.hasExtinction() ? BaseColor.RED : BaseColor.GREEN;
        
        Paragraph statusPara = new Paragraph(status, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, statusColor));
        statusPara.setAlignment(Element.ALIGN_CENTER);
        document.add(statusPara);
        
        document.add(Chunk.NEWLINE);
        
        String summary = "";
        if (ecosystem.hasExtinction()) {
            if (ecosystem.countPreys() == 0) {
                summary = "All preys were eliminated by predators. Subsequently, predators also died from lack of food, " +
                         "demonstrating the critical interdependence between species in the ecosystem.";
            } else {
                summary = "All predators died from starvation, allowing preys to dominate the ecosystem completely. " +
                         "This shows the importance of maintaining adequate prey populations for predator survival.";
            }
        } else {
            summary = "Both species coexist at the end of the simulation. The ecosystem achieved a " +
                     (isBalanced(ecosystem) ? "relatively balanced" : "stable but imbalanced") + " state.";
        }
        
        Paragraph summaryPara = new Paragraph(summary, NORMAL_FONT);
        summaryPara.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(summaryPara);
    }
    
    /**
     * Agrega estadísticas finales.
     */
    private static void addFinalStatistics(Document document, Ecosystem ecosystem) throws DocumentException {
        Paragraph subtitle = new Paragraph("FINAL STATISTICS", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 1, 1});
        
        // Header
        addTableHeaderCell(table, "Species");
        addTableHeaderCell(table, "Count");
        addTableHeaderCell(table, "Percentage");
        
        // Datos
        int preys = ecosystem.countPreys();
        int predators = ecosystem.countPredators();
        int total = preys + predators;
        
        addTableCell(table, "🐰 Preys");
        addTableCell(table, String.valueOf(preys));
        addTableCell(table, String.format("%.1f%%", (preys * 100.0 / total)));
        
        addTableCell(table, "🐺 Predators");
        addTableCell(table, String.valueOf(predators));
        addTableCell(table, String.format("%.1f%%", (predators * 100.0 / total)));
        
        if (ecosystem.isTerceraEspecieActiva()) {
            int caimans = ecosystem.countCaimans();
            total += caimans;
            
            addTableCell(table, "🐊 Caimans");
            addTableCell(table, String.valueOf(caimans));
            addTableCell(table, String.format("%.1f%%", (caimans * 100.0 / total)));
        }
        
        document.add(table);
        
        // Celdas vacías
        document.add(Chunk.NEWLINE);
        Paragraph emptyCells = new Paragraph("Empty Cells: " + ecosystem.countEmptyCells() + " / 100", BOLD_FONT);
        document.add(emptyCells);
    }
    
    /**
     * Agrega gráfico de pastel de distribución de especies.
     */
    private static void addPieChart(Document document, PdfWriter writer, Ecosystem ecosystem) 
            throws DocumentException, IOException {
        
        Paragraph subtitle = new Paragraph("SPECIES DISTRIBUTION", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        // Crear gráfico simple con iText
        PdfContentByte canvas = writer.getDirectContent();
        
        int preys = ecosystem.countPreys();
        int predators = ecosystem.countPredators();
        int caimans = ecosystem.isTerceraEspecieActiva() ? ecosystem.countCaimans() : 0;
        int total = preys + predators + caimans;
        
        if (total == 0) {
            document.add(new Paragraph("No animals remaining in ecosystem.", NORMAL_FONT));
            return;
        }
        
        // Dibujar gráfico de pastel manual
        float centerX = 350;
        float centerY = 350;
        float radius = 80;
        
        float preyAngle = (preys * 360f / total);
        float predatorAngle = (predators * 360f / total);
        float caimanAngle = (caimans * 360f / total);
        
        // Presa (verde)
        canvas.saveState();
        canvas.setColorFill(new BaseColor(144, 238, 144));
        drawPieSlice(canvas, centerX, centerY, radius, 0, preyAngle);
        canvas.fill();
        canvas.restoreState();
        
        // Depredador (rojo)
        canvas.saveState();
        canvas.setColorFill(new BaseColor(255, 160, 122));
        drawPieSlice(canvas, centerX, centerY, radius, preyAngle, preyAngle + predatorAngle);
        canvas.fill();
        canvas.restoreState();
        
        // Caimán (azul)
        if (caimans > 0) {
            canvas.saveState();
            canvas.setColorFill(new BaseColor(70, 130, 180));
            drawPieSlice(canvas, centerX, centerY, radius, preyAngle + predatorAngle, 360);
            canvas.fill();
            canvas.restoreState();
        }
        
        // Leyenda
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        Paragraph legend = new Paragraph();
        legend.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(144, 238, 144))));
        legend.add(new Chunk("Preys: " + preys + " (" + String.format("%.1f%%)", preys * 100.0 / total), NORMAL_FONT));
        legend.add(Chunk.NEWLINE);
        legend.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(255, 160, 122))));
        legend.add(new Chunk("Predators: " + predators + " (" + String.format("%.1f%%)", predators * 100.0 / total), NORMAL_FONT));
        
        if (caimans > 0) {
            legend.add(Chunk.NEWLINE);
            legend.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(70, 130, 180))));
            legend.add(new Chunk("Caimans: " + caimans + " (" + String.format("%.1f%%)", caimans * 100.0 / total), NORMAL_FONT));
        }
        
        document.add(legend);
    }
    
    /**
     * Agrega gráfico de ocupación del ecosistema.
     */
    private static void addOccupationPieChart(Document document, PdfWriter writer, Ecosystem ecosystem) 
            throws DocumentException {
        
        Paragraph subtitle = new Paragraph("ECOSYSTEM OCCUPATION", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        PdfContentByte canvas = writer.getDirectContent();
        
        int occupied = 100 - ecosystem.countEmptyCells();
        int empty = ecosystem.countEmptyCells();
        
        float centerX = 350;
        float centerY = 150;
        float radius = 80;
        
        float occupiedAngle = (occupied * 360f / 100);
        
        // Ocupado (azul)
        canvas.saveState();
        canvas.setColorFill(new BaseColor(100, 149, 237));
        drawPieSlice(canvas, centerX, centerY, radius, 0, occupiedAngle);
        canvas.fill();
        canvas.restoreState();
        
        // Vacío (gris claro)
        canvas.saveState();
        canvas.setColorFill(new BaseColor(220, 220, 220));
        drawPieSlice(canvas, centerX, centerY, radius, occupiedAngle, 360);
        canvas.fill();
        canvas.restoreState();
        
        // Leyenda
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        Paragraph legend = new Paragraph();
        legend.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(100, 149, 237))));
        legend.add(new Chunk("Occupied: " + occupied + " cells (" + occupied + "%)", NORMAL_FONT));
        legend.add(Chunk.NEWLINE);
        legend.add(new Chunk("■ ", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(220, 220, 220))));
        legend.add(new Chunk("Empty: " + empty + " cells (" + empty + "%)", NORMAL_FONT));
        
        document.add(legend);
    }
    
    /**
     * Dibuja una porción de pastel.
     */
    private static void drawPieSlice(PdfContentByte canvas, float centerX, float centerY, 
                                     float radius, float startAngle, float endAngle) {
        canvas.moveTo(centerX, centerY);
        
        for (float angle = startAngle; angle <= endAngle; angle += 1) {
            double radians = Math.toRadians(angle);
            float x = centerX + radius * (float) Math.cos(radians);
            float y = centerY + radius * (float) Math.sin(radians);
            canvas.lineTo(x, y);
        }
        
        canvas.closePath();
    }
    
    /**
     * Agrega análisis de extinción.
     */
    private static void addExtinctionAnalysis(Document document, Ecosystem ecosystem, StateDAO stateDAO) 
            throws DocumentException {
        
        Paragraph subtitle = new Paragraph("EXTINCTION ANALYSIS", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        if (ecosystem.hasExtinction()) {
            int extinctionTurn = findExtinctionTurn(stateDAO, ecosystem);
            
            Paragraph para = new Paragraph();
            para.add(new Chunk("Extinction occurred at turn: ", NORMAL_FONT));
            para.add(new Chunk(String.valueOf(extinctionTurn), BOLD_FONT));
            para.add(Chunk.NEWLINE);
            
            if (ecosystem.countPreys() == 0) {
                para.add(new Chunk("Species extinct: Preys", BOLD_FONT));
                para.add(Chunk.NEWLINE);
                para.add(new Chunk("Cause: Overhunting by predators followed by predator starvation.", NORMAL_FONT));
            } else {
                para.add(new Chunk("Species extinct: Predators", BOLD_FONT));
                para.add(Chunk.NEWLINE);
                para.add(new Chunk("Cause: Insufficient prey population leading to mass starvation.", NORMAL_FONT));
            }
            
            document.add(para);
        } else {
            Paragraph para = new Paragraph("No extinction occurred. Both species survived until the end of the simulation.", 
                                         NORMAL_FONT);
            document.add(para);
        }
    }
    
    /**
     * Agrega tabla de evolución por turnos - VERSIÓN CORREGIDA.
     */
    private static void addTurnEvolutionTable(Document document, StateDAO stateDAO, Ecosystem ecosystem) 
            throws DocumentException {
        
        Paragraph subtitle = new Paragraph("TURN-BY-TURN EVOLUTION", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        subtitle.setSpacingAfter(10);
        document.add(subtitle);
        
        PdfPTable table = new PdfPTable(ecosystem.isTerceraEspecieActiva() ? 5 : 4);
        table.setWidthPercentage(100);
        
        // Header
        addTableHeaderCell(table, "Turn");
        addTableHeaderCell(table, "Preys");
        addTableHeaderCell(table, "Predators");
        if (ecosystem.isTerceraEspecieActiva()) {
            addTableHeaderCell(table, "Caimans");
        }
        addTableHeaderCell(table, "Empty");
        
        // FIX CRÍTICO: Obtener el simulation ID correcto
        // El StateDAO guarda con formato: "scenario_username_timestamp"
        // Necesitamos obtener todos los IDs y buscar el más reciente del escenario actual
        List<String> allSimIds = stateDAO.getAllSimulationIds();
        String targetSimId = null;
        
        // Buscar el ID más reciente que coincida con el escenario actual
        String scenarioPrefix = ecosystem.getScenario() + "_";
        for (int i = allSimIds.size() - 1; i >= 0; i--) {
            String simId = allSimIds.get(i);
            if (simId.startsWith(scenarioPrefix)) {
                targetSimId = simId;
                break;
            }
        }
        
        // Si no encontramos ID, mostrar mensaje informativo
        if (targetSimId == null) {
            addTableCell(table, "No data available");
            addTableCell(table, "-");
            addTableCell(table, "-");
            if (ecosystem.isTerceraEspecieActiva()) {
                addTableCell(table, "-");
            }
            addTableCell(table, "-");
            document.add(table);
            
            Paragraph note = new Paragraph(
                "Note: Turn-by-turn data is only available when simulation is executed from the interface.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY)
            );
            document.add(note);
            return;
        }
        
        // Cargar estados con el ID correcto
        List<StateDAO.TurnState> states = stateDAO.loadSimulationStates(targetSimId);
        
        System.out.println("[REPORT] Loading turn states for: " + targetSimId);
        System.out.println("[REPORT] Found " + states.size() + " turn states");
        
        if (states.isEmpty()) {
            addTableCell(table, "No turns recorded");
            addTableCell(table, "-");
            addTableCell(table, "-");
            if (ecosystem.isTerceraEspecieActiva()) {
                addTableCell(table, "-");
            }
            addTableCell(table, "-");
            document.add(table);
            return;
        }
        
        // Determinar paso: mostrar máximo 15 filas
        int step = Math.max(1, states.size() / 15);
        
        // Agregar datos
        for (int i = 0; i < states.size(); i += step) {
            StateDAO.TurnState state = states.get(i);
            addTableCell(table, String.valueOf(state.turn));
            addTableCell(table, String.valueOf(state.preys));
            addTableCell(table, String.valueOf(state.predators));
            if (ecosystem.isTerceraEspecieActiva()) {
                addTableCell(table, "N/A"); // TODO: agregar en StateDAO
            }
            addTableCell(table, String.valueOf(state.empty));
        }
        
        // Siempre agregar el último turno si no está incluido
        StateDAO.TurnState lastState = states.get(states.size() - 1);
        if ((states.size() - 1) % step != 0) {
            addTableCell(table, String.valueOf(lastState.turn));
            addTableCell(table, String.valueOf(lastState.preys));
            addTableCell(table, String.valueOf(lastState.predators));
            if (ecosystem.isTerceraEspecieActiva()) {
                addTableCell(table, "N/A");
            }
            addTableCell(table, String.valueOf(lastState.empty));
        }
        
        document.add(table);
        
        // Nota informativa sobre el muestreo
        if (step > 1) {
            Paragraph note = new Paragraph(
                String.format("Note: Showing data every %d turn(s) for readability (total: %d turns)",
                             step, states.size()),
                new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY)
            );
            note.setSpacingBefore(5);
            document.add(note);
        }
    }
    
    /**
     * Agrega footer.
     */
    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph("Generated by Ecosystem Simulator v1.0", 
                                        new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    // Helper methods
    
    private static void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }
    
    private static void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private static boolean isBalanced(Ecosystem ecosystem) {
        int preys = ecosystem.countPreys();
        int predators = ecosystem.countPredators();
        if (preys == 0 || predators == 0) return false;
        
        double ratio = (double) preys / predators;
        return ratio >= 1.5 && ratio <= 3.0; // Balance ideal: 1.5 a 3 presas por depredador
    }
    
    /**
     * Encuentra el turno de extinción - VERSIÓN CORREGIDA.
     */
    private static int findExtinctionTurn(StateDAO stateDAO, Ecosystem ecosystem) {
        String extinctSpecies = ecosystem.countPreys() == 0 ? "PREYS" : "PREDATORS";
        
        // Buscar el simulation ID más reciente del escenario actual
        List<String> allSimIds = stateDAO.getAllSimulationIds();
        String targetSimId = null;
        String scenarioPrefix = ecosystem.getScenario() + "_";
        
        for (int i = allSimIds.size() - 1; i >= 0; i--) {
            String simId = allSimIds.get(i);
            if (simId.startsWith(scenarioPrefix)) {
                targetSimId = simId;
                break;
            }
        }
        
        if (targetSimId == null) {
            return ecosystem.getCurrentTurn();
        }
        
        int turn = stateDAO.findExtinctionTurn(targetSimId, extinctSpecies);
        
        return turn != -1 ? turn : ecosystem.getCurrentTurn();
    }
}