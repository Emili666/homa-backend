package poo.uniquindio.edu.co.homa.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String fromPassword;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Configura la sesión SMTP (por ejemplo, para Gmail).
     */
    private Session getMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });
    }

    /**
     * Método genérico para enviar correos.
     */
    private void enviarCorreo(String destinatario, String asunto, String contenido) {
        try {
            MimeMessage message = new MimeMessage(getMailSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(contenido);

            Transport.send(message);
            log.info("✅ Correo enviado correctamente a {}", destinatario);

        } catch (MessagingException e) {
            log.error("❌ Error al enviar correo: {}", e.getMessage());
        }
    }

    /**
     * Enviar correo de activación de cuenta.
     */
    public void enviarEmailActivacion(String email, String codigoActivacion) {
        String asunto = "Activa tu cuenta en HOMA";
        String cuerpo = String.format("""
                ¡Bienvenido a HOMA!

                Para activar tu cuenta, haz clic en el siguiente enlace:
                %s/activar-cuenta?codigo=%s

                Si no solicitaste esta cuenta, ignora este mensaje.

                Saludos,
                El equipo de HOMA
                """, frontendUrl, codigoActivacion);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Enviar correo de recuperación de contraseña.
     */
    public void enviarEmailRecuperacion(String email, String codigo) {
        String asunto = "Recuperación de contraseña - HOMA";
        String cuerpo = String.format("""
                Hola,

                Recibimos una solicitud para restablecer tu contraseña.

                Para restablecerla, haz clic en el siguiente enlace:
                %s/restablecer-contrasena?codigo=%s

                Este enlace expirará en 24 horas.

                Si no solicitaste este cambio, ignora este mensaje.

                Saludos,
                El equipo de HOMA
                """, frontendUrl, codigo);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Enviar correo de confirmación de reserva.
     */
    public void enviarEmailConfirmacionReserva(String email, String nombreAlojamiento, String fechas) {
        String asunto = "Confirmación de reserva - HOMA";
        String cuerpo = String.format("""
                Hola,

                Tu reserva ha sido confirmada.

                Alojamiento: %s
                Fechas: %s

                Puedes ver los detalles en tu perfil.

                Saludos,
                El equipo de HOMA
                """, nombreAlojamiento, fechas);

        enviarCorreo(email, asunto, cuerpo);
    }
}
