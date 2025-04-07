package com.restaurant.Revenue_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.restaurant.Revenue_service.DTO.UserDto;
import com.restaurant.Revenue_service.DTO.UserResponse;
import com.restaurant.Revenue_service.Exception.ServiceUnavailableException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserServiceClient {

	private final RestTemplate restTemplate;
	private final String userServiceUrl;

	public UserServiceClient(RestTemplate restTemplate, @Value("${services.user.url}") String userServiceUrl) {
		this.restTemplate = restTemplate;
		this.userServiceUrl = userServiceUrl;
	}

	public UserDto getUserById(Long userId, HttpServletRequest request) {
		try {
			String authHeader = request.getHeader("Authorization");

			HttpHeaders headers = new HttpHeaders();
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				headers.set("Authorization", authHeader);
			}

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<UserResponse> response = restTemplate.exchange(userServiceUrl + "/api/users/{id}",
					HttpMethod.GET, entity, UserResponse.class, userId);
			if (response.getBody() == null) {
				throw new RuntimeException("Không tìm thâý người dùng với id: " + userId);
			}

			UserResponse user = response.getBody();
			return new UserDto(user.getId(), user.getUsername(), user.getFullName());

		} catch (Exception e) {
			e.printStackTrace(); // In stack trace đầy đủ
			System.err.println("Chi tiết lỗi: " + e.getMessage());
			throw new ServiceUnavailableException("User service không hoạt động: " + e.getMessage());
		}
	}
}
