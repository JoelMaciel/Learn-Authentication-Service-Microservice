package com.joel.authservice.domain.services.impl;

import com.joel.authservice.api.configs.security.AuthenticationCurrentUserService;
import com.joel.authservice.api.configs.security.UserDetailsImpl;
import com.joel.authservice.api.publishers.UserEventPublisher;
import com.joel.authservice.domain.dtos.request.*;
import com.joel.authservice.domain.dtos.response.UserDTO;
import com.joel.authservice.domain.enums.ActionType;
import com.joel.authservice.domain.enums.RoleType;
import com.joel.authservice.domain.exceptions.*;
import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.models.UserModel;
import com.joel.authservice.domain.repositories.UserRepository;
import com.joel.authservice.domain.services.RoleService;
import com.joel.authservice.domain.services.converter.UserConverter;
import com.joel.authservice.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    public static final String MSG_USER_NOT_FOUND = "User 081caae9-358d-4ded-9a37-e2a66573549a not found in the database";
    public static final String ALREADY_IN_USE = "Email is already in use.";
    public static final String MISMATCHED_OLD_PASSWORD = "Mismatched old password";
    public static final String FORBIDDEN = "Forbidden";
    public static final String CPF_IS_IN_USE = "CPF is already in use.";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private RoleService roleService;

    @Mock
    private AuthenticationCurrentUserService authenticationCurrentUserService;

    @Mock
    private UserEventPublisher userEventPublisher;

    private UserRequestDTO userRequestDTO;
    private UserDTO userDTO;
    private UserModel userModel;
    private RoleModel roleModel;
    private UserEventDTO userEventDTO;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private UserDTO updateUserDTO;
    private UUID userId;
    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRequestDTO = TestUtils.getMockUserRequestDTO();
        userDTO = TestUtils.getMockUserDTO();
        userModel = TestUtils.getMockUserModel();
        userModel.setUserId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"));
        roleModel = TestUtils.getMockRoleModel();
        userEventDTO = TestUtils.getMockUserEventDTO();
        userUpdateRequestDTO = TestUtils.getMockUserUpdateRequestDTO();
        updateUserDTO = TestUtils.getUpdatedUserDTO(userModel, userUpdateRequestDTO);
        userId = UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a");
        invalidUserId = UUID.fromString("e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8");

        when(authenticationCurrentUserService.getCurrentUser()).thenReturn(UserDetailsImpl.build(userModel));
    }

    @Test
    @DisplayName("Given UserRequestDTO Valid When Save User Then Should Save User Successfully")
    void givenUserRequestDTOValid_WhenSaveUser_ThenShouldSaveUserSuccessfully() {
        when(userConverter.toEntity(userRequestDTO)).thenReturn(userModel);
        when(roleService.findByRoleName(RoleType.ROLE_STUDENT)).thenReturn(roleModel);
        when(userRepository.save(userModel)).thenReturn(userModel);
        when(userConverter.toEventDTO(userModel, ActionType.CREATE)).thenReturn(userEventDTO);
        when(userConverter.toDTO(userModel)).thenReturn(userDTO);

        UserDTO result = userService.save(userRequestDTO);

        assertNotNull(result);
        assertEquals(result.getUserId(), userModel.getUserId());
        assertEquals(result.getUserType(), userModel.getUserType().name());
        assertEquals(result.getUsername(), userModel.getUsername());
        verify(userRepository, times(1)).save(userModel);
        verify(userEventPublisher, times(1)).publisherEvent(any(UserEventDTO.class));
    }

    @Test
    @DisplayName("Given Existing Email When Save Then Throw EmailAlreadyExistsException")
    void givenExistingEmail_whenSave_thenThrowEmailAlreadyExistsException() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .email("existingemail@example.com")
                .cpf("12345678901")
                .build();

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.save(userRequestDTO);
        });

        assertEquals(ALREADY_IN_USE, exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userRequestDTO.getEmail());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Existing CPF When Save Then Throw CpfAlreadyExistsException")
    void givenExistingCpf_whenSave_thenThrowCpfAlreadyExistsException() {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .email("newemail@example.com")
                .cpf("000111222365")
                .build();

        when(userRepository.existsByCpf(userRequestDTO.getCpf())).thenReturn(true);

        CpfAlreadyExistsException exception = assertThrows(CpfAlreadyExistsException.class, () -> {
            userService.save(userRequestDTO);
        });

        assertEquals(CPF_IS_IN_USE, exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userRequestDTO.getEmail());
        verify(userRepository, times(1)).existsByCpf(userRequestDTO.getCpf());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Valid UserId When FindById Then Return UserDTO")
    void givenValidUserId_whenFindById_thenReturnsUserDTO() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        when(userConverter.toDTO(userModel)).thenReturn(userDTO);

        UserDTO result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userDTO.getUserId(), result.getUserId());
        assertEquals(userDTO.getUserType(), result.getUserType());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(authenticationCurrentUserService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("Given Logged-in User Does Not Match Requested User When FindById Then Throw UserDoesNotHavePermissionException")
    void givenLoggedInUserDoesNotMatchRequestedUser_whenFindById_thenThrowUserDoesNotHavePermissionException() {
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        UserDoesNotHavePermissionException exception = assertThrows(UserDoesNotHavePermissionException.class, () -> {
            userService.findById(invalidUserId);
        });

        assertEquals(FORBIDDEN, exception.getMessage());
        verify(authenticationCurrentUserService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("Given Valid Pageable When FindAll Then Return Page of UserDTOs")
    void givenValidPageable_whenFindAll_thenReturnsPageOfUserDTOs() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);

        List<UserModel> mockUserModels = Collections.singletonList(TestUtils.getMockUserModel());
        Page<UserModel> mockUserModelPage = new PageImpl<>(mockUserModels, pageable, mockUserModels.size());
        when(userRepository.findAll(pageable)).thenReturn(mockUserModelPage);

        List<UserDTO> mockUserDTOs = Collections.singletonList(TestUtils.getMockUserDTO());
        Page<UserDTO> mockUserDTOPage = new PageImpl<>(mockUserDTOs, pageable, mockUserDTOs.size());
        when(userConverter.userDTOPage(mockUserModelPage)).thenReturn(mockUserDTOPage);

        Page<UserDTO> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(mockUserDTOPage.getTotalElements(), result.getTotalElements());
        assertEquals(mockUserDTOPage.getContent().size(), result.getContent().size());
        assertEquals(mockUserDTOPage.getContent().get(0).getUserId(), result.getContent().get(0).getUserId());
        assertEquals(mockUserDTOPage.getContent().get(0).getUsername(), result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAll(pageable);
        verify(userConverter, times(1)).userDTOPage(mockUserModelPage);
    }

    @Test
    @DisplayName("Given Valid UserId and UserUpdateRequestDTO When UpdateUser Then Return Updated UserDTO")
    void givenValidUserIdAndUserUpdateRequestDTO_whenUpdateUser_thenReturnsUpdatedUserDTO() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        UserModel updatedUserModel = TestUtils.getUpdatedUserModel(userModel, userUpdateRequestDTO);

        when(userConverter.toUpdateUser(userModel, userUpdateRequestDTO)).thenReturn(updatedUserModel);
        when(userRepository.save(any(UserModel.class))).thenReturn(updatedUserModel);
        when(userConverter.toDTO(any(UserModel.class))).thenReturn(updateUserDTO);
        when(userConverter.toEventDTO(any(UserModel.class), eq(ActionType.UPDATE))).thenReturn(userEventDTO);

        UserDTO result = userService.updateUser(userId, userUpdateRequestDTO);

        assertNotNull(result);
        assertEquals(updateUserDTO.getUserId(), result.getUserId());
        assertEquals(userUpdateRequestDTO.getUsername(), result.getUsername());
        assertEquals(userUpdateRequestDTO.getEmail(), result.getEmail());
        assertEquals(userUpdateRequestDTO.getFullName(), result.getFullName());
        assertEquals(userUpdateRequestDTO.getPhoneNumber(), result.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserModel.class));
        verify(userEventPublisher, times(1)).publisherEvent(any(UserEventDTO.class));
        verify(authenticationCurrentUserService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("Given Logged-in User Does Not Match Requested User When Update User Then Throw UserDoesNotHavePermissionException")
    void givenLoggedInUserDoesNotMatchRequestedUser_whenUpdateUser_thenThrowUserDoesNotHavePermissionException() {
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        UserDoesNotHavePermissionException exception = assertThrows(UserDoesNotHavePermissionException.class, () -> {
            userService.updateUser(invalidUserId, userUpdateRequestDTO);
        });

        assertEquals(FORBIDDEN, exception.getMessage());
        verify(authenticationCurrentUserService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("Given Valid UserId When Delete User Then Should Delete User Successfully")
    void givenValidUserId_whenDeleteUser_thenShouldDeleteUserSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        userService.delete(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(userModel);
    }

    @Test
    @DisplayName("Given Invalid UserId When Delete User Then Throw UserNotFoundException")
    void givenInvalidUserId_whenDeleteUser_thenThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.delete(userId);
        });

        assertEquals(MSG_USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, never()).delete(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Valid UserId and UserUpdatePasswordRequestDTO When Update Password Then Should Update Password Successfully")
    void givenValidUserIdAndUserUpdatePasswordRequestDTO_whenUpdatePassword_thenShouldUpdatePasswordSuccessfully() {
        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtils.getMockUserUpdatePasswordRequestDTO(userModel);
        UserModel userUpdated = TestUtils.getMockUserUpdatedPassword(userModel, userUpdatePasswordRequestDTO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        when(userConverter.toUpdatePassword(userModel, userUpdatePasswordRequestDTO)).thenReturn(userUpdated);

        userService.updatePassword(userId, userUpdatePasswordRequestDTO);

        assertNotEquals(userModel.getPassword(), userUpdated.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userUpdated);

    }

    @Test
    @DisplayName("Given Invalid UserId When Update Password Then Throw UserNotFoundException")
    void givenInvalidUserId_whenUpdatePassword_thenThrowUserNotFoundException() {
        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtils.getMockUserUpdatePasswordRequestDTO(userModel);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updatePassword(userId, userUpdatePasswordRequestDTO);
        });

        assertEquals(MSG_USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Given Invalid Old Password When Update Password Then Throw PasswordMismatchedException")
    void givenInvalidOldPassword_whenUpdatePassword_thenThrowPasswordMismatchedException() {
        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtils.getMockUserUpdatePasswordRequestDTO(userModel);
        userUpdatePasswordRequestDTO.setOldPassword("23232323");
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        PasswordMismatchedException exception = assertThrows(PasswordMismatchedException.class, () -> {
            userService.updatePassword(userId, userUpdatePasswordRequestDTO);
        });

        assertEquals(MISMATCHED_OLD_PASSWORD, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserModel.class));
    }


    @Test
    @DisplayName("Given Valid UserAdminRequestDTO When SaveAdmin Then Return UserDTO")
    void givenValidUserAdminRequestDTO_whenSaveAdmin_thenReturnsUserDTO() {
        UserModel userAdmin = TestUtils.getMockUserAdmin(userModel);
        UserAdminRequestDTO userAdminRequestDTO = TestUtils.getMockUserAdminRequestDTO();
        UserDTO userAdminDTO = TestUtils.getMockUserDTOAdmin(userDTO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        RoleModel roleModel = new RoleModel();
        roleModel.setRoleName(RoleType.ROLE_ADMIN);
        when(roleService.findByRoleName(RoleType.ROLE_ADMIN)).thenReturn(roleModel);

        when(userConverter.toUserTypeAdmin(userModel)).thenReturn(userAdmin);
        when(userRepository.save(userAdmin)).thenReturn(userAdmin);
        when(userConverter.toDTO(userAdmin)).thenReturn(userAdminDTO);

        UserDTO result = userService.saveAdmin(userAdminRequestDTO);

        assertNotNull(result);
        assertEquals(userAdminDTO, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userConverter, times(1)).toUserTypeAdmin(userModel);
        verify(userRepository, times(1)).save(userAdmin);
        verify(userConverter, times(1)).toDTO(userAdmin);
        verify(roleService, times(1)).findByRoleName(RoleType.ROLE_ADMIN);
    }
}