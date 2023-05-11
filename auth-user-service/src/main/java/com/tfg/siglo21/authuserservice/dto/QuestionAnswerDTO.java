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
public class QuestionAnswerDTO {

    @NotBlank(message = "Secret question cannot be blank")
    private String password;
    @NotBlank(message = "Secret question cannot be blank")
    private String secretQuestion;
    @NotBlank(message = "Secret answer cannot be blank")
    private String secretAnswer;
}
