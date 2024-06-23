package com.joel.authservice.domain.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class EmailAlreadyExists extends DataIntegrityViolationException {

    public EmailAlreadyExists(String message) {
        super(message);
    }
}
