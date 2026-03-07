package poo.uniquindio.edu.co.Homa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Servicio para verificar tokens de Cloudflare Turnstile en el servidor.
 *
 * Flujo:
 * 1. El frontend genera un token con la Site Key pública.
 * 2. El backend envía ese token + la Secret Key a Cloudflare para verificarlo.
 * 3. Cloudflare responde si el token es válido (success: true/false).
 *
 * Secret Key configurada en application.properties:
 * cloudflare.turnstile.secret=0x4AAAAAACnmLUH8G8QHJ1oXdwBNdg2616g
 */
@Slf4j
@Service
public class TurnstileVerificationService {

    @Value("${cloudflare.turnstile.secret}")
    private String secretKey;

    @Value("${cloudflare.turnstile.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Verifica si el token de Turnstile es válido.
     *
     * @param token    Token generado por el widget en el frontend
     * @param remoteIp IP del cliente (opcional, mejora la verificación)
     * @return true si el token es válido, false si no
     */
    public boolean verificar(String token, String remoteIp) {
        // Si la secret key está vacía o es la de testing, dejar pasar
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("[Turnstile] Secret key no configurada, se omite la verificación.");
            return true;
        }

        // La clave de testing "siempre válida" de Cloudflare
        if (secretKey.equals("1x0000000000000000000000000000000AA")) {
            log.debug("[Turnstile] Modo testing activo (secret key de desarrollo).");
            return true;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("secret", secretKey);
            body.add("response", token);
            if (remoteIp != null && !remoteIp.isBlank()) {
                body.add("remoteip", remoteIp);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, request, Map.class);

            Map<?, ?> responseBody = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                boolean success = Boolean.TRUE.equals(responseBody.get("success"));
                if (!success) {
                    Object errorCodes = responseBody.get("error-codes");
                    log.warn("[Turnstile] Verificación fallida. Error codes: {}", errorCodes);
                }
                return success;
            }

            log.error("[Turnstile] Respuesta inesperada de Cloudflare: {}", response.getStatusCode());
            return false;

        } catch (Exception e) {
            log.error("[Turnstile] Error al verificar token con Cloudflare: {}", e.getMessage());
            // En caso de error de red con Cloudflare, permitir el registro
            // para no bloquear a usuarios legítimos por un problema de conectividad
            return true;
        }
    }
}
