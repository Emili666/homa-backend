package poo.uniquindio.edu.co.homa.util;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

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
     * Configura la sesion SMTP (por ejemplo, para Gmail).
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
     * Envia un correo simple en texto plano.
     */
    private void enviarCorreo(String destinatario, String asunto, String contenido) {
        try {
            MimeMessage message = new MimeMessage(getMailSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(contenido);

            Transport.send(message);
            log.info("Correo enviado correctamente a {}", destinatario);
        } catch (MessagingException e) {
            log.error("Error al enviar correo: {}", e.getMessage());
        }
    }

    /**
     * Envia el correo con el enlace y el codigo de activacion.
     */
    public void enviarEmailActivacion(String email, String codigoActivacion) {
        String asunto = "Activa tu cuenta en HOMA";
        String cuerpo = String.format("""
                Bienvenido a HOMA!

                Para activar tu cuenta, visita:
                %s/activar-cuenta?codigo=%s

                Si prefieres activarla manualmente, usa este codigo:
                %s

                Si no solicitaste esta cuenta, ignora este mensaje.

                Saludos,
                Equipo HOMA
                """, frontendUrl, codigoActivacion, codigoActivacion);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Envia el correo con el enlace para recuperar la contrasena.
     */
    public void enviarEmailRecuperacion(String email, String codigo) {
        String asunto = "Recuperacion de contrasena - HOMA";
        String cuerpo = String.format("""
                Hola,

                Recibimos una solicitud para restablecer tu contrasena.

                Para restablecerla, visita:
                %s/restablecer-contrasena?codigo=%s

                Este enlace expira en 15 minutos.

                Si no solicitaste este cambio, ignora este mensaje.

                Saludos,
                Equipo HOMA
                """, frontendUrl, codigo);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Envia la confirmacion de una reserva.
     */
    public void enviarEmailConfirmacionReserva(String email, String nombreAlojamiento, String fechas) {
        String asunto = "Confirmacion de reserva - HOMA";
        String cuerpo = String.format("""
                Hola,

                Tu reserva ha sido confirmada.

                Alojamiento: %s
                Fechas: %s

                Puedes ver los detalles completos en tu perfil.

                Saludos,
                Equipo HOMA
                """, nombreAlojamiento, fechas);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Envía la cancelación de una reserva.
     */
    public void enviarEmailCancelacionReserva(String email, String nombreAlojamiento, String fechas) {
        String asunto = "Cancelacion de reserva - HOMA";
        String cuerpo = String.format("""
                Hola,

                Tu reserva ha sido cancelada.

                Alojamiento: %s
                Fechas: %s

                Si tienes dudas contacta al anfitrion desde la plataforma.

                Saludos,
                Equipo HOMA
                """, nombreAlojamiento, fechas);

        enviarCorreo(email, asunto, cuerpo);
    }

    /**
     * Notifica al anfitrión que recibió una nueva reserva.
     */
    public void enviarEmailNuevaReservaAnfitrion(String email, String nombreAlojamiento, String nombreHuesped, String fechas) {
        String asunto = "Nueva reserva recibida - HOMA";
        String cuerpo = String.format("""
                Hola,

                Has recibido una nueva solicitud de reserva.

                Alojamiento: %s
                Huesped: %s
                Fechas: %s

                Ingresa a la plataforma para gestionarla.

                Saludos,
                Equipo HOMA
                """, nombreAlojamiento, nombreHuesped, fechas);

        enviarCorreo(email, asunto, cuerpo);
    }
}
