package com.joel.authservice.domain.services.impl;

import com.joel.authservice.domain.dtos.request.UserAdminRequestDTO;
import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.enums.RoleType;
import com.joel.authservice.domain.exceptions.PasswordMismatchedException;
import com.joel.authservice.domain.exceptions.UserNotFoundException;
import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.models.UserModel;
import com.joel.authservice.domain.repositories.UserRepository;
import com.joel.authservice.domain.services.RoleService;
import com.joel.authservice.domain.services.UserService;
import com.joel.authservice.domain.services.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String MISMATCHED_OLD_PASSWORD = "Mismatched old password";
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final RoleService roleService;

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<UserModel> users = userRepository.findAll(pageable);
        return userConverter.userDTOPage(users);
    }

    @Transactional
    @Override
    public UserDTO save(UserRequestDTO userRequestDTO) {
        UserModel user = userConverter.toEntity(userRequestDTO);
        addRoleToUser(user, RoleType.ROLE_STUDENT);
        return userConverter.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findById(UUID userId) {
        UserModel userModel = optionalUser(userId);
        return userConverter.toDTO(userModel);
    }

    @Transactional
    @Override
    public UserDTO updateUser(UUID userId, UserUpdateRequestDTO userUpdate) {
        UserModel user = optionalUser(userId);
        UserModel userUpdated = userConverter.toUpdateUser(user, userUpdate);
        return userConverter.toDTO(userRepository.save(userUpdated));
    }

    @Override
    public UserModel optionalUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        UserModel userModel = optionalUser(userId);
        userRepository.delete(userModel);
    }

    @Transactional
    @Override
    public void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        UserModel userModel = optionalUser(userId);
        validatePassword(userUpdatePasswordRequestDTO, userModel);

        UserModel userUpdated = userConverter.toUpdatePassword(userModel, userUpdatePasswordRequestDTO);
        userRepository.save(userUpdated);
    }

    @Transactional
    @Override
    public UserDTO saveAdmin(UserAdminRequestDTO adminRequestDTO) {
        UserModel user = optionalUser(adminRequestDTO.getUserId());

        UserModel userTypeAdmin = userConverter.toUserTypeAdmin(user);
        addRoleAdmin(userTypeAdmin);

        return userConverter.toDTO(userRepository.save(userTypeAdmin));
    }

    private void addRoleAdmin(UserModel user) {
        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_ADMIN);
        user.getRoles().add(roleModel);
    }

    private static void validatePassword(UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO, UserModel userModel) {
        if (!userModel.getPassword().equals(userUpdatePasswordRequestDTO.getOldPassword())) {
            throw new PasswordMismatchedException(MISMATCHED_OLD_PASSWORD);
        }
    }

    private void addRoleToUser(UserModel userModel, RoleType roleType) {
        RoleModel role = roleService.findByRoleName(roleType);
        userModel.getRoles().add(role);
    }
}
