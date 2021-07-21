package com.authservice.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authservice.models.CustomUserDetails;
import com.authservice.services.JwtTokenProvider;
import com.authservice.services.UserService;

import io.jsonwebtoken.Claims;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

	private final JwtConfig jwtConfig;
	private JwtTokenProvider tokenProvider;
	private UserService userService;

	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, JwtTokenProvider tokenProvider, UserService userService) {

		this.jwtConfig = jwtConfig;
		this.tokenProvider = tokenProvider;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//1. get the authentication header. Tokens are supposed to be passed in the authentication header
		String header = request.getHeader(jwtConfig.getHeader());
		
		//2. validate the header and check the prefix
		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
			filterChain.doFilter(request, response);
			return;
		}
		

        // If there is no token provided and hence the user won't be authenticated.
        // It's Ok. Maybe the user accessing a public path or asking for a token.

        // All secured paths that needs a token are already defined and secured in config class.
        // And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

        // 3. Get the token
		String token = header.replace(jwtConfig.getPrefix(), "");
		
		if(tokenProvider.validateToken(token)) {
			Claims claims = tokenProvider.getClaimsFromJWT(token);
			String username = claims.getSubject();
			
			UsernamePasswordAuthenticationToken auth =
					userService.findByUserName(username)
							.map(CustomUserDetails::new)
							.map(userDetails -> {
								UsernamePasswordAuthenticationToken authetication =
										new UsernamePasswordAuthenticationToken(
												userDetails, null, userDetails.getAuthorities());
								authetication
										.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
								return authetication;
							})
							.orElse(null);
			SecurityContextHolder.getContext().setAuthentication(auth);
			
		} else {
			SecurityContextHolder.clearContext();
		}
		
		//go to the next filter in the filter chain
		filterChain.doFilter(request, response);
		
	}
}
