package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

public class ImagePanel extends JPanel {
    private BufferedImage backgroundImage;

    public ImagePanel(String imagePath) {
        // Usamos GridBagLayout por defecto para centrar lo que pongamos dentro (la matriz)
        setLayout(new GridBagLayout()); 
        
        try {
            URL url = getClass().getResource(imagePath);
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            } else {
                System.err.println("Warning: Image not found at: " + imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Dibuja la imagen escalada para llenar todo el panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(176, 206, 136));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}