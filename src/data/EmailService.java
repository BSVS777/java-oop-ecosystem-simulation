package data;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Servicio para envio de correos electronicos.
 * Utiliza JavaMail API para notificaciones del sistema.
 */
public class EmailService {
    
    // Configuracion del servidor SMTP (Gmail como ejemplo por ser el mas comun)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "emailsendertest9@gmail.com"; // direccion email
    private static final String SENDER_PASSWORD = "olgv ijta giso ccvj       "; // password generada por google
    
    /**
     * Envia un correo de bienvenida al registrarse
     * @param recipientEmail Email del destinatario
     * @param userName Nombre del usuario
     * @return true si se envio exitosamente
     */
    public boolean sendWelcomeEmail(String recipientEmail, String userName) {
        String subject = "Welcome to Ecosystem Simulator";
        String body = buildWelcomeEmailBody(userName);
        
        return sendEmail(recipientEmail, subject, body);
    }
    
    /**
     * Envia un correo con reporte de simulacion
     * @param recipientEmail Email del destinatario
     * @param userName Nombre del usuario
     * @param reportContent Contenido del reporte
     * @return true si se envio exitosamente
     */
    public boolean sendReportEmail(String recipientEmail, String userName, String reportContent) {
        String subject = "Ecosystem Simulation Report";
        String body = buildReportEmailBody(userName, reportContent);
        
        return sendEmail(recipientEmail, subject, body);
    }
    
    /**
     * Envia un correo con archivo adjunto (para PDF)
     * @param recipientEmail Email del destinatario
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     * @param attachmentPath Ruta del archivo adjunto
     * @return true si se envio exitosamente
     */
    public boolean sendEmailWithAttachment(String recipientEmail, String subject, 
                                          String body, String attachmentPath) {
        try {
            // Configurar propiedades
            Properties props = getMailProperties();
            
            // Crear sesion
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });
            
            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, 
                                 InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            
            // Crear multipart para adjunto
            Multipart multipart = new MimeMultipart();
            
            // Parte del texto
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body, "UTF-8", "html");
            multipart.addBodyPart(textPart);
            
            // Parte del archivo adjunto
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachmentPath);
            multipart.addBodyPart(attachmentPart);
            
            // Establecer contenido
            message.setContent(multipart);
            
            // Enviar
            Transport.send(message);
            
            System.out.println("[OK] Email with attachment sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Envia un correo simple sin adjuntos
     * @param recipientEmail Email del destinatario
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     * @return true si se envio exitosamente
     */
    private boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            // Configurar propiedades
            Properties props = getMailProperties();
            
            // Crear sesion con autenticacion
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });
            
            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, 
                                 InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");
            
            // Enviar
            Transport.send(message);
            
            System.out.println("[OK] Email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Configura las propiedades del servidor SMTP
     * @return Properties configuradas
     */
    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        return props;
    }
    
    /**
     * Construye el cuerpo del correo de bienvenida
     * @param userName Nombre del usuario
     * @return HTML del correo
     */
    private String buildWelcomeEmailBody(String userName) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #B0CE88; padding: 20px;'>" +
               "  <div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;'>" +
               "    <h1 style='color: #04391D;'>Welcome to Ecosystem Simulator!</h1>" +
               "    <p style='color: #4C763B; font-size: 16px;'>Hello <strong>" + userName + "</strong>,</p>" +
               "    <div style='background-color: #FFFD8F; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
               "      <p style='color: #04391D; margin: 0;'><strong>Features:</strong></p>" +
               "      <ul style='color: #4C763B;'>" +
               "        <li>Simulate prey and predator interactions</li>" +
               "        <li>View detailed statistics and reports</li>" +
               "        <li>Export simulation results as PDF</li>" +
               "      </ul>" +
               "    </div>" +
               "    <p style='color: #04391D;'><strong>The Ecosystem Simulator Team</strong></p>" +
               "  </div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Construye el cuerpo del correo de reporte
     * @param userName Nombre del usuario
     * @param reportContent Contenido del reporte
     * @return HTML del correo
     */
    private String buildReportEmailBody(String userName, String reportContent) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #B0CE88; padding: 20px;'>" +
               "  <div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;'>" +
               "    <h1 style='color: #04391D;'>Simulation Report</h1>" +
               "    <p style='color: #4C763B; font-size: 16px;'>Hello <strong>" + userName + "</strong>,</p>" +
               "    <p style='color: #4C763B;'>Your simulation has completed. Here are the results:</p>" +
               "    <div style='background-color: #F5F5F5; padding: 20px; border-radius: 5px; margin: 20px 0;'>" +
               "      <pre style='color: #04391D; white-space: pre-wrap; font-family: monospace;'>" + reportContent + "</pre>" +
               "    </div>" +
               "    <p style='color: #4C763B;'>Check the attached PDF for detailed analysis.</p>" +
               "    <p style='color: #04391D;'><strong>The Ecosystem Simulator Team</strong></p>" +
               "  </div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Metodo de prueba
     */
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        
        System.out.println("=== EMAIL SERVICE TEST ===\n");
        System.out.println("[INFO] Testing welcome email...");
        
        // IMPORTANTE: Configurar SENDER_EMAIL y SENDER_PASSWORD antes de probar
        boolean sent = emailService.sendWelcomeEmail(
            "uyv310599@gmail.com", 
            "BSVS"
        );
        
        if (sent) {
            System.out.println("[OK] Test passed - Email sent successfully");
        } else {
            System.out.println("[FAIL] Test failed - Check configuration");
        }
    }
}