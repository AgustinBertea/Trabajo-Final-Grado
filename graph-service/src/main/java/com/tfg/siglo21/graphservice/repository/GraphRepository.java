package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface GraphRepository extends Neo4jRepository <LocationEntity, Long> {

    // GET ALL LOCATIONS AND ROADS FROM THE GRAPH
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    List<LocationEntity> getAllLocationsAndRoads();

    // GET ALL LOCATIONS
    @Query("MATCH (locationEntity:UBICACION) RETURN locationEntity")
    List<LocationEntity>  getAllLocations();

    // GET A SPECIFIC ROAD BY START NODE ID AND TARGET NODE ID
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(locationEntity)= $startId AND ID(locationTargetEntity)= $targetId " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity) " +
            "UNION MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(locationEntity)= $targetId AND ID(locationTargetEntity)= $startId " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> getSpecificRoad(Long startId, Long targetId);

    // GET LIST OF EXISTING GRAPHS
    @Query("CALL gds.graph.list() YIELD graphName;")
    List<String> getGraphList();

    // CREATE A GRAPH
    @Query("CALL gds.graph.project('grafo','UBICACION',{CAMINO:{orientation: 'UNDIRECTED'}}," +
            "{relationshipProperties: 'peso'})")
    void createGraph();

    // DROP THE GRAPH
    @Query("CALL gds.graph.drop('grafo')")
    void dropGraph();

    // CREATE A MOTOR GRAPH
    @Query("CALL gds.graph.project('grafo-motriz','UBICACION',{CAMINO:{orientation: 'UNDIRECTED'}}," +
            "{relationshipProperties: 'peso_motriz'})")
    void createMotorGraph();

    // DROP THE MOTOR GRAPH
    @Query("CALL gds.graph.drop('grafo-motriz')")
    void dropMotorGraph();

    // CREATE A VISION GRAPH
    @Query("CALL gds.graph.project('grafo-vision','UBICACION',{CAMINO:{orientation: 'UNDIRECTED'}}," +
            "{relationshipProperties: 'peso_vision'})")
    void createVisionGraph();

    // DROP THE VISION GRAPH
    @Query("CALL gds.graph.drop('grafo-vision')")
    void dropVisionGraph();

    // GET THE SHORTEST PATH IN GRAPH
    @Query("MATCH (source:UBICACION {calle: $startStreet, numero: $startNumber}), " +
            "(target:UBICACION {calle: $targetStreet, numero: $targetNumber}) CALL gds.shortestPath.dijkstra.stream" +
            "('grafo', {sourceNode: source, targetNode: target, relationshipWeightProperty: 'peso'}) " +
            "YIELD path RETURN nodes(path) as path")
    List<LocationEntity> getShortestPathInGraph(String startStreet, Integer startNumber,
                                                     String targetStreet, Integer targetNumber);

    // GET THE SHORTEST PATH IN A MOTOR GRAPH
    @Query("MATCH (source:UBICACION {calle: $startStreet, numero: $startNumber}), " +
            "(target:UBICACION {calle: $targetStreet, numero: $targetNumber}) CALL gds.shortestPath.dijkstra.stream" +
            "('grafo-motriz', {sourceNode: source, targetNode: target, relationshipWeightProperty: 'peso_motriz'}) " +
            "YIELD path RETURN nodes(path) as path")
    List<LocationEntity> getShortestPathInMotorGraph(String startStreet, Integer startNumber,
                                                     String targetStreet, Integer targetNumber);

    // GET THE SHORTEST PATH IN A VISION GRAPH
    @Query("MATCH (source:UBICACION {calle: $startStreet, numero: $startNumber}), " +
            "(target:UBICACION {calle: $targetStreet, numero: $targetNumber}) CALL gds.shortestPath.dijkstra.stream" +
            "('grafo-vision', {sourceNode: source, targetNode: target, relationshipWeightProperty: 'peso_vision'}) " +
            "YIELD path RETURN nodes(path) as path")
    List<LocationEntity> getShortestPathInVisionGraph(String startStreet, Integer startNumber,
                                                      String targetStreet, Integer targetNumber);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad RETURN COUNT(road)")
    Integer getTotalOneTypeOfRoad(String typeOfRoad);


    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.bloqueo_existe=true RETURN COUNT(road)")
    Integer getTotalOfBlockingsByType(String typeOfRoad);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.faltaRampa_existe=true RETURN COUNT(road)")
    Integer getTotalOfRampMissingByType(String typeOfRoad);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.malEstado_existe=true RETURN COUNT(road)")
    Integer getTotalOfBadConditionsByType(String typeOfRoad);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.podotactil_existe=true RETURN COUNT(road)")
    Integer getTotalOfPodotactileByType(String typeOfRoad);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.faltaSenda_existe=true RETURN COUNT(road)")
    Integer getTotalOfCrosswalkMissingByType(String typeOfRoad);

    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE road.tipo=$typeOfRoad AND road.alertaAuditiva_existe=true RETURN COUNT(road)")
    Integer getTotalOfAuditiveAlertByType(String typeOfRoad);

    @Query("MATCH (locationEntity)-[]-() WHERE locationEntity.calle=$street RETURN DISTINCT locationEntity")
    List<LocationEntity> getAllNodesOfAStreet(String street);
}