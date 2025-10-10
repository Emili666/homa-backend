package co.edu.uniquindio.homa.controller;

import co.edu.uniquindio.homa.dto.request.ReservaRequest;
import co.edu.uniquindio.homa.dto.response.ReservaResponse;
import co.edu.uniquindio.homa.model.enums.EstadoReserva;
import co.edu.uniquindio.homa.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Reservas", description = "Endpoints para gestión de reservas")
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody ReservaRequest request,
            Authentication authentication) {
        Long clienteId = 1L; // Placeholder
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request, clienteId));
    }

    @Operation(summary = "Obtener reserva por ID", description = "Obtiene los detalles de una reserva")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente")
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication authentication) {
        Long clienteId = 1L; // Placeholder
        reservaService.cancelar(id, clienteId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cambiar estado de reserva", description = "Cambia el estado de una reserva (solo admin)")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReserva estado) {
        reservaService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar reservas por cliente", description = "Lista todas las reservas de un cliente")
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Page<ReservaResponse>> listarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId, pageable));
    }

    @Operation(summary = "Listar reservas por alojamiento", description = "Lista todas las reservas de un alojamiento")
    @GetMapping("/alojamiento/{alojamientoId}")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<Page<ReservaResponse>> listarPorAlojamiento(
            @PathVariable Long alojamientoId,
            Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorAlojamiento(alojamientoId, pageable));
    }

    @Operation(summary = "Verificar disponibilidad", description = "Verifica si un alojamiento está disponible en fechas específicas")
    @GetMapping("/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @RequestParam Long alojamientoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reservaService.verificarDisponibilidad(alojamientoId, fechaInicio, fechaFin));
    }
}
