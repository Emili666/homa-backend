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
import io.swagger.v3.oas.annotations.tags.Tag;
import poo.uniquindio.edu.co.Homa.dto.UsuarioDto;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios en el sistema")
public class UsuarioController {
    @Operation(summary = "Listar usuarios")
    @GetMapping
    public List<UsuarioDto> listarUsuarios() {
        return List.of();
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public UsuarioDto obtenerUsuario(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "Crear usuario")
    @PostMapping
    public UsuarioDto crearUsuario(@RequestBody UsuarioDto dto) {
        return dto;
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public UsuarioDto actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        return dto;
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        // lógica delete
    }
}

