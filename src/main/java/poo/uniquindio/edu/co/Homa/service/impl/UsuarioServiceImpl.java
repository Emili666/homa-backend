package poo.uniquindio.edu.co.homa.service.impl;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.homa.dto.request.ActualizarUsuarioRequest;
import poo.uniquindio.edu.co.homa.dto.request.CambiarContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.RecuperarContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.RestablecerContrasenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.homa.exception.BusinessException;
import poo.uniquindio.edu.co.homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.homa.mapper.UsuarioMapper;
import poo.uniquindio.edu.co.homa.model.entity.ContrasenaCodigoReinicio;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.homa.repository.ContrasenaCodigoReinicioRepository;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.UsuarioService;
import poo.uniquindio.edu.co.homa.util.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContrasenaCodigoReinicioRepository codigoReinicioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public UsuarioResponse registrar(UsuarioRegistroRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());

        // Verificar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuario.setContrasena(UUID.randomUUID().toString());
        usuario.setCreadoEn(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        // Enviar email de activación
        emailService.enviarEmailActivacion(usuario.getEmail(), usuario.getCodigosReinicio());

        log.info("Usuario registrado exitosamente: {}", usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
        log.info("Actualizando usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuarioMapper.updateEntityFromRequest(request, usuario);
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario actualizado exitosamente: {}", usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setEstado(EstadoUsuario.ELIMINADO);
        usuarioRepository.save(usuario);

        log.info("Usuario eliminado exitosamente: {}", usuario.getEmail());
    }

    @Override
    @Transactional
    public void cambiarContrasena(Long id, CambiarContrasenaRequest request) {
        log.info("Cambiando contraseña para usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada exitosamente para usuario: {}", usuario.getEmail());
    }

    @Override
    @Transactional
    public void solicitarRecuperacionContrasena(RecuperarContrasenaRequest request) {
        log.info("Solicitud de recuperación de contraseña para: {}", request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.getEmail()));

        // Generar código de reinicio
        String codigo = UUID.randomUUID().toString();
        LocalDateTime expiracion = LocalDateTime.now().plusHours(24);

        ContrasenaCodigoReinicio codigoReinicio = ContrasenaCodigoReinicio.builder()
                .usuario(usuario)
                .codigo(codigo)
                .fechaExpiracion(expiracion)
                .usado(false)
                .build();

        codigoReinicioRepository.save(codigoReinicio);

        // Enviar email con código
        emailService.enviarEmailRecuperacion(usuario.getEmail(), codigo);

        log.info("Código de recuperación enviado a: {}", usuario.getEmail());
    }

    @Override
    @Transactional
    public void restablecerContrasena(RestablecerContrasenaRequest request) {
        log.info("Restableciendo contraseña con código: {}", request.getCodigo());

        ContrasenaCodigoReinicio codigoReinicio = codigoReinicioRepository.findByCodigo(request.getCodigo())
                .orElseThrow(() -> new BusinessException("Código de reinicio inválido"));

        // Verificar que no esté usado
        if (codigoReinicio.isUsado()) {
            throw new BusinessException("El código de reinicio ya fue utilizado");
        }

        // Verificar que no esté expirado
        if (codigoReinicio.getCreadoEn().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El código de reinicio ha expirado");
        }

        // Actualizar contraseña
        Usuario usuario = codigoReinicio.getUsuario();
        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        // Marcar código como usado
        codigoReinicio.setUsado(true);
        codigoReinicioRepository.save(codigoReinicio);

        log.info("Contraseña restablecida exitosamente para usuario: {}", usuario.getEmail());
    }

    @Override
    @Transactional
    public void activarCuenta(String codigo) {
        log.info("Activando cuenta con código: {}", codigo);

        Usuario usuario = usuarioRepository.findById(codigo)
                .orElseThrow(() -> new BusinessException("Código de activación inválido"));

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            throw new BusinessException("La cuenta ya está activada");
        }

        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setCodigosReinicio(null);
        usuarioRepository.save(usuario);

        log.info("Cuenta activada exitosamente: {}", usuario.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, String estado) {
        log.info("Cambiando estado de usuario {} a {}", id, estado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setEstado(EstadoUsuario.valueOf(estado));
        usuarioRepository.save(usuario);

        log.info("Estado cambiado exitosamente para usuario: {}", usuario.getEmail());
    }
       
}
