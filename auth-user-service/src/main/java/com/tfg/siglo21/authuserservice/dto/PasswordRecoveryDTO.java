package com.tfg.siglo21.authuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PasswordRecoveryDTO {

    @NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email address")
    private String email;
    @Size(min = 8, message = "Password must be more than 8 characters")
    private String newPassword;
    @NotBlank(message = "Secret question cannot be blank")
    private String secretQuestion;
    @NotBlank(message = "Secret answer cannot be blank")
    private String secretAnswer;

}
