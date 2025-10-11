package poo.uniquindio.edu.co.homa.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;



@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void enviarEmailActivacion(String email, String codigoActivacion) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Activa tu cuenta en HOMA");
            message.setText(String.format(
                    "Bienvenido a HOMA!\n\n" +
                    "Para activar tu cuenta, haz clic en el siguiente enlace:\n" +
                    "%s/activar-cuenta?codigo=%s\n\n" +
                    "Si no solicitaste esta cuenta, ignora este mensaje.\n\n" +
                    "Saludos,\n" +
                    "El equipo de HOMA",
                    frontendUrl, codigoActivacion
            ));

            mailSender.send(message);
            log.info("Email de activación enviado a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de activación: {}", e.getMessage());
        }
    }

    public void enviarEmailRecuperacion(String email, String codigo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Recuperación de contraseña - HOMA");
            message.setText(String.format(
                    "Hola,\n\n" +
                    "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                    "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" +
                    "%s/restablecer-contrasena?codigo=%s\n\n" +
                    "Este enlace expirará en 24 horas.\n\n" +
                    "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                    "Saludos,\n" +
                    "El equipo de HOMA",
                    frontendUrl, codigo
            ));

            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación: {}", e.getMessage());
        }
    }

    public void enviarEmailConfirmacionReserva(String email, String nombreAlojamiento, String fechas) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Confirmación de reserva - HOMA");
            message.setText(String.format(
                    "Hola,\n\n" +
                    "Tu reserva ha sido confirmada!\n\n" +
                    "Alojamiento: %s\n" +
                    "Fechas: %s\n\n" +
                    "Puedes ver los detalles de tu reserva en tu perfil.\n\n" +
                    "Saludos,\n" +
                    "El equipo de HOMA",
                    nombreAlojamiento, fechas
            ));

            mailSender.send(message);
            log.info("Email de confirmación de reserva enviado a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de confirmación: {}", e.getMessage());
        }
    }
}
