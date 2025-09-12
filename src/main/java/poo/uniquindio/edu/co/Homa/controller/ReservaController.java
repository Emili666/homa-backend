package poo.uniquindio.edu.co.Homa.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas en la plataforma Homa")
public class ReservaController {
     @Operation(summary = "Listar reservas")
    @GetMapping
    public List<ReservaDTO> listarReservas() {
        return List.of();
    }

    @Operation(summary = "Obtener reserva por ID")
    @GetMapping("/{id}")
    public ReservaDTO obtenerReserva(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "Crear reserva")
    @PostMapping
    public ReservaDTO crearReserva(@RequestBody ReservaDTO dto) {
        return dto;
    }

    @Operation(summary = "Actualizar reserva")
    @PutMapping("/{id}")
    public ReservaDTO actualizarReserva(@PathVariable Long id, @RequestBody ReservaDTO dto) {
        return dto;
    }

    @Operation(summary = "Eliminar reserva")
    @DeleteMapping("/{id}")
    public void eliminarReserva(@PathVariable Long id) {
        // lógica delete
    }
}

