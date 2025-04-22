package com.example.tttn_restaurant.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.ui.LoginActivity;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailFragment extends Fragment {
    private TextView tvUsername, tvFullName, tvEmail, tvPhone, tvRole, tvCreatedAt, tvLastLogin;
    private Switch switchStatus;
    private Button btnEdit, btnChangeStatus, btnDelete;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserResponse currentUser, detailUser;
    private Long userId;

    public UserDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Cho phép fragment có menu options
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);

        try {
            sessionManager = new SessionManager(requireContext());

            if(!sessionManager.isLoggedIn()){
                Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                redirectToLogin();
                return view;
            }

            currentUser = sessionManager.getUser();

            if(currentUser == null){
                Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!!", Toast.LENGTH_SHORT).show();
                redirectToLogin();
                return view;
            }

            // Khởi tạo các thành phần UI
            tvUsername = view.findViewById(R.id.tvUsername);
            tvFullName = view.findViewById(R.id.tvFullName);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvPhone = view.findViewById(R.id.tvPhone);
            tvRole = view.findViewById(R.id.tvRole);
            tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
            tvLastLogin = view.findViewById(R.id.tvLastLogin);
            switchStatus = view.findViewById(R.id.switchStatus);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnChangeStatus = view.findViewById(R.id.btnChangeStatus);
            btnDelete = view.findViewById(R.id.btnDelete);
            progressBar = view.findViewById(R.id.progressBar);

            // Lấy userId từ Bundle arguments
            if (getArguments() != null) {
                userId = getArguments().getLong("userId", -1);
            }

            if (userId == -1) {
                // Nếu không có userId, lấy thông tin người dùng hiện tại
                userId = currentUser.getId();
            }

            btnEdit.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, UserEditFragment.newInstance(userId))
                        .addToBackStack(null)
                        .commit();
            });

            btnChangeStatus.setOnClickListener(v -> {
                if (detailUser != null) {
                    boolean newStatus = !detailUser.getIsActive();
                    updateUserStatus(newStatus);
                }
            });

            btnDelete.setOnClickListener(v -> {
                showDeleteConfirmationDialog();
            });
        } catch(Exception e) {
            Log.e("UserDetailFragment", "Error in onCreateView", e);
            Toast.makeText(requireContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

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
        activity.getSupportActionBar().setTitle("Chi tiết người dùng");
//        loadUserDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserDetails(); // Tải lại thông tin khi quay lại fragment
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed(); // Quay lại fragment trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserDetails() {
        if (!isAdded()) return; // Kiểm tra xem fragment đã được đính kèm chưa

        progressBar.setVisibility(View.VISIBLE);
        String token = sessionManager.getAuthToken();

        if(token == null || token.equals("Bearer null")){
            Toast.makeText(requireContext(), "Phiên đăng nhập đã hết hạn vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        RetrofitClient.getUserApiService().getUserById(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    detailUser = response.body();
                    displayUserDetails(detailUser);
                    configureUIForUserRole();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void displayUserDetails(UserResponse user) {
        if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

        tvUsername.setText(user.getUsername());
        tvFullName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());
        tvRole.setText(user.getRole());
        tvCreatedAt.setText(user.getCreatedAt());
        tvLastLogin.setText(user.getLastLogin() != null ? user.getLastLogin() : "Chưa đăng nhập");
        switchStatus.setChecked(user.getIsActive());
    }

    private void configureUIForUserRole() {
        if (!isAdded()) return; // Kiểm tra xem fragment còn gắn vào acttivity không, đảm bảo fragment này vẫn còn sống

        // Kiểm tra xem người dùng hiện tại có quyền chỉnh sửa thông tin người dùng này không
        boolean canEdit = false;
        boolean canChangeStatus = false;
        boolean canDelete = false;

        if (currentUser != null) {
            // Người dùng có thể chỉnh sửa thông tin của chính mình
            if (currentUser.getId().equals(userId)) {
                canEdit = true;
                canChangeStatus = false; // Không thể thay đổi trạng thái của chính mình
            }
            // ADMIN có thể chỉnh sửa và thay đổi trạng thái của bất kỳ ai
            else if ("ADMIN".equals(currentUser.getRole())) {
                canEdit = true;
                canChangeStatus = true;
                canDelete = true;
            }
            // MANAGER có thể chỉnh sửa và thay đổi trạng thái của các STAFF và CASHIER
            else if ("MANAGER".equals(currentUser.getRole()) &&
                    (detailUser != null && ("STAFF".equals(detailUser.getRole())))) {
                canEdit = true;
                canChangeStatus = true;
                canDelete = true;
            }
            // ADMIN có thể xóa bất kỳ ai trừ chính mình
            if ("ADMIN".equals(currentUser.getRole()) && !currentUser.getId().equals(userId)) {
                canDelete = true;
            }
        }

        btnEdit.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        btnChangeStatus.setVisibility(canChangeStatus ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
        switchStatus.setEnabled(false); // Switch là chỉ để hiển thị
    }

    private void updateUserStatus(boolean newStatus) {
        if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Boolean> statusMap = new HashMap<>();
        statusMap.put("isActive", newStatus);

        RetrofitClient.getUserApiService().updateUserStatus(userId, statusMap).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    detailUser = response.body();
                    displayUserDetails(detailUser);
                    Toast.makeText(requireContext(),
                            "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không, còn sống không

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng này? Nếu xóa là các giao dịch liên quan tới người dùng sẽ bị ảnh hưởng!!!")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser() {
        if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getUserApiService().deleteUser(userId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // Quay lại màn hình trước
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void redirectToLogin() {
        if (!isAdded()) return; // Kiểm tra xem fragment còn được đính kèm không

        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}