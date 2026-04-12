package poo.uniquindio.edu.co.Homa.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.dto.request.ActualizarUsuarioRequest;
import poo.uniquindio.edu.co.Homa.dto.request.CambiarContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.RecuperarContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.RestablecerContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.Homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.exception.ForbiddenException;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.mapper.UsuarioMapper;
import poo.uniquindio.edu.co.Homa.model.entity.ContrasenaCodigoReinicio;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.Homa.repository.ContrasenaCodigoReinicioRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.ImageStorageService;
import poo.uniquindio.edu.co.Homa.service.UsuarioService;
import poo.uniquindio.edu.co.Homa.util.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ContrasenaCodigoReinicioRepository codigoReinicioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ImageStorageService imageStorageService;

    @Override
    @Transactional
    public UsuarioResponse registrar(UsuarioRegistroRequest request) {
        log.info("Registrando nuevo usuario con rol: {}", request.getRol());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuario.setEsAnfitrion(request.getRol() == RolUsuario.Anfitrion);
        usuario.setCodigoActivacion(UUID.randomUUID().toString());

        usuario = usuarioRepository.save(usuario);

        try {
            emailService.enviarEmailActivacion(usuario.getEmail(), usuario.getCodigoActivacion());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de activacion para {}: {}", usuario.getEmail(), e.getMessage());
        }

        log.info("Usuario registrado exitosamente con rol: {}", usuario.getRol());
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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

        log.info("Usuario actualizado exitosamente");
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarConFoto(Long id, ActualizarUsuarioRequest request, MultipartFile foto) {
        log.info("Actualizando usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            usuario.setNombre(request.getNombre().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String nuevoEmail = request.getEmail().trim();
            if (!usuario.getEmail().equals(nuevoEmail) && usuarioRepository.existsByEmail(nuevoEmail)) {
                throw new BusinessException("El email ya está registrado");
            }
            usuario.setEmail(nuevoEmail);
        }
        if (request.getTelefono() != null && !request.getTelefono().trim().isEmpty()) {
            usuario.setTelefono(request.getTelefono().trim());
        }
        if (request.getContrasena() != null && !request.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        if (foto != null && !foto.isEmpty()) {
            try {
                if (usuario.getFoto() != null && !usuario.getFoto().isBlank()) {
                    String publicId = extraerPublicIdDeUrl(usuario.getFoto());
                    if (publicId != null) {
                        imageStorageService.eliminarImagen(publicId);
                    }
                }
                ImageStorageService.UploadResult resultado = imageStorageService.subirImagen(foto);
                usuario.setFoto(resultado.url());
            } catch (Exception e) {
                log.error("Error al procesar foto de perfil: {}", e.getMessage());
                throw new BusinessException("Error al subir la foto de perfil");
            }
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente con foto");
        return usuarioMapper.toResponse(usuario);
    }

    private String extraerPublicIdDeUrl(String url) {
        if (url == null || !url.contains("cloudinary.com")) return null;
        try {
            String[] partes = url.split("/upload/");
            if (partes.length < 2) return null;
            String[] segmentos = partes[1].split("/");
            if (segmentos.length < 3) return null;
            String folder = segmentos[1];
            String archivo = segmentos[2].substring(0, segmentos[2].lastIndexOf('.'));
            return folder + "/" + archivo;
        } catch (Exception e) {
            log.warn("No se pudo extraer public_id de URL de Cloudinary");
            return null;
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setEstado(EstadoUsuario.ELIMINADO);
        usuarioRepository.save(usuario);

        log.info("Usuario eliminado exitosamente");
    }

    @Override
    @Transactional
    public void cambiarContrasena(Long id, CambiarContrasenaRequest request, String emailAutenticado) {
        log.info("Cambiando contraseña para usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar que el usuario autenticado sea el dueño del recurso
        if (!usuario.getEmail().equals(emailAutenticado)) {
            throw new ForbiddenException("No tienes permiso para cambiar la contraseña de otro usuario");
        }

        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada exitosamente");
    }

    @Override
    @Transactional
    public void solicitarRecuperacionContrasena(RecuperarContrasenaRequest request) {
        log.info("Solicitud de recuperación de contraseña recibida");

        // Respuesta genérica para evitar enumeración de usuarios
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(usuario -> {
            String codigo = UUID.randomUUID().toString();
            LocalDateTime expiracion = LocalDateTime.now().plusMinutes(15);

            ContrasenaCodigoReinicio codigoReinicio = ContrasenaCodigoReinicio.builder()
                    .usuario(usuario)
                    .codigo(codigo)
                    .expiraEn(expiracion)
                    .usado(false)
                    .build();

            codigoReinicioRepository.save(codigoReinicio);
            emailService.enviarEmailRecuperacion(usuario.getEmail(), usuario.getNombre(), codigo);
            log.info("Código de recuperación enviado");
        });
        // Si el email no existe, no se lanza excepción (evita enumeración)
    }

    @Override
    @Transactional
    public void restablecerContrasena(RestablecerContrasenaRequest request) {
        log.info("Restableciendo contraseña con código");

        ContrasenaCodigoReinicio codigoReinicio = codigoReinicioRepository.findByCodigo(request.getCodigo())
                .orElseThrow(() -> new BusinessException("Código de reinicio inválido o expirado"));

        if (codigoReinicio.isUsado()) {
            throw new BusinessException("El código de reinicio ya fue utilizado");
        }

        if (codigoReinicio.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El código de reinicio ha expirado");
        }

        Usuario usuario = codigoReinicio.getUsuario();
        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);

        codigoReinicio.setUsado(true);
        codigoReinicioRepository.save(codigoReinicio);

        log.info("Contraseña restablecida exitosamente");
    }

    @Override
    @Transactional
    public void activarCuenta(String codigo) {
        log.info("Activando cuenta con código");

        Usuario usuario = usuarioRepository.findByCodigoActivacion(codigo)
                .orElseThrow(() -> new BusinessException("Código de activación inválido"));

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            throw new BusinessException("La cuenta ya está activada");
        }

        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        log.info("Cuenta activada exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, String estado) {
        log.info("Cambiando estado de usuario {} a {}", id, estado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setEstado(EstadoUsuario.valueOf(estado));
        usuarioRepository.save(usuario);

        log.info("Estado cambiado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().name().toUpperCase())
                .build();
    }
}
