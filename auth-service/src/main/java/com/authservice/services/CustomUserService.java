package com.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authservice.models.CustomUserDetails;

@Service
public class CustomUserService implements UserDetailsService {
	
	@Autowired
	private UserService userService;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userService
				.findByUserName(username)
				.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}

}
