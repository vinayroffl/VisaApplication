package com.example.Visa.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Visa.Auth.AuthenticationRequest;
import com.example.Visa.Auth.AuthenticationResponse;
import com.example.Visa.Config.UserInfoUserDetailsService;
import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Repositories.UserInfoRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

	@Autowired
	private UserInfoRepository urepo;

	@Autowired
	private AuthenticationManager am;

	@Autowired
	private UserInfoUserDetailsService mud;

	@Autowired
	private JwtService jwtService;

	public ResponseEntity<Object> createAuthenticationtoken(AuthenticationRequest ar) {
		try {
			am.authenticate(new UsernamePasswordAuthenticationToken(ar.getEmail(), ar.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid credentials"));
		}

		UserDetails userDetails;
		try {
			userDetails = mud.loadUserByUsername(ar.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "User not found"));
		}

		String jwtToken = jwtService.generateToken(userDetails);

		// find UserInfo to return name as username per README (username = name)
		Optional<UserInfo> opt = urepo.findByEmail(ar.getEmail());
		String name = opt.map(UserInfo::getName).orElse(userDetails.getUsername());

		AuthenticationResponse resp = new AuthenticationResponse(jwtToken);
		resp.setUsername(name);
		return ResponseEntity.ok(resp);
	}
}
