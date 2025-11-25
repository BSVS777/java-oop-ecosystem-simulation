package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana de inicio de sesion del sistema.
 * Permite a usuarios registrados acceder al simulador.
 * 
 * @author Tu Nombre
 * @version 1.0
 */
public class LoginView extends JFrame {
    
    // Componentes de la interfaz
    private JTextField txtIdNumber;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblTitle;
    private JLabel lblIdNumber;
    private JLabel lblPassword;
    private JPanel panelMain;
    private JPanel panelForm;
    private JPanel panelButtons;
    
    // Controlador
    private UserController userController;
    
    /**
     * Constructor de LoginView
     */
    public LoginView() {
        userController = new UserController();
        initComponents();
        setLocationRelativeTo(null); // Centrar ventana
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void initComponents() {
        // Configuracion de la ventana
        setTitle("Ecosystem Simulator - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setResizable(false);
        
        // Panel principal con padding
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelMain.setBackground(new Color(176, 206, 136)); // Verde claro - fondo general
        
        // Titulo
        lblTitle = new JLabel("ECOSYSTEM SIMULATOR", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(4, 57, 21)); // Verde oscuro - texto principal
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel del formulario
        panelForm = new JPanel();
        panelForm.setLayout(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Etiqueta: ID Number
        lblIdNumber = new JLabel("ID Number:");
        lblIdNumber.setFont(new Font("Arial", Font.PLAIN, 14));
        lblIdNumber.setForeground(new Color(4, 57, 21)); // Verde oscuro - texto
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panelForm.add(lblIdNumber, gbc);
        
        // Campo de texto: ID Number
        txtIdNumber = new JTextField(20);
        txtIdNumber.setFont(new Font("Arial", Font.PLAIN, 14));
        txtIdNumber.setBackground(Color.WHITE);
        txtIdNumber.setForeground(new Color(4, 57, 21)); // Verde oscuro
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelForm.add(txtIdNumber, gbc);
        
        // Etiqueta: Password
        lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(new Color(4, 57, 21)); // Verde oscuro - texto
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblPassword, gbc);
        
        // Campo de contraseña
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(new Color(4, 57, 21)); // Verde oscuro
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panelForm.add(txtPassword, gbc);
        
        // Panel de botones
        panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelButtons.setBackground(new Color(176, 206, 136)); // Verde claro - fondo
        
        // Boton Login
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(76, 118, 59)); // Verde medio - boton principal
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setBorderPainted(false);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Boton Register
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(255, 253, 143)); // Amarillo suave - accion secundaria
        btnRegister.setForeground(new Color(4, 57, 21)); // Verde oscuro - contraste
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(120, 35));
        btnRegister.setBorderPainted(false);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterView();
            }
        });
        
        panelButtons.add(btnLogin);
        panelButtons.add(btnRegister);
        
        // Agregar componentes al panel principal
        panelMain.add(lblTitle, BorderLayout.NORTH);
        panelMain.add(panelForm, BorderLayout.CENTER);
        panelMain.add(panelButtons, BorderLayout.SOUTH);
        
        add(panelMain);
        
        // Permitir login con Enter
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    /**
     * Maneja el evento de inicio de sesion
     */
    private void handleLogin() {
        String idNumberStr = txtIdNumber.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validar campos vacios
        if (idNumberStr.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields",
                "Incomplete Data",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Validar formato de ID
        int idNumber;
        try {
            idNumber = Integer.parseInt(idNumberStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "ID Number must be a valid number",
                "Invalid Format",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Intentar login
        User user = userController.login(idNumber, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(
                this,
                "Welcome, " + user.getName() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Abrir ventana principal del ecosistema
            openEcosystemView(user);
            
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Invalid ID Number or Password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            txtPassword.setText("");
        }
    }
    
    /**
     * Abre la ventana de registro
     */
    private void openRegisterView() {
        RegisterView registerView = new RegisterView(this);
        registerView.setVisible(true);
        this.setVisible(false);
    }
    
    /**
     * Abre la ventana principal del ecosistema
     * @param user Usuario que inicio sesion
     */
    private void openEcosystemView(User user) {
        this.dispose();
        EcosystemView ecosystemView = new EcosystemView(user);
        ecosystemView.setVisible(true);
    }
    
    /**
     * Metodo principal para ejecutar la ventana
     */
    public static void main(String[] args) {
        // Usar el Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Crear y mostrar la ventana
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            }
        });
    }
}