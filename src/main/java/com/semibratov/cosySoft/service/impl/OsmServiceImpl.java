package com.semibratov.cosySoft.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semibratov.cosySoft.dao.InMemoryCash;
import com.semibratov.cosySoft.dto.GeoObjectDto;
import com.semibratov.cosySoft.service.OsmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OsmServiceImpl implements OsmService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final InMemoryCash cash;

    @Override
    public GeoObjectDto processQuery(String type, String name) {
        GeoJsonObject geoJsonObject;

        if ((geoJsonObject = cash.getJson(name.toLowerCase())) == null) {

            String paramType = type.equalsIgnoreCase("region") ? "state" : "q";

            String json = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam(paramType, name)
                            .queryParam("country", "russia")
                            .queryParam("format", "json")
                            .queryParam("polygon_geojson", "1")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            try {
                JsonNode jsonNode = objectMapper.readTree(json);
                if (jsonNode.get(0) == null) {
                    return new GeoObjectDto();
                }
                JsonNode geojson = jsonNode.get(0).get("geojson");

                geoJsonObject = objectMapper.readValue(objectMapper.writeValueAsString(geojson), GeoJsonObject.class);
                cash.save(name.toLowerCase(), geoJsonObject);
            } catch (JsonProcessingException e) {
                log.error("Json parsing error {}", json);
                e.printStackTrace();
            }
        }

        return calculateGeoCenter(geoJsonObject);
    }

    private GeoObjectDto calculateGeoCenter(GeoJsonObject jsonObject) {
        Map<Extremum, Double> extremes = new HashMap<>();
        GeoObjectDto geoObjectDto = new GeoObjectDto();

        if (jsonObject instanceof Polygon polygon) {
            List<List<LngLatAlt>> coordinates = polygon.getCoordinates();
            geoObjectDto.setCoordinates(coordinates);

            double startLongitude = coordinates.get(0).get(0).getLongitude();
            if (startLongitude < 0) {
                startLongitude = -startLongitude;
            }
            double startLatitude = coordinates.get(0).get(0).getLatitude();

            extremes.put(Extremum.MIN_LONGITUDE, startLongitude);
            extremes.put(Extremum.MAX_LONGITUDE, startLongitude);
            extremes.put(Extremum.MIN_LATITUDE, startLatitude);
            extremes.put(Extremum.MAX_LATITUDE, startLatitude);

            for (List<LngLatAlt> coordinateList : coordinates) {
                findExtremes(extremes, coordinateList);
            }
        } else if (jsonObject instanceof MultiPolygon multiPolygon) {
            List<List<List<LngLatAlt>>> coordinates = multiPolygon.getCoordinates();
            geoObjectDto.setCoordinates(coordinates);

            double startLatitude = coordinates.get(0).get(0).get(0).getLatitude();
            double startLongitude = coordinates.get(0).get(0).get(0).getLongitude();
            if (startLongitude < 0) {
                startLongitude = -startLongitude;
            }

            extremes.put(Extremum.MIN_LONGITUDE, startLongitude);
            extremes.put(Extremum.MAX_LONGITUDE, startLongitude);
            extremes.put(Extremum.MIN_LATITUDE, startLatitude);
            extremes.put(Extremum.MAX_LATITUDE, startLatitude);

            for (List<List<LngLatAlt>> coordinate : coordinates) {
                for (List<LngLatAlt> coordinateList : coordinate) {
                    findExtremes(extremes, coordinateList);
                }
            }
        }

        double logCenter = extremes.get(Extremum.MIN_LONGITUDE) +
                (extremes.get(Extremum.MAX_LONGITUDE) - extremes.get(Extremum.MIN_LONGITUDE)) / 2;
        double latCenter = extremes.get(Extremum.MIN_LATITUDE) +
                (extremes.get(Extremum.MAX_LATITUDE) - extremes.get(Extremum.MIN_LATITUDE)) / 2;

        geoObjectDto.setCenter(new double[] {logCenter, latCenter});

        return geoObjectDto;
    }

    private void findExtremes(Map<Extremum, Double> extremes, List<LngLatAlt> coordinateList) {
        for (int i = 1; i < coordinateList.size(); i++) {
            double currentLongitude = coordinateList.get(i).getLongitude();
            double currentLatitude = coordinateList.get(i).getLatitude();

            if (currentLongitude < 0) {
                currentLongitude = -currentLatitude;
            }

            if (currentLongitude > extremes.get(Extremum.MAX_LONGITUDE)) {
                extremes.put(Extremum.MAX_LONGITUDE, currentLongitude);
            }
            if (currentLongitude < extremes.get(Extremum.MIN_LONGITUDE)) {
                extremes.put(Extremum.MIN_LONGITUDE, currentLongitude);
            }
            if (currentLatitude > extremes.get(Extremum.MAX_LATITUDE)) {
                extremes.put(Extremum.MAX_LATITUDE, currentLatitude);
            }
            if (currentLatitude < extremes.get(Extremum.MIN_LATITUDE)) {
                extremes.put(Extremum.MIN_LATITUDE, currentLatitude);
            }
        }
    }

    private enum Extremum {
        MIN_LONGITUDE, MAX_LONGITUDE, MIN_LATITUDE, MAX_LATITUDE
    }
}
