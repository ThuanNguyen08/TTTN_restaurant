package com.restaurant.Revenue_service.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.Revenue_service.Exception.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");

		// Nếu không có token, tiếp tục chuỗi filter
		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = header.replace("Bearer ", "");
		try {
			String username = jwtUtil.extractUsername(token);
			Long userId = jwtUtil.extractUserId(token);
			Collection<GrantedAuthority> authorities = jwtUtil.extractAuthorities(token);

			// Check if token is expired
			if (jwtUtil.isTokenExpired(token)) {
				handleJwtException(response, "Token đã hết hạn");
				return;
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					authorities);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			// thêm thông tin user vào attribute để sử dụng sau
			request.setAttribute("userId", userId);

			chain.doFilter(request, response);

		} catch (ExpiredJwtException e) {
			// Xử lý token hết hạn
			handleJwtException(response, "Token đã hết hạn");
		} catch (MalformedJwtException e) {
			// Xử lý token có định dạng không hợp lệ
			handleJwtException(response, "Token không hợp lệ");
		} catch (SignatureException e) {
			// Xử lý token có chữ ký không hợp lệ
			handleJwtException(response, "Chữ ký token không hợp lệ");
		} catch (UnsupportedJwtException e) {
			// Xử lý token không được hỗ trợ
			handleJwtException(response, "Token không được hỗ trợ");
		} catch (Exception e) {
			// Xử lý các lỗi khác
			handleJwtException(response, "Lỗi xác thực: " + e.getMessage());
		}
	}

	private void handleJwtException(HttpServletResponse response, String message) throws IOException {
		// Xoá context bảo mật
		SecurityContextHolder.clearContext();

		// Thiết lập response
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// Tạo đối tượng lỗi và ghi vào response
		ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, message);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}