package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.utils.SessionManager;

public class MenuFragment extends Fragment {

    private Button btnFoodList;
    private Button btnCategoryList;
    private Button btnAddFood;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Đặt tiêu đề cho ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý thực đơn");

        // Ẩn nút back khi ở MenuFragment
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);

        // Ánh xạ các button từ layout
        btnFoodList = view.findViewById(R.id.btnFoodList);
        btnCategoryList = view.findViewById(R.id.btnCategoryList);
        btnAddFood = view.findViewById(R.id.btnAddFood);

        sessionManager = new SessionManager(requireContext());
        String role = sessionManager.getUser().getRole();
        if(("STAFF").equals(role)){
            btnAddFood.setVisibility(View.GONE);
        }

        // Thiết lập sự kiện click cho button danh sách món ăn
        btnFoodList.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FoodListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Thiết lập sự kiện click cho button quản lý danh mục
        btnCategoryList.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CategoryListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Thiết lập sự kiện click cho button thêm món ăn mới
        btnAddFood.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddFoodFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Cập nhật lại ActionBar mỗi khi fragment được hiển thị lại
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý thực đơn");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
}