package com.joel.authservice.domain.dtos.request;


import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserRequestDTO {

    @Size(min = 8, max = 50)
    private String username;

    @Email
    @NotBlank
    private String email;

    @CPF
    private String cpf;

    @Size(min = 8, max = 100)
    private String password;

    @Size(min = 10, max = 50)
    private String fullName;

    @NotBlank
    private String phoneNumber;
}
