package com.tfg.siglo21.graphservice.service;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.exception.CrosswalkMissingException;
import com.tfg.siglo21.graphservice.exception.NotFoundException;
import com.tfg.siglo21.graphservice.repository.CrosswalkMissingMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class CrosswalkMissingService {

    @Autowired
    CrosswalkMissingMilestoneRepository crosswalkMissingMilestoneRepository;

    @Transactional
    public LocationEntity addMilestone(Long roadId, String userId) {
        try {
            return crosswalkMissingMilestoneRepository.addMilestone(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone is already reported, the type of this road does not allow this milestone ");
        } catch (Exception ex) {
            throw new CrosswalkMissingException("Error when trying to add the crosswalk missing milestone in the " +
                    "road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity deleteMilestone(Long roadId, String userId) {
        try {
            return crosswalkMissingMilestoneRepository.deleteMilestone(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the user did not report this milestone");
        } catch (Exception ex) {
            throw new CrosswalkMissingException("Error when trying to delete the crosswalk missing milestone in the " +
                    "road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity addPositiveVote(Long roadId, String userId) {
        try {
            return crosswalkMissingMilestoneRepository.addPositiveVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the type of this road does not allow this milestone, " +
                    "the user is the complainant of this milestone, the user already vote positively this milestone");
        } catch (Exception ex) {
            throw new CrosswalkMissingException("Error when trying to add a positive vote from the crosswalk missing " +
                    "milestone in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity addNegativeVote(Long roadId, String userId) {
        try {
            return crosswalkMissingMilestoneRepository.addNegativeVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the type of this road does not allow this milestone, " +
                    "the user is the complainant of this milestone, the user already vote negatively this milestone");
        } catch (Exception ex) {
            throw new CrosswalkMissingException("Error when trying to add a negative vote from the crosswalk missing " +
                    "milestone in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity deleteVote(Long roadId, String userId) {
        try {
            return crosswalkMissingMilestoneRepository.deleteVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the user did not vote this milestone");
        } catch (Exception ex) {
            throw new CrosswalkMissingException("Error when trying to delete the vote from the crosswalk missing " +
                    "milestone in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }
}