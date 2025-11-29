package com.example.Visa.Config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Visa.Entities.UserInfo;

import java.util.Collection;
import java.util.List;

public class UserInfoUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final String name;

    public UserInfoUserDetails(UserInfo userInfo) {
        this.email = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.name = userInfo.getName();
        // assume roles stored as single token like "USER" or "OFFICER"
        String role = userInfo.getRoles();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
