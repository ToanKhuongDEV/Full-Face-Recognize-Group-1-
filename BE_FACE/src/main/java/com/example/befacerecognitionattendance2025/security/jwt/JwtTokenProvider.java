package com.example.befacerecognitionattendance2025.security.jwt;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.exception.InvalidException;
import com.example.befacerecognitionattendance2025.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String CLAIM_TYPE = "type";
    private final String TYPE_ACCESS = "access";
    private final String TYPE_REFRESH = "refresh";
    private final String USERNAME_KEY = "username";
    private final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access.expiration_time}")
    private long accessExpirationMinutes;

    @Value("${jwt.refresh.expiration_time}")
    private long refreshExpirationMinutes;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserPrincipal userPrincipal, boolean isRefreshToken) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis +
                (isRefreshToken ? refreshExpirationMinutes : accessExpirationMinutes) * 60_000;

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TYPE, isRefreshToken ? TYPE_REFRESH : TYPE_ACCESS);
        claims.put(USERNAME_KEY, userPrincipal.getUsername());
        claims.put(AUTHORITIES_KEY, userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
        claims.put("id", userPrincipal.getId());


        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(getSigningKey());

        if (!isRefreshToken) {
            builder.setSubject(userPrincipal.getId());
        }

        return builder.compact();
    }

    // Lấy Authentication từ refresh token
    public Authentication getAuthenticationByRefreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);

        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE)) ||
                ObjectUtils.isEmpty(claims.get(USERNAME_KEY)) ||
                ObjectUtils.isEmpty(claims.get(AUTHORITIES_KEY))) {
            throw new InvalidException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new UserPrincipal(claims.get("id", String.class),claims.get(USERNAME_KEY).toString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // Lấy Authentication từ Access token
    public Authentication getAuthenticationByAccessToken(String accessToken) {
        Claims claims = parseToken(accessToken);

        if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE)) ||
                ObjectUtils.isEmpty(claims.get(USERNAME_KEY)) ||
                ObjectUtils.isEmpty(claims.get(AUTHORITIES_KEY))) {
            throw new InvalidException(ErrorMessage.Auth.ERR_INVALID_TOKEN);
        }

        // Parse authorities từ claim "auth"
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Tạo UserPrincipal từ ID và username
        UserDetails principal = new UserPrincipal(
                claims.get("id", String.class),
                claims.get(USERNAME_KEY, String.class),
                "", // password không cần
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // Validate token (Access hoặc Refresh)
    public boolean validateToken(String token) {
        try {
            this.parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token không hợp lệ: {}", e.getMessage());
            return false;
        }
    }

    // Lấy Claims từ token
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parseToken(token).get(USERNAME_KEY, String.class);
    }

    public String extractUserId(String token) {
        return parseToken(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
