package poo.uniquindio.edu.co.Homa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.Homa.dto.response.map.GeoJsonFeatureCollection;
import poo.uniquindio.edu.co.Homa.service.MapboxService;

@Tag(name = "Mapas", description = "Endpoints de mapas — usa OpenStreetMap + Leaflet (sin token requerido)")
@RestController
@RequestMapping("/api/mapas")
@RequiredArgsConstructor
public class MapboxController {

    private final MapboxService mapboxService;

    @Operation(summary = "Obtiene los alojamientos en formato GeoJSON para renderizar en el mapa")
    @GetMapping("/alojamientos")
    public ResponseEntity<GeoJsonFeatureCollection> obtenerAlojamientosGeoJson() {
        return ResponseEntity.ok(mapboxService.obtenerAlojamientosGeoJson());
    }
}
