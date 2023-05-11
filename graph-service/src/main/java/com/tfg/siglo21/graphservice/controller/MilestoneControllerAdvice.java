package com.tfg.siglo21.graphservice.controller;

import com.tfg.siglo21.graphservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MilestoneControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach( error ->
                errorsMap.put(error.getField(), error.getDefaultMessage()));
        return errorsMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex){
        Map<String, String> errorsMap = new HashMap<>();
        ex.getConstraintViolations().forEach( error ->
                errorsMap.put("errorMessage", error.getMessage()));
        return errorsMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BadConditionException.class)
    public Map<String, String> handleBadConditionException(BadConditionException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AuditiveAlertException.class)
    public Map<String, String> handleAuditiveAlertException(AuditiveAlertException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BlockingException.class)
    public Map<String, String> handleBlockingException(BlockingException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CrosswalkMissingException.class)
    public Map<String, String> handleCrosswalkMissingException(CrosswalkMissingException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PodotactileException.class)
    public Map<String, String> handlePodotactileException(PodotactileException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RampMissingException.class)
    public Map<String, String> handleRampMissingException(RampMissingException ex) {
        return getErrorsMap(ex);
    }

    private static Map<String, String> getErrorsMap(Exception ex) {
        Map<String, String> errorsMap = new HashMap<>();
        errorsMap.put("errorMessage", ex.getMessage());
        return errorsMap;
    }
}