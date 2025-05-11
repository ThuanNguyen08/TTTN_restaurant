package com.restaurant.User_service.DTO;

public class AuthResponse {
	private UserResponse user;
	private String token;
	private String refreshToken;

	public AuthResponse() {
	}

	public AuthResponse(UserResponse user, String token, String refreshToken){
		this.user = user;
		this.token = token;
		this.refreshToken = refreshToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public UserResponse getUser() {
		return user;
	}

	public void setUser(UserResponse user) {
		this.user = user;
	}

}