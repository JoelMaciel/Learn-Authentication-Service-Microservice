package com.joel.authservice.domain.dtos.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdatePasswordRequestDTO {

    @Size(min = 8, max = 50)
    @NotBlank
    private String password;
    @NotBlank
    private String oldPassword;
}
