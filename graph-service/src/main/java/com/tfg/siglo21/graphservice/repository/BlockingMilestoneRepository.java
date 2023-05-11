package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface BlockingMilestoneRepository extends Neo4jRepository<LocationEntity, Long> {

    int BLOCKING_COEFFICIENT = 3;
    int REGULAR_COEFFICIENT = 1;

    // ADD BLOCKING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.bloqueo_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.bloqueo_existe=true, " +
            "road.bloqueo_coeficiente=" + BLOCKING_COEFFICIENT + ", " +
            "road.bloqueo_idUsuarioReporte= $userId, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* " + BLOCKING_COEFFICIENT + " * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * " + BLOCKING_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente, " +
            "road.peso = road.distancia * road.tipo_coeficiente * " + BLOCKING_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE BLOCKING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.bloqueo_existe=true " +
            "AND road.bloqueo_idUsuarioReporte=$userId " +
            "SET road.bloqueo_existe=false, " +
            "road.bloqueo_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.bloqueo_idUsuarioReporte='', " +
            "road.bloqueo_idUsuariosVotosNegativos=[], " +
            "road.bloqueo_idUsuariosVotosPositivos=[], " +
            "road.bloqueo_recuentoVotos=0, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente, " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente, " +
            "road.peso = road.distancia * road.tipo_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A BLOCKING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.bloqueo_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.bloqueo_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.bloqueo_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.bloqueo_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.bloqueo_idUsuariosVotosPositivos=road.bloqueo_idUsuariosVotosPositivos+$userId, " +
            "road.bloqueo_idUsuariosVotosNegativos = " +
            "[x IN road.bloqueo_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.bloqueo_recuentoVotos=road.bloqueo_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A BLOCKING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.bloqueo_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.bloqueo_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.bloqueo_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.bloqueo_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.bloqueo_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarBloqueo " +
            "SET road.bloqueo_idUsuariosVotosNegativos = CASE WHEN eliminarBloqueo " +
            "THEN [] ELSE road.bloqueo_idUsuariosVotosNegativos+$userId END, " +
            "road.bloqueo_idUsuariosVotosPositivos = CASE WHEN eliminarBloqueo " +
            "THEN [] ELSE [x IN road.bloqueo_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.bloqueo_recuentoVotos = CASE WHEN eliminarBloqueo " +
            "THEN 0 ELSE road.bloqueo_recuentoVotos+decrementoVoto END, " +
            "road.bloqueo_existe = CASE WHEN eliminarBloqueo THEN false ELSE road.bloqueo_existe END, " +
            "road.bloqueo_coeficiente = CASE WHEN eliminarBloqueo THEN " + REGULAR_COEFFICIENT +
            " ELSE road.bloqueo_coeficiente END, " +
            "road.bloqueo_idUsuarioReporte = CASE WHEN eliminarBloqueo " +
            "THEN '' ELSE road.bloqueo_idUsuarioReporte END, " +
            "road.peso_vision = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * " + REGULAR_COEFFICIENT + " * road.malEstado_coeficiente " +
            "* road.podotactil_coeficiente * road.faltaSenda_coeficiente ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.malEstado_coeficiente * road.faltaRampa_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.malEstado_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A BLOCKING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.bloqueo_existe=true " +
            "AND $userId IN road.bloqueo_idUsuariosVotosPositivos " +
            "OR $userId IN road.bloqueo_idUsuariosVotosNegativos " +
            "SET road.bloqueo_idUsuariosVotosPositivos = " +
            "[x IN road.bloqueo_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.bloqueo_idUsuariosVotosNegativos = " +
            "[x IN road.bloqueo_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.bloqueo_idUsuariosVotosPositivos)" +
            "-size(road.bloqueo_idUsuariosVotosNegativos) <= -3 THEN true ELSE false END AS eliminarBloqueo " +
            "SET road.bloqueo_existe = CASE WHEN eliminarBloqueo THEN false ELSE road.bloqueo_existe END, " +
            "road.bloqueo_coeficiente = CASE WHEN eliminarBloqueo THEN " + REGULAR_COEFFICIENT +
            " ELSE road.bloqueo_coeficiente END, " +
            "road.bloqueo_idUsuarioReporte = CASE WHEN eliminarBloqueo " +
            "THEN '' ELSE road.bloqueo_idUsuarioReporte END, " +
            "road.bloqueo_idUsuariosVotosNegativos = CASE WHEN eliminarBloqueo " +
            "THEN [] ELSE road.bloqueo_idUsuariosVotosNegativos END, " +
            "road.bloqueo_idUsuariosVotosPositivos = CASE WHEN eliminarBloqueo " +
            "THEN [] ELSE road.bloqueo_idUsuariosVotosPositivos END, " +
            "road.bloqueo_recuentoVotos = CASE WHEN eliminarBloqueo " +
            "THEN 0 ELSE size(road.bloqueo_idUsuariosVotosPositivos)" +
            "-size(road.bloqueo_idUsuariosVotosNegativos) END, " +
            "road.peso_vision = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.malEstado_coeficiente * road.podotactil_coeficiente " +
            "* road.faltaSenda_coeficiente ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente * " +
            REGULAR_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaRampa_coeficiente * road.faltaSenda_coeficiente " +
            "ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarBloqueo THEN road.distancia * road.tipo_coeficiente * " +
            REGULAR_COEFFICIENT + " " +
            "* road.malEstado_coeficiente * road.faltaSenda_coeficiente " +
            "ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}