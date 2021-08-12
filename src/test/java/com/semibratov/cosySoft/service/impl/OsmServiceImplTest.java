package com.semibratov.cosySoft.service.impl;

import com.semibratov.cosySoft.CosySoftApplicationTests;
import com.semibratov.cosySoft.dto.GeoObjectDto;
import com.semibratov.cosySoft.service.OsmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class OsmServiceImplTest extends CosySoftApplicationTests {

    @Autowired
    private OsmService osmService;

    public static final String REGION = "Удмуртская республика";
    public static final String NOT_VALID = "невалидный запрос";

    @Test
    @DisplayName("check not null")
    void processQuery() {
        GeoObjectDto geoObjectDto = osmService.processQuery("region", REGION);
        assertThat(geoObjectDto).isNotNull();
        assertThat(geoObjectDto.getCenter()).isNotNull();
        assertThat(geoObjectDto.getCoordinates()).isNotNull();
    }

    @Test
    @DisplayName("check not null if data is not found")
    void testProcessQuery() {
        GeoObjectDto geoObjectDto = osmService.processQuery("",NOT_VALID);
        assertThat(geoObjectDto).isNotNull();
        assertThat(geoObjectDto.getCoordinates()).isNull();
        assertThat(geoObjectDto.getCenter()).isNull();
    }
}
