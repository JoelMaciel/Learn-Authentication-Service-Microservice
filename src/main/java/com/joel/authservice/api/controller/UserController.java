package com.joel.authservice.api.controller;

import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserDTO> getAll(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)
                                Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{userId}")
    public UserDTO getOne(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PutMapping("/{userId}")
    public UserDTO updateUser(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequestDTO userUpdate) {
        return userService.updateUser(userId, userUpdate);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }

}
