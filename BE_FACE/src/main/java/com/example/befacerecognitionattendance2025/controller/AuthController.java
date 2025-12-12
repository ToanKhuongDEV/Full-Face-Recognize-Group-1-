package com.example.befacerecognitionattendance2025.controller;

import com.example.befacerecognitionattendance2025.base.RestApiV1;
import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.UrlConstant;
import com.example.befacerecognitionattendance2025.domain.dto.request.LoginRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.RefreshTokenRequest;
import com.example.befacerecognitionattendance2025.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @Operation(summary = "đăng nhập")
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<RestData<?>> login(@Valid @RequestBody LoginRequest request ) {
        return VsResponseUtil.success(service.login(request));
    }

    @Operation(summary = "lấy access token bằng refresh")
    @PostMapping(UrlConstant.Auth.REFRESH)
    public ResponseEntity<RestData<?>> refreshToken(@Valid @RequestBody RefreshTokenRequest request ) {
        return VsResponseUtil.success(service.refreshToken(request));
    }

}
