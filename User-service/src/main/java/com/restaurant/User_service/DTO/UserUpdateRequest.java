package com.restaurant.User_service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
	@Email(message = "Email phải đúng cú pháp")
	private String email;

	@Pattern(regexp = "^0[0-9]{9}$", message = "Phone number phải bắt đầu từ 0 và đủ 10 chữ số")
	private String phone;

	@Size(max = 100, message = "Full name không vượt quá 100 kí tự")
	private String fullName;

	@NotBlank(message = "Password không được bỏ trống")
	@Size(min = 6, message = "Password phải ít nhất 6 kí tự")	
	private String oldPassword;
	
	@NotBlank(message = "Password không được bỏ trống")
	@Size(min = 6, message = "Password phải ít nhất 6 kí tự")
	private String newPassword;

	public UserUpdateRequest() {
	}

	public UserUpdateRequest(String email, String phone, String fullName, String oldPassword, String newPassword) {
		this.email = email;
		this.phone = phone;
		this.fullName = fullName;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
