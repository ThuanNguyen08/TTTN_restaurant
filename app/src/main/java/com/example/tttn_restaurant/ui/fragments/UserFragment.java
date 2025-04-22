package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.utils.SessionManager;

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

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvRole = view.findViewById(R.id.tvRole);
        btnUserList = view.findViewById(R.id.btnUserList);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnRegister = view.findViewById(R.id.btnRegister);
        ll1 = view.findViewById(R.id.ll1);

        updateUI();

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
            // Tạo UserDetailFragment và truyền tham số
            UserDetailFragment detailFragment = new UserDetailFragment();
            Bundle args = new Bundle();
            args.putLong("userId", currentUser.getId());
            detailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
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