package com.joel.authservice.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {
    SYSTEM_ERROR("system-error", "System error"),
    FORBIDDEN("forbidden", "Forbidden"),
    INVALID_DATA("invalid-data", "Invalid data"),
    INCOMPREHENSIBLE_MESSAGE("/incomprehesible_message", "Incomprehesible Message"),
    RESOURCE_NOT_FOUND("/resource-not-found", "Resource Not Found"),
    ENTITY_IN_USE("/entity-in-use", "Entity In Use"),
    INVALID_PARAMETER("invalid-parameter", "Invalid parameter"),
    BUSINESS_ERROR("/business-error", "Business Rule Violation");

    private final String  title;
    private final String uri;

    ProblemType(String path, String title) {
        this.uri = "https://ead_authu.com.br" + path;
        this.title = title;
    }
}
