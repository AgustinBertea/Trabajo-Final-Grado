package com.tfg.siglo21.authuserservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserEntity {

    @Id
    private String id;
    @Indexed(unique = true) @Field("correo_electronico")
    private String email;
    @Field("contrasena")
    private String password;
    @Field("pregunta_secreta")
    private String secretQuestion;
    @Field("respuesta_secreta")
    private String secretAnswer;
    @Field("perfil")
    private String profile;
}
