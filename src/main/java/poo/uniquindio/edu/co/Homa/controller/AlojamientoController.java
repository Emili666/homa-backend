package co.edu.uniquindio.homa.controller;

import co.edu.uniquindio.homa.dto.request.AlojamientoRequest;
import co.edu.uniquindio.homa.dto.response.AlojamientoResponse;
import co.edu.uniquindio.homa.model.enums.EstadoAlojamiento;
import co.edu.uniquindio.homa.service.AlojamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Alojamientos", description = "Endpoints para gestión de alojamientos")
@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
public class AlojamientoController {

    private final AlojamientoService alojamientoService;

    @Operation(summary = "Crear alojamiento", description = "Crea un nuevo alojamiento (solo anfitriones)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<AlojamientoResponse> crear(
            @Valid @RequestBody AlojamientoRequest request,
            Authentication authentication) {
        // En una implementación real, obtendríamos el ID del usuario autenticado
        Long anfitrionId = 1L; // Placeholder
        return ResponseEntity.status(HttpStatus.CREATED).body(alojamientoService.crear(request, anfitrionId));
    }

    @Operation(summary = "Obtener alojamiento por ID", description = "Obtiene los detalles de un alojamiento")
    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alojamientoService.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar alojamiento", description = "Actualiza un alojamiento existente")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<AlojamientoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlojamientoRequest request,
            Authentication authentication) {
        Long anfitrionId = 1L; // Placeholder
        return ResponseEntity.ok(alojamientoService.actualizar(id, request, anfitrionId));
    }

    @Operation(summary = "Eliminar alojamiento", description = "Elimina un alojamiento")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        Long anfitrionId = 1L; // Placeholder
        alojamientoService.eliminar(id, anfitrionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los alojamientos", description = "Lista todos los alojamientos activos")
    @GetMapping
    public ResponseEntity<Page<AlojamientoResponse>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(alojamientoService.listarTodos(pageable));
    }

    @Operation(summary = "Listar alojamientos por anfitrión", description = "Lista los alojamientos de un anfitrión")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/anfitrion/{anfitrionId}")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<Page<AlojamientoResponse>> listarPorAnfitrion(
            @PathVariable Long anfitrionId,
            Pageable pageable) {
        return ResponseEntity.ok(alojamientoService.listarPorAnfitrion(anfitrionId, pageable));
    }

    @Operation(summary = "Buscar alojamientos", description = "Busca alojamientos con filtros")
    @GetMapping("/buscar")
    public ResponseEntity<Page<AlojamientoResponse>> buscar(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer capacidad,
            Pageable pageable) {
        return ResponseEntity.ok(alojamientoService.buscar(ciudad, precioMin, precioMax, capacidad, pageable));
    }

    @Operation(summary = "Cambiar estado de alojamiento", description = "Cambia el estado de un alojamiento (solo admin)")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoAlojamiento estado) {
        alojamientoService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Agregar imágenes", description = "Agrega imágenes a un alojamiento")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/imagenes")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> agregarImagenes(
            @PathVariable Long id,
            @RequestParam("imagenes") List<MultipartFile> imagenes) {
        alojamientoService.agregarImagenes(id, imagenes);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen de un alojamiento")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{alojamientoId}/imagenes/{imagenId}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long alojamientoId,
            @PathVariable Long imagenId) {
        alojamientoService.eliminarImagen(alojamientoId, imagenId);
        return ResponseEntity.noContent().build();
    }
}
