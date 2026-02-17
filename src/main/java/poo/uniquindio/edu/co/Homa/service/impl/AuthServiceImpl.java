package poo.uniquindio.edu.co.Homa.service.impl;

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
import poo.uniquindio.edu.co.Homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.Homa.dto.response.LoginResponse;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.mapper.UsuarioMapper;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.security.JwtUtil;
import poo.uniquindio.edu.co.Homa.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para el usuario: {}", request.getEmail());

        // Verificar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new UnauthorizedException("Cuenta no activa");
        }

        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        String token = jwtUtil.generarToken(usuario.getEmail(), claims);

        log.info("Login exitoso para el usuario: {}", request.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .usuario(usuarioMapper.toResponse(usuario))
                .build();
    }

    @Override
    public void logout(String token) {
        // Aquí podrías agregar el token a una blacklist si quieres invalidarlo
        SecurityContextHolder.clearContext();
        log.info("Logout exitoso");
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Token de actualización inválido");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        String newToken = jwtUtil.generarToken(email, claims);

        return LoginResponse.builder()
                .token(newToken)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .usuario(usuarioMapper.toResponse(usuario))
                .build();
    }
}
