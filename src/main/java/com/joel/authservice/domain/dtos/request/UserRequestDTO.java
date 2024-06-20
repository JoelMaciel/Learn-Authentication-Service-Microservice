package com.joel.authservice.domain.dtos.request;


import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserRequestDTO {

    @Size(min = 8, max = 50)
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @CPF
    @NotNull
    private String cpf;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(min = 10, max = 50)
    private String fullName;

//    @NotNull
//    private String userType;

    @NotBlank
    private String phoneNumber;
}
