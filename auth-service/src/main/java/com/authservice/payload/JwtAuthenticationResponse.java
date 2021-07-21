package com.authservice.payload;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtAuthenticationResponse {
	private String accessToken;

	private String tokenType = "Bearer";

	public JwtAuthenticationResponse(String accessToken) {
		this.accessToken = accessToken;
	}

}
