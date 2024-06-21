package com.joel.authservice.api.controller;

import com.joel.authservice.api.configs.security.JwtProvider;
import com.joel.authservice.domain.dtos.request.LoginRequestDTO;
import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.response.JwtDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO saveUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.save(userRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> authenticateUser(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.ok(new JwtDTO(jwt));
    }
}
