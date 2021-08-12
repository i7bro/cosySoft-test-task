package com.semibratov.cosySoft.service;

import com.semibratov.cosySoft.dto.GeoObjectDto;

public interface OsmService {

    GeoObjectDto processQuery(String type, String name);
}
