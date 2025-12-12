package com.example.befacerecognitionattendance2025.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UploadFileException extends RuntimeException {

    private final HttpStatus status;

    public UploadFileException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public UploadFileException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
