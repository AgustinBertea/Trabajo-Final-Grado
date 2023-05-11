package com.tfg.siglo21.authuserservice.controller;

import com.tfg.siglo21.authuserservice.dto.*;
import com.tfg.siglo21.authuserservice.entity.UserEntity;
import com.tfg.siglo21.authuserservice.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/users")
public class AuthUserController {

    @Autowired
    AuthUserService authUserService;

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(authUserService.createUser(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<String> changeProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String token,
                                                    @PathVariable @NotBlank String userId,
                                                    @RequestBody @NotBlank String profile) {
        return new ResponseEntity<>(authUserService.updateProfile(token, userId, profile), HttpStatus.OK);
    }

    @PutMapping("/recovery")
    public ResponseEntity<String> recoveryPassword(@RequestBody @Valid PasswordRecoveryDTO passwordRecoveryDTO) {
        return new ResponseEntity<>(authUserService.recoveryPassword(passwordRecoveryDTO), HttpStatus.OK);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String token,
                                                     @PathVariable @NotBlank String userId,
                                                     @RequestBody @Valid PasswordChangeDTO passwordChangeDTO) {
        return new ResponseEntity<>(authUserService.updatePassword(token, userId, passwordChangeDTO), HttpStatus.OK);
    }

    @PutMapping("/{userId}/question-answer")
    public ResponseEntity<String> changeSecretQuestionAnswer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String token,
            @PathVariable @NotBlank String userId, @RequestBody @Valid QuestionAnswerDTO questionAnswerDTO) {
        return new ResponseEntity<>(authUserService.updateSecretQuestionAnswer(token, userId, questionAnswerDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String token,
            @PathVariable @NotBlank String userId, @RequestBody @Valid QuestionAnswerDTO questionAnswerDTO) {
        authUserService.deleteUser(token, userId, questionAnswerDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        return new ResponseEntity<>(authUserService.login(loginRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/auth/validate")
    public ResponseEntity<TokenDTO> validate(@RequestParam @NotBlank String token){
        return new ResponseEntity<>(authUserService.validate(token), HttpStatus.OK);
    }
}