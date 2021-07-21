package com.authservice.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authservice.exception.EmailAlreadyExistsException;
import com.authservice.exception.UsernameAlreadyExistsException;
import com.authservice.models.Role;
import com.authservice.models.User;
import com.authservice.repositories.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String loginUser(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		return tokenProvider.generateToken(authentication);
	}

	public User registerUser(User user, Role role) {
		log.info("registering user {}", user.getUsername());

		if (userRepository.existsByUsername(user.getUsername())) {
			log.warn("username {} already exists", user.getUsername());

			throw new UsernameAlreadyExistsException(String.format("username %s already exists", user.getUsername()));
		}

		if (userRepository.existsByEmail(user.getEmail()) && role.getName().equals("USER")) {
			log.warn("email {} already exists.", user.getEmail());

			throw new EmailAlreadyExistsException(String.format("email %s already exists", user.getEmail()));
		}

		user.setActive(true);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		user.setRoles(new HashSet<>() {
			private static final long serialVersionUID = 1L;

			{
				add(role);
			}
		});

		return userRepository.save(user);
	}

	public List<User> findAll() {
		log.info("retrieving all users");
		return userRepository.findAll();
	}

	public Optional<User> findByUserName(String username) {
		log.info("retrieving user {}", username);
		return userRepository.findByUsername(username);
	}

	public Optional<User> findById(String id) {
		log.info("retrieving user {}", id);
		return userRepository.findById(id);
	}
}
