package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Generador de iconos personalizados para animales.
 * Dibuja iconos vectoriales en lugar de usar imagenes externas.
 */
public class AnimalIcon {
    
    /**
     * Crea un icono para una presa (conejo)
     * @param size Tamaño del icono
     * @return ImageIcon con el dibujo de la presa
     */
    public static ImageIcon createPreyIcon(int size) {
        return new ImageIcon(createPreyImage(size));
    }
    
    /**
     * Crea un icono para un depredador (lobo)
     * @param size Tamaño del icono
     * @return ImageIcon con el dibujo del depredador
     */
    public static ImageIcon createPredatorIcon(int size) {
        return new ImageIcon(createPredatorImage(size));
    }
    
    /**
     * Crea un icono para celda vacia (pasto)
     * @param size Tamaño del icono
     * @return ImageIcon con el dibujo del pasto
     */
    public static ImageIcon createEmptyIcon(int size) {
        return new ImageIcon(createEmptyImage(size));
    }
    
    /**
     * Dibuja una presa (conejo estilizado)
     */
    private static Image createPreyImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Color verde para presa
        Color preyColor = new Color(100, 200, 100);
        
        int center = size / 2;
        int bodySize = size / 3;
        
        // Cuerpo (circulo)
        g2d.setColor(preyColor);
        g2d.fillOval(center - bodySize/2, center - bodySize/2, bodySize, bodySize);
        
        // Orejas (2 elipses)
        int earWidth = bodySize / 3;
        int earHeight = bodySize;
        g2d.fillOval(center - bodySize/3, center - bodySize, earWidth, earHeight);
        g2d.fillOval(center + bodySize/10, center - bodySize, earWidth, earHeight);
        
        // Ojos
        g2d.setColor(Color.WHITE);
        g2d.fillOval(center - bodySize/4, center - bodySize/6, bodySize/5, bodySize/5);
        g2d.fillOval(center + bodySize/12, center - bodySize/6, bodySize/5, bodySize/5);
        
        // Pupilas
        g2d.setColor(Color.BLACK);
        g2d.fillOval(center - bodySize/5, center - bodySize/8, bodySize/8, bodySize/8);
        g2d.fillOval(center + bodySize/8, center - bodySize/8, bodySize/8, bodySize/8);
        
        g2d.dispose();
        return img;
    }
    
    /**
     * Dibuja un depredador (lobo estilizado)
     */
    private static Image createPredatorImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Color rojo oscuro para depredador
        Color predatorColor = new Color(180, 50, 50);
        
        int center = size / 2;
        int bodySize = size / 3;
        
        // Cuerpo (circulo)
        g2d.setColor(predatorColor);
        g2d.fillOval(center - bodySize/2, center - bodySize/2, bodySize, bodySize);
        
        // Orejas triangulares
        Polygon leftEar = new Polygon();
        leftEar.addPoint(center - bodySize/3, center - bodySize/2);
        leftEar.addPoint(center - bodySize/2, center - bodySize);
        leftEar.addPoint(center - bodySize/6, center - bodySize/2);
        g2d.fillPolygon(leftEar);
        
        Polygon rightEar = new Polygon();
        rightEar.addPoint(center + bodySize/6, center - bodySize/2);
        rightEar.addPoint(center + bodySize/2, center - bodySize);
        rightEar.addPoint(center + bodySize/3, center - bodySize/2);
        g2d.fillPolygon(rightEar);
        
        // Ojos
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(center - bodySize/4, center - bodySize/6, bodySize/5, bodySize/5);
        g2d.fillOval(center + bodySize/12, center - bodySize/6, bodySize/5, bodySize/5);
        
        // Pupilas
        g2d.setColor(Color.BLACK);
        g2d.fillOval(center - bodySize/5, center - bodySize/8, bodySize/10, bodySize/10);
        g2d.fillOval(center + bodySize/7, center - bodySize/8, bodySize/10, bodySize/10);
        
        // Hocico
        g2d.setColor(predatorColor.darker());
        g2d.fillOval(center - bodySize/6, center, bodySize/3, bodySize/4);
        
        g2d.dispose();
        return img;
    }
    
    /**
     * Dibuja celda vacia (pasto)
     */
    private static Image createEmptyImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo beige claro
        g2d.setColor(new Color(245, 245, 220));
        g2d.fillRect(0, 0, size, size);
        
        // Dibujar briznas de pasto
        g2d.setColor(new Color(150, 180, 120));
        g2d.setStroke(new BasicStroke(2));
        
        Random rand = new Random(42); // Seed fijo para consistencia
        for (int i = 0; i < 5; i++) {
            int x = rand.nextInt(size);
            int baseY = size - 5;
            int topY = baseY - rand.nextInt(size/3);
            
            g2d.drawLine(x, baseY, x + rand.nextInt(5) - 2, topY);
        }
        
        g2d.dispose();
        return img;
    }
}

