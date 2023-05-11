package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface BadConditionMilestoneRepository extends Neo4jRepository <LocationEntity, Long> {

    double BAD_CONDITION_COEFFICIENT = 2;
    int REGULAR_COEFFICIENT = 1;

    // ADD BAD CONDITION MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.malEstado_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.malEstado_existe=true, " +
            "road.malEstado_coeficiente=" + BAD_CONDITION_COEFFICIENT + ", " +
            "road.malEstado_idUsuarioReporte= $userId, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * " + BAD_CONDITION_COEFFICIENT + " * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* " + BAD_CONDITION_COEFFICIENT + " * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente, " +
            "road.peso = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* " + BAD_CONDITION_COEFFICIENT + " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE BAD CONDITION MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.malEstado_existe=true " +
            "AND road.malEstado_idUsuarioReporte=$userId " +
            "SET road.malEstado_existe=false, " +
            "road.malEstado_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.malEstado_idUsuarioReporte='', " +
            "road.malEstado_idUsuariosVotosNegativos=[], " +
            "road.malEstado_idUsuariosVotosPositivos=[], " +
            "road.malEstado_recuentoVotos=0, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT + " * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente, " +
            "road.peso = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A BAD CONDITION MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.malEstado_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.malEstado_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.malEstado_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.malEstado_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.malEstado_idUsuariosVotosPositivos=road.malEstado_idUsuariosVotosPositivos+$userId, " +
            "road.malEstado_idUsuariosVotosNegativos = " +
            "[x IN road.malEstado_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.malEstado_recuentoVotos=road.malEstado_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A BAD CONDITION MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.malEstado_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.malEstado_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.malEstado_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.malEstado_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.malEstado_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarMalEstado " +
            "SET road.malEstado_idUsuariosVotosNegativos = CASE WHEN eliminarMalEstado " +
            "THEN [] ELSE road.malEstado_idUsuariosVotosNegativos+$userId END, " +
            "road.malEstado_idUsuariosVotosPositivos = CASE WHEN eliminarMalEstado " +
            "THEN [] ELSE [x IN road.malEstado_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.malEstado_recuentoVotos = CASE WHEN eliminarMalEstado " +
            "THEN 0 ELSE road.malEstado_recuentoVotos+decrementoVoto END, " +
            "road.malEstado_existe = CASE WHEN eliminarMalEstado THEN false ELSE road.malEstado_existe END, " +
            "road.malEstado_coeficiente = CASE WHEN eliminarMalEstado THEN " + REGULAR_COEFFICIENT +
            " ELSE road.malEstado_coeficiente END, " +
            "road.malEstado_idUsuarioReporte = CASE WHEN eliminarMalEstado " +
            "THEN '' ELSE road.malEstado_idUsuarioReporte END, " +
            "road.peso_vision = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.podotactil_coeficiente * road.faltaSenda_coeficiente ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT + " * road.faltaRampa_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.faltaSenda_coeficiente ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A BAD CONDITION MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.malEstado_existe=true " +
            "AND $userId IN road.malEstado_idUsuariosVotosPositivos " +
            "OR $userId IN road.malEstado_idUsuariosVotosNegativos " +
            "SET road.malEstado_idUsuariosVotosPositivos = " +
            "[x IN road.malEstado_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.malEstado_idUsuariosVotosNegativos = " +
            "[x IN road.malEstado_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.malEstado_idUsuariosVotosPositivos)" +
            "-size(road.malEstado_idUsuariosVotosNegativos) <= -3 THEN true ELSE false END AS eliminarMalEstado " +
            "SET road.malEstado_existe = CASE WHEN eliminarMalEstado THEN false ELSE road.malEstado_existe END, " +
            "road.malEstado_coeficiente = CASE WHEN eliminarMalEstado THEN " + REGULAR_COEFFICIENT +
            " ELSE road.malEstado_coeficiente END, " +
            "road.malEstado_idUsuarioReporte = CASE WHEN eliminarMalEstado " +
            "THEN '' ELSE road.malEstado_idUsuarioReporte END, " +
            "road.malEstado_idUsuariosVotosNegativos = CASE WHEN eliminarMalEstado " +
            "THEN [] ELSE road.malEstado_idUsuariosVotosNegativos END, " +
            "road.malEstado_idUsuariosVotosPositivos = CASE WHEN eliminarMalEstado " +
            "THEN [] ELSE road.malEstado_idUsuariosVotosPositivos END, " +
            "road.malEstado_recuentoVotos = CASE WHEN eliminarMalEstado " +
            "THEN 0 ELSE size(road.malEstado_idUsuariosVotosPositivos)" +
            "-size(road.malEstado_idUsuariosVotosNegativos) END, " +
            "road.peso_vision = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente " +
            "ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarMalEstado THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.faltaSenda_coeficiente " +
            "ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}