package com.example.Visa.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Visa.Auth.AuthenticationRequest;
import com.example.Visa.Dto.UpdateDto;
import com.example.Visa.Entities.Visa;
import com.example.Visa.Services.ApplicationService;
import com.example.Visa.Services.AuthService;

@RestController
public class Controller {

	@Autowired
	ApplicationService as;

	@Autowired
	AuthService auth;

	@PostMapping("/user/login")
	public ResponseEntity<Object> login(@RequestBody AuthenticationRequest ar) {
		return auth.createAuthenticationtoken(ar);
	}

	@PostMapping("/visa/add")
	public ResponseEntity<Object> createApplication(@RequestBody Visa application) {
		return as.createApplication(application);
	}

	@DeleteMapping("/visa/delete/{id}")
	public ResponseEntity<Object> deleteApplication(@PathVariable("id") int id) {
		return as.deleteApplication(id);
	}

	@GetMapping("/visa/list")
	public ResponseEntity<Object> getAllApplication(
			@RequestParam(name = "applicationID", required = false) String applicationID) {
		return as.getApplication(applicationID);
	}

	@PatchMapping("/visa/update/{id}")
	public ResponseEntity<Object> updateApplication(@PathVariable("id") int id, @RequestBody UpdateDto dto) {
		return as.updateApplication(id, dto);
	}
}
