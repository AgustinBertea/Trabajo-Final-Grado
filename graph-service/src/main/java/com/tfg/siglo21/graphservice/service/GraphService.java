package com.tfg.siglo21.graphservice.service;

import com.tfg.siglo21.graphservice.dto.ShortestPathDTO;
import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.entity.RoadEntity;
import com.tfg.siglo21.graphservice.exception.GraphException;
import com.tfg.siglo21.graphservice.exception.NotFoundException;
import com.tfg.siglo21.graphservice.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class GraphService {

    @Autowired
    GraphRepository graphRepository;

    @Transactional(readOnly = true)
    public List<RoadEntity> getAllRoads() {
        try {
            List<LocationEntity> locationsWithRoads = graphRepository.getAllLocationsAndRoads();
            locationsWithRoads
                    .forEach(location -> location.getRoads()
                    .forEach(road -> road.setLocationTargetEntity(null)));
            List<RoadEntity> roads = new ArrayList<>(locationsWithRoads.stream()
                    .flatMap(location -> location.getRoads().stream())
                    .toList());
            roads.removeIf(road -> road.getType().equals("Nexo vereda") || road.getType().equals("Nexo peatonal"));
            return roads;
        } catch (Exception ex) {
            throw new GraphException("Error when trying to get all locations and roads, reason: "+ ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<LocationEntity> getAllNodesOfAStreet(String street) {
        try {
            List<LocationEntity> allNodesOfAStreet = graphRepository.getAllNodesOfAStreet(street);
            return allNodesOfAStreet.stream()
                    .sorted(Comparator.comparing(LocationEntity::getNumber)).toList();
        } catch (Exception ex) {
            throw new GraphException("Error when trying to get all nodes of a street, reason: "+ ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<LocationEntity> getAllLocations() {
        try {
            return graphRepository.getAllLocations();
        } catch (Exception ex) {
            throw new GraphException("Error when trying to get all locations, reason: "+ ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RoadEntity getRoadBetweenLocations(LocationEntity startEntity, LocationEntity targetLocation) {
        try {
            LocationEntity location = graphRepository.getSpecificRoad(startEntity.getId(), targetLocation.getId())
                    .orElseThrow();
            return location.getRoads().get(0);
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Road between location "+ startEntity.getStreet() +" "+ startEntity.getNumber()
                    +" and "+ targetLocation.getStreet() +" "+ targetLocation.getNumber() +" not found");
        } catch (Exception ex) {
            throw new GraphException("Error when trying to get the road between location id: "+ startEntity.getId()
                    +" and location id: "+ targetLocation.getId() +", reason: "+ ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public HashMap<String, Integer> getGraphSummary() {
        try {
            HashMap<String, Integer> summary = new HashMap<>();
            // FOOTPATHS
            Integer totalFootpaths = graphRepository.getTotalOneTypeOfRoad("Senda peatonal");
            Integer totalFootpathsBlocking = graphRepository.getTotalOfBlockingsByType("Senda peatonal");
            Integer totalFootpathsRampMissing =
                    graphRepository.getTotalOfRampMissingByType("Senda peatonal");
            Integer totalFootpathsCrosswalkMissing =
                    graphRepository.getTotalOfCrosswalkMissingByType("Senda peatonal");
            Integer totalFootpathsAuditiveAlert =
                    graphRepository.getTotalOfAuditiveAlertByType("Senda peatonal");
            Integer totalFootpathsBadConditions =
                    graphRepository.getTotalOfBadConditionsByType("Senda peatonal");
            Integer totalFootPathsProblems = totalFootpathsBlocking + totalFootpathsRampMissing
                    + totalFootpathsCrosswalkMissing + totalFootpathsBadConditions;

            summary.put("total_footpaths", totalFootpaths);
            summary.put("total_footpaths_problems", totalFootPathsProblems);
            summary.put("total_footpaths_blocking", totalFootpathsBlocking);
            summary.put("total_footpaths_rampMissing", totalFootpathsRampMissing);
            summary.put("total_footpaths_crosswalkMissing", totalFootpathsCrosswalkMissing);
            summary.put("total_footpaths_auditiveAlert", totalFootpathsAuditiveAlert);
            summary.put("total_footpaths_badConditions", totalFootpathsBadConditions);

            // PEDESTRIANS
            Integer totalPedestrians = graphRepository.getTotalOneTypeOfRoad("Peatonal");
            Integer totalPedestriansBlocking = graphRepository.getTotalOfBlockingsByType("Peatonal");
            Integer totalPedestriansPodotactile = graphRepository.getTotalOfPodotactileByType("Peatonal");
            Integer totalPedestriansBadConditions = graphRepository.getTotalOfBadConditionsByType("Peatonal");
            Integer totalPedestriansProblems = totalPedestriansBlocking + totalPedestriansBadConditions;

            summary.put("total_pedestrians", totalPedestrians);
            summary.put("total_pedestrians_problems", totalPedestriansProblems);
            summary.put("total_pedestrians_blocking", totalPedestriansBlocking);
            summary.put("total_pedestrians_podotactile", totalPedestriansPodotactile);
            summary.put("total_pedestrians_badConditions", totalPedestriansBadConditions);

            // SIDEWALKS
            Integer totalSidewalks = graphRepository.getTotalOneTypeOfRoad("Vereda");
            Integer totalSidewalksBlocking = graphRepository.getTotalOfBlockingsByType("Vereda");
            Integer totalSidewalksPodotactile = graphRepository.getTotalOfPodotactileByType("Vereda");
            Integer totalSidewalksBadConditions = graphRepository.getTotalOfBadConditionsByType("Vereda");
            Integer totalSidewalksProblems = totalSidewalksBlocking + totalSidewalksBadConditions;

            summary.put("total_sidewalks", totalSidewalks);
            summary.put("total_sidewalks_problems", totalSidewalksProblems);
            summary.put("total_sidewalks_blocking", totalSidewalksBlocking);
            summary.put("total_sidewalks_podotactile", totalSidewalksPodotactile);
            summary.put("total_sidewalks_badConditions", totalSidewalksBadConditions);

            return summary;
        } catch (Exception ex) {
            throw  new GraphException("Error when trying to get the graph summary, reason: "+ ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ShortestPathDTO getShortestPathInGraph(String startStreet, Integer startNumber,
                                                       String targetStreet, Integer targetNumber) {
        try {
            if(getGraphList().contains("grafo")){
                dropGraph();
            }
            createGraph();
            List<LocationEntity> locations = graphRepository.getShortestPathInGraph(startStreet, startNumber,
                    targetStreet, targetNumber);
            List<RoadEntity> roads = IntStream.range(0, locations.size() - 1)
                    .mapToObj(i -> getRoadBetweenLocations(locations.get(i), locations.get(i + 1))).toList();
            List<String> instructions = getShortestPathInstructions(locations, roads);
            return ShortestPathDTO.builder()
                    .locations(locations)
                    .roads(roads)
                    .instructions(instructions)
                    .build();
        } catch (GraphException ex) {
            throw new GraphException("Error when trying to prepare the graph to get the shortest path, reason: "
                    + ex.getMessage());
        } catch (Exception ex) {
            throw  new GraphException("Error when trying to get the shortest path between location: "+ startStreet
                    +" "+ startNumber + " and location: "+ targetStreet +" "+ targetNumber
                    +", reason: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ShortestPathDTO getShortestPathInMotorGraph(String startStreet, Integer startNumber,
                                                       String targetStreet, Integer targetNumber) {
        try {
            if(getGraphList().contains("grafo-motriz")){
                dropMotorGraph();
            }
            createMotorGraph();
            List<LocationEntity> locations = graphRepository.getShortestPathInMotorGraph(startStreet, startNumber,
                    targetStreet, targetNumber);
            List<RoadEntity> roads = IntStream.range(0, locations.size() - 1)
                    .mapToObj(i -> getRoadBetweenLocations(locations.get(i), locations.get(i + 1))).toList();
            List<String> instructions = getMotorShortestPathInstructions(locations, roads);
            return ShortestPathDTO.builder()
                    .locations(locations)
                    .roads(roads)
                    .instructions(instructions)
                    .build();
        } catch (GraphException ex) {
            throw new GraphException("Error when trying to prepare the graph to get the (motor) shortest path, reason: "
                    + ex.getMessage());
        } catch (Exception ex) {
            throw  new GraphException("Error when trying to get the shortest path between location: "+ startStreet
                    +" "+ startNumber + " and location: "+ targetStreet +" "+ targetNumber);
        }
    }

    @Transactional(readOnly = true)
    public ShortestPathDTO getShortestPathInVisionGraph(String startStreet, Integer startNumber,
                                                       String targetStreet, Integer targetNumber) {
        try {
            if(getGraphList().contains("grafo-vision")){
                dropVisionGraph();
            }
            createVisionGraph();
            List<LocationEntity> locations = graphRepository.getShortestPathInVisionGraph(startStreet, startNumber,
                    targetStreet, targetNumber);
            List<RoadEntity> roads = IntStream.range(0, locations.size() - 1)
                    .mapToObj(i -> getRoadBetweenLocations(locations.get(i), locations.get(i + 1))).toList();
            List<String> instructions = getVisionShortestPathInstructions(locations, roads);
            return ShortestPathDTO.builder()
                    .locations(locations)
                    .roads(roads)
                    .instructions(instructions)
                    .build();
        } catch (GraphException ex) {
            throw new GraphException("Error when trying to prepare the graph to get the (vision) shortest path, reason: "
                    + ex.getMessage());
        } catch (Exception ex) {
            throw  new GraphException("Error when trying to get the shortest path between location: "+ startStreet
                    +" "+ startNumber + " and location: "+ targetStreet +" "+ targetNumber);
        }
    }

    @Transactional(readOnly = true)
    private List<String> getGraphList() {
        try {
            return graphRepository.getGraphList();
        } catch (Exception ex) {
            throw new GraphException(ex.getMessage());
        }
    }

    @Transactional
    private void dropGraph() {
        try {
            graphRepository.dropGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    @Transactional
    private void createGraph() {
        try {
            graphRepository.createGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    @Transactional
    private void dropMotorGraph() {
        try {
            graphRepository.dropMotorGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    @Transactional
    private void createMotorGraph() {
        try {
            graphRepository.createMotorGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    @Transactional
    private void dropVisionGraph() {
        try {
            graphRepository.dropVisionGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    @Transactional
    private void createVisionGraph() {
        try {
            graphRepository.createVisionGraph();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("Records with more than one value cannot be converted without a mapper.")) {
                throw new GraphException(ex.getMessage());
            }
        }
    }

    private List<String> getShortestPathInstructions(List<LocationEntity> locations, List<RoadEntity> roads) {
        List<String> instructions = new ArrayList<>();
        instructions.add("Dirígete al punto de partida en "+ locations.get(0).getStreet()+ " '"
                + locations.get(0).getNumber() + "'.");
        for (int i = 0, roadsSize = roads.size(); i < roadsSize; i++) {

            String milestone = "";
            if(roads.get(i).isBlockingExists()
                    || roads.get(i).isBadConditionExists()
                    || roads.get(i).isCrosswalkMissingExists()) {
                milestone = ", se ha reportado en esta sección del camino que ";
                milestone = (roads.get(i).isBlockingExists())
                        ? milestone+"existe un bloqueo parcial o total, " : milestone;
                milestone = (roads.get(i).isBadConditionExists())
                        ? milestone+"se encuentra en malas condiciones, " : milestone;
                milestone = (roads.get(i).isCrosswalkMissingExists())
                        ? milestone+"la senda petaonal no esta demarcada, " : milestone;

                if (milestone.endsWith(", ")) {
                    milestone = milestone.substring(0, milestone.length() - 2);
                }
            }

            switch (roads.get(i).getType()) {
                case "Nexo vereda" -> instructions.add("SKIP");
                case "Vereda" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance())
                                + " metros por la vereda de " + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Peatonal" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance()) + " metros por la peatonal "
                                + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Senda peatonal" -> {
                    String aux = "Atraviesa " + locations.get(i).getStreet() + " por la senda peatonal para llegar a ";
                    if (i < roads.size() - 1 && roads.get(i + 1).getType().equals("Nexo vereda")) {
                        aux = aux + locations.get(i + 2).getStreet() + " '" + locations.get(i + 2).getNumber() + "'"
                                + milestone + ".";
                    } else {
                        aux = aux + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".";
                    }
                    instructions.add(aux);
                }
                case "Nexo peatonal" -> instructions.add("Cruza al lado opuesto de la peatonal.");
            }
        }
        instructions.add("Has llegado a destino.");
        return instructions;
    }

    private List<String> getMotorShortestPathInstructions(List<LocationEntity> locations, List<RoadEntity> roads) {
        List<String> instructions = new ArrayList<>();
        instructions.add("Dirígete al punto de partida en "+ locations.get(0).getStreet()+ " '"
                + locations.get(0).getNumber() + "'.");
        for (int i = 0, roadsSize = roads.size(); i < roadsSize; i++) {

            String milestone = "";
            if(roads.get(i).isBlockingExists()
                    || roads.get(i).isBadConditionExists()
                    || roads.get(i).isRampMissingExists()
                    || roads.get(i).isCrosswalkMissingExists()) {
                milestone = ", se ha reportado en esta sección del camino que ";
                milestone = (roads.get(i).isBlockingExists())
                        ? milestone+"existe un bloqueo parcial o total, " : milestone;
                milestone = (roads.get(i).isBadConditionExists())
                        ? milestone+"se encuentra en malas condiciones, " : milestone;
                milestone = (roads.get(i).isRampMissingExists())
                        ? milestone+"no cuenta con rampa, " : milestone;
                milestone = (roads.get(i).isCrosswalkMissingExists())
                        ? milestone+"la senda petaonal no esta demarcada, " : milestone;

                if (milestone.endsWith(", ")) {
                    milestone = milestone.substring(0, milestone.length() - 2);
                }
            }

            switch (roads.get(i).getType()) {
                case "Nexo vereda" -> instructions.add("SKIP");
                case "Vereda" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance())
                                + " metros por la vereda de " + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Peatonal" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance()) + " metros por la peatonal "
                                + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Senda peatonal" -> {
                    String aux = "Atraviesa " + locations.get(i).getStreet() + " por la senda peatonal para llegar a ";
                    if (i < roads.size() - 1 && roads.get(i + 1).getType().equals("Nexo vereda")) {
                        aux = aux + locations.get(i + 2).getStreet() + " '" + locations.get(i + 2).getNumber() + "'"
                                + milestone + ".";
                    } else {
                        aux = aux + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".";
                    }
                    instructions.add(aux);
                }
                case "Nexo peatonal" -> instructions.add("Cruza al lado opuesto de la peatonal.");
            }
        }
        instructions.add("Has llegado a destino.");
        return instructions;
    }

    private List<String> getVisionShortestPathInstructions(List<LocationEntity> locations, List<RoadEntity> roads) {
        List<String> instructions = new ArrayList<>();
        instructions.add("Dirígete al punto de partida en "+ locations.get(0).getStreet()+ " '"
                +locations.get(0).getNumber() + "'.");
        for (int i = 0, roadsSize = roads.size(); i < roadsSize; i++) {

            String milestone = "";
            if(roads.get(i).isBlockingExists()
                    || roads.get(i).isBadConditionExists()
                    || roads.get(i).isPodotactileExists()
                    || roads.get(i).isAuditiveAlertExists()
                    || roads.get(i).isCrosswalkMissingExists()) {
                milestone = ", se ha reportado en esta sección del camino que ";
                milestone = (roads.get(i).isBlockingExists())
                        ? milestone+"existe un bloqueo parcial o total, " : milestone;
                milestone = (roads.get(i).isBadConditionExists())
                        ? milestone+"se encuentra en malas condiciones, " : milestone;
                milestone = (roads.get(i).isPodotactileExists())
                        ? milestone+"cuenta con baldosas podotáctiles, " : milestone;
                milestone = (roads.get(i).isAuditiveAlertExists())
                        ? milestone+"cuenta con alerta auditiva en el semáforo, " : milestone;
                milestone = (roads.get(i).isCrosswalkMissingExists())
                        ? milestone+"la senda petaonal no esta demarcada, " : milestone;

                if (milestone.endsWith(", ")) {
                    milestone = milestone.substring(0, milestone.length() - 2);
                }
            }

            switch (roads.get(i).getType()) {
                case "Nexo vereda" -> instructions.add("SKIP");
                case "Vereda" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance())
                                + " metros por la vereda de " + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Peatonal" ->
                        instructions.add("Avanza " + Math.round(roads.get(i).getDistance()) + " metros por la peatonal "
                                + locations.get(i + 1).getStreet() + " hasta llegar a "
                                + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".");
                case "Senda peatonal" -> {
                    String aux = "Atraviesa " + locations.get(i).getStreet() + " por la senda peatonal para llegar a ";
                    if (i < roads.size() - 1 && roads.get(i + 1).getType().equals("Nexo vereda")) {
                        aux = aux + locations.get(i + 2).getStreet() + " '" + locations.get(i + 2).getNumber() + "'"
                                + milestone + ".";
                    } else {
                        aux = aux + locations.get(i + 1).getStreet() + " '" + locations.get(i + 1).getNumber() + "'"
                                + milestone + ".";
                    }
                    instructions.add(aux);
                }
                case "Nexo peatonal" -> instructions.add("Cruza al lado opuesto de la peatonal.");
            }
        }
        instructions.add("Has llegado a destino.");
        return instructions;
    }
}