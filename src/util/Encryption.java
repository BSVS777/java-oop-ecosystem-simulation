package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Clase utilitaria para encriptación de contraseñas usando SHA-256.
 * Implementa hash unidireccional seguro.
 */
public class Encryption {
    
    /**
     * Encripta una contraseña usando SHA-256
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 en formato hexadecimal
     */
    public static String encryptSHA256(String password) {
        try {
            // Crea instancia de MessageDigest con algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Aplica hash a la contraseña
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convierte bytes a formato hexadecimal
            return bytesToHex(hashBytes);
            
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre está disponible en Java
            throw new RuntimeException("Error al encriptar: SHA-256 no disponible", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con un hash
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado en el sistema
     * @return true si la contraseña es correcta
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String newHash = encryptSHA256(password);
        return newHash.equals(storedHash);
    }
    
    /**
     * Convierte array de bytes a string hexadecimal
     * @param bytes Array de bytes
     * @return String hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    /**
     * Método para pruebas - Genera hash de ejemplo
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Ejemplo de uso
        String password = "MyPassword123";
        String hash = encryptSHA256(password);
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash SHA-256: " + hash);
        System.out.println("Verificación correcta: " + verifyPassword(password, hash));
        System.out.println("Verificación incorrecta: " + verifyPassword("OtherPassword", hash));
    }
}