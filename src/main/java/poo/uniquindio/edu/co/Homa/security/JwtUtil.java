package poo.uniquindio.edu.co.Homa.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generarToken(String id, Map<String, String> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .Claims(claims)
                .subject(id)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))
                .signWith(obtenerKey())
                .compact();
    }

    public Jws<Claims> decodificarJwt(String jwtString) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(obtenerKey())
                .build();
        return parser.parseClaimsJws(jwtString);
    }

    private SecretKey obtenerKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
