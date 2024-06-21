package com.joel.authservice.api.controller;

import com.joel.authservice.domain.dtos.request.UserAdminRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    public static final String MSG_UPDATE_PASSWORD = "Password updated successfully.";
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

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(
            @PathVariable UUID userId,
            @RequestBody @Valid UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        userService.updatePassword(userId, userUpdatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(MSG_UPDATE_PASSWORD);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO saveAdmin(@RequestBody @Valid UserAdminRequestDTO adminRequestDTO) {
        return userService.saveAdmin(adminRequestDTO);
    }

}
