package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.ErrorResponse;
import com.example.tttn_restaurant.model.UserRegistrationRequest;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUserFragment extends Fragment {
    private EditText etUsername, etPassword, etConfirmPassword, etFullName, etEmail, etPhone;
    private Spinner spinnerRole;
    private Button btnRegister;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, container, false);

        sessionManager = new SessionManager(requireContext());

        // Ánh xạ các thành phần UI
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        spinnerRole = view.findViewById(R.id.spinnerRole);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressBar = view.findViewById(R.id.progressBar);

        setupRoleSpinner();

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Đặt tiêu đề cho ActionBar
        activity.getSupportActionBar().setTitle("Đăng ký người dùng");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Cho phép fragment có menu options
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed(); // Quay lại fragment trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRoleSpinner() {
        List<String> roles = new ArrayList<>();
        UserResponse currentUser = sessionManager.getUser();

        // ADMIN có thể tạo tất cả các role
        if ("ADMIN".equals(currentUser.getRole())) {
            roles = Arrays.asList("ADMIN", "MANAGER", "STAFF");
        }
        // MANAGER chỉ có thể tạo STAFF
        else if ("MANAGER".equals(currentUser.getRole())) {
            roles = Arrays.asList("STAFF");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private boolean validateInputs() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Kiểm tra username
        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return false;
        }

        // Kiểm tra password
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        // Kiểm tra confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Kiểm tra fullName
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return false;
        }

        // Kiểm tra email
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        // Kiểm tra phone (không bắt buộc)
        if (!phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Vui lòng nhập số điện thoại hợp lệ");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        UserRegistrationRequest request = new UserRegistrationRequest(username, password, email, phone, fullName, role);
        RetrofitClient.getApiService().register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Đăng ký người dùng thành công", Toast.LENGTH_LONG).show();
                    // Quay lại fragment trước đó
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);

                            if (errorResponse.getValidationErrors() != null && !errorResponse.getValidationErrors().isEmpty()) {
                                // Hiển thị lỗi validation cụ thể
                                for (Map.Entry<String, String> error : errorResponse.getValidationErrors().entrySet()) {
                                    String fieldName = error.getKey();
                                    String errorMessage = error.getValue();

                                    switch (fieldName) {
                                        case "username":
                                            etUsername.setError(errorMessage);
                                            etUsername.requestFocus();
                                            break;
                                        case "password":
                                            etPassword.setError(errorMessage);
                                            etPassword.requestFocus();
                                            break;
                                        case "email":
                                            etEmail.setError(errorMessage);
                                            etEmail.requestFocus();
                                            break;
                                        case "phone":
                                            etPhone.setError(errorMessage);
                                            etPhone.requestFocus();
                                            break;
                                        case "fullName":
                                            etFullName.setError(errorMessage);
                                            etFullName.requestFocus();
                                            break;
                                        default:
                                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("RegisterUserFragment", "Error parsing error body", e);
                        Toast.makeText(requireContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Log.e("RegisterUserFragment", "Network error", t);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}