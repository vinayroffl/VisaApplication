package com.example.Visa.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Repositories.UserInfoRepository;

import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> opt = repository.findByEmail(username);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return new UserInfoUserDetails(opt.get());
    }
}
