package com.joel.authservice.domain.exceptions;

public class FileStorageException extends BusinessException{

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
