package poo.uniquindio.edu.co.Homa.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.Homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.Homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.Homa.service.FavoritoService;
import poo.uniquindio.edu.co.Homa.service.UsuarioService;

@Tag(name = "Favoritos", description = "Endpoints para la gestión de alojamientos favoritos")
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Marcar un alojamiento como favorito")
    @PostMapping("/{alojamientoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> marcarFavorito(
            @PathVariable Long alojamientoId,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        favoritoService.marcarFavorito(usuarioId, alojamientoId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Eliminar un alojamiento de favoritos")
    @DeleteMapping("/{alojamientoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminarFavorito(
            @PathVariable Long alojamientoId,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        favoritoService.quitarFavorito(usuarioId, alojamientoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar los alojamientos favoritos del usuario autenticado")
    @GetMapping("/mios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AlojamientoResponse>> listarMisFavoritos(
            Pageable pageable,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(favoritoService.listarFavoritos(usuarioId, pageable));
    }

    @Operation(summary = "Obtener el número de usuarios que han marcado como favorito un alojamiento")
    @GetMapping("/alojamiento/{alojamientoId}/conteo")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Long> contarFavoritosAlojamiento(
            @PathVariable Long alojamientoId,
            Authentication authentication) {
        Long anfitrionId = obtenerUsuarioId(authentication);
        long conteo = favoritoService.contarFavoritosParaAnfitrion(alojamientoId, anfitrionId);
        return ResponseEntity.ok(conteo);
    }

    @Operation(summary = "Verificar si un alojamiento está en favoritos del usuario autenticado")
    @GetMapping("/alojamiento/{alojamientoId}/estado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> esFavorito(
            @PathVariable Long alojamientoId,
            Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(favoritoService.esFavorito(usuarioId, alojamientoId));
    }

    private Long obtenerUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        UsuarioResponse usuario = usuarioService.obtenerPorEmail(email);
        return usuario.getId();
    }
}
