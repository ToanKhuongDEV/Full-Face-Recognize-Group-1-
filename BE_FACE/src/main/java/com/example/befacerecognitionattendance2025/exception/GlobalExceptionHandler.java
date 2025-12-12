package com.example.befacerecognitionattendance2025.exception;

import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestData<?>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String message = fieldError.getDefaultMessage();

        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestData<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return VsResponseUtil.error(HttpStatus.FORBIDDEN,ErrorMessage.ERR_FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestData<?>> handleNotFoundException(NotFoundException ex) {
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
        return VsResponseUtil.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<RestData<?>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }


    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<RestData<?>> handleInvalidException(InvalidException ex) {
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<RestData<?>> handleUploadFileException(UploadFileException ex) {
        return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestData<?>> handleUncaughtException(Exception ex) {
        ex.printStackTrace();

        return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.ERR);
    }

}
