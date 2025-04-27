package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.ErrorResponse;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.model.UserUpdateRequest;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditFragment extends Fragment {

    private static final String TAG = "UserEditFragment";

    private EditText etEmail, etPhone, etFullName, etOldPassword, etNewPassword;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private Long userId;
    private UserResponse currentUser;

    public static UserEditFragment newInstance(Long userId) {
        UserEditFragment fragment = new UserEditFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit, container, false);

        // Ánh xạ các thành phần UI
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etFullName = view.findViewById(R.id.etFullName);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        progressBar = view.findViewById(R.id.progressBar);

        sessionManager = new SessionManager(requireContext());

        // Lấy ID người dùng từ arguments hoặc sử dụng ID của người dùng hiện tại
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", -1);
        }

        if (userId == -1) {
            // Nếu không có ID được truyền vào, lấy ID của người dùng hiện tại
            currentUser = sessionManager.getUser();
            if (currentUser != null) {
                userId = currentUser.getId();
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
                return view;
            }
        }

        // Lấy thông tin người dùng từ server
        loadUserData();

        btnCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnSave.setOnClickListener(v -> updateUserInfo());

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
        activity.getSupportActionBar().setTitle("Chỉnh sửa người dùng");
//        loadUserDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed(); // Quay lại fragment trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadUserData() {
        showLoading(true);
        // Nếu đang chỉnh sửa thông tin của chính mình
        if (userId.equals(sessionManager.getUser().getId())) {
            // Sử dụng endpoint getCurrentUser
            RetrofitClient.getUserApiService().getCurrentUser()
                    .enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null) {
                                populateUserData(response.body());
                            } else {
                                ErrorHandler.handleErrorResponse(requireContext(), response);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            showLoading(false);
                            ErrorHandler.handleFailure(requireContext(), t);
                        }
                    });
        } else {
            // Sử dụng endpoint getUserById
            RetrofitClient.getUserApiService().getUserById(userId)
                    .enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null) {
                                populateUserData(response.body());
                            } else {
                                ErrorHandler.handleErrorResponse(requireContext(), response);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            showLoading(false);
                            ErrorHandler.handleFailure(requireContext(), t);
                        }
                    });
        }
    }

    private void populateUserData(UserResponse user) {
        currentUser = user;
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());
        etFullName.setText(user.getFullName());
    }

    private void updateUserInfo() {
        // Kiểm tra validation
        if (!validateInputs()) {
            return;
        }

        showLoading(true);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail(etEmail.getText().toString().trim());
        updateRequest.setPhone(etPhone.getText().toString().trim());
        updateRequest.setFullName(etFullName.getText().toString().trim());

        // Chỉ cập nhật mật khẩu nếu người dùng nhập cả mật khẩu cũ và mới
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)) {
            updateRequest.setOldPassword(oldPassword);
            updateRequest.setNewPassword(newPassword);
        }

        RetrofitClient.getUserApiService().updateUser(userId, updateRequest)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse updatedUser = response.body();
                            // Nếu đang cập nhật thông tin của chính mình, cập nhật thông tin trong session
                            if (userId.equals(sessionManager.getUser().getId())) {
                                sessionManager.saveUser(updatedUser);
                            }
                            Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        showLoading(false);
                        ErrorHandler.handleFailure(requireContext(), t);
                    }
                });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được để trống");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        // Validate số điện thoại
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Số điện thoại không được để trống");
            isValid = false;
        } else if (!phone.matches("^0[0-9]{9}$")) {
            etPhone.setError("Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số");
            isValid = false;
        }

        // Validate họ tên
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Họ tên không được để trống");
            isValid = false;
        } else if (fullName.length() > 100) {
            etFullName.setError("Họ tên không được vượt quá 100 ký tự");
            isValid = false;
        }

        // Validate mật khẩu - chỉ kiểm tra nếu một trong hai trường mật khẩu được nhập
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();

        if (!TextUtils.isEmpty(oldPassword) || !TextUtils.isEmpty(newPassword)) {
            if (TextUtils.isEmpty(oldPassword)) {
                etOldPassword.setError("Vui lòng nhập mật khẩu cũ");
                isValid = false;
            }

            if (TextUtils.isEmpty(newPassword)) {
                etNewPassword.setError("Vui lòng nhập mật khẩu mới");
                isValid = false;
            } else if (newPassword.length() < 6) {
                etNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
                isValid = false;
            }
        }

        return isValid;
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);

                if (response.code() == 409) {
                    // Xử lý lỗi trùng lặp dữ liệu (email, số điện thoại đã tồn tại)
                    Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    // Xử lý lỗi validation
                    if (errorResponse.getValidationErrors() != null && !errorResponse.getValidationErrors().isEmpty()) {
                        // Hiển thị lỗi validation chi tiết cho từng trường
                        for (Map.Entry<String, String> error : errorResponse.getValidationErrors().entrySet()) {
                            String fieldName = error.getKey();
                            String errorMessage = error.getValue();

                            switch (fieldName) {
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
                                case "oldPassword":
                                    etOldPassword.setError(errorMessage);
                                    etOldPassword.requestFocus();
                                    break;
                                case "newPassword":
                                    etNewPassword.setError(errorMessage);
                                    etNewPassword.requestFocus();
                                    break;
                                default:
                                    // Nếu không map được field với EditText, hiển thị thông báo chung
                                    Toast.makeText(requireContext(), fieldName + ": " + errorMessage, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    } else {
                        // Hiển thị thông báo lỗi chung nếu không có chi tiết lỗi validation
                        Toast.makeText(requireContext(), errorResponse.getMessage() != null ?
                                        errorResponse.getMessage() : "Dữ liệu không hợp lệ",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Các lỗi khác
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            } else {
                Toast.makeText(requireContext(), "Có lỗi xảy ra: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Không thể phân tích phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
        }
    }


    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
    }
}