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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.FoodAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodListFragment extends Fragment implements FoodAdapter.OnFoodClickListener {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddFood;
    private SessionManager sessionManager;
    private Spinner spnCategory;
    private Spinner spnStatus;

    private List<FoodResponse> foodList = new ArrayList<>();
    private List<CategoryResponse> categoryList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        sessionManager = new SessionManager(requireContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddFood = view.findViewById(R.id.fabAddFood);
        spnCategory = view.findViewById(R.id.spnCategory);
        spnStatus = view.findViewById(R.id.spnStatus);

        // Tải danh mục món ăn
        loadCategories();

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        foodAdapter = new FoodAdapter(foodList, this);
        recyclerView.setAdapter(foodAdapter);

        // Thiết lập Spinner trạng thái
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Tất cả", "Có sẵn", "Tạm hết", "Đã vô hiệu hóa"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterFoodsByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Chỉ ADMIN và MANAGER có thể thêm món ăn
        String userRole = sessionManager.getUser().getRole();
        if ("ADMIN".equals(userRole) || "MANAGER".equals(userRole)) {
            fabAddFood.setVisibility(View.VISIBLE);
        } else {
            fabAddFood.setVisibility(View.GONE);
        }

        fabAddFood.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddFoodFragment())
                    .addToBackStack(null)
                    .commit();
        });



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
        activity.getSupportActionBar().setTitle("Danh sách món ăn");
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
        loadFoods();
    }

    private void loadCategories() {
        RetrofitClient.getMenuApiService().getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();

                    // Thêm mục "Tất cả" vào đầu danh sách
                    CategoryResponse allCategory = new CategoryResponse();
                    allCategory.setId(0L);
                    allCategory.setName("Tất cả");
                    categoryList.add(allCategory);

                    // Thêm các danh mục khác
                    categoryList.addAll(response.body());

                    // Thiết lập Spinner danh mục
                    ArrayAdapter<CategoryResponse> categoryAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryList);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCategory.setAdapter(categoryAdapter);

                    spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            CategoryResponse selectedCategory = (CategoryResponse) parent.getItemAtPosition(position);
                            if (selectedCategory.getId() == 0) {
                                // "Tất cả" được chọn
                                loadFoods();
                            } else {
                                // Lọc theo danh mục
                                loadFoodsByCategory(selectedCategory.getId());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    // Tải tất cả món ăn sau khi đã tải danh mục
                    loadFoods();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                if (!isAdded()) return;
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void loadFoods() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getAllFoods().enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void loadFoodsByCategory(Long categoryId) {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getFoodsByCategory(categoryId).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void filterFoodsByStatus(String status) {
        if (status.equals("Tất cả")) {
            // Nếu category đã được chọn, giữ nguyên lựa chọn đó
            CategoryResponse selectedCategory = (CategoryResponse) spnCategory.getSelectedItem();
            if (selectedCategory != null && selectedCategory.getId() != 0) {
                loadFoodsByCategory(selectedCategory.getId());
            } else {
                loadFoods();
            }
            return;
        }

        if (status.equals("Đã vô hiệu hóa")) {
            loadDisabledFoods();
            return;
        }

        String apiStatus = status.equals("Có sẵn") ? "AVAILABLE" : "OUT_OF_STOCK";

        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getFoodsByStatus(apiStatus).enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void loadDisabledFoods() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getDisableFoods().enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    @Override
    public void onFoodClick(FoodResponse food) {
        // Mở FoodDetailFragment và truyền thông tin food
        FoodDetailFragment detailFragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putLong("foodId", food.getId());
        detailFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}