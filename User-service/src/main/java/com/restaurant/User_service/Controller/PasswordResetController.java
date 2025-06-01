package com.restaurant.User_service.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.User_service.DTO.ForgotPasswordRequest;
import com.restaurant.User_service.DTO.ResetPasswordRequest;
import com.restaurant.User_service.Service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/password")
@Tag(name = "Password Reset", description = "API quên mật khẩu và đặt lại mật khẩu")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    
    @Operation(summary = "Yêu cầu quên mật khẩu", description = "Gửi email với mã xác thực để đặt lại mật khẩu")
    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.processForgotPassword(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã xác thực đã được gửi đến email của bạn");
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu với mã xác thực")
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đặt lại mật khẩu thành công");
        return ResponseEntity.ok(response);
    }
    
}