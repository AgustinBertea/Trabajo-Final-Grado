package com.tfg.siglo21.authuserservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO {

    @NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email address")
    private String email;
    @Size(min = 8, message = "Password must be more than 8 characters")
    private String password;
    @NotBlank(message = "Secret question cannot be blank")
    private String secretQuestion;
    @NotBlank(message = "Secret answer cannot be blank")
    private String secretAnswer;
    @NotBlank(message = "Profile cannot be blank")
    private String profile;
}
