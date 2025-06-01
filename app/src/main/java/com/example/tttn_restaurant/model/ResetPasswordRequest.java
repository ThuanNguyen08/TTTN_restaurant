package com.example.tttn_restaurant.model;

public class ResetPasswordRequest {
    private String email;
    private String verificationCode;
    private String newPassword;

    public ResetPasswordRequest(String email, String verificationCode, String newPassword) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.newPassword = newPassword;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}