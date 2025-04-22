package com.example.tttn_restaurant.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.tttn_restaurant.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailFragment extends Fragment {
    
    private TextView tvCategoryName, tvStatus, tvCreatedAt;
    private Button btnEdit, btnDisable, btnDelete;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private Long categoryId;
    private CategoryResponse category;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        // Lấy categoryId từ Bundle arguments
        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getLong("categoryId", -1);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        // Khởi tạo views
        tvCategoryName = view.findViewById(R.id.tvCategoryName);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnDisable = view.findViewById(R.id.btnDisable);
        btnDelete = view.findViewById(R.id.btnDelete);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Kiểm tra quyền người dùng
        String userRole = sessionManager.getUser().getRole();
        boolean canEditCategory = "ADMIN".equals(userRole) || "MANAGER".equals(userRole);
        
        btnEdit.setVisibility(canEditCategory ? View.VISIBLE : View.GONE);
        btnDisable.setVisibility(canEditCategory ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(canEditCategory ? View.VISIBLE : View.GONE);
        
        // Set onClick listeners
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnDisable.setOnClickListener(v -> disableCategory());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        
        // Tải dữ liệu danh mục
        if (categoryId != -1) {
            loadCategoryDetails();
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
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
        activity.getSupportActionBar().setTitle("Chi tiết danh mục");
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadCategoryDetails() {
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getMenuApiService().getCategoryById(categoryId).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!isAdded()) return;  // Kiểm tra fragment vẫn được attach
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    category = response.body();
                    updateUI();
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
    
    private void updateUI() {
        if (category != null) {
            tvCategoryName.setText(category.getName());
            tvStatus.setText(category.isActive() ? "Trạng thái: Hoạt động" : "Trạng thái: Không hoạt động");
            tvCreatedAt.setText("Ngày tạo: " + category.getCreatedAt());
            
            // Cập nhật text cho nút chuyển đổi trạng thái
            btnDisable.setText(category.isActive() ? "Vô hiệu hóa" : "Kích hoạt");
        }
    }
    
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa danh mục");
        
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_category, null);
        EditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        etCategoryName.setText(category.getName());
        
        builder.setView(dialogView);
        
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = etCategoryName.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateCategory(newName);
            } else {
                Toast.makeText(requireContext(), "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        
        builder.create().show();
    }
    
    private void updateCategory(String newName) {
        progressBar.setVisibility(View.VISIBLE);
        
        CategoryRequest request = new CategoryRequest(newName);
        
        RetrofitClient.getMenuApiService().updateCategory(categoryId, request).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    category = response.body();
                    updateUI();
                    Toast.makeText(requireContext(), "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
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

    private void disableCategory() {
        progressBar.setVisibility(View.VISIBLE);

        if (category.isActive()) {
            // Vô hiệu hóa danh mục
            RetrofitClient.getMenuApiService().disableCategory(categoryId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        // Tải lại thông tin danh mục sau khi cập nhật trạng thái
                        loadCategoryDetails();
                        Toast.makeText(requireContext(), "Vô hiệu hóa danh mục thành công", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        ErrorHandler.handleErrorResponse(requireContext(), response);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleFailure(requireContext(), t);
                }
            });
        } else {
            // Kích hoạt lại danh mục
            RetrofitClient.getMenuApiService().restoreCategory(categoryId).enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        // Tải lại thông tin danh mục sau khi cập nhật trạng thái
                        loadCategoryDetails();
                        Toast.makeText(requireContext(), "Kích hoạt danh mục thành công", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
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
    
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategory())
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteCategory() {
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getMenuApiService().deleteCategory(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }
}