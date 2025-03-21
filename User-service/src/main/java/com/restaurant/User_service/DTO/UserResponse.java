package com.restaurant.User_service.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Entity.User.Role;

public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private User.Role role;
	private Boolean isActive;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastLogin;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	public UserResponse() {
	}

	public UserResponse(Long id, String username, String email, String phone, String fullName, Role role,
			Boolean isActive, LocalDateTime lastLogin, LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.fullName = fullName;
		this.role = role;
		this.isActive = isActive;
		this.lastLogin = lastLogin;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public User.Role getRole() {
		return role;
	}

	public void setRole(User.Role role) {
		this.role = role;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static UserResponse fromEntity(User user) {
		if (user == null) {
			return null; // Tránh lỗi NullPointerException nếu user là null
		}

		return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getFullName(),
				user.getRole(), user.getIsActive(), user.getLastLogin(), user.getCreatedAt());
	}

}
