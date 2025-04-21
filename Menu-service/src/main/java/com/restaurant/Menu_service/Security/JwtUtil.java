package com.restaurant.Menu_service.Security;

import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);// cách viết rút gọn của claims -> claims.getSubject()
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {// Function<Claims, String> getUsername
																					// = claims -> claims.getSubject();
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())) // Trong phiên bản 0.12.x, setSigningKey
																				// đã bị thay thế bằng verifyWith
				.build().parseSignedClaims(token) // Trong phiên bản 0.12.x, parseClaimsJws đã bị thay thế bằng
													// parseSignedClaims
				.getPayload(); // Trong phiên bản 0.12.x, getBody đã bị thay thế bằng getPayload
	}

	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public Long extractUserId(String token) {
		Claims claims = extractAllClaims(token);
		return ((Number) claims.get("userId")).longValue();
	}

	public Collection<GrantedAuthority> extractAuthorities(String token) {
		Claims claims = extractAllClaims(token);
		@SuppressWarnings("unchecked")
		Collection<String> authorities = (Collection<String>) claims.get("authorities");
		return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
}
