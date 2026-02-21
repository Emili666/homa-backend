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
    log.info("Registrando nuevo usuario: {} con rol: {}", request.getEmail(), request.getRol());

    // Verificar que el email no esté registrado
    if (usuarioRepository.existsByEmail(request.getEmail())) {
        throw new BusinessException("El email ya está registrado");
    }

    // Crear el usuario a partir del mapper
    Usuario usuario = usuarioMapper.toEntity(request);

    // Encriptar contraseña del usuario
    usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

    // Estado inicial: INACTIVO (hasta que active la cuenta)
    usuario.setEstado(EstadoUsuario.INACTIVO);

    // Establecer esAnfitrion basado en el rol
    usuario.setEsAnfitrion(request.getRol() == RolUsuario.Anfitrion);

    log.info("Usuario configurado - Rol: {}, esAnfitrion: {}", usuario.getRol(), usuario.getEsAnfitrion());

    // Generar y asignar el código único de activación
    String codigoActivacion = UUID.randomUUID().toString();
    usuario.setCodigoActivacion(codigoActivacion);

    // Fecha de creación
    usuario.setCreadoEn(LocalDateTime.now());

    // Guardar en base de datos
    usuario = usuarioRepository.save(usuario);

    // Enviar email de activación con el código
    try {
        emailService.enviarEmailActivacion(usuario.getEmail(), usuario.getCodigoActivacion());
    } catch (Exception e) {
        log.warn("No se pudo enviar correo de activacion para {}: {}", usuario.getEmail(), e.getMessage());
    }

    log.info("Usuario registrado exitosamente: {} - Rol: {}, esAnfitrion: {}",
             usuario.getEmail(), usuario.getRol(), usuario.getEsAnfitrion());
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
    public UsuarioResponse actualizarConFoto(Long id, ActualizarUsuarioRequest request, MultipartFile foto) {
        log.info("Actualizando usuario con id: {} (con foto)", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Actualizar campos básicos
        log.debug("Datos recibidos - Nombre: {}, Email: {}, Telefono: {}, Contrasena: {}",
                  request.getNombre(), request.getEmail(), request.getTelefono(),
                  request.getContrasena() != null ? "****" : null);

        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            log.debug("Actualizando nombre de '{}' a '{}'", usuario.getNombre(), request.getNombre());
            usuario.setNombre(request.getNombre().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Verificar que el nuevo email no esté en uso por otro usuario
            String nuevoEmail = request.getEmail().trim();
            if (!usuario.getEmail().equals(nuevoEmail) &&
                usuarioRepository.existsByEmail(nuevoEmail)) {
                throw new BusinessException("El email ya está registrado");
            }
            log.debug("Actualizando email de '{}' a '{}'", usuario.getEmail(), nuevoEmail);
            usuario.setEmail(nuevoEmail);
        }
        if (request.getTelefono() != null && !request.getTelefono().trim().isEmpty()) {
            log.debug("Actualizando telefono de '{}' a '{}'", usuario.getTelefono(), request.getTelefono());
            usuario.setTelefono(request.getTelefono().trim());
        }
        if (request.getContrasena() != null && !request.getContrasena().trim().isEmpty()) {
            log.debug("Actualizando contraseña");
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        // Subir foto a Cloudinary si se proporciona
        if (foto != null && !foto.isEmpty()) {
            try {
                // Eliminar foto anterior si existe
                if (usuario.getFoto() != null && !usuario.getFoto().isBlank()) {
                    // Extraer public_id de la URL de Cloudinary
                    String publicId = extraerPublicIdDeUrl(usuario.getFoto());
                    if (publicId != null) {
                        imageStorageService.eliminarImagen(publicId);
                    }
                }

                // Subir nueva foto
                ImageStorageService.UploadResult resultado = imageStorageService.subirImagen(foto);
                usuario.setFoto(resultado.url());
                log.info("Foto de perfil actualizada para usuario: {}", usuario.getEmail());
            } catch (Exception e) {
                log.error("Error al procesar foto de perfil: {}", e.getMessage());
                throw new BusinessException("Error al subir la foto de perfil");
            }
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario guardado en BD - ID: {}, Email: {}, Nombre: {}, Telefono: {}",
                 usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getTelefono());

        UsuarioResponse response = usuarioMapper.toResponse(usuario);
        log.info("Usuario actualizado exitosamente con foto: {}", usuario.getEmail());
        return response;
    }

    private String extraerPublicIdDeUrl(String url) {
        // URL de Cloudinary tiene formato: https://res.cloudinary.com/.../upload/v.../folder/imagen.jpg
        // Necesitamos extraer "folder/imagen" (sin extensión)
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }

        try {
            String[] partes = url.split("/upload/");
            if (partes.length < 2) return null;

            String[] segmentos = partes[1].split("/");
            if (segmentos.length < 3) return null;

            // Construir public_id: folder/archivo (sin extensión)
            String folder = segmentos[1];
            String archivo = segmentos[2].substring(0, segmentos[2].lastIndexOf('.'));
            return folder + "/" + archivo;
        } catch (Exception e) {
            log.warn("No se pudo extraer public_id de URL: {}", url);
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
            .orElseThrow(
                    () -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.getEmail()));

    // Generar código de reinicio
    String codigo = UUID.randomUUID().toString();
    LocalDateTime expiracion = LocalDateTime.now().plusMinutes(15);

    // ✅ Aquí usamos la entidad correcta
    ContrasenaCodigoReinicio codigoReinicio = ContrasenaCodigoReinicio.builder()
            .usuario(usuario)
            .codigo(codigo)
            .creadoEn(expiracion)
            .usado(false)
            .build();

    codigoReinicioRepository.save(codigoReinicio);

    // Enviar email con el código
    emailService.enviarEmailRecuperacion(usuario.getEmail(), usuario.getNombre(), codigo);

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

    Usuario usuario = usuarioRepository.findByCodigoActivacion(codigo)
            .orElseThrow(() -> new BusinessException("Código de activación inválido"));

    if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
        throw new BusinessException("La cuenta ya está activada");
    }

    usuario.setEstado(EstadoUsuario.ACTIVO);
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

   
   @Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getContrasena())
            .roles(usuario.getRol().name().toUpperCase()) // Asegúrate de tener el campo 'rol' en tu entidad Usuario
            .build();
}


}
