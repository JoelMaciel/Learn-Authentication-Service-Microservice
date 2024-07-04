package com.joel.authservice.domain.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {

    String storeFile(UUID userId, MultipartFile file);
}
