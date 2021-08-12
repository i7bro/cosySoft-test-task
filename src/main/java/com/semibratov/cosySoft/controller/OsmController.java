package com.semibratov.cosySoft.controller;

import com.semibratov.cosySoft.dto.GeoObjectDto;
import com.semibratov.cosySoft.dto.GeoRequestDto;
import com.semibratov.cosySoft.service.OsmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OsmController {

    private final OsmService osmService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeoObjectDto> test(@RequestBody GeoRequestDto dto) {
        var centerDto = osmService.processQuery(dto.type(), dto.name());

        return new ResponseEntity<>(centerDto, HttpStatus.OK);
    }
}
