package com.example.befacerecognitionattendance2025.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "id")
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Employee employee) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(employee.getRole().name())
        );
        return new UserPrincipal(
                employee.getId(),
                employee.getUsername(),
                employee.getPassword(),
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // tất cả account đều còn hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // account không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // mật khẩu còn hiệu lực
    }

    @Override
    public boolean isEnabled() {
        return true; // account active
    }
}

