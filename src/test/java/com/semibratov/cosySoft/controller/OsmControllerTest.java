package com.semibratov.cosySoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semibratov.cosySoft.CosySoftApplicationTests;
import com.semibratov.cosySoft.dto.GeoObjectDto;
import com.semibratov.cosySoft.dto.GeoRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OsmControllerTest extends CosySoftApplicationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    @SneakyThrows
    void test1() {
        GeoRequestDto geoRequestDto = new GeoRequestDto("Ижевск", "");
        String json = mapper.writeValueAsString(geoRequestDto);
        String contentAsString = mvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GeoObjectDto geoObjectDto = mapper.readValue(contentAsString, GeoObjectDto.class);
        assertThat(geoObjectDto).isNotNull();
    }
}
