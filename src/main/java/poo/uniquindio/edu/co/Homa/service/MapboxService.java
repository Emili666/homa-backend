package poo.uniquindio.edu.co.homa.service;

import poo.uniquindio.edu.co.homa.dto.response.map.GeoJsonFeatureCollection;

public interface MapboxService {

    GeoJsonFeatureCollection obtenerAlojamientosGeoJson();

    String obtenerAccessToken();
}
