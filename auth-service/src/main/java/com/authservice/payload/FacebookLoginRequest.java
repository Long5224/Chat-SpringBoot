package com.authservice.payload;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookLoginRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotBlank
	private String accessToken;
}
