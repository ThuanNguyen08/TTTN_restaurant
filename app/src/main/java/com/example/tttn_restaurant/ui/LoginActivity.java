package com.example.tttn_restaurant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.ErrorResponse;
import com.example.tttn_restaurant.model.LoginResponse;
import com.example.tttn_restaurant.model.UserLoginRequest;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RetrofitClient.init(this);

        sessionManager = new SessionManager(this);

        // Kiểm tra nếu đã đăng nhập thì chuyển qua MainActivity
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        Log.d("DEBUG_PREF", "Token: " + sessionManager.getToken());
//        Log.d("DEBUG_PREF", "User: " + new Gson().toJson(sessionManager.getUser()));
//        Log.d("DEBUG_PREF", "IsLoggedIn: " + sessionManager.isLoggedIn());
//        Log.d("DEBUG_PREF", "refreshToken: " + sessionManager.getRefreshToken());

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError("Vui lòng nhập tên đăng nhập");
                etUsername.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                etPassword.requestFocus();
                return;
            }

            login(username, password);
        });
    }

    private void login(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        UserLoginRequest loginRequest = new UserLoginRequest(username, password);

        try {
            RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        sessionManager.saveToken(loginResponse.getToken());
                        sessionManager.saveRefreshToken(loginResponse.getRefreshToken());
                        sessionManager.saveUser(loginResponse.getUser());
                        sessionManager.setLoggedIn(true);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorJson = response.errorBody().string();
                                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
                                Toast.makeText(LoginActivity.this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e("LoginActivity", "Error parsing error body", e);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Log.e("LoginActivity", "Network error", t);
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("LoginActivity", "Exception during login: ", e);
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}