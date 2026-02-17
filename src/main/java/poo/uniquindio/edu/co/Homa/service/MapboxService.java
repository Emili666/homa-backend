package poo.uniquindio.edu.co.Homa.service;

import poo.uniquindio.edu.co.Homa.dto.response.map.GeoJsonFeatureCollection;

public interface MapboxService {

    GeoJsonFeatureCollection obtenerAlojamientosGeoJson();

    String obtenerAccessToken();
}
