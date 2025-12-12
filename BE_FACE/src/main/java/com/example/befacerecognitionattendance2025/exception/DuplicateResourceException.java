package com.example.befacerecognitionattendance2025.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public DuplicateResourceException(String message) {
        super(String.format(message));
    }


}