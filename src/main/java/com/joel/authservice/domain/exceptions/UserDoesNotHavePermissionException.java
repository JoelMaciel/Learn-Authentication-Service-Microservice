package com.joel.authservice.domain.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class UserDoesNotHavePermissionException extends AccessDeniedException {

    public UserDoesNotHavePermissionException(String msg) {
        super(msg);
    }
}
