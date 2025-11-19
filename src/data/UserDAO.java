package data;

import model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de usuarios en archivo.
 * Maneja lectura y escritura en usuarios.txt
 */
public class UserDAO {
    
    private static final String USERS_FILE = "usuarios.txt";
    
    /**
     * Guarda un nuevo usuario en el archivo
     * @param user Usuario a guardar
     * @return true si se guardó exitosamente
     */
    public boolean saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(USERS_FILE, true))) {
            
            writer.write(user.toFileFormat());
            writer.newLine();
            
            System.out.println("[OK] User saved: " + user.getName());
            return true;
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga todos los usuarios del archivo
     * @return Lista de usuarios
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("Archivo de usuarios no existe. Se creará al guardar el primer usuario.");
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        User user = User.fromFileFormat(line);
                        users.add(user);
                    } catch (Exception e) {
                        System.err.println("Error al parsear línea: " + line);
                    }
                }
            }
            
            System.out.println("Cargados " + users.size() + " usuarios");
            
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Busca un usuario por cédula
     * @param idNumber Cédula a buscar
     * @return Usuario encontrado o null
     */
    public User findByIdNumber(int idNumber) {
        List<User> users = loadUsers();
        
        for (User user : users) {
            if (user.getIdNumber() == idNumber) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Busca un usuario por correo electrónico
     * @param email Correo a buscar
     * @return Usuario encontrado o null
     */
    public User findByEmail(String email) {
        List<User> users = loadUsers();
        
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Verifica si una cédula ya está registrada
     * @param idNumber Cédula a verificar
     * @return true si ya existe
     */
    public boolean existsIdNumber(int idNumber) {
        return findByIdNumber(idNumber) != null;
    }
    
    /**
     * Verifica si un correo ya está registrado
     * @param email Correo a verificar
     * @return true si ya existe
     */
    public boolean existsEmail(String email) {
        return findByEmail(email) != null;
    }
    
    /**
     * Valida credenciales de inicio de sesión
     * @param idNumber Cédula del usuario
     * @param passwordHash Hash de la contraseña
     * @return Usuario si las credenciales son correctas, null si no
     */
    public User validateCredentials(int idNumber, String passwordHash) {
        User user = findByIdNumber(idNumber);
        
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            System.out.println(":) Login exitoso: " + user.getName());
            return user;
        }
        
        System.out.println(":( Credenciales inválidas");
        return null;
    }
    
    /**
     * Actualiza la información de un usuario en el archivo
     * @param updatedUser Usuario con información actualizada
     * @return true si se actualizó exitosamente
     */
    public boolean updateUser(User updatedUser) {
        List<User> users = loadUsers();
        boolean found = false;
        
        // Busca y actualiza el usuario
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getIdNumber() == updatedUser.getIdNumber()) {
                users.set(i, updatedUser);
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.err.println("Usuario no encontrado para actualizar");
            return false;
        }
        
        // Reescribe el archivo completo
        return rewriteFile(users);
    }
    
    /**
     * Elimina un usuario del archivo
     * @param idNumber Cédula del usuario a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean deleteUser(int idNumber) {
        List<User> users = loadUsers();
        boolean deleted = users.removeIf(u -> u.getIdNumber() == idNumber);
        
        if (deleted) {
            return rewriteFile(users);
        }
        
        System.err.println("Usuario no encontrado para eliminar");
        return false;
    }
    
    /**
     * Reescribe el archivo completo con la lista de usuarios
     * @param users Lista de usuarios
     * @return true si se reescribió exitosamente
     */
    private boolean rewriteFile(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            
            for (User user : users) {
                writer.write(user.toFileFormat());
                writer.newLine();
            }
            
            System.out.println("Archivo actualizado correctamente");
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al reescribir archivo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Limpia el archivo de usuarios (para pruebas)
     * @return true si se limpió exitosamente
     */
    public boolean clearFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            writer.write("");
            System.out.println("Archivo de usuarios limpiado");
            return true;
        } catch (IOException e) {
            System.err.println("Error al limpiar archivo: " + e.getMessage());
            return false;
        }
    }
}