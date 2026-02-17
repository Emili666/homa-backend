package poo.uniquindio.edu.co.Homa.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.Homa.dto.request.ActualizarUsuarioRequest;
import poo.uniquindio.edu.co.Homa.dto.request.CambiarContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.RecuperarContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.RestablecerContrasenaRequest;
import poo.uniquindio.edu.co.Homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.Homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.Homa.service.UsuarioService;

@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @Operation(summary = "Activar cuenta", description = "Activa la cuenta de un usuario mediante código")
    @GetMapping("/activar/{codigo}")
    public ResponseEntity<Void> activarCuenta(@PathVariable String codigo) {
        usuarioService.activarCuenta(codigo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtener perfil actual", description = "Obtiene los datos del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'HUESPED', 'ANFITRION')")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(authentication.getName()));
    }

    @Operation(summary = "Actualizar perfil actual", description = "Actualiza los datos del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/me", consumes = { "multipart/form-data", "application/json" })
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'HUESPED', 'ANFITRION')")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            Authentication authentication,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @RequestPart(value = "nombre", required = false) String nombre,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "telefono", required = false) String telefono,
            @RequestPart(value = "contrasena", required = false) String contrasena) {

        UsuarioResponse usuario = usuarioService.obtenerPorEmail(authentication.getName());

        // Crear el request con los datos recibidos
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest();
        if (nombre != null) request.setNombre(nombre);
        if (email != null) request.setEmail(email);
        if (telefono != null) request.setTelefono(telefono);
        if (contrasena != null) request.setContrasena(contrasena);

        // Si hay foto, procesarla (necesitarás implementar esto en el servicio)
        return ResponseEntity.ok(usuarioService.actualizarConFoto(usuario.getId(), request, foto));
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los datos de un usuario por su ID")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'HUESPED', 'ANFITRION')")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'HUESPED', 'ANFITRION')")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina (desactiva) un usuario del sistema")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña de un usuario")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/cambiar-contrasena")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'HUESPED', 'ANFITRION')")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Long id,
            @Valid @RequestBody CambiarContrasenaRequest request) {
        usuarioService.cambiarContrasena(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un código para recuperar la contraseña")
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Void> solicitarRecuperacion(@Valid @RequestBody RecuperarContrasenaRequest request) {
        usuarioService.solicitarRecuperacionContrasena(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Restablecer contraseña", description = "Restablece la contraseña usando el código enviado")
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaRequest request) {
        usuarioService.restablecerContrasena(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar todos los usuarios", description = "Lista todos los usuarios del sistema (solo admin)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<UsuarioResponse>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @Operation(summary = "Cambiar estado de usuario", description = "Cambia el estado de un usuario (solo admin)")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") String estado) {
        usuarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }
}
