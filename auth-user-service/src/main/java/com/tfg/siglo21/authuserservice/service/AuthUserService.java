package com.tfg.siglo21.authuserservice.service;

import com.tfg.siglo21.authuserservice.dto.*;
import com.tfg.siglo21.authuserservice.entity.UserEntity;
import com.tfg.siglo21.authuserservice.exception.*;
import com.tfg.siglo21.authuserservice.repository.UserRepository;
import com.tfg.siglo21.authuserservice.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.NoSuchElementException;

@Service
public class AuthUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    public String createUser(UserDTO userDTO) {
        try{
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            String encodedSecretQuestion = passwordEncoder.encode(userDTO.getSecretQuestion());
            String encodedSecretAnswer = passwordEncoder.encode(userDTO.getSecretAnswer());
            UserEntity userCreated = userRepository.save(UserEntity.builder()
                    .email(userDTO.getEmail())
                    .password(encodedPassword)
                    .secretQuestion(encodedSecretQuestion)
                    .secretAnswer(encodedSecretAnswer)
                    .profile(userDTO.getProfile())
                    .build());
            return userCreated.getId();
        } catch (DuplicateKeyException ex) {
            throw new DuplicateKeyException("The email is already taken");
        } catch (Exception ex) {
            throw new CreateUserException("Error when trying to create the user, reason: "+ ex.getMessage());
        }
    }

    public String updateProfile(String token, String userId, String profile) {
        try {
                UserEntity userEntity = userRepository.findById(userId).orElseThrow();
            if(getUserIdFromToken(token).equals(userId)) {
                userEntity.setProfile(profile);
                userRepository.save(userEntity);
                return userEntity.getId();
            } else {
                throw new Exception();
            }
        }  catch (NoSuchElementException ex) {
            throw new NotFoundException("User with ID "+ userId +" not found");
        } catch (Exception ex) {
            throw new UpdateUserException("Error when trying to update the requested user");
        }
    }

    public String recoveryPassword(PasswordRecoveryDTO passwordRecoveryDTO) {
        try {
            UserEntity userEntity = userRepository.findByEmail(passwordRecoveryDTO.getEmail()).orElseThrow();
            if(passwordEncoder.matches(passwordRecoveryDTO.getSecretQuestion(), userEntity.getSecretQuestion())
                    && passwordEncoder.matches(passwordRecoveryDTO.getSecretAnswer(), userEntity.getSecretAnswer())) {
                String encodedPassword = passwordEncoder.encode(passwordRecoveryDTO.getNewPassword());
                userEntity.setPassword(encodedPassword);
                userRepository.save(userEntity);
                return userEntity.getId();
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            throw new UpdateUserException("Error when trying to update the requested user");
        }
    }

    public String updatePassword(String token, String userId, PasswordChangeDTO passwordChangeDTO) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow();
            if(getUserIdFromToken(token).equals(userId)
                    && passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), userEntity.getPassword())) {
                String encodedPassword = passwordEncoder.encode(passwordChangeDTO.getNewPassword());
                userEntity.setPassword(encodedPassword);
                userRepository.save(userEntity);
                return userEntity.getId();
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            throw new UpdateUserException("Error when trying to update the requested user");
        }
    }

    public String updateSecretQuestionAnswer(String token, String userId, QuestionAnswerDTO questionAnswerDTO) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow();
            if(getUserIdFromToken(token).equals(userId)
                    && passwordEncoder.matches(questionAnswerDTO.getPassword(), userEntity.getPassword())) {
                String encodedNewSecretQuestion = passwordEncoder.encode(questionAnswerDTO.getSecretQuestion());
                String encodedNewAnswerQuestion = passwordEncoder.encode(questionAnswerDTO.getSecretAnswer());
                userEntity.setSecretQuestion(encodedNewSecretQuestion);
                userEntity.setSecretAnswer(encodedNewAnswerQuestion);
                userRepository.save(userEntity);
                return userEntity.getId();
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            throw new UpdateUserException("Error when trying to update the requested user");
        }
    }

    public void deleteUser(String token, String userId, QuestionAnswerDTO questionAnswerDTO) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow();
            if(getUserIdFromToken(token).equals(userId)
                    && passwordEncoder.matches(questionAnswerDTO.getPassword(), userEntity.getPassword())
                    && passwordEncoder.matches(questionAnswerDTO.getSecretQuestion(), userEntity.getSecretQuestion())
                    && passwordEncoder.matches(questionAnswerDTO.getSecretAnswer(), userEntity.getSecretAnswer())) {
                userRepository.deleteById(userId);
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            throw new DeleteUserException("Error when trying to delete the requested user");
        }
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try{
            UserEntity userEntity = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow();
            if(passwordEncoder.matches(loginRequestDTO.getPassword(), userEntity.getPassword())) {
                TokenDTO tokenDTO = new TokenDTO(jwtProvider.createToken(userEntity));
                return LoginResponseDTO.builder()
                        .id(userEntity.getId())
                        .profile(userEntity.getProfile())
                        .token(tokenDTO.getToken())
                        .email(userEntity.getEmail())
                        .build();
            } else {
                throw new Exception();
            }
        } catch (Exception ex){
            throw new LoginException("Error when trying to log in the user, reason: Invalid email or password");
        }
    }

    public TokenDTO validate(String token) {
        try{
            if(!jwtProvider.validate(token)) {
                throw new Exception();
            }
            String email = jwtProvider.getEmailFromToken(token);
            if(userRepository.findByEmail(email).isEmpty()) {
                throw new Exception();
            }
            return new TokenDTO(token);
        } catch (Exception ex) {
            throw new TokenValidationException("Error when trying to validate the token, reason: Bad token");
        }
    }

    private static String getUserIdFromToken(String token) {
        String[] chunks = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
        String[] id = payload.split("\"id\":\"", 23);
        id =  id[1].split("\"");
        return id[0];
    }
}
