package com.tfg.siglo21.authuserservice.controller;

import com.tfg.siglo21.authuserservice.exception.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthUserControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach( error ->
                errorsMap.put(error.getField(), error.getDefaultMessage()));
        return errorsMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<String, String> handleDuplicateKeyException(DuplicateKeyException ex){
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CreateUserException.class)
    public Map<String, String> handleCreateUserException(CreateUserException ex){
        return getErrorsMap(ex);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(GetUserException.class)
    public Map<String, String> handleGetUserException(GetUserException ex){
        return getErrorsMap(ex);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UpdateUserException.class)
    public Map<String, String> handleUpdateUserException(UpdateUserException ex){
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DeleteUserException.class)
    public Map<String, String> handleDeleteUserException(DeleteUserException ex) {
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenValidationException.class)
    public Map<String, String> handleTokenValidationException(TokenValidationException ex){
        return getErrorsMap(ex);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(LoginException.class)
    public Map<String, String> handleLoginException(LoginException ex){
        return getErrorsMap(ex);
    }

    private static Map<String, String> getErrorsMap(Exception ex) {
        Map<String, String> errorsMap = new HashMap<>();
        errorsMap.put("errorMessage", ex.getMessage());
        return errorsMap;
    }
}
