package com.joel.authservice.domain.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class CpfAlreadyExists extends DataIntegrityViolationException {

    public CpfAlreadyExists(String message) {
        super(message);
    }
}
