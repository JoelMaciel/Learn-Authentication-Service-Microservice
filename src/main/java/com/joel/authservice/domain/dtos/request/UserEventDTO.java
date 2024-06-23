package com.joel.authservice.domain.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDTO {

    private UUID userId;
    private String email;
    private String fullName;
    private String userType;
    private String cpf;
    private String actionType;
}
