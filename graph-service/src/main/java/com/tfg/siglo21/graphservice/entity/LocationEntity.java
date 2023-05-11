package com.tfg.siglo21.graphservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("UBICACION")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LocationEntity {

    @Id @GeneratedValue()
    private Long id;
    @Property("calle")
    private String street;
    @Property("numero")
    private Integer number;
    @Property("latitud")
    private double latitude;
    @Property("longitud")
    private double longitude;

    @Relationship(type="CAMINO", direction = Relationship.Direction.OUTGOING)
    private List<RoadEntity> roads;
}