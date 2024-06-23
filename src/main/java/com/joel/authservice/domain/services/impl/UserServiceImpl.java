package com.joel.authservice.domain.services.impl;

import com.joel.authservice.api.configs.security.AuthenticationCurrentUserService;
import com.joel.authservice.api.publishers.UserEventPublisher;
import com.joel.authservice.domain.dtos.request.UserAdminRequestDTO;
import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.enums.ActionType;
import com.joel.authservice.domain.enums.RoleType;
import com.joel.authservice.domain.exceptions.*;
import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.models.UserModel;
import com.joel.authservice.domain.repositories.UserRepository;
import com.joel.authservice.domain.services.RoleService;
import com.joel.authservice.domain.services.UserService;
import com.joel.authservice.domain.services.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String MISMATCHED_OLD_PASSWORD = "Mismatched old password";
    public static final String ALREADY_IN_USE = "Email is already in use.";
    public static final String CPF_IS_ALREADY_IN_USE = "CPF is already in use.";
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final RoleService roleService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;
    private final UserEventPublisher userEventPublisher;

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<UserModel> users = userRepository.findAll(pageable);
        return userConverter.userDTOPage(users);
    }

    @Transactional
    @Override
    public UserDTO save(UserRequestDTO userRequestDTO) {
        validateEmailAndCpf(userRequestDTO);
        UserModel user = userConverter.toEntity(userRequestDTO);
        addRoleToUser(user, RoleType.ROLE_STUDENT);

        userRepository.save(user);

        userEventPublisher.publisherEvent(userConverter.toEventDTO(user, ActionType.CREATE));

        return userConverter.toDTO(user);
    }

    @Override
    public UserDTO findById(UUID userId) {
        validateCurrentUser(userId);

        UserModel userModel = optionalUser(userId);
        return userConverter.toDTO(userModel);
    }

    @Transactional
    @Override
    public UserDTO updateUser(UUID userId, UserUpdateRequestDTO userUpdate) {
        validateCurrentUser(userId);

        UserModel user = optionalUser(userId);
        UserModel userUpdated = userConverter.toUpdateUser(user, userUpdate);
        userRepository.save(userUpdated);

        userEventPublisher.publisherEvent(userConverter.toEventDTO(user, ActionType.UPDATE));

        return userConverter.toDTO(userUpdated);
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

        userEventPublisher.publisherEvent(userConverter.toEventDTO(userModel, ActionType.DELETE));
    }

    @Transactional
    @Override
    public void updatePassword(UUID userId, UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {
        validateCurrentUser(userId);
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

    private void validateEmailAndCpf(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new EmailAlreadyExists(ALREADY_IN_USE);
        }

        if (userRepository.existsByCpf(userRequestDTO.getCpf())) {
            throw new CpfAlreadyExists(CPF_IS_ALREADY_IN_USE);
        }
    }

    private void validateCurrentUser(UUID userId) {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        if (!currentUserId.equals(userId)) {
            throw new UserDoesNotHavePermissionException("Forbidden");
        }
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
