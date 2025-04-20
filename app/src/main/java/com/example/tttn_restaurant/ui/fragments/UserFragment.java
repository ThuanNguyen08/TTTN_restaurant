package com.example.tttn_restaurant.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private static final String TAG = "UserFragment";
    private SessionManager sessionManager;
    private TextView tvWelcome, tvRole;
    private Button btnUserList, btnProfile, btnRegister;
    private LinearLayout ll1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        sessionManager = new SessionManager(requireContext());

        // Kiểm tra nếu chưa đăng nhập thì chuyển qua màn hình đăng nhập
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return view;
        }

        // Xác thực token với server
//        validateTokenWithServer();

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvRole = view.findViewById(R.id.tvRole);
        btnUserList = view.findViewById(R.id.btnUserList);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnRegister = view.findViewById(R.id.btnRegister);
        ll1 = view.findViewById(R.id.ll1);

        // Lấy thông tin người dùng đã đăng nhập
        UserResponse user = sessionManager.getUser();
        if (user != null) {
            tvWelcome.setText("Xin chào, " + user.getFullName());
            tvRole.setText("Vai trò: " + user.getRole());

            // Chỉ hiển thị nút Đăng ký người dùng mới cho ADMIN và MANAGER
            if ("ADMIN".equals(user.getRole()) || "MANAGER".equals(user.getRole())) {
                btnRegister.setVisibility(View.VISIBLE);
                btnUserList.setVisibility(View.VISIBLE);
            } else {
                btnRegister.setVisibility(View.GONE);
                btnUserList.setVisibility(View.GONE);
                ll1.setVisibility(View.GONE);
            }
        } else {
            logout();
        }

        btnUserList.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserListFragment())
                    .addToBackStack(null)  // Để có thể quay lại fragment trước đó
                    .commit();
        });

        btnRegister.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnProfile.setOnClickListener(v -> {
            UserResponse currentUser = sessionManager.getUser();
            if(currentUser != null){
                // Tạo UserDetailFragment và truyền tham số
                UserDetailFragment detailFragment = new UserDetailFragment();
                Bundle args = new Bundle();
                args.putLong("userId", currentUser.getId());
                detailFragment.setArguments(args);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy người dùng, vui lòng đăng nhập lại nhé!", Toast.LENGTH_SHORT).show();
                logout();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Đặt tiêu đề cho ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý người dùng");

        // Ẩn nút back khi ở UserFragment
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Cập nhật lại ActionBar mỗi khi fragment được hiển thị lại
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý người dùng");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void logout() {
        sessionManager.clearSession();
        Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        sessionManager.clearSession();  // Xóa thông tin phiên
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void validateTokenWithServer() {
        RetrofitClient.getApiService().getCurrentUser().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Token hợp lệ, cập nhật thông tin người dùng nếu cần
                    sessionManager.saveUser(response.body());
                    // cập nhật UI
                    updateUI();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "Không thể kết nối đến server: " + t.getMessage());
                // Không chuyển hướng đến đăng nhập ngay trong trường hợp lỗi mạng
                // Chỉ hiển thị thông báo và để người dùng tiếp tục sử dụng với dữ liệu offline
                Toast.makeText(requireContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (!isAdded()) {
            return; // Tránh crash khi fragment không còn được gắn vào activity
        }

        UserResponse user = sessionManager.getUser();
        if (user != null) {
            tvWelcome.setText("Xin chào, " + user.getFullName());
            tvRole.setText("Vai trò: " + user.getRole());

            // Chỉ hiển thị nút Đăng ký người dùng mới cho ADMIN và MANAGER
            if ("ADMIN".equals(user.getRole()) || "MANAGER".equals(user.getRole())) {
                btnRegister.setVisibility(View.VISIBLE);
                btnUserList.setVisibility(View.VISIBLE);
                ll1.setVisibility(View.VISIBLE);
            } else {
                btnRegister.setVisibility(View.GONE);
                btnUserList.setVisibility(View.GONE);
                ll1.setVisibility(View.GONE);
            }
        }
    }
}