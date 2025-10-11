package poo.uniquindio.edu.co.homa.service.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.homa.dto.response.LoginResponse;
import poo.uniquindio.edu.co.homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.security.JwtUtil;
import poo.uniquindio.edu.co.homa.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para el usuario: {}", request.getEmail());

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // Verificar que la cuenta está activa
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new UnauthorizedException("La cuenta no está activa. Por favor, verifica tu correo electrónico.");
        }

        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasena()
                )
        );

       // ...existing code...
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token
        String email = usuario.getEmail();
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        String token = jwtUtil.generarToken(email, claims);
// ...existing code...

        log.info("Login exitoso para el usuario: {}", request.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public void logout(String token) {
        // En una implementación real, aquí se invalidaría el token
        // Por ejemplo, agregándolo a una lista negra en Redis
        SecurityContextHolder.clearContext();
        log.info("Logout exitoso");
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // Validar el refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Token de actualización inválido");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String newToken = jwtUtil.generateTokenFromEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        return LoginResponse.builder()
                .token(newToken)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }
}
