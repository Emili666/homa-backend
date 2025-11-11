package poo.uniquindio.edu.co.homa.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;
import poo.uniquindio.edu.co.homa.dto.request.ResponderResenaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ResenaResponse;
import poo.uniquindio.edu.co.homa.service.ResenaService;
import poo.uniquindio.edu.co.homa.service.UsuarioService;

@Tag(name = "Reseñas", description = "Endpoints para gestión de reseñas y calificaciones")
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Crear reseña", description = "Crea una nueva reseña para un alojamiento")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('HUESPED')")
    public ResponseEntity<ResenaResponse> crear(
            @Valid @RequestBody ResenaRequest request,
            Authentication authentication) {
        Long clienteId = usuarioService.obtenerPorEmail(authentication.getName()).getId();
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
    @PreAuthorize("hasRole('HUESPED')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        Long clienteId = usuarioService.obtenerPorEmail(authentication.getName()).getId();
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
        Long anfitrionId = usuarioService.obtenerPorEmail(authentication.getName()).getId();
        resenaService.responder(id, request, anfitrionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Calcular promedio de calificación", description = "Calcula el promedio de calificaciones de un alojamiento")
    @GetMapping("/alojamiento/{alojamientoId}/promedio")
    public ResponseEntity<Double> calcularPromedio(@PathVariable Long alojamientoId) {
        return ResponseEntity.ok(resenaService.calcularPromedioCalificacion(alojamientoId));
    }

    @Operation(summary = "Listar reseñas destacadas", description = "Lista las reseñas destacadas para mostrar en el home")
    @GetMapping("/destacadas")
    public ResponseEntity<Page<ResenaResponse>> listarDestacadas(Pageable pageable) {
        return ResponseEntity.ok(resenaService.listarDestacadas(pageable));
    }
}
