package com.authservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.exception.ResourceNotFoundException;
import com.authservice.models.CustomUserDetails;
import com.authservice.models.User;
import com.authservice.payload.UserSummary;
import com.authservice.services.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserEndpoint {
	  @Autowired
	    private UserService userService;

	    @GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> findUser(@PathVariable("username") String username) {
	        log.info("retrieving user {}", username);

	        return  userService
	                .findByUserName(username)
	                .map(user -> ResponseEntity.ok(user))
	                .orElseThrow(() -> new ResourceNotFoundException(username));
	    }

	    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> findAll() {
	        log.info("retrieving all users");

	        return ResponseEntity
	                .ok(userService.findAll());
	    }

	    @GetMapping(value = "/users/summaries", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> findAllUserSummaries(
	            @AuthenticationPrincipal CustomUserDetails userDetails) {
	        log.info("retrieving all users summaries");

	        return ResponseEntity.ok(userService
	                .findAll()
	                .stream()
	                .filter(user -> !user.getUsername().equals(userDetails.getUsername()))
	                .map(this::convertTo));
	    }

	    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
	    @PreAuthorize("hasRole('USER') or hasRole('FACEBOOK_USER')")
	    @ResponseStatus(HttpStatus.OK)
	    public UserSummary getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
	        return UserSummary
	                .builder()
	                .id(userDetails.getId())
	                .username(userDetails.getUsername())
	                .name(userDetails.getUserProfile().getDisplayName())
	                .profilePicture(userDetails.getUserProfile().getProfilePictureUrl())
	                .build();
	    }

	    @GetMapping(value = "/users/summary/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> getUserSummary(@PathVariable("username") String username) {
	        log.info("retrieving user {}", username);

	        return  userService
	                .findByUserName(username)
	                .map(user -> ResponseEntity.ok(convertTo(user)))
	                .orElseThrow(() -> new ResourceNotFoundException(username));
	    }

	    private UserSummary convertTo(User user) {
	        return UserSummary
	                .builder()
	                .id(user.getId())
	                .username(user.getUsername())
	                .name(user.getUserProfile().getDisplayName())
	                .profilePicture(user.getUserProfile().getProfilePictureUrl())
	                .build();
	    }
}
