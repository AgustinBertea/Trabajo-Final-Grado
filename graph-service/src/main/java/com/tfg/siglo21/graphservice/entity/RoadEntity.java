package com.tfg.siglo21.graphservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@RelationshipProperties
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoadEntity {

    @TargetNode
    private LocationEntity locationTargetEntity;

    @Id @GeneratedValue
    private long id;
    @Property("distancia")
    private double distance;
    @Property("peso")
    private double weight;
    @Property("peso_motriz")
    private double motorWeight;
    @Property("peso_vision")
    private double visionWeight;
    @Property("tipo")
    private String type;
    @Property("tipo_coeficiente")
    private double typeCoefficient;
    @Property("centroCamino_latitud")
    private double roadCenterLatitude;
    @Property("centroCamino_longitud")
    private double roadCenterLongitude;

    @Property("alertaAuditiva_coeficiente")
    private double auditiveAlertCoefficient;
    @Property("alertaAuditiva_existe")
    private boolean auditiveAlertExists;
    @Property("alertaAuditiva_idUsuarioReporte")
    private String auditiveAlertIdUserReport;
    @Property("alertaAuditiva_idUsuariosVotosNegativos")
    private List<String> auditiveAlertIdsUserNegativeVotes;
    @Property("alertaAuditiva_idUsuariosVotosPositivos")
    private List<String> auditiveAlertIdsUserPositiveVotes;
    @Property("alertaAuditiva_recuentoVotos")
    private Integer auditiveAlertVoteRecount;

    @Property("bloqueo_coeficiente")
    private double blockingCoefficient;
    @Property("bloqueo_existe")
    private boolean blockingExists;
    @Property("bloqueo_idUsuarioReporte")
    private String blockingIdUserReport;
    @Property("bloqueo_idUsuariosVotosNegativos")
    private List<String> blockingIdsUserNegativeVotes;
    @Property("bloqueo_idUsuariosVotosPositivos")
    private List<String> blockingIdsUserPositiveVotes;
    @Property("bloqueo_recuentoVotos")
    private Integer blockingVoteRecount;

    @Property("faltaRampa_coeficiente")
    private double rampMissingCoefficient;
    @Property("faltaRampa_existe")
    private boolean rampMissingExists;
    @Property("faltaRampa_idUsuarioReporte")
    private String rampMissingIdUserReport;
    @Property("faltaRampa_idUsuariosVotosNegativos")
    private List<String> rampMissingIdsUserNegativeVotes;
    @Property("faltaRampa_idUsuariosVotosPositivos")
    private List<String> rampMissingIdsUserPositiveVotes;
    @Property("faltaRampa_recuentoVotos")
    private Integer rampMissingVoteRecount;

    @Property("faltaSenda_coeficiente")
    private double crosswalkMissingCoefficient;
    @Property("faltaSenda_existe")
    private boolean crosswalkMissingExists;
    @Property("faltaSenda_idUsuarioReporte")
    private String crosswalkMissingIdUserReport;
    @Property("faltaSenda_idUsuariosVotosNegativos")
    private List<String> crosswalkMissingIdsUserNegativeVotes;
    @Property("faltaSenda_idUsuariosVotosPositivos")
    private List<String> crosswalkMissingIdsUserPositiveVotes;
    @Property("faltaSenda_recuentoVotos")
    private Integer crosswalkMissingVoteRecount;

    @Property("malEstado_coeficiente")
    private double badConditionCoefficient;
    @Property("malEstado_existe")
    private boolean badConditionExists;
    @Property("malEstado_idUsuarioReporte")
    private String badConditionIdUserReport;
    @Property("malEstado_idUsuariosVotosNegativos")
    private List<String> badConditionIdsUserNegativeVotes;
    @Property("malEstado_idUsuariosVotosPositivos")
    private List<String> badConditionIdsUserPositiveVotes;
    @Property("malEstado_recuentoVotos")
    private Integer badConditionVoteRecount;

    @Property("podotactil_coeficiente")
    private double podotactileCoefficient;
    @Property("podotactil_existe")
    private boolean podotactileExists;
    @Property("podotactil_idUsuarioReporte")
    private String podotactileIdUserReport;
    @Property("podotactil_idUsuariosVotosNegativos")
    private List<String> podotactileIdsUserNegativeVotes;
    @Property("podotactil_idUsuariosVotosPositivos")
    private List<String> podotactileIdsUserPositiveVotes;
    @Property("podotactil_recuentoVotos")
    private Integer podotactileVoteRecount;
}