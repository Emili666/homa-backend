package co.edu.uniquindio.homa.controller;

import co.edu.uniquindio.homa.dto.request.ResenaRequest;
import co.edu.uniquindio.homa.dto.request.ResponderResenaRequest;
import co.edu.uniquindio.homa.dto.response.ResenaResponse;
import co.edu.uniquindio.homa.service.ResenaService;
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

@Tag(name = "Reseñas", description = "Endpoints para gestión de reseñas y calificaciones")
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(summary = "Crear reseña", description = "Crea una nueva reseña para un alojamiento")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResenaResponse> crear(
            @Valid @RequestBody ResenaRequest request,
            Authentication authentication) {
        Long clienteId = 1L; // Placeholder
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request, clienteId));
    }

    @Operation(summary = "Obtener reseña por ID", description = "Obtiene los detalles de una reseña")
    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        Long clienteId = 1L; // Placeholder
        resenaService.eliminar(id, clienteId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar reseñas por alojamiento", description = "Lista todas las reseñas de un alojamiento")
    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<Page<ResenaResponse>> listarPorAlojamiento(
            @PathVariable Long alojamientoId,
            Pageable pageable) {
        return ResponseEntity.ok(resenaService.listarPorAlojamiento(alojamientoId, pageable));
    }

    @Operation(summary = "Responder reseña", description = "Permite al anfitrión responder una reseña")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/responder")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderResenaRequest request,
            Authentication authentication) {
        Long anfitrionId = 1L; // Placeholder
        resenaService.responder(id, request, anfitrionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Calcular promedio de calificación", description = "Calcula el promedio de calificaciones de un alojamiento")
    @GetMapping("/alojamiento/{alojamientoId}/promedio")
    public ResponseEntity<Double> calcularPromedio(@PathVariable Long alojamientoId) {
        return ResponseEntity.ok(resenaService.calcularPromedioCalificacion(alojamientoId));
    }
}
