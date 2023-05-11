package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface RampMissingMilestoneRepository extends Neo4jRepository<LocationEntity, Long> {

    double RAMP_MISSING_COEFFICIENT = 1.5;
    int REGULAR_COEFFICIENT = 1;

    // ADD RAMP MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaRampa_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.faltaRampa_existe=true, " +
            "road.faltaRampa_coeficiente=" + RAMP_MISSING_COEFFICIENT + ", " +
            "road.faltaRampa_idUsuarioReporte= $userId, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * " + RAMP_MISSING_COEFFICIENT + " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE RAMP MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaRampa_existe=true " +
            "AND road.faltaRampa_idUsuarioReporte=$userId " +
            "SET road.faltaRampa_existe=false, " +
            "road.faltaRampa_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.faltaRampa_idUsuarioReporte='', " +
            "road.faltaRampa_idUsuariosVotosNegativos=[], " +
            "road.faltaRampa_idUsuariosVotosPositivos=[], " +
            "road.faltaRampa_recuentoVotos=0, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * " + REGULAR_COEFFICIENT + " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A RAMP MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaRampa_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.faltaRampa_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.faltaRampa_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.faltaRampa_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.faltaRampa_idUsuariosVotosPositivos=road.faltaRampa_idUsuariosVotosPositivos+$userId, " +
            "road.faltaRampa_idUsuariosVotosNegativos = " +
            "[x IN road.faltaRampa_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.faltaRampa_recuentoVotos=road.faltaRampa_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A RAMP MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaRampa_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.faltaRampa_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.faltaRampa_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.faltaRampa_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.faltaRampa_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarFaltaRampa " +
            "SET road.faltaRampa_idUsuariosVotosNegativos = CASE WHEN eliminarFaltaRampa " +
            "THEN [] ELSE road.faltaRampa_idUsuariosVotosNegativos+$userId END, " +
            "road.faltaRampa_idUsuariosVotosPositivos = CASE WHEN eliminarFaltaRampa " +
            "THEN [] ELSE [x IN road.faltaRampa_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.faltaRampa_recuentoVotos = CASE WHEN eliminarFaltaRampa " +
            "THEN 0 ELSE road.faltaRampa_recuentoVotos+decrementoVoto END, " +
            "road.faltaRampa_existe = CASE WHEN eliminarFaltaRampa THEN false ELSE road.faltaRampa_existe END, " +
            "road.faltaRampa_coeficiente = CASE WHEN eliminarFaltaRampa THEN " + REGULAR_COEFFICIENT +
            " ELSE road.faltaRampa_coeficiente END, " +
            "road.faltaRampa_idUsuarioReporte = CASE WHEN eliminarFaltaRampa " +
            "THEN '' ELSE road.faltaRampa_idUsuarioReporte END, " +
            "road.peso_motriz = CASE WHEN eliminarFaltaRampa THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.faltaSenda_coeficiente ELSE road.peso_motriz END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A RAMP MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaRampa_existe=true " +
            "AND $userId IN road.faltaRampa_idUsuariosVotosPositivos " +
            "OR $userId IN road.faltaRampa_idUsuariosVotosNegativos " +
            "SET road.faltaRampa_idUsuariosVotosPositivos = " +
            "[x IN road.faltaRampa_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.faltaRampa_idUsuariosVotosNegativos = " +
            "[x IN road.faltaRampa_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.faltaRampa_idUsuariosVotosPositivos)" +
            "-size(road.faltaRampa_idUsuariosVotosNegativos) <= -3 THEN true ELSE false END AS eliminarFaltaRampa " +
            "SET road.faltaRampa_existe = CASE WHEN eliminarFaltaRampa THEN false ELSE road.faltaRampa_existe END, " +
            "road.faltaRampa_coeficiente = CASE WHEN eliminarFaltaRampa THEN " + REGULAR_COEFFICIENT +
            " ELSE road.faltaRampa_coeficiente END, " +
            "road.faltaRampa_idUsuarioReporte = CASE WHEN eliminarFaltaRampa " +
            "THEN '' ELSE road.faltaRampa_idUsuarioReporte END, " +
            "road.faltaRampa_idUsuariosVotosNegativos = CASE WHEN eliminarFaltaRampa " +
            "THEN [] ELSE road.faltaRampa_idUsuariosVotosNegativos END, " +
            "road.faltaRampa_idUsuariosVotosPositivos = CASE WHEN eliminarFaltaRampa " +
            "THEN [] ELSE road.faltaRampa_idUsuariosVotosPositivos END, " +
            "road.faltaRampa_recuentoVotos = CASE WHEN eliminarFaltaRampa " +
            "THEN 0 ELSE size(road.faltaRampa_idUsuariosVotosPositivos)" +
            "-size(road.faltaRampa_idUsuariosVotosNegativos) END, " +
            "road.peso_motriz = CASE WHEN eliminarFaltaRampa THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.faltaSenda_coeficiente " +
            "ELSE road.peso_motriz END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}