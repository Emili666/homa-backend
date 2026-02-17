package poo.uniquindio.edu.co.Homa.security;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_HOURS = 1L;
    // ...existing code...

    // 🔹 Valida si el token es correcto y no ha expirado
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = decodificarJwt(token);
            Date expiration = claimsJws.getPayload().getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

// ...existing code...

    // 🔹 Genera un token JWT
    public String generarToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        SecretKey key = obtenerKey();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(EXPIRATION_HOURS, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    // 🔹 Valida y decodifica un token JWT
    public Jws<Claims> decodificarJwt(String token) {
        SecretKey key = obtenerKey();

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    // 🔹 Convierte el secretKey a formato HMAC
    private SecretKey obtenerKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    // ...existing code...

    // 🔹 Obtiene el email (subject) desde el token
    public String getEmailFromToken(String token) {
        Jws<Claims> claimsJws = decodificarJwt(token);
        return claimsJws.getPayload().getSubject();
    }
    // ...existing code...

    // 🔹 Genera un token JWT solo con el email
    public String generateTokenFromEmail(String email) {
        Map<String, Object> claims = new HashMap<>();
        // Puedes agregar claims adicionales si lo necesitas
        return generarToken(email, claims);
    }

// ...existing code...

// ...existing code...
}