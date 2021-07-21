package com.authservice.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {
	public User(User user) {
		this.id = user.id;
		this.username = user.username;
		this.password = user.password;
		this.email = user.email;
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.active = user.active;
		this.userProfile = user.userProfile;
		this.roles = user.roles;
	}

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.active = true;	
		
		HashSet<Role> listRoles = new HashSet<>();
		listRoles.add(new Role("USER"));
		this.roles = listRoles;
	}

	@Id
	private String id;

	private String username;

	@JsonIgnore
	private String password;

	private String email;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	private boolean active;
	private Profile userProfile;
	private Set<Role> roles;
}
