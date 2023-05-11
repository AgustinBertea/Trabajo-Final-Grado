package com.tfg.siglo21.authuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PasswordChangeDTO {

    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "Password must be more than 8 characters")
    private String newPassword;
}
