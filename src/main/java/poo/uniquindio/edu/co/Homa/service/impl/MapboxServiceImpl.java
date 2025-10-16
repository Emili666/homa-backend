package poo.uniquindio.edu.co.homa.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.homa.dto.response.map.GeoJsonFeature;
import poo.uniquindio.edu.co.homa.dto.response.map.GeoJsonFeatureCollection;
import poo.uniquindio.edu.co.homa.dto.response.map.GeoJsonGeometry;
import poo.uniquindio.edu.co.homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.homa.service.MapboxService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapboxServiceImpl implements MapboxService {

    private final AlojamientoRepository alojamientoRepository;

    @Value("${mapbox.access-token:}")
    private String mapboxAccessToken;

    @Override
    @Transactional(readOnly = true)
    public GeoJsonFeatureCollection obtenerAlojamientosGeoJson() {
        log.info("Generando FeatureCollection para alojamientos activos");

        List<Alojamiento> alojamientos = alojamientoRepository.findByEstado(EstadoAlojamiento.ACTIVO);

        List<GeoJsonFeature> features = alojamientos.stream()
                .filter(alojamiento -> alojamiento.getLatitud() != null && alojamiento.getLongitud() != null)
                .map(this::alojamientoToFeature)
                .collect(Collectors.toList());

        return GeoJsonFeatureCollection.builder()
                .features(features)
                .build();
    }

    @Override
    public String obtenerAccessToken() {
        return mapboxAccessToken;
    }

    private GeoJsonFeature alojamientoToFeature(Alojamiento alojamiento) {
        GeoJsonGeometry geometry = GeoJsonGeometry.builder()
                .type("Point")
                .coordinates(List.of(
                        Double.valueOf(alojamiento.getLongitud()),
                        Double.valueOf(alojamiento.getLatitud())))
                .build();

        Map<String, Object> properties = Map.of(
                "id", alojamiento.getId(),
                "titulo", alojamiento.getTitulo(),
                "descripcion", alojamiento.getDescripcion(),
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
