package com.joel.authservice.domain.dtos.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class UserAdminRequestDTO {

    @NotNull
    private UUID userId;
}
