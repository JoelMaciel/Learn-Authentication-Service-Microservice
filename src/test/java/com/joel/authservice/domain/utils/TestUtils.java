package com.joel.authservice.domain.utils;

import com.joel.authservice.domain.dtos.request.*;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.enums.ActionType;
import com.joel.authservice.domain.enums.RoleType;
import com.joel.authservice.domain.enums.UserType;
import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.models.UserModel;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;

public class TestUtils {

    public static UserRequestDTO getMockUserRequestDTO() {
        return UserRequestDTO.builder()
                .username("vianateste")
                .email("viana@gmail.com")
                .cpf("723.465.900-53")
                .fullName("Viana Maciel")
                .password("12345678")
                .phoneNumber("085 999999999")
                .build();
    }

    public static UserDTO getMockUserDTO() {
        return UserDTO.builder()
                .userId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"))
                .username("vianateste")
                .email("viana@gmail.com")
                .cpf("723.465.900-53")
                .fullName("Viana Maciel")
                .phoneNumber("085 999999999")
                .userType(UserType.STUDENT.name())
                .updateDate(OffsetDateTime.now())
                .creationDate(OffsetDateTime.now())
                .build();
    }

    public static UserModel getMockUserModel() {
      return   UserModel.builder()
                .userId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"))
                .username("vianateste")
                .email("viana@gmail.com")
                .cpf("723.465.900-53")
                .fullName("Viana Maciel")
                .password("12345678")
                .phoneNumber("085 999999999")
                .userType(UserType.STUDENT)
                .roles(new HashSet<>())
                .updateDate(OffsetDateTime.now())
                .creationDate(OffsetDateTime.now())
                .build();
    }

    public static UserEventDTO getMockUserEventDTO() {
        return UserEventDTO.builder()
                .userId(UUID.randomUUID())
                .email("viana@gmail.com")
                .cpf("723.465.900-53")
                .fullName("Viana Maciel")
                .actionType(String.valueOf(ActionType.CREATE))
                .build();
    }

    public static RoleModel getMockRoleModel() {
        return RoleModel.builder()
                .roleId(UUID.randomUUID())
                .roleName(RoleType.ROLE_STUDENT)
                .build();
    }

    public static UserUpdateRequestDTO getMockUserUpdateRequestDTO() {
        return UserUpdateRequestDTO.builder()
                .username("usernameUpdated")
                .email("vianaupdated@gmail.com")
                .fullName("Viana Maciel Updated")
                .phoneNumber("085 888888888")
                .build();
    }

    public static UserModel getUpdatedUserModel(UserModel userModel, UserUpdateRequestDTO userUpdateRequestDTO) {
        return userModel.toBuilder()
                .username(userUpdateRequestDTO.getUsername())
                .email(userUpdateRequestDTO.getEmail())
                .fullName(userUpdateRequestDTO.getFullName())
                .phoneNumber(userUpdateRequestDTO.getPhoneNumber())
                .updateDate(OffsetDateTime.now())
                .build();
    }

    public static UserDTO getUpdatedUserDTO(UserModel originalUser, UserUpdateRequestDTO userUpdateRequestDTO) {
        return UserDTO.builder()
                .userId(originalUser.getUserId())
                .username(userUpdateRequestDTO.getUsername())
                .email(userUpdateRequestDTO.getEmail())
                .fullName(userUpdateRequestDTO.getFullName())
                .phoneNumber(userUpdateRequestDTO.getPhoneNumber())
                .userType(originalUser.getUserType().name())
                .build();
    }

    public static UserUpdatePasswordRequestDTO getMockUserUpdatePasswordRequestDTO(UserModel originalUser) {
        return UserUpdatePasswordRequestDTO.builder()
                .password("11223344")
                .oldPassword(originalUser.getPassword())
                .build();
    }

    public static UserModel getMockUserUpdatedPassword(UserModel originalUser, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        return originalUser.toBuilder()
                .password(userUpdatePasswordRequestDTO.getPassword())
                .build();
    }

    public static UserModel getMockUserAdmin(UserModel originalUser) {
        return originalUser.toBuilder()
                .userType(UserType.ADMIN)
                .build();
    }

    public static UserDTO getMockUserDTOAdmin(UserDTO originalUserDTO) {
        return originalUserDTO.toBuilder()
                .userType(UserType.ADMIN.name())
                .build();
    }

    public static UserAdminRequestDTO getMockUserAdminRequestDTO() {
        return UserAdminRequestDTO.builder()
                .userId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"))
                .build();
    }
}
