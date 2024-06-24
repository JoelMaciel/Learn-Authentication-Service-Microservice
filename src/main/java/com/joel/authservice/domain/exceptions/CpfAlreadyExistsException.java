package com.joel.authservice.domain.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class CpfAlreadyExistsException extends DataIntegrityViolationException {

    public CpfAlreadyExistsException(String message) {
        super(message);
    }
}
