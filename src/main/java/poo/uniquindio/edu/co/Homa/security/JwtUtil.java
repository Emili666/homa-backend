package poo.uniquindio.edu.co.homa.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_HOURS = 1L;

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
}