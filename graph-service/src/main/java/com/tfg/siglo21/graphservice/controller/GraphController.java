package com.tfg.siglo21.graphservice.controller;

import com.tfg.siglo21.graphservice.dto.ShortestPathDTO;
import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.entity.RoadEntity;
import com.tfg.siglo21.graphservice.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

@RestController
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/graph")
public class GraphController {

    @Autowired
    GraphService graphService;

    @GetMapping("/roads-all")
    public ResponseEntity<List<RoadEntity>> getAllRoads() {
        return new ResponseEntity<>(graphService.getAllRoads(), HttpStatus.OK);
    }

    @GetMapping("/nodes-all-street")
    public ResponseEntity<List<LocationEntity>> getAllNodesOfAStreet(@RequestParam @NotBlank String street) {
        return new ResponseEntity<>(graphService.getAllNodesOfAStreet(street), HttpStatus.OK);
    }

    @GetMapping("/locations-all")
    public ResponseEntity<List<LocationEntity>> getAllLocations() {
        return new ResponseEntity<>(graphService.getAllLocations(), HttpStatus.OK);
    }

    @GetMapping("/shortest-path")
    public ResponseEntity<ShortestPathDTO> getShortestPathInGraph(@RequestParam @NotBlank String startStreet,
                                                                       @RequestParam @NotNull @Min(0) Integer startNumber,
                                                                       @RequestParam @NotBlank String targetStreet,
                                                                       @RequestParam @NotNull @Min(0) Integer targetNumber) {
        return new ResponseEntity<>(graphService.getShortestPathInGraph(startStreet, startNumber, targetStreet,
                targetNumber), HttpStatus.OK);
    }

    @GetMapping("/motor-shortest-path")
    public ResponseEntity<ShortestPathDTO> getShortestPathInMotorGraph(@RequestParam @NotBlank String startStreet,
                                                                  @RequestParam @NotNull @Min(0) Integer startNumber,
                                                                  @RequestParam @NotBlank String targetStreet,
                                                                  @RequestParam @NotNull @Min(0) Integer targetNumber) {
        return new ResponseEntity<>(graphService.getShortestPathInMotorGraph(startStreet, startNumber, targetStreet,
                targetNumber), HttpStatus.OK);
    }

    @GetMapping("/vision-shortest-path")
    public ResponseEntity<ShortestPathDTO> getShortestPathInVisionGraph(@RequestParam @NotBlank String startStreet,
                                                                  @RequestParam @NotNull @Min(0) Integer startNumber,
                                                                  @RequestParam @NotBlank String targetStreet,
                                                                  @RequestParam @NotNull @Min(0) Integer targetNumber) {
        return new ResponseEntity<>(graphService.getShortestPathInVisionGraph(startStreet, startNumber, targetStreet,
                targetNumber), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<HashMap<String, Integer>> getSummary() {
        return new ResponseEntity<>(graphService.getGraphSummary(), HttpStatus.OK);
    }
}