package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface AuditiveAlertMilestoneRepository extends Neo4jRepository<LocationEntity, Long> {

    double AUDITIVE_ALERT_COEFFICIENT = 0.3;
    int REGULAR_COEFFICIENT = 1;

    // ADD AUDITIVE ALERT MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.alertaAuditiva_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.alertaAuditiva_existe=true, " +
            "road.alertaAuditiva_coeficiente=" + AUDITIVE_ALERT_COEFFICIENT + ", " +
            "road.alertaAuditiva_idUsuarioReporte= $userId, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * " + AUDITIVE_ALERT_COEFFICIENT + " " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE AUDITIVE ALERT MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.alertaAuditiva_existe=true " +
            "AND road.alertaAuditiva_idUsuarioReporte=$userId " +
            "SET road.alertaAuditiva_existe=false, " +
            "road.alertaAuditiva_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.alertaAuditiva_idUsuarioReporte='', " +
            "road.alertaAuditiva_idUsuariosVotosNegativos=[], " +
            "road.alertaAuditiva_idUsuariosVotosPositivos=[], " +
            "road.alertaAuditiva_recuentoVotos=0, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A AUDITIVE ALERT MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.alertaAuditiva_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.alertaAuditiva_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.alertaAuditiva_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.alertaAuditiva_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.alertaAuditiva_idUsuariosVotosPositivos=road.alertaAuditiva_idUsuariosVotosPositivos+$userId, " +
            "road.alertaAuditiva_idUsuariosVotosNegativos = " +
            "[x IN road.alertaAuditiva_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.alertaAuditiva_recuentoVotos=road.alertaAuditiva_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A AUDITIVE ALERT MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.alertaAuditiva_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.alertaAuditiva_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.alertaAuditiva_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.alertaAuditiva_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.alertaAuditiva_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarAlertaAuditiva " +
            "SET road.alertaAuditiva_idUsuariosVotosNegativos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN [] ELSE road.alertaAuditiva_idUsuariosVotosNegativos+$userId END, " +
            "road.alertaAuditiva_idUsuariosVotosPositivos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN [] ELSE [x IN road.alertaAuditiva_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.alertaAuditiva_recuentoVotos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN 0 ELSE road.alertaAuditiva_recuentoVotos+decrementoVoto END, " +
            "road.alertaAuditiva_existe = CASE WHEN eliminarAlertaAuditiva " +
            "THEN false ELSE road.alertaAuditiva_existe END, " +
            "road.alertaAuditiva_coeficiente = CASE WHEN eliminarAlertaAuditiva " +
            "THEN " + REGULAR_COEFFICIENT + " ELSE road.alertaAuditiva_coeficiente END, " +
            "road.alertaAuditiva_idUsuarioReporte = CASE WHEN eliminarAlertaAuditiva " +
            "THEN '' ELSE road.alertaAuditiva_idUsuarioReporte END, " +
            "road.peso_vision = CASE WHEN eliminarAlertaAuditiva THEN road.distancia * road.tipo_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.bloqueo_coeficiente * road.malEstado_coeficiente " +
            "* road.podotactil_coeficiente * road.faltaSenda_coeficiente ELSE road.peso_vision END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A AUDITIVE ALERT MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.alertaAuditiva_existe=true " +
            "AND $userId IN road.alertaAuditiva_idUsuariosVotosPositivos " +
            "OR $userId IN road.alertaAuditiva_idUsuariosVotosNegativos " +
            "SET road.alertaAuditiva_idUsuariosVotosPositivos = " +
            "[x IN road.alertaAuditiva_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.alertaAuditiva_idUsuariosVotosNegativos = " +
            "[x IN road.alertaAuditiva_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.alertaAuditiva_idUsuariosVotosPositivos)" +
            "-size(road.alertaAuditiva_idUsuariosVotosNegativos) <= -3 " +
            "THEN true ELSE false END AS eliminarAlertaAuditiva " +
            "SET road.alertaAuditiva_existe = CASE WHEN eliminarAlertaAuditiva " +
            "THEN false ELSE road.alertaAuditiva_existe END, " +
            "road.alertaAuditiva_coeficiente = CASE WHEN eliminarAlertaAuditiva " +
            "THEN " + REGULAR_COEFFICIENT + " ELSE road.alertaAuditiva_coeficiente END, " +
            "road.alertaAuditiva_idUsuarioReporte = CASE WHEN eliminarAlertaAuditiva " +
            "THEN '' ELSE road.alertaAuditiva_idUsuarioReporte END, " +
            "road.alertaAuditiva_idUsuariosVotosNegativos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN [] ELSE road.alertaAuditiva_idUsuariosVotosNegativos END, " +
            "road.alertaAuditiva_idUsuariosVotosPositivos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN [] ELSE road.alertaAuditiva_idUsuariosVotosPositivos END, " +
            "road.alertaAuditiva_recuentoVotos = CASE WHEN eliminarAlertaAuditiva " +
            "THEN 0 ELSE size(road.alertaAuditiva_idUsuariosVotosPositivos)" +
            "-size(road.alertaAuditiva_idUsuariosVotosNegativos) END, " +
            "road.peso_vision = CASE WHEN eliminarAlertaAuditiva THEN road.distancia * road.tipo_coeficiente * " +
            REGULAR_COEFFICIENT + " " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso_vision END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}