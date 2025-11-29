package com.example.Visa.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.Visa.Dto.UpdateDto;
import com.example.Visa.Entities.UserInfo;
import com.example.Visa.Entities.Visa;
import com.example.Visa.Repositories.UserInfoRepository;
import com.example.Visa.Repositories.VisaRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationService {

	@Autowired
	private VisaRepository visaRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;

	public ResponseEntity<Object> createApplication(Visa application) {
		// check applicationId presence
		if (application.getApplicationId() == null || application.getApplicationId().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "applicationId required"));
		}
		// check unique
		if (visaRepository.existsByApplicationId(application.getApplicationId())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "applicationId already exists"));
		}

		// get current user email from SecurityContext
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
		}
		String email = auth.getName();
		Optional<UserInfo> optUser = userInfoRepository.findByEmail(email);
		if (optUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
		}

		UserInfo user = optUser.get();
		// check role is USER
		boolean isUser = user.getRoles() != null && user.getRoles().equals("USER");
		if (!isUser) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Only USER role can create applications"));
		}

		application.setApplicant(user);
		Visa saved = visaRepository.save(application);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	public ResponseEntity<Object> deleteApplication(int id) {
		Optional<Visa> opt = visaRepository.findById(id);
		if (opt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found"));
		}
		Visa visa = opt.get();

		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
		}
		String email = auth.getName();
		Optional<UserInfo> optUser = userInfoRepository.findByEmail(email);
		if (optUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
		}
		UserInfo user = optUser.get();

		// only creator can delete
		if (visa.getApplicant() == null || visa.getApplicant().getId() != user.getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only creator can delete"));
		}

		visaRepository.delete(visa);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	public ResponseEntity<Object> getApplication(String applicationId) {
		if (applicationId == null || applicationId.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "applicationId required"));
		}
		Optional<Visa> opt = visaRepository.findByApplicationId(applicationId);
		if (opt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found"));
		}
		return ResponseEntity.ok(opt.get());
	}

	public ResponseEntity<Object> updateApplication(int id, UpdateDto dto) {
		Optional<Visa> opt = visaRepository.findById(id);
		if (opt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Not found"));
		}
		Visa visa = opt.get();

		// check authenticated user role OFFICER per README (should return 401 if not
		// OFFICER)
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
		}
		// authorities are present in auth
		boolean hasOfficer = auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"));
		if (!hasOfficer) {
			// README says return 401 in this case; follow that
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Officer role required"));
		}

		// apply patch
		visa.setStatus(dto.getStatus());
		Visa saved = visaRepository.save(visa);
		return ResponseEntity.ok(saved);
	}
}
