package com.generation.organi.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String userEmail = null;
		
		try {
			if(authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				userEmail = jwtService.extractUseremail(token);
			}
			if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
				
				if(jwtService.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(userDetails,null,
									userDetails.getAuthorities());
				}
			}
			
			filterChain.doFilter(request, response);
		}catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
				| SignatureException | ResponseStatusException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}
		
	}

}