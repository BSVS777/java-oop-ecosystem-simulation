package model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa un usuario del sistema.
 * Almacena la información de registro y autenticación.
 */
public class User {
    
    private int idNumber;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String passwordHash; // Almacena el hash, no la contraseña plana
    private String email;
    
    /**
     * Constructor completo
     */
    public User(int idNumber, String name, LocalDate birthDate, 
                   String gender, String passwordHash, String email) {
        this.idNumber = idNumber;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.passwordHash = passwordHash;
        this.email = email;
    }
    
    /**
     * Constructor sin contraseña (para cargar desde archivo antes de verificar)
     */
    public User(int idNumber, String name, LocalDate birthDate, 
                   String gender, String email) {
        this.idNumber = idNumber;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
    }
    
    /**
     * Calcula la edad del usuario
     * @return Edad en años
     */
    public int calculateAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * Verifica si el usuario es mayor de edad (18+)
     * @return true si tiene 18 años o más
     */
    public boolean isAdult() {
        return calculateAge() >= 18;
    }
    
    /**
     * Convierte el usuario a formato para guardar en archivo
     * Formato: idNumber|name|birthDate|gender|passwordHash|email
     * @return String formateado
     */
    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("%d|%s|%s|%s|%s|%s",
            idNumber, name, birthDate.format(formatter), 
            gender, passwordHash, email);
    }
    
    /**
     * Crea un User desde una línea del archivo
     * @param line Línea del archivo usuarios.txt
     * @return User creado
     */
    public static User fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        
        if (parts.length != 6) {
            throw new IllegalArgumentException("Formato de usuario inválido");
        }
        
        int idNumber = Integer.parseInt(parts[0]);
        String name = parts[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate birthDate = LocalDate.parse(parts[2], formatter);
        String gender = parts[3];
        String passwordHash = parts[4];
        String email = parts[5];
        
        return new User(idNumber, name, birthDate, gender, passwordHash, email);
    }
    
    // ========== GETTERS Y SETTERS ==========
    
    public int getIdNumber() {
        return idNumber;
    }
    
    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario: %s (Cédula: %d, Edad: %d años, Email: %s)",
            name, idNumber, calculateAge(), email);
    }
}