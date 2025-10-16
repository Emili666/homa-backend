package poo.uniquindio.edu.co.homa.dto.response.map;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoJsonFeatureCollection {
    @Builder.Default
    String type = "FeatureCollection";
    List<GeoJsonFeature> features;
}
