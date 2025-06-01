package com.restaurant.User_service.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "verification_codes")
public class VerificationCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String verificationCode;

	@Column(name = "expiry_date", nullable = false)
	private LocalDateTime expiryDate;

	@Column(name = "is_used", nullable = false)
	private boolean isUsed = false;

	// Thời gian hiệu lực của token (15 phút)
	public static final int EXPIRATION_MINUTES = 15;

	public VerificationCode() {
	}

	public VerificationCode(String email, String verificationCode) {
		this.email = email;
		this.verificationCode = verificationCode;
		this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expiryDate);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
}