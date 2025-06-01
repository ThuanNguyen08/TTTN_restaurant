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
import com.example.tttn_restaurant.model.ForgotPasswordRequest;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private EditText etEmail;
    private Button btnSendCode;
    private ProgressBar progressBar;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        // Ánh xạ các view
        etEmail = findViewById(R.id.etEmail);
        btnSendCode = findViewById(R.id.btnSendCode);
        progressBar = findViewById(R.id.progressBar);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Xử lý sự kiện khi nhấn nút gửi mã xác thực
        btnSendCode.setOnClickListener(v -> {
            if (validateInput()) {
                requestVerificationCode();
            }
        });

        // Xử lý sự kiện khi nhấn quay lại đăng nhập
        tvBackToLogin.setOnClickListener(v -> {
            finish(); // Trở lại màn hình trước đó (LoginActivity)
        });
    }

    private boolean validateInput() {
        String email = etEmail.getText().toString().trim();

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

        return true;
    }

    private void requestVerificationCode() {
        String email = etEmail.getText().toString().trim();
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        showLoading(true);

        RetrofitClient.getUserApiService().forgotPassword(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông báo từ response
                    String message = response.body().get("message");
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                    // Chuyển sang màn hình đặt lại mật khẩu
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();

                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);

                            // Kiểm tra xem lỗi có phải là "Mã xác thực đã được gửi đến email" không
                            String errorMessage = errorResponse.getMessage();
                            if (errorMessage != null && (
                                    errorMessage.contains("Mã xác thực đã được gửi") ||
                                            errorMessage.contains("Vui lòng kiểm tra"))) {

                                // Nếu có, chuyển thẳng đến màn hình reset password
                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            }

                            Toast.makeText(ForgotPasswordActivity.this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Không thể gửi mã xác thực", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing error body", e);
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                showLoading(false);
                ErrorHandler.handleFailure(ForgotPasswordActivity.this, t);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSendCode.setEnabled(!isLoading);
    }
}