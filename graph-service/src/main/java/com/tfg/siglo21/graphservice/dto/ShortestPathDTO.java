package com.tfg.siglo21.graphservice.dto;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.entity.RoadEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShortestPathDTO {

    @NotNull
    private List<LocationEntity> locations;
    @NotNull
    private List<RoadEntity> roads;
    @NotNull
    private List<String> instructions;
}