package com.joel.authservice.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserUpdatePasswordRequestDTO {

    @Size(min = 8, max = 50)
    @NotBlank
    private String password;
    @NotBlank
    private String oldPassword;
}
