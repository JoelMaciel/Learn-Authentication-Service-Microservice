package com.joel.authservice.utils;

import static io.restassured.RestAssured.given;

public class AuthenticationHelper {

    public static String getJwtTokenAdmin() {
        return given()
                .contentType("application/json")
                .body(TestUtils.getLoginAdminRequestDTO())
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static String getJwtTokenForUserStudent() {
        return given()
                .contentType("application/json")
                .body(TestUtils.getLoginStudentRequestDTO())
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}

