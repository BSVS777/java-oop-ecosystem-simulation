package view;

import controller.UserController;
import model.User;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Ventana de registro de nuevos usuarios.
 * Valida edad minima de 18 años y datos requeridos.
 */
public class RegisterView extends JFrame {
    
    // Componentes de la interfaz
    private JTextField txtIdNumber;
    private JTextField txtName;
    private JDateChooser dateChooserBirth;
    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private ButtonGroup genderGroup;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel panelMain;
    private JPanel panelForm;
    private JPanel panelButtons;
    
    // Controlador y ventana anterior
    private UserController userController;
    private LoginView loginView;
    
    /**
     * Constructor de RegisterView
     * @param loginView Ventana de login para regresar
     */
    public RegisterView(LoginView loginView) {
        this.loginView = loginView;
        this.userController = new UserController();
        initComponents();
        setLocationRelativeTo(loginView);
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void initComponents() {
        // Configuracion de la ventana
        setTitle("Ecosystem Simulator - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setResizable(false);
        
        // Panel principal
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelMain.setBackground(new Color(176, 206, 136)); // Verde claro - fondo general
        
        // Titulo
        JLabel lblTitle = new JLabel("NEW USER REGISTRATION", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(239, 71, 111)); // Verde oscuro - titulo
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
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
        gbc.insets = new Insets(8, 5, 8, 5);
        
        int row = 0;
        
        // ID Number
        addFormLabel("ID Number:", row, gbc);
        txtIdNumber = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(txtIdNumber, gbc);
        
        // Name
        addFormLabel("Full Name:", row, gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(txtName, gbc);
        
        // Birth Date
        addFormLabel("Birth Date:", row, gbc);
        dateChooserBirth = new JDateChooser();
        dateChooserBirth.setDateFormatString("dd/MM/yyyy");
        dateChooserBirth.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(dateChooserBirth, gbc);
        
        // Gender
        addFormLabel("Gender:", row, gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbMale.setBackground(Color.WHITE);
        rbFemale.setBackground(Color.WHITE);
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(genderPanel, gbc);
        
        // Email
        addFormLabel("Email:", row, gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(txtEmail, gbc);
        
        // Password
        addFormLabel("Password:", row, gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(txtPassword, gbc);
        
        // Confirm Password
        addFormLabel("Confirm Password:", row, gbc);
        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panelForm.add(txtConfirmPassword, gbc);
        
        // Nota de requisitos
        JLabel lblRequirements = new JLabel("<html><i>* Minimum age: 18 years<br>* Password: min 6 chars, 1 letter, 1 number</i></html>");
        lblRequirements.setFont(new Font("Arial", Font.PLAIN, 11));
        lblRequirements.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panelForm.add(lblRequirements, gbc);
        
        // Panel de botones
        panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelButtons.setBackground(new Color(240, 248, 255));
        
        // Boton Register
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(76, 175, 80));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(120, 35));
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        // Boton Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(120, 35));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToLogin();
            }
        });
        
        panelButtons.add(btnRegister);
        panelButtons.add(btnCancel);
        
        // Agregar componentes al panel principal
        panelMain.add(lblTitle, BorderLayout.NORTH);
        panelMain.add(panelForm, BorderLayout.CENTER);
        panelMain.add(panelButtons, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    /**
     * Helper para agregar etiquetas al formulario
     */
    private void addFormLabel(String text, int row, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panelForm.add(label, gbc);
        gbc.weightx = 0.7;
    }
    
    /**
     * Maneja el evento de registro
     */
    private void handleRegister() {
        // Obtener datos del formulario
        String idNumberStr = txtIdNumber.getText().trim();
        String name = txtName.getText().trim();
        Date birthDate = dateChooserBirth.getDate();
        String gender = rbMale.isSelected() ? "Male" : (rbFemale.isSelected() ? "Female" : "");
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        // Validar campos vacios
        if (idNumberStr.isEmpty() || name.isEmpty() || birthDate == null || 
            gender.isEmpty() || email.isEmpty() || password.isEmpty()) {
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
        
        // Convertir fecha
        LocalDate birthLocalDate = birthDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        // Intentar registrar
        String result = userController.register(
            idNumber, name, birthLocalDate, gender, password, confirmPassword, email
        );
        
        if (result.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(
                this,
                "Registration successful!\nYou can now login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            returnToLogin();
        } else {
            JOptionPane.showMessageDialog(
                this,
                result,
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Regresa a la ventana de login
     */
    private void returnToLogin() {
        loginView.setVisible(true);
        this.dispose();
    }
}