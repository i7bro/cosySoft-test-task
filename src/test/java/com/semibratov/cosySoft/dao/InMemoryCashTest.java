package com.semibratov.cosySoft.dao;

import com.semibratov.cosySoft.CosySoftApplicationTests;
import com.semibratov.cosySoft.dto.GeoObjectDto;
import com.semibratov.cosySoft.service.OsmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class InMemoryCashTest extends CosySoftApplicationTests {

    @Autowired
    private InMemoryCash cash;

    @Autowired
    private OsmService osmService;

    private static final String REGION = "Удмуртия";

    @Test
    void save_get() {
        osmService.processQuery("", REGION);

        assertThat(cash.getJson(REGION.toLowerCase())).isNotNull();
    }
}
