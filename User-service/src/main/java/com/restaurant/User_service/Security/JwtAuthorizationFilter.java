package com.restaurant.User_service.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public JwtAuthorizationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = header.replace("Bearer ", "");
		try {
			String username = jwtUtil.extractUsername(token);
			Long userId = jwtUtil.extractUserId(token);
			Collection<GrantedAuthority> authorities = jwtUtil.extractAuthorities(token);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					authorities);//u,p,r

			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Add the user ID to the request attributes for later use
			request.setAttribute("userId", userId);

		} catch (Exception e) {
			// In case of any exception, clear the security context
			SecurityContextHolder.clearContext();
		}

		chain.doFilter(request, response);
	}
}
