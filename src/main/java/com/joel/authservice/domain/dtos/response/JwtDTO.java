package com.joel.authservice.domain.dtos.response;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtDTO {

    @NonNull
    private String token;
    private String type = "Bearer";

}
