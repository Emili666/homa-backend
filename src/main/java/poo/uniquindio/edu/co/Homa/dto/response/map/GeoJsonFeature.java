package poo.uniquindio.edu.co.Homa.dto.response.map;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoJsonFeature {
    @Builder.Default
    String type = "Feature";
    GeoJsonGeometry geometry;
    Map<String, Object> properties;
}
