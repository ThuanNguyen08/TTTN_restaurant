package com.example.tttn_restaurant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.ErrorResponse;
import com.example.tttn_restaurant.model.ResetPasswordRequest;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    
    private EditText etEmail;
    private EditText etVerificationCode;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnResetPassword;
    private ProgressBar progressBar;
    private TextView tvBackToLogin;
    
    private String emailFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        
        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);
        
        // Nhận email từ intent nếu có
        emailFromIntent = getIntent().getStringExtra("email");
        
        // Ánh xạ các view
        etEmail = findViewById(R.id.etEmail);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        
        // Đặt email vào EditText nếu có
        if (emailFromIntent != null && !emailFromIntent.isEmpty()) {
            etEmail.setText(emailFromIntent);
        }
        
        // Xử lý sự kiện khi nhấn nút đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> {
            if (validateInput()) {
                resetPassword();
            }
        });
        
        // Xử lý sự kiện khi nhấn quay lại đăng nhập
        tvBackToLogin.setOnClickListener(v -> {
            // Chuyển về màn hình đăng nhập và xóa stack activity
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    
    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();
        String verificationCode = etVerificationCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Kiểm tra email không được để trống
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }
        
        // Kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập đúng định dạng email");
            etEmail.requestFocus();
            return false;
        }
        
        // Kiểm tra mã xác thực không được để trống
        if (verificationCode.isEmpty()) {
            etVerificationCode.setError("Vui lòng nhập mã xác thực");
            etVerificationCode.requestFocus();
            return false;
        }
        
        // Kiểm tra mật khẩu mới không được để trống
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return false;
        }
        
        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return false;
        }
        
        // Kiểm tra xác nhận mật khẩu không được để trống
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        // Kiểm tra mật khẩu mới và xác nhận mật khẩu phải giống nhau
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        String verificationCode = etVerificationCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        
        ResetPasswordRequest request = new ResetPasswordRequest(email, verificationCode, newPassword);
        
        showLoading(true);
        
        RetrofitClient.getUserApiService().resetPassword(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông báo từ response
                    String message = response.body().get("message");
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    
                    // Chuyển về màn hình đăng nhập sau khi đặt lại mật khẩu thành công
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);

                            // Hiển thị thông báo lỗi và GIỮ NGUYÊN tại màn hình reset password
                            String errorMessage = errorResponse.getMessage();
                            Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Không thể đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing error body", e);
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                showLoading(false);
                ErrorHandler.handleFailure(ResetPasswordActivity.this, t);
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!isLoading);
    }
}