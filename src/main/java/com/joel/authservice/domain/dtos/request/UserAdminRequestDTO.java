package com.joel.authservice.domain.dtos.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserAdminRequestDTO {

    @NotNull
    private UUID userId;
}
