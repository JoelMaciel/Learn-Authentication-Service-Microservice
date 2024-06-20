package com.joel.authservice.domain.services;

import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<UserDTO> findAll(Pageable pageable);

    UserDTO findById(UUID userId);

    UserModel optionalUser(UUID userId);

    UserDTO updateUser(UUID userId, UserUpdateRequestDTO userUpdate);

    UserDTO save(UserRequestDTO userRequestDTO);

    void delete(UUID userId);
}
