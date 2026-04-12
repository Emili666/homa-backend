package poo.uniquindio.edu.co.Homa.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.dto.response.map.GeoJsonFeature;
import poo.uniquindio.edu.co.Homa.dto.response.map.GeoJsonFeatureCollection;
import poo.uniquindio.edu.co.Homa.dto.response.map.GeoJsonGeometry;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.service.MapboxService;

/**
 * Genera GeoJSON estándar con los alojamientos activos.
 * El frontend usa Leaflet + OpenStreetMap (gratuito, sin token).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapboxServiceImpl implements MapboxService {

    private final AlojamientoRepository alojamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public GeoJsonFeatureCollection obtenerAlojamientosGeoJson() {
        log.info("Generando GeoJSON de alojamientos activos");

        List<Alojamiento> alojamientos = alojamientoRepository.findByEstado(EstadoAlojamiento.ACTIVO);

        List<GeoJsonFeature> features = alojamientos.stream()
                .filter(a -> a.getLatitud() != null && a.getLongitud() != null)
                .map(this::toFeature)
                .collect(Collectors.toList());

        log.info("GeoJSON generado con {} alojamientos", features.size());
        return GeoJsonFeatureCollection.builder()
                .features(features)
                .build();
    }

    private GeoJsonFeature toFeature(Alojamiento alojamiento) {
        GeoJsonGeometry geometry = GeoJsonGeometry.builder()
                .type("Point")
                .coordinates(List.of(alojamiento.getLongitud(), alojamiento.getLatitud()))
                .build();

        Map<String, Object> properties = Map.of(
                "id", alojamiento.getId(),
                "titulo", alojamiento.getTitulo(),
                "descripcion", alojamiento.getDescripcion() != null ? alojamiento.getDescripcion() : "",
                "precioPorNoche", alojamiento.getPrecioPorNoche(),
                "ciudad", alojamiento.getCiudad(),
                "direccion", alojamiento.getDireccion(),
                "anfitrionId", alojamiento.getAnfitrion().getId(),
                "anfitrionNombre", alojamiento.getAnfitrion().getNombre()
        );

        return GeoJsonFeature.builder()
                .geometry(geometry)
                .properties(properties)
                .build();
    }
}
