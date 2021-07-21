package com.authservice.services;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.authservice.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenProvider {
	private final JwtConfig jwtConfig;

	public JwtTokenProvider(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	public String generateToken(Authentication authentication) {
		Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();
	

	}

	public Claims getClaimsFromJWT(String token) {
		return Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(token).getBody();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWt signature");
		} catch (MalformedJwtException e) {
			log.error("Invalid Jwt token");
		} catch (ExpiredJwtException e) {
			log.error(("Expried Jwt token"));
		} catch (UnsupportedJwtException e) {
			log.error(("Unsupported Jwt token"));
		} catch (IllegalArgumentException e) {
			log.error(("JWT claims string is empty"));
		}
		return false;
	}
}
