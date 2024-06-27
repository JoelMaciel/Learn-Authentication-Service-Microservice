package com.joel.authservice.domain.services.impl;

import com.joel.authservice.domain.dtos.request.UserRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdatePasswordRequestDTO;
import com.joel.authservice.domain.dtos.request.UserUpdateRequestDTO;
import com.joel.authservice.domain.models.UserModel;
import com.joel.authservice.domain.repositories.UserRepository;
import com.joel.authservice.utils.AuthenticationHelper;
import com.joel.authservice.utils.TestUtils;
import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserServiceImplIntegrationIT {

    public static final String MSG_UPDATE_PASSWORD = "Password updated successfully.";
    public static final String MISMATCHED_OLD_PASSWORD = "Mismatched old password";
    public static final String MSG_EMAIL_IN_USE = "Email already exists.";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Flyway flyway;

    @LocalServerPort
    private int port;

    private UUID invalidUserId;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/learn-auth/api";

        invalidUserId = UUID.randomUUID();
        flyway.migrate();
    }

    @Test
    @DisplayName("Given Valid UserRequestDTO, When Signup Then Save User Successfully")
    void givenValidUserRequestDTO_whenSaveUser_ThenSaveUserSuccessfully() {
        UserRequestDTO userRequestDTO = TestUtils.getMockUserRequestDTO();
        given()
                .contentType("application/json")
                .body(userRequestDTO)
             .when()
                .post("/auth/signup")
             .then()
                .statusCode(201)
                .body("username", equalTo(userRequestDTO.getUsername()))
                .body("email", equalTo(userRequestDTO.getEmail()))
                .body("cpf", equalTo(userRequestDTO.getCpf()))
                .body("fullName", equalTo(userRequestDTO.getFullName()))
                .body("phoneNumber", equalTo(userRequestDTO.getPhoneNumber()));
    }

    @Test
    @DisplayName("Given Invalid UserRequestDTO, When Signup Then Should Throw StatusCode 400")
    void givenInvalidUserRequestDTO_whenSaveUser_ThenShouldThrowStatusCode400() {
        UserRequestDTO userRequestDTO = TestUtils.getInvalidMockUserRequestDTO();
        given()
                .contentType("application/json")
                .body(userRequestDTO)
             .when()
                .post("/auth/signup")
             .then()
                .statusCode(400)
                .body("detail", equalTo("One or more fields are invalid. Fill in correctly and try again."))
                .body("objects.find { it.name == 'fullName' }.userMessage", equalTo("FullName must be at least 10 characters."))
                .body("objects.find { it.name == 'username' }.userMessage", equalTo("Username must be at least 6 characters."))
                .body("objects.find { it.name == 'password' }.userMessage", equalTo("Password must be at least 8 characters."))
                .body("objects.find { it.name == 'phoneNumber' }.userMessage", equalTo("PhoneNumber is required."))
                .body("objects.find { it.name == 'email' }.userMessage", equalTo("Email is required."))
                .body("objects.find { it.name == 'cpf' }.userMessage", equalTo("Invalid CPF. Enter a valid CPF."));
    }

    @Test
    @DisplayName("Given CPF And Email Already Exists , When Save User , Then Should Throw StatusCode 409")
    void givenCPFAndEmailAlreadyExists_whenSave_thenShouldThrowStatusCode409() {
        UserRequestDTO userRequestDTO = TestUtils.getMockUserRequestDTO();
        userRequestDTO.setEmail("admin@example.com");

        given()
                .contentType("application/json")
                .body(userRequestDTO)
             .when()
                .post("/auth/signup")
             .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("userMessage", equalTo(MSG_EMAIL_IN_USE));
    }

    @Test
    @DisplayName("Given Valid Login Credentials, When GelAll Users Then Should Return StatusCode 200")
    void givenValidLoginCredentials_whenGetAlleUsers_thenReturnStatusCode200() {
        String token = AuthenticationHelper.getJwtTokenAdmin();
        assertNotNull(token);

        UserModel userModel = TestUtils.getMockUserModel();
        userModel.setUserId(UUID.fromString("081caae9-358d-4ded-9a37-e2a66573549a"));
        userRepository.save(userModel);

        given()
                .auth().oauth2(token)
             .when()
                .get("/users")
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("content[0].username", equalTo("vianateste"))
                .body("content[0].email", equalTo("viana@gmail.com"))
                .body("content[0].userType", equalTo("STUDENT"))
                .body("content[0].phoneNumber", equalTo("085 999999999"))
                .body("content[1].username", equalTo("admintest"))
                .body("content[1].email", equalTo("admin@example.com"))
                .body("content[1].userType", equalTo("ADMIN"))
                .body("content[1].phoneNumber", equalTo("+55 11 999999999"));
    }

    @Test
    @DisplayName("Given Invalid Token, When GetAll Users Then Should Return StatusCode 401")
    void givenInvalidToken_whenGetAllUsers_thenReturnStatusCode401() {
        String invalidToken = "invalid_token";

        given()
                .auth().oauth2(invalidToken)
             .when()
                .get("/users")
             .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("error", equalTo("Unauthorized"));
    }

    @Test
    @DisplayName("Given Valid UserId, When FindById , Then Should Return StatusCode 200")
    void givenValidUserId_whenFindById_thenShouldReturnUserSuccessfully() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        String token = AuthenticationHelper.getJwtTokenForUserStudent();

        String url = "/users/" + userModel.getUserId();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
             .when()
                .get(url)
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo(userModel.getUsername()))
                .body("cpf", equalTo(userModel.getCpf()))
                .body("phoneNumber", equalTo(userModel.getPhoneNumber()));
    }

    @Test
    @DisplayName("Given Invalid UserId, When FindById , Then Should Throw StatusCode 403")
    void givenInvalidUserId_whenFindById_thenShouldThrowStatusCode403() {
        String token = AuthenticationHelper.getJwtTokenAdmin();

        String url = "/users/" + invalidUserId;

        given()
                .auth().oauth2(token)
                .contentType("application/json")
             .when()
                .get(url)
             .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Given Valid Credentials token, When Updating Own Profile, Then Return StatusCode 200")
    void givenValidStudentToken_whenUpdatingOwnProfile_thenReturnStatusCode200() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        String token = AuthenticationHelper.getJwtTokenForUserStudent();
        UserUpdateRequestDTO updateRequest = TestUtils.getMockUserUpdateRequestDTO();

        String url = "/users/" + userModel.getUserId();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(updateRequest)
              .when()
                .put(url)
              .then()
                .statusCode(HttpStatus.OK.value())
                .body("username", equalTo(updateRequest.getUsername()))
                .body("email", equalTo(updateRequest.getEmail()))
                .body("fullName", equalTo(updateRequest.getFullName()))
                .body("phoneNumber", equalTo(updateRequest.getPhoneNumber()));
    }

    @Test
    @DisplayName("Given Invalid Credentials Token, When Updating Own Profile, Then Should Throw StatusCode 403")
    void givenInvalidCredentialsToken_whenUpdatingOwnProfile_thenShouldThrowStatusCode403() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        String token = AuthenticationHelper.getJwtTokenAdmin();
        UserUpdateRequestDTO updateRequest = TestUtils.getMockUserUpdateRequestDTO();

        String url = "/users/" + userModel.getUserId();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(updateRequest)
              .when()
                .put(url)
              .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Given Valid Credentials Token, When Deleting User, Then Should Return StatusCode 204")
    void givenValidCredentialsToken_whenDeleteUser_thenShouldReturnStatusCode204() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        String token = AuthenticationHelper.getJwtTokenAdmin();

        String url = "/users/" + userModel.getUserId();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
              .when()
                .delete(url)
              .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Given Invalid Credentials Token, When Deleting User, Then Should Throw StatusCode 403")
    void givenInvalidCredentialsToken_whenDeletingUser_thenShouldThrowStatusCode403() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        String token = AuthenticationHelper.getJwtTokenForUserStudent();

        String url = "/users/" + userModel.getUserId();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
              .when()
                .delete(url)
              .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Given Valid UserId And UserUpdatePasswordRequestDTO , When Updating Password , Then Should Return StatusCode 200")
    void givenValidUserUpdatePasswordRequestDTO_whenUpdatingPassword_thenShouldThrowStatusCode200() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtils.getMockUserUpdatePasswordRequestDTO(userModel);

        String token = AuthenticationHelper.getJwtTokenForUserStudent();

        String url = "/users/" + userModel.getUserId() + "/password";

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(userUpdatePasswordRequestDTO)
              .when()
                .patch(url)
              .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo(MSG_UPDATE_PASSWORD));
    }

    @Test
    @DisplayName("Given Invalid UserUpdatePasswordRequestDTO , When Updating Password , Then Should Throw StatusCode 400")
    void givenValidUserUpdatePasswordRequestDTO_whenUpdatingPassword_thenShouldThrowStatusCode400() {
        UserModel userModel = TestUtils.getMockUserModel();
        userRepository.save(userModel);

        UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO = TestUtils.getMockInvalidUpdatePasswordRequestDTO(userModel);

        String token = AuthenticationHelper.getJwtTokenForUserStudent();

        String url = "/users/" + userModel.getUserId() + "/password";

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(userUpdatePasswordRequestDTO)
              .when()
                .patch(url)
              .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("userMessage", equalTo(MISMATCHED_OLD_PASSWORD));
    }
}
