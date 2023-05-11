package com.tfg.siglo21.authuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenDTO {

    @NotBlank(message = "Token cannot be blank")
    private String token;
}
