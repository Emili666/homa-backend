package poo.uniquindio.edu.co.homa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.homa.dto.response.map.GeoJsonFeatureCollection;
import poo.uniquindio.edu.co.homa.service.MapboxService;

@Tag(name = "Mapas", description = "Integración con Mapbox para visualizar alojamientos")
@RestController
@RequestMapping("/api/mapas")
@RequiredArgsConstructor
public class MapboxController {

    private final MapboxService mapboxService;

    @Operation(summary = "Obtiene los alojamientos en formato GeoJSON")
    @GetMapping("/alojamientos")
    public ResponseEntity<GeoJsonFeatureCollection> obtenerAlojamientosGeoJson() {
        return ResponseEntity.ok(mapboxService.obtenerAlojamientosGeoJson());
    }

    @Operation(summary = "Obtiene el token público de Mapbox configurado en el backend")
    @GetMapping("/token")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public ResponseEntity<String> obtenerToken() {
        return ResponseEntity.ok(mapboxService.obtenerAccessToken());
    }
}
