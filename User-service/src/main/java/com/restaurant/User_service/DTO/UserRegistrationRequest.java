package com.restaurant.User_service.DTO;

import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Entity.User.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {
	@NotBlank(message = "Username không được bỏ trống")
	@Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 kí tự")
	private String username;

	@NotBlank(message = "Password không được bỏ trống")
	@Size(min = 6, message = "Password phải ít nhất 6 kí tự")
	private String password;

	@NotBlank(message = "Email không được bỏ trống")
	@Email(message = "Email phải đúng cú pháp")
	private String email;

	@Pattern(regexp = "^0[0-9]{9}$", message = "Phone number phải bắt đầu từ 0 và duy nhất 10 chữ số ")
	private String phone;

	@Size(max = 100, message = "Full name không vượt quá 100 kí tự")
	private String fullName;

	private User.Role role;

	public UserRegistrationRequest() {
	}

	public String getUsername() {
		return username;
	}

	public UserRegistrationRequest(String username, String password, String email, String phone, String fullName,
			Role role) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.fullName = fullName;
		this.role = role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

}
