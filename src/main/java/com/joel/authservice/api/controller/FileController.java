package com.joel.authservice.api.controller;

import com.joel.authservice.domain.dtos.response.UploadFileDTO;
import com.joel.authservice.domain.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/upload/avatar")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadFileDTO uploadFile(@PathVariable UUID userId,  @RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(userId, file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/")
                .path(userId.toString())
                .path("/avatars/")
                .path(fileName)
                .toUriString();

        return new UploadFileDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());

    }
}
