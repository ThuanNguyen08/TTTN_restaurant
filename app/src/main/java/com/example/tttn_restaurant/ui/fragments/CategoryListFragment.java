package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.CategoryAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCategory;
    private SessionManager sessionManager;
    private List<CategoryResponse> categoryList = new ArrayList<>();
    private Spinner spinnerCategoryFilter;
    private static final int FILTER_ALL = 0;
    private static final int FILTER_DISABLED = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);
        spinnerCategoryFilter = view.findViewById(R.id.spinnerCategoryFilter);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(categoryAdapter);

        setupSpinner();
        
        // Chỉ ADMIN và MANAGER có thể thêm danh mục
        String userRole = sessionManager.getUser().getRole();
        if ("ADMIN".equals(userRole) || "MANAGER".equals(userRole)) {
            fabAddCategory.setVisibility(View.VISIBLE);
        } else {
            fabAddCategory.setVisibility(View.GONE);
        }
        
        fabAddCategory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });
        
        return view;
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Tất cả", "Đã vô hiệu hóa"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(adapter);

        // Xử lý sự kiện khi người dùng chọn một mục trong spinner
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Tải danh sách dựa trên lựa chọn
                if (position == FILTER_ALL) {
                    loadCategories();
                } else if (position == FILTER_DISABLED) {
                    loadDisabledCategories();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Đặt tiêu đề cho ActionBar
        activity.getSupportActionBar().setTitle("Danh mục món ăn");
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }
    
    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getMenuApiService().getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                if (!isAdded()) return;
                
                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void loadDisabledCategories() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getDisableCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }
    
    @Override
    public void onCategoryClick(CategoryResponse category) {
        // Mở CategoryDetailFragment và truyền thông tin category
        CategoryDetailFragment detailFragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putLong("categoryId", category.getId());
        detailFragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}