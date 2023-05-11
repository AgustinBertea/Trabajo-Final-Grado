package com.tfg.siglo21.graphservice.controller;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.service.PodotactileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/podotactiles")
public class PodotactileController {

    @Autowired
    PodotactileService podotactileService;

    @PutMapping("/milestone")
    public ResponseEntity<LocationEntity> addMilestone(@RequestParam @NotNull @Min(0) Long roadId,
                                                       @RequestParam @NotBlank String userId) {
        return new ResponseEntity<>(podotactileService.addMilestone(roadId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/milestone")
    public ResponseEntity<LocationEntity> deleteMilestone(@RequestParam @NotNull @Min(0) Long roadId,
                                                          @RequestParam @NotBlank String userId) {
        return new ResponseEntity<>(podotactileService.deleteMilestone(roadId, userId), HttpStatus.OK);
    }

    @PutMapping("/positive-vote")
    public ResponseEntity<LocationEntity> addPositiveVote(@RequestParam @NotNull @Min(0) Long roadId,
                                                          @RequestParam @NotBlank String userId) {
        return new ResponseEntity<>(podotactileService.addPositiveVote(roadId, userId), HttpStatus.OK);
    }

    @PutMapping("/negative-vote")
    public ResponseEntity<LocationEntity> addNegativeVote(@RequestParam @NotNull @Min(0) Long roadId,
                                                          @RequestParam @NotBlank String userId) {
        return new ResponseEntity<>(podotactileService.addNegativeVote(roadId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/vote")
    public ResponseEntity<LocationEntity> deleteVote(@RequestParam @NotNull @Min(0) Long roadId,
                                                     @RequestParam @NotBlank String userId) {
        return new ResponseEntity<>(podotactileService.deleteVote(roadId, userId), HttpStatus.OK);
    }
}