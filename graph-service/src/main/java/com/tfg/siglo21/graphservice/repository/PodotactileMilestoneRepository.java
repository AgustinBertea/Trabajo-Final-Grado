package com.tfg.siglo21.graphservice.repository;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface PodotactileMilestoneRepository extends Neo4jRepository<LocationEntity, Long> {

    double PODOTACTILE_COEFFICIENT = 0.6;
    int REGULAR_COEFFICIENT = 1;

    // ADD PODOTACTILE MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.podotactil_existe=false " +
            "AND NOT road.tipo='Nexo vereda' " +
            "SET road.podotactil_existe=true, " +
            "road.podotactil_coeficiente=" + PODOTACTILE_COEFFICIENT + ", " +
            "road.podotactil_idUsuarioReporte= $userId, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * " + PODOTACTILE_COEFFICIENT +
            " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addMilestone(Long roadId, String userId);

    // DELETE PODOTACTILE MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.podotactil_existe=true " +
            "AND road.podotactil_idUsuarioReporte=$userId " +
            "SET road.podotactil_existe=false, " +
            "road.podotactil_coeficiente=" + REGULAR_COEFFICIENT + ", " +
            "road.podotactil_idUsuarioReporte='', " +
            "road.podotactil_idUsuariosVotosNegativos=[], " +
            "road.podotactil_idUsuariosVotosPositivos=[], " +
            "road.podotactil_recuentoVotos=0, " +
            "road.peso_vision = road.distancia * road.tipo_coeficiente * road.alertaAuditiva_coeficiente " +
            "* road.bloqueo_coeficiente * road.malEstado_coeficiente * " + REGULAR_COEFFICIENT +
            " * road.faltaSenda_coeficiente " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteMilestone(Long roadId, String userId);

    // ADD POSITIVE VOTE TO A PODOTACTILE MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.podotactil_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.podotactil_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.podotactil_idUsuariosVotosPositivos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.podotactil_idUsuariosVotosNegativos THEN 2 ELSE 1 END AS incrementoVoto " +
            "SET road.podotactil_idUsuariosVotosPositivos=road.podotactil_idUsuariosVotosPositivos+$userId, " +
            "road.podotactil_idUsuariosVotosNegativos = " +
            "[x IN road.podotactil_idUsuariosVotosNegativos WHERE x <> $userId], " +
            "road.podotactil_recuentoVotos=road.podotactil_recuentoVotos + incrementoVoto " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addPositiveVote(Long roadId, String userId);

    // ADD NEGATIVE VOTE TO A PODOTACTILE MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.podotactil_existe=true " +
            "AND NOT road.tipo='Nexo vereda' " +
            "AND road.podotactil_idUsuarioReporte<>$userId " +
            "AND NOT $userId IN road.podotactil_idUsuariosVotosNegativos " +
            "WITH road, locationEntity, locationTargetEntity, " +
            "CASE WHEN $userId IN road.podotactil_idUsuariosVotosPositivos THEN -2 ELSE -1 END AS decrementoVoto " +
            "WITH road, locationEntity, locationTargetEntity, decrementoVoto, " +
            "CASE WHEN road.podotactil_recuentoVotos+decrementoVoto <= -3 THEN true ELSE false " +
            "END AS eliminarPodotactil " +
            "SET road.podotactil_idUsuariosVotosNegativos = CASE WHEN eliminarPodotactil " +
            "THEN [] ELSE road.podotactil_idUsuariosVotosNegativos+$userId END, " +
            "road.podotactil_idUsuariosVotosPositivos = CASE WHEN eliminarPodotactil " +
            "THEN [] ELSE [x IN road.podotactil_idUsuariosVotosPositivos WHERE x <> $userId] END, " +
            "road.podotactil_recuentoVotos = CASE WHEN eliminarPodotactil " +
            "THEN 0 ELSE road.podotactil_recuentoVotos+decrementoVoto END, " +
            "road.podotactil_existe = CASE WHEN eliminarPodotactil THEN false ELSE road.podotactil_existe END, " +
            "road.podotactil_coeficiente = CASE WHEN eliminarPodotactil THEN " + REGULAR_COEFFICIENT +
            " ELSE road.podotactil_coeficiente END, " +
            "road.podotactil_idUsuarioReporte = CASE WHEN eliminarPodotactil " +
            "THEN '' ELSE road.podotactil_idUsuarioReporte END, " +
            "road.peso_vision = CASE WHEN eliminarPodotactil THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * road.malEstado_coeficiente " +
            "* " + REGULAR_COEFFICIENT + " * road.faltaSenda_coeficiente ELSE road.peso_vision END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> addNegativeVote(Long roadId, String userId);

    // DELETE POSITIVE OR NEGATIVE VOTE FROM A PODOTACTILE MILESTONE
    @Query("MATCH (locationEntity:UBICACION)-[road:CAMINO]->(locationTargetEntity:UBICACION) " +
            "WHERE ID(road)=$roadId " +
            "AND road.podotactil_existe=true " +
            "AND $userId IN road.podotactil_idUsuariosVotosPositivos " +
            "OR $userId IN road.podotactil_idUsuariosVotosNegativos " +
            "SET road.podotactil_idUsuariosVotosPositivos = " +
            "[x IN road.podotactil_idUsuariosVotosPositivos WHERE x <> $userId] " +
            "SET road.podotactil_idUsuariosVotosNegativos = " +
            "[x IN road.podotactil_idUsuariosVotosNegativos WHERE x <> $userId] " +
            "WITH locationEntity, road, locationTargetEntity, " +
            "CASE WHEN size(road.podotactil_idUsuariosVotosPositivos)" +
            "-size(road.podotactil_idUsuariosVotosNegativos) <= -3 THEN true ELSE false END AS eliminarPodotactil " +
            "SET road.podotactil_existe = CASE WHEN eliminarPodotactil THEN false ELSE road.podotactil_existe END, " +
            "road.podotactil_coeficiente = CASE WHEN eliminarPodotactil THEN " + REGULAR_COEFFICIENT +
            " ELSE road.podotactil_coeficiente END, " +
            "road.podotactil_idUsuarioReporte = CASE WHEN eliminarPodotactil " +
            "THEN '' ELSE road.podotactil_idUsuarioReporte END, " +
            "road.podotactil_idUsuariosVotosNegativos = CASE WHEN eliminarPodotactil " +
            "THEN [] ELSE road.podotactil_idUsuariosVotosNegativos END, " +
            "road.podotactil_idUsuariosVotosPositivos = CASE WHEN eliminarPodotactil " +
            "THEN [] ELSE road.podotactil_idUsuariosVotosPositivos END, " +
            "road.podotactil_recuentoVotos = CASE WHEN eliminarPodotactil " +
            "THEN 0 ELSE size(road.podotactil_idUsuariosVotosPositivos)" +
            "-size(road.podotactil_idUsuariosVotosNegativos) END, " +
            "road.peso_vision = CASE WHEN eliminarPodotactil THEN road.distancia * road.tipo_coeficiente " +
            "* road.alertaAuditiva_coeficiente * road.bloqueo_coeficiente * road.malEstado_coeficiente * " +
            REGULAR_COEFFICIENT + " " +
            "* road.faltaSenda_coeficiente ELSE road.peso_vision END " +
            "RETURN locationEntity, COLLECT(road), COLLECT(locationTargetEntity)")
    Optional<LocationEntity> deleteVote(Long roadId, String userId);
}