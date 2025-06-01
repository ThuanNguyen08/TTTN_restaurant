package com.restaurant.User_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    
    @NotBlank(message = "Email không được để trống")
    private String email;
    
    @NotBlank(message = "Mã xác thực không được để trống")
    private String verificationCode;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;

    public ResetPasswordRequest() {
    }

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