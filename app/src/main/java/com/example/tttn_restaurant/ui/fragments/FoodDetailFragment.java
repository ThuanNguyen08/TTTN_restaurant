package com.example.tttn_restaurant.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailFragment extends Fragment {
    
    private ImageView imgFood;
    private TextView tvFoodName, tvPrice, tvDescription, tvCategory, tvStatus, tvCreatedAt;
    private Button btnEdit, btnToggleStatus, btnDisable, btnDelete;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private Long foodId;
    private FoodResponse food;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        // Lấy foodId từ Bundle arguments
        Bundle args = getArguments();
        if (args != null) {
            foodId = args.getLong("foodId", -1);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        // Khởi tạo views
        imgFood = view.findViewById(R.id.imgFood);
        tvFoodName = view.findViewById(R.id.tvFoodName);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnToggleStatus = view.findViewById(R.id.btnToggleStatus);
        btnDisable = view.findViewById(R.id.btnDisable);
        btnDelete = view.findViewById(R.id.btnDelete);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Kiểm tra quyền người dùng
        String userRole = sessionManager.getUser().getRole();
        boolean canEditFood = "ADMIN".equals(userRole) || "MANAGER".equals(userRole);
        
        btnEdit.setVisibility(canEditFood ? View.VISIBLE : View.GONE);
        btnToggleStatus.setVisibility(canEditFood ? View.VISIBLE : View.GONE);
        btnDisable.setVisibility(canEditFood ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(canEditFood ? View.VISIBLE : View.GONE);
        
        // Set onClick listeners
        btnEdit.setOnClickListener(v -> editFood());
        btnToggleStatus.setOnClickListener(v -> changeFoodStatus());
        btnDisable.setOnClickListener(v -> disableFood());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        
        // Tải dữ liệu món ăn
        if (foodId != -1) {
            loadFoodDetails();
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
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
        activity.getSupportActionBar().setTitle("Chi tiết món ăn");
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadFoodDetails() {
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getMenuApiService().getFoodById(foodId).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    food = response.body();
                    updateUI();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }
    
    private void updateUI() {
        if (food != null) {
            // Định dạng giá tiền theo VND
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            tvFoodName.setText(food.getName());
            tvPrice.setText(currencyFormatter.format(food.getPrice()));
            tvDescription.setText(food.getDescription());
            tvCategory.setText("Danh mục: " + food.getCategory().getName());
            tvCreatedAt.setText("Ngày tạo: " + food.getCreatedAt());
            
            // Hiển thị trạng thái
            String status = "";
            int statusColor = R.color.black;
            
            if (!food.getActive()) {
                status = "Đã vô hiệu hóa";
                statusColor = R.color.red;
                btnDisable.setText("Kích hoạt lại");
                btnToggleStatus.setVisibility(View.GONE);
            } else {
                btnDisable.setText("Vô hiệu hóa");
                btnToggleStatus.setVisibility(View.VISIBLE);
                
                if ("OUT_OF_STOCK".equals(food.getStatus())) {
                    status = "Trạng thái: Tạm hết";
                    statusColor = R.color.orange;
                    btnToggleStatus.setText("Đánh dấu có sẵn");
                } else {
                    status = "Trạng thái: Có sẵn";
                    statusColor = R.color.green;
                    btnToggleStatus.setText("Đánh dấu tạm hết");
                }
            }
            
            tvStatus.setText(status);
            tvStatus.setTextColor(requireContext().getResources().getColor(statusColor));
            
            // Hiển thị hình ảnh từ Base64
            if (food.getImageBitmap() != null) {
                imgFood.setImageBitmap(food.getImageBitmap());
            } else {
                imgFood.setImageResource(R.drawable.food_placeholder); // Thay bằng drawable mặc định của bạn
            }
        }
    }
    
    private void editFood() {
        // Chuyển sang EditFoodFragment để chỉnh sửa món ăn
        EditFoodFragment editFragment = new EditFoodFragment();
        Bundle args = new Bundle();
        args.putLong("foodId", foodId);
        editFragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void changeFoodStatus() {
        progressBar.setVisibility(View.VISIBLE);
        
        String newStatus = "AVAILABLE";
        if ("AVAILABLE".equals(food.getStatus())) {
            newStatus = "OUT_OF_STOCK";
        }
        
        final String statusToUpdate = newStatus;
        
        RetrofitClient.getMenuApiService().updateFoodStatus(foodId, statusToUpdate).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    food = response.body();
                    updateUI();
                    
                    String message = "OUT_OF_STOCK".equals(statusToUpdate) ?
                            "Đã đánh dấu món ăn là 'Tạm hết'" :
                            "Đã đánh dấu món ăn là 'Có sẵn'";
                    
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void disableFood() {
        progressBar.setVisibility(View.VISIBLE);

        if (food.getActive()) {
            // Vô hiệu hóa món ăn
            RetrofitClient.getMenuApiService().disableFood(foodId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        // Tải lại thông tin món ăn sau khi cập nhật trạng thái
                        loadFoodDetails();
                        Toast.makeText(requireContext(), "Vô hiệu hóa món ăn thành công", Toast.LENGTH_SHORT).show();
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
            // Kích hoạt lại món ăn
            RetrofitClient.getMenuApiService().restoreFood(foodId).enqueue(new Callback<FoodResponse>() {
                @Override
                public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            food = response.body(); // Cập nhật đối tượng food với dữ liệu mới
                            updateUI(); // Cập nhật giao diện người dùng
                        } else {
                            // Nếu không có dữ liệu trả về, tải lại thông tin
                            loadFoodDetails();
                        }
                        Toast.makeText(requireContext(), "Kích hoạt món ăn thành công", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        ErrorHandler.handleErrorResponse(requireContext(), response);
                    }
                }

                @Override
                public void onFailure(Call<FoodResponse> call, Throwable t) {
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
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteFood())
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteFood() {
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getMenuApiService().deleteFood(foodId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
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