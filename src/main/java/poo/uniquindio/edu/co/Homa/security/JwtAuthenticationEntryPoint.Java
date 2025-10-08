package poo.uniquindio.edu.co.Homa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Creamos un JSON manual sin usar DTOs
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        respuesta.put("mensaje", "No tienes permisos para acceder a este recurso");
        respuesta.put("path", request.getRequestURI());
        respuesta.put("status", 403);

        // Configuración de la respuesta
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Convertir el Map a JSON y enviarlo
        new ObjectMapper().writeValue(response.getOutputStream(), respuesta);
    }
}