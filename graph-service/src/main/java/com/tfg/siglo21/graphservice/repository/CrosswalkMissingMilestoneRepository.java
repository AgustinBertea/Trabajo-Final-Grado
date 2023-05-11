package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface CrosswalkMissingMilestoneRepository extends Neo4jRepository<LocationEntity, Long> {

    double CROSSWALK_MISSING_COEFFICIENT = 2;
    int REGULAR_COEFFICIENT = 1;

    // ADD CROSSWALK MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaSenda_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.faltaSenda_existe=true, " +
            "road.faltaSenda_coeficiente=" + CROSSWALK_MISSING_COEFFICIENT + ", " +
            "road.faltaSenda_idUsuarioReporte= $userId, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.podotactil_coeficiente * "
            + CROSSWALK_MISSING_COEFFICIENT + ", " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * road.faltaRampa_coeficiente * " + CROSSWALK_MISSING_COEFFICIENT + ", " +
            "road.peso = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * " + CROSSWALK_MISSING_COEFFICIENT + " " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE CROSSWALK MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaSenda_existe=true " +
            "AND road.faltaSenda_idUsuarioReporte=$userId " +
            "SET road.faltaSenda_existe=false, " +
            "road.faltaSenda_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.faltaSenda_idUsuarioReporte='', " +
            "road.faltaSenda_idUsuariosVotosNegativos=[], " +
            "road.faltaSenda_idUsuariosVotosPositivos=[], " +
            "road.faltaSenda_recuentoVotos=0, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.podotactil_coeficiente * "
            + REGULAR_COEFFICIENT + ", " +
            "road.peso_motriz = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * road.faltaRampa_coeficiente * " + REGULAR_COEFFICIENT + ", " +
            "road.peso = road.distancia * road.tipo_coeficiente * road.bloqueo_coeficiente " +
            "* road.malEstado_coeficiente * " + REGULAR_COEFFICIENT + " " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A CROSSWALK MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaSenda_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.faltaSenda_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.faltaSenda_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.faltaSenda_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.faltaSenda_idUsuariosVotosPositivos=road.faltaSenda_idUsuariosVotosPositivos+$userId, " +
            "road.faltaSenda_idUsuariosVotosNegativos = " +
            "[x IN road.faltaSenda_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.faltaSenda_recuentoVotos=road.faltaSenda_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A CROSSWALK MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaSenda_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.faltaSenda_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.faltaSenda_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.faltaSenda_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.faltaSenda_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarFaltaSenda " +
            "SET road.faltaSenda_idUsuariosVotosNegativos = CASE WHEN eliminarFaltaSenda " +
            "THEN [] ELSE road.faltaSenda_idUsuariosVotosNegativos+$userId END, " +
            "road.faltaSenda_idUsuariosVotosPositivos = CASE WHEN eliminarFaltaSenda " +
            "THEN [] ELSE [x IN road.faltaSenda_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.faltaSenda_recuentoVotos = CASE WHEN eliminarFaltaSenda " +
            "THEN 0 ELSE road.faltaSenda_recuentoVotos+decrementoVoto END, " +
            "road.faltaSenda_existe = CASE WHEN eliminarFaltaSenda THEN false ELSE road.faltaSenda_existe END, " +
            "road.faltaSenda_coeficiente = CASE WHEN eliminarFaltaSenda THEN " + REGULAR_COEFFICIENT +
            " ELSE road.faltaSenda_coeficiente END, " +
            "road.faltaSenda_idUsuarioReporte = CASE WHEN eliminarFaltaSenda " +
            "THEN '' ELSE road.faltaSenda_idUsuarioReporte END, " +
            "road.peso_vision = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * road.malEstado_coeficiente " +
            "* road.podotactil_coeficiente * " + REGULAR_COEFFICIENT + " ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.faltaRampa_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A CROSSWALK MISSING MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.faltaSenda_existe=true " +
            "AND $userId IN road.faltaSenda_idUsuariosVotosPositivos " +
            "OR $userId IN road.faltaSenda_idUsuariosVotosNegativos " +
            "SET road.faltaSenda_idUsuariosVotosPositivos = " +
            "[x IN road.faltaSenda_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.faltaSenda_idUsuariosVotosNegativos = " +
            "[x IN road.faltaSenda_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.faltaSenda_idUsuariosVotosPositivos)" +
            "-size(road.faltaSenda_idUsuariosVotosNegativos) <= -3 THEN true ELSE false END AS eliminarFaltaSenda " +
            "SET road.faltaSenda_existe = CASE WHEN eliminarFaltaSenda THEN false ELSE road.faltaSenda_existe END, " +
            "road.faltaSenda_coeficiente = CASE WHEN eliminarFaltaSenda THEN " + REGULAR_COEFFICIENT +
            " ELSE road.faltaSenda_coeficiente END, " +
            "road.faltaSenda_idUsuarioReporte = CASE WHEN eliminarFaltaSenda " +
            "THEN '' ELSE road.faltaSenda_idUsuarioReporte END, " +
            "road.faltaSenda_idUsuariosVotosNegativos = CASE WHEN eliminarFaltaSenda " +
            "THEN [] ELSE road.faltaSenda_idUsuariosVotosNegativos END, " +
            "road.faltaSenda_idUsuariosVotosPositivos = CASE WHEN eliminarFaltaSenda " +
            "THEN [] ELSE road.faltaSenda_idUsuariosVotosPositivos END, " +
            "road.faltaSenda_recuentoVotos = CASE WHEN eliminarFaltaSenda " +
            "THEN 0 ELSE size(road.faltaSenda_idUsuariosVotosPositivos)" +
            "-size(road.faltaSenda_idUsuariosVotosNegativos) END, " +
            "road.peso_vision = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * road.malEstado_coeficiente " +
            "* road.podotactil_coeficiente * " + REGULAR_COEFFICIENT + " ELSE road.peso_vision END, " +
            "road.peso_motriz = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * road.faltaRampa_coeficiente * "
            + REGULAR_COEFFICIENT + " " +
            "ELSE road.peso_motriz END, " +
            "road.peso = CASE WHEN eliminarFaltaSenda THEN road.distancia * road.tipo_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * "
            + REGULAR_COEFFICIENT + " " +
            "ELSE road.peso END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}