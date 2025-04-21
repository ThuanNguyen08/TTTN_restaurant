package com.restaurant.User_service.Security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.User_service.DTO.UserLoginRequest;
import com.restaurant.User_service.DTO.UserResponse;
import com.restaurant.User_service.Entity.RefreshToken;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Service.RefreshTokenService;
import com.restaurant.User_service.Service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserService userService;
	private final RefreshTokenService refreshTokenService;
	private final ObjectMapper objectMapper;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			UserService userService, RefreshTokenService refreshTokenService, ObjectMapper objectMapper) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
		this.objectMapper = objectMapper;
		setFilterProcessesUrl("/api/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			UserLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);

			// validation
			if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
				throw new InsufficientAuthenticationException("Tên đăng nhập không được để trống");
			}

			if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
				throw new InsufficientAuthenticationException("Mật khẩu không được để trống");
			}

			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (IOException e) {
			throw new RuntimeException("Lỗi khi đọc dữ liệu đăng nhập từ request", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		UserDetails userDetails = (UserDetails) authResult.getPrincipal();
		User user = userService.findByUsername(userDetails.getUsername());

//        System.out.println("Login successful for user: " + user.getUsername());

		userService.updateLastLogin(user.getId());

		String token = jwtUtil.generateToken(userDetails, user);
//        System.out.println("Generated token: " + token);
		
		 // Tạo refresh token
	    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("token", token);
		responseBody.put("refreshToken", refreshToken.getToken());
		responseBody.put("user", UserResponse.fromEntity(user));

		objectMapper.writeValue(response.getOutputStream(), responseBody);
//        System.out.println("Response sent to client");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		HttpStatus status;
		Map<String, Object> errorResponse = new HashMap<>();

		if (failed instanceof BadCredentialsException) {
			status = HttpStatus.UNAUTHORIZED; // 401
			errorResponse.put("message", "Sai tài khoản hoặc mật khẩu!");

		} else if (failed instanceof DisabledException) {
			status = HttpStatus.FORBIDDEN; // 403
			errorResponse.put("message", "Tài khoản bị vô hiệu hóa!");

		} else if (failed instanceof InsufficientAuthenticationException) {
			status = HttpStatus.BAD_REQUEST; // 400
			errorResponse.put("message",   failed.getMessage());

		} else {
	        status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
	        errorResponse.put("message", "Lỗi server: " + failed.getMessage());
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status.value());

		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}

}
