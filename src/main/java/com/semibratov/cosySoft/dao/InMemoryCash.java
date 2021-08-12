package com.semibratov.cosySoft.dao;

import org.geojson.GeoJsonObject;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class InMemoryCash {

    private final Map<String, GeoJsonObject> cash = new ConcurrentHashMap<>();

    private InMemoryCash() {}

    public void save(String name, GeoJsonObject json) {
        cash.put(name, json);
    }

    public GeoJsonObject getJson(String name) {
        return cash.get(name);
    }
}
