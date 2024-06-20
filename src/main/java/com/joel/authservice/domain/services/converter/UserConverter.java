package com.joel.authservice.domain.services.converter;

import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.enums.UserType;
import com.joel.authservice.domain.models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;


@Component
@RequiredArgsConstructor
public class UserConverter {

    public Page<UserDTO> userDTOPage(Page<UserModel> users) {
        return users.map(this::toDTO);
    }

    public UserDTO toDTO(UserModel user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .fullName(user.getFullName())
                .userType(String.valueOf(user.getUserType()))
                .phoneNumber(user.getPhoneNumber())
                .creationDate(user.getCreationDate())
                .updateDate(user.getUpdateDate())
                .build();
    }

    public UserModel toEntity(UserRequestDTO userRequestDTO) {
        return UserModel.builder()
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .cpf(userRequestDTO.getCpf())
                .password(userRequestDTO.getPassword())
                .fullName(userRequestDTO.getFullName())
                .userType(UserType.STUDENT)
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .roles(new HashSet<>())
                .build();
    }

    public UserModel toUpdateUser(UserModel user, UserUpdateRequestDTO userUpdate) {
        return user.toBuilder()
                .username(userUpdate.getUsername())
                .email(userUpdate.getEmail())
                .fullName(userUpdate.getFullName())
                .phoneNumber(userUpdate.getPhoneNumber())
                .updateDate(OffsetDateTime.now())
                .build();
    }
}
