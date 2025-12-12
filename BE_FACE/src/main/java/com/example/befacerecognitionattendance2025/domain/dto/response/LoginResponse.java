package com.example.befacerecognitionattendance2025.domain.dto.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private String id;
    Collection<? extends GrantedAuthority> authorities;

    public LoginResponse(String accessToken, String refreshToken, String id, Collection<? extends GrantedAuthority> authorities) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.authorities = authorities;
    }

}
