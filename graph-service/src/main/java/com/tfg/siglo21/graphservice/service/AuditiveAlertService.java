package com.tfg.siglo21.graphservice.service;

import com.tfg.siglo21.graphservice.entity.LocationEntity;
import com.tfg.siglo21.graphservice.exception.AuditiveAlertException;
import com.tfg.siglo21.graphservice.exception.NotFoundException;
import com.tfg.siglo21.graphservice.repository.AuditiveAlertMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class AuditiveAlertService {

    @Autowired
    AuditiveAlertMilestoneRepository auditiveAlertMilestoneRepository;

    @Transactional
    public LocationEntity addMilestone(Long roadId, String userId) {
        try {
            return auditiveAlertMilestoneRepository.addMilestone(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone is already reported, the type of this road does not allow this milestone ");
        } catch (Exception ex) {
            throw new AuditiveAlertException("Error when trying to add the auditive alert milestone in the road " +
                    "with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity deleteMilestone(Long roadId, String userId) {
        try {
            return auditiveAlertMilestoneRepository.deleteMilestone(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the user did not report this milestone");
        } catch (Exception ex) {
            throw new AuditiveAlertException("Error when trying to delete the auditive alert milestone in the road " +
                    "with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity addPositiveVote(Long roadId, String userId) {
        try {
            return auditiveAlertMilestoneRepository.addPositiveVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the type of this road does not allow this milestone, " +
                    "the user is the complainant of this milestone, the user already vote positively this milestone");
        } catch (Exception ex) {
            throw new AuditiveAlertException("Error when trying to add a positive vote from the auditive alert " +
                    "milestone in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity addNegativeVote(Long roadId, String userId) {
        try {
            return auditiveAlertMilestoneRepository.addNegativeVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the type of this road does not allow this milestone, " +
                    "the user is the complainant of this milestone, the user already vote negatively this milestone");
        } catch (Exception ex) {
            throw new AuditiveAlertException("Error when trying to add a negative vote from the auditive alert " +
                    "milestone in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }

    @Transactional
    public LocationEntity deleteVote(Long roadId, String userId) {
        try {
            return auditiveAlertMilestoneRepository.deleteVote(roadId, userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException("Cannot perform this action, possible reasons: the road does not exist, " +
                    "the milestone does not exist, the user did not vote this milestone");
        } catch (Exception ex) {
            throw new AuditiveAlertException("Error when trying to delete the vote from the auditive alert milestone " +
                    "in the road with id: "+ roadId +" reason: "+ ex.getMessage());
        }
    }
}