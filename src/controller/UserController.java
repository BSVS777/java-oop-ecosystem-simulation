package controller;

import model.User;
import data.UserDAO;
import data.EmailService;
import util.Encryption;
import util.Validations;
import java.time.LocalDate;

/**
 * Controlador para la logica de usuarios.
 * Maneja registro, login y validaciones.
 */
public class UserController {
    
    private UserDAO userDAO;
    private EmailService emailService;
    
    /**
     * Constructor del controlador
     */
    public UserController() {
        this.userDAO = new UserDAO();
        this.emailService = new EmailService();
    }
    
    /**
     * Maneja el proceso de login
     * @param idNumber ID del usuario
     * @param password Password en texto plano
     * @return Usuario si login exitoso, null si fallo
     */
    public User login(int idNumber, String password) {
        // Encriptar password
        String passwordHash = Encryption.encryptSHA256(password);
        
        // Validar credenciales
        User user = userDAO.validateCredentials(idNumber, passwordHash);
        
        return user;
    }
    
    /**
     * Maneja el proceso de registro
     * @param idNumber ID del usuario
     * @param name Nombre completo
     * @param birthDate Fecha de nacimiento
     * @param gender Genero
     * @param password Password en texto plano
     * @param confirmPassword Confirmacion de password
     * @param email Email
     * @return "SUCCESS" si registro exitoso, mensaje de error si fallo
     */
    public String register(int idNumber, String name, LocalDate birthDate, 
                          String gender, String password, String confirmPassword, 
                          String email) {
        
        // 1. Validar ID Number
        if (!Validations.validateIdNumber(String.valueOf(idNumber))) {
            return "Invalid ID Number format";
        }
        
        // 2. Verificar si el ID ya existe
        if (userDAO.existsIdNumber(idNumber)) {
            return "ID Number already registered";
        }
        
        // 3. Validar nombre
        if (!Validations.validateName(name)) {
            return "Invalid name. Must be at least 2 characters and only letters";
        }
        
        // 4. Validar edad (18+)
        User tempUser = new User(idNumber, name, birthDate, gender, email);
        if (!tempUser.isAdult()) {
            return "You must be at least 18 years old to register";
        }
        
        // 5. Validar email
        if (!Validations.validateEmail(email)) {
            return "Invalid email format";
        }
        
        // 6. Verificar si el email ya existe
        if (userDAO.existsEmail(email)) {
            return "Email already registered";
        }
        
        // 7. Validar password
        if (!Validations.validatePassword(password)) {
            return Validations.getPasswordError(password);
        }
        
        // 8. Verificar que las passwords coincidan
        if (!Validations.validatePasswordMatch(password, confirmPassword)) {
            return "Passwords do not match";
        }
        
        // 9. Encriptar password
        String passwordHash = Encryption.encryptSHA256(password);
        
        // 10. Crear usuario
        User newUser = new User(idNumber, name, birthDate, gender, passwordHash, email);
        
        // 11. Guardar en archivo
        boolean saved = userDAO.saveUser(newUser);
        
        if (saved) {
            return "SUCCESS";
        } else {
            return "Error saving user. Please try again";
        }
    }
    
    /**
     * Obtiene un usuario por su ID
     * @param idNumber ID del usuario
     * @return Usuario encontrado o null
     */
    public User getUserById(int idNumber) {
        return userDAO.findByIdNumber(idNumber);
    }
    
    /**
     * Obtiene un usuario por su email
     * @param email Email del usuario
     * @return Usuario encontrado o null
     */
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    
    /**
     * Actualiza la informacion de un usuario
     * @param user Usuario con informacion actualizada
     * @return true si se actualizo exitosamente
     */
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
    
    /**
     * Elimina un usuario del sistema
     * @param idNumber ID del usuario a eliminar
     * @return true si se elimino exitosamente
     */
    public boolean deleteUser(int idNumber) {
        return userDAO.deleteUser(idNumber);
    }
}