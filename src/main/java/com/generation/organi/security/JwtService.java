package com.generation.organi.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
	public static final String SECRET = "1c35c6a352697a8d8c26b82f3f150b6104973c7b08f6beb093ae04bab62451ef";
			
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
		}
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String extractUseremail(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String userEmail = extractUseremail(token);
		return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token)) ;
	}
	
	private String createToken(Map<String, Object> claims, String userEmail) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userEmail)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 ))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	
	public String generateToken(String userEmail) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims,userEmail);
	}
	
}