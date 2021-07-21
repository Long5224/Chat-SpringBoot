package com.authservice.endpoint;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.authservice.exception.BadRequestException;
import com.authservice.exception.EmailAlreadyExistsException;
import com.authservice.exception.UsernameAlreadyExistsException;
import com.authservice.models.Profile;
import com.authservice.models.Role;
import com.authservice.models.User;
import com.authservice.payload.ApiResponse;
import com.authservice.payload.FacebookLoginRequest;
import com.authservice.payload.JwtAuthenticationResponse;
import com.authservice.payload.LoginRequest;
import com.authservice.payload.SignUpRequest;
import com.authservice.services.FacebookService;
import com.authservice.services.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AutheEndpoint {

	@Autowired
	private UserService userService;
	@Autowired
	private FacebookService facebookService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}

	@PostMapping("/facebook/signin")
	public ResponseEntity<?> facebookAuth(@Valid @RequestBody FacebookLoginRequest facebookLoginRequest) {
		log.info("facebook login {}", facebookLoginRequest);
		String token = facebookService.loginUser(facebookLoginRequest.getAccessToken());
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}

	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@RequestBody SignUpRequest payload) {
		log.info("creating user {}", payload.getUsername());
		User user = User.builder().username(payload.getUsername()).email(payload.getEmail())
				.password(payload.getPassword()).userProfile(Profile.builder().displayName(payload.getName()).build())
				.build();
		try {
			userService.registerUser(user, Role.USER);
		} catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
			throw new BadRequestException(e.getMessage());
		}

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
				.buildAndExpand(user.getUsername()).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}

}
