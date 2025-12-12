package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.domain.dto.request.ChangePasswordRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.LoginRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.RefreshTokenRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.EmployeeResponse;
import com.example.befacerecognitionattendance2025.domain.dto.response.LoginResponse;
import com.example.befacerecognitionattendance2025.domain.mapper.EmployeeMapper;
import com.example.befacerecognitionattendance2025.exception.InvalidException;
import com.example.befacerecognitionattendance2025.exception.UnauthorizedException;
import com.example.befacerecognitionattendance2025.repository.EmployeeRepository;
import com.example.befacerecognitionattendance2025.security.UserPrincipal;
import com.example.befacerecognitionattendance2025.security.jwt.JwtTokenProvider;
import com.example.befacerecognitionattendance2025.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Attempting login for username: {}", request.getUsername());

            // Xác thực thông tin đăng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Cập nhật last_login mà không cần load toàn bộ entity
            employeeRepository.updateLastLogin(userPrincipal.getId(), LocalDateTime.now());

            // Tạo tokens
            String accessToken = jwtTokenProvider.generateToken(userPrincipal, false);
            String refreshToken = jwtTokenProvider.generateToken(userPrincipal, true);

            log.info("Login successful for user ID: {}", userPrincipal.getId());

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    userPrincipal.getId(),
                    authentication.getAuthorities()
            );

        } catch (InternalAuthenticationServiceException | BadCredentialsException ex) {
            log.warn("Login failed for username: {} - {}", request.getUsername(), ex.getMessage());
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_CREDENTIALS);
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        try {
            log.info("Refreshing token...");

            // 1. Validate token trước khi dùng
            if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
            }

            // 2. Lấy Authentication từ refresh token
            Authentication authentication = jwtTokenProvider.getAuthenticationByRefreshToken(request.getRefreshToken());
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // 3. Sinh access token mới
            String newAccessToken = jwtTokenProvider.generateToken(userPrincipal, false);

            log.info("Refresh token thành công cho user ID: {}", userPrincipal.getId());

            return new LoginResponse(
                    newAccessToken,
                    request.getRefreshToken(),
                    userPrincipal.getId(),
                    userPrincipal.getAuthorities()
            );

        } catch (Exception ex) {
            log.warn("Refresh token failed: {}", ex.getMessage());
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public EmployeeResponse verifyPassword(String password) {
        return null;
    }

    @Override
    public String getCurrentUserId() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) return null;

        var principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userDetails) {
            log.info("Current user ID: {}", userDetails.getId());
            return userDetails.getId();
        }
        return null;
    }

}
