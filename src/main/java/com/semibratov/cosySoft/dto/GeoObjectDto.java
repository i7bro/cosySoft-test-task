package com.semibratov.cosySoft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoObjectDto {

    private List<?> coordinates;
    private double[] center;
}
