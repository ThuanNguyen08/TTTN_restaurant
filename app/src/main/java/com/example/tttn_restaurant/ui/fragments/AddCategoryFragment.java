package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
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
import com.example.tttn_restaurant.model.CategoryRequest;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryFragment extends Fragment {
    
    private EditText etCategoryName;
    private Button btnSave;
    private ProgressBar progressBar;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        
        etCategoryName = view.findViewById(R.id.etCategoryName);
        btnSave = view.findViewById(R.id.btnSave);
        progressBar = view.findViewById(R.id.progressBar);
        
        btnSave.setOnClickListener(v -> saveCategory());
        
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
        activity.getSupportActionBar().setTitle("Thêm danh mục mới");
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();
        
        if (categoryName.isEmpty()) {
            etCategoryName.setError("Vui lòng nhập tên danh mục");
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        
        CategoryRequest request = new CategoryRequest(categoryName);
        
        RetrofitClient.getMenuApiService().createCategory(request).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // Quay lại danh sách danh mục
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }
}