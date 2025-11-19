package oop.ecosystem.simulation;

import view.LoginView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal para probar el sistema completo.
 * Prueba tanto el ecosistema como el sistema de usuarios.
 */
public class JavaOopEcosystemSimulation {
    
    public static void main(String[] args) {
            // Configurar Look and Feel del sistema operativo
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[WARNING] Could not load system Look and Feel");
                System.err.println("         Using default Java Look and Feel");
            }

            // Crear y mostrar la ventana de login en el Event Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                }
            });
        }
}