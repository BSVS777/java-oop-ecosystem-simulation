package util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Clase utilitaria para validación de datos de entrada.
 * Implementa validaciones para campos de usuario.
 */
public class Validations {
    
    // Patrón para validar email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    /**
     * Valida que una cédula sea un número positivo válido
     * @param idNumber Cédula a validar
     * @return true si es válida
     */
    public static boolean validateIdNumber(String idNumber) {
        if (idNumber == null || idNumber.trim().isEmpty()) {
            return false;
        }
        
        try {
            int idNum = Integer.parseInt(idNumber.trim());
            return idNum > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida que un nombre no esté vacío y tenga al menos 2 caracteres
     * @param name Nombre a validar
     * @return true si es válido
     */
    public static boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String cleanName = name.trim();
        return cleanName.length() >= 2 && cleanName.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }
    
    /**
     * Valida formato de correo electrónico
     * @param email Correo a validar
     * @return true si el formato es válido
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
        return matcher.matches();
    }
    
    /**
     * Valida fortaleza de contraseña
     * Requisitos: mínimo 6 caracteres, al menos una letra y un número
     * @param password Contraseña a validar
     * @return true si cumple requisitos
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Mínimo 6 caracteres
        if (password.length() < 6) {
            return false;
        }
        
        // Al menos una letra
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        
        // Al menos un número
        boolean hasNumber = password.matches(".*[0-9].*");
        
        return hasLetter && hasNumber;
    }
    
    /**
     * Obtiene mensaje de error para contraseña débil
     * @param password Contraseña a evaluar
     * @return Mensaje descriptivo del error
     */
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña no puede estar vacía";
        }
        
        if (password.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        
        if (!password.matches(".*[a-zA-Z].*")) {
            return "La contraseña debe contener al menos una letra";
        }
        
        if (!password.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }
        
        return "Contraseña válida";
    }
    
    /**
     * Valida que dos contraseñas coincidan
     * @param password1 Primera contraseña
     * @param password2 Segunda contraseña (confirmación)
     * @return true si son idénticas
     */
    public static boolean validatePasswordMatch(String password1, String password2) {
        if (password1 == null || password2 == null) {
            return false;
        }
        return password1.equals(password2);
    }
    
    /**
     * Valida que un campo no esté vacío
     * @param value Valor a validar
     * @return true si no está vacío
     */
    public static boolean isFieldNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Método de prueba
     */
    public static void main(String[] args) {
        // Pruebas de validación
        System.out.println("=== PRUEBAS DE VALIDACIÓN ===");
        
        System.out.println("\nCédulas:");
        System.out.println("123456789: " + validateIdNumber("123456789"));
        System.out.println("-123: " + validateIdNumber("-123"));
        System.out.println("abc: " + validateIdNumber("abc"));
        
        System.out.println("\nNombres:");
        System.out.println("Juan Pérez: " + validateName("Juan Pérez"));
        System.out.println("A: " + validateName("A"));
        System.out.println("123: " + validateName("123"));
        
        System.out.println("\nEmails:");
        System.out.println("user@example.com: " + validateEmail("user@example.com"));
        System.out.println("invalid@: " + validateEmail("invalid@"));
        System.out.println("noAtSign.com: " + validateEmail("noAtSign.com"));
        
        System.out.println("\nContraseñas:");
        System.out.println("Pass123: " + validatePassword("Pass123"));
        System.out.println("12345: " + validatePassword("12345"));
        System.out.println("short: " + validatePassword("short"));
        System.out.println("Error: " + getPasswordError("abc"));
    }
}