package poo.uniquindio.edu.co.homa.service.impl;

import co.edu.uniquindio.homa.dto.request.LoginRequest;
import co.edu.uniquindio.homa.dto.response.LoginResponse;
import co.edu.uniquindio.homa.exception.BusinessException;
import co.edu.uniquindio.homa.model.entity.Usuario;
import co.edu.uniquindio.homa.model.enums.EstadoUsuario;
import co.edu.uniquindio.homa.repository.UsuarioRepository;
import co.edu.uniquindio.homa.service.AuthService;
import co.edu.uniquindio.homa.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        // Verificar que la cuenta está activa
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BusinessException("La cuenta no está activa. Por favor, verifica tu correo electrónico.");
        }

        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasena()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token
        String token = jwtUtil.generateToken(authentication);

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
            throw new BusinessException("Token de actualización inválido");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String newToken = jwtUtil.generateTokenFromEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        return LoginResponse.builder()
                .token(newToken)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }
}
