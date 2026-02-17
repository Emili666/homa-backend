package poo.uniquindio.edu.co.Homa.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.Homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.Homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.Homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.Homa.service.ReservaService;
import poo.uniquindio.edu.co.Homa.service.UsuarioService;

@Tag(name = "Reservas", description = "Endpoints para gestión de reservas")
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva")
    @PostMapping
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody ReservaRequest request,
            Authentication authentication) {
        Long clienteId = obtenerIdCliente(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request, clienteId));
    }

    @Operation(summary = "Listar mis reservas", description = "Lista las reservas del usuario autenticado")
    @GetMapping("/mis-reservas")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    public ResponseEntity<Page<ReservaResponse>> listarMisReservas(
            Authentication authentication,
            Pageable pageable) {
        System.out.println("=== ENDPOINT /mis-reservas LLAMADO ===");
        Long clienteId = obtenerIdCliente(authentication);
        System.out.println("Cliente ID: " + clienteId);
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId, pageable));
    }

    @Operation(summary = "Obtener reserva por ID", description = "Obtiene los detalles de una reserva")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente")
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('HUESPED')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication authentication) {
        Long clienteId = obtenerIdCliente(authentication);
        reservaService.cancelar(id, clienteId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cambiar estado de reserva", description = "Cambia el estado de una reserva (solo admin)")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") EstadoReserva estado) {
        reservaService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar reservas por cliente", description = "Lista todas las reservas de un cliente")
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('HUESPED', 'ADMINISTRADOR')")
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

    @Operation(summary = "Listar reservas del anfitrión", description = "Lista todas las reservas de los alojamientos del anfitrión autenticado")
    @GetMapping("/anfitrion")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<Page<ReservaResponse>> listarPorAnfitrion(
            Authentication authentication,
            Pageable pageable) {
        Long anfitrionId = obtenerIdCliente(authentication);
        System.out.println("=== ENDPOINT /anfitrion LLAMADO ===");
        System.out.println("Email del usuario: " + authentication.getName());
        System.out.println("ID del anfitrion: " + anfitrionId);
        Page<ReservaResponse> result = reservaService.listarPorAnfitrion(anfitrionId, pageable);
        System.out.println("Total de reservas encontradas: " + result.getTotalElements());
        System.out.println("Contenido: " + result.getContent());
        System.out.println("========================");
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Confirmar reserva", description = "Confirma una reserva pendiente (solo anfitrión)")
    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<ReservaResponse> confirmarReserva(
            @PathVariable Long id,
            Authentication authentication) {
        Long anfitrionId = obtenerIdCliente(authentication);
        reservaService.confirmarReserva(id, anfitrionId);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Rechazar reserva", description = "Rechaza una reserva pendiente (solo anfitrión)")
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<ReservaResponse> rechazarReserva(
            @PathVariable Long id,
            Authentication authentication) {
        Long anfitrionId = obtenerIdCliente(authentication);
        reservaService.rechazarReserva(id, anfitrionId);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Completar reserva", description = "Marca una reserva como completada (solo anfitrión)")
    @PutMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<ReservaResponse> completarReserva(
            @PathVariable Long id,
            Authentication authentication) {
        Long anfitrionId = obtenerIdCliente(authentication);
        reservaService.completarReserva(id, anfitrionId);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar reservas completadas del usuario", description = "Lista las reservas completadas donde el usuario puede dejar reseña")
    @GetMapping("/completadas")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    public ResponseEntity<Page<ReservaResponse>> listarReservasCompletadas(
            Authentication authentication,
            Pageable pageable) {
        Long clienteId = obtenerIdCliente(authentication);
        return ResponseEntity.ok(reservaService.listarReservasCompletadas(clienteId, pageable));
    }

    @Operation(summary = "Verificar disponibilidad", description = "Verifica si un alojamiento está disponible en fechas específicas")
    @GetMapping("/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @RequestParam("alojamientoId") Long alojamientoId,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reservaService.verificarDisponibilidad(alojamientoId, fechaInicio, fechaFin));
    }

    private Long obtenerIdCliente(Authentication authentication) {
        String email = authentication.getName();
        UsuarioResponse usuario = usuarioService.obtenerPorEmail(email);
        return usuario.getId();
    }
}
