package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.UserAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListFragment extends Fragment implements UserAdapter.OnUserClickListener {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private SessionManager sessionManager;
    private List<UserResponse> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        sessionManager = new SessionManager(requireContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        // Chỉ hiển thị nút thêm người dùng cho ADMIN và MANAGER
        UserResponse currentUser = sessionManager.getUser();
        if (currentUser != null && ("ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole()))) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        fabAdd.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Cho phép fragment có menu options
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Đặt tiêu đề cho ActionBar
        activity.getSupportActionBar().setTitle("Danh sách người dùng");

//        loadUsers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed(); // Quay lại fragment trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers(); // Tải lại danh sách khi quay lại fragment
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getUserApiService().getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (!isAdded()) return; // Kiểm tra fragment còn được gắn vào activity không

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                if (!isAdded()) return; // Kiểm tra fragment còn được gắn vào activity không
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    @Override
    public void onUserClick(UserResponse user) {
        UserDetailFragment detailFragment = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putLong("userId", user.getId());
        detailFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}