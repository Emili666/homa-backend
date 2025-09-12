package poo.uniquindio.edu.co.Homa.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;

@RestController
@RequestMapping("/api/alojamientos")
@Tag(name = "Alojamientos", description = "Gestión de alojamientos en la plataforma Homa")
public class AlojamientoController {
    @Operation(summary = "Listar alojamientos", description = "Obtiene todos los alojamientos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de alojamientos obtenida")
    @GetMapping
    public List<AlojamientoDto> listarAlojamientos() {
        return List.of(); // Implementación real conectará con el servicio
    }

    @Operation(summary = "Obtener alojamiento por ID")
    @ApiResponse(responseCode = "200", description = "Alojamiento encontrado")
    @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado")
    @GetMapping("/{id}")
    public Object AlojamientoDtoobtenerAlojamiento(@PathVariable Long id) {
        return null; // Implementar lógica
    }

    @Operation(summary = "Crear alojamiento")
    @ApiResponse(responseCode = "201", description = "Alojamiento creado")
    @PostMapping
    public AlojamientoDto crearAlojamiento(@RequestBody AlojamientoDto dto) {
        return dto;
    }

    @Operation(summary = "Actualizar alojamiento")
    @PutMapping("/{id}")
    public AlojamientoDto actualizarAlojamiento(@PathVariable Long id, @RequestBody AlojamientoDto dto) {
        return dto;
    }

    @Operation(summary = "Eliminar alojamiento")
    @DeleteMapping("/{id}")
    public void eliminarAlojamiento(@PathVariable Long id) {
        // lógica delete
    }
}


