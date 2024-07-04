package com.joel.authservice.domain.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDTO {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
