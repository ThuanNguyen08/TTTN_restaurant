// Cập nhật OrderFoodFragment.java

package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.OrderFoodAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.Bill;
import com.example.tttn_restaurant.model.BillItemDto;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.model.DetailBill;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.model.TableBillRequest;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.TableStatus;
import com.example.tttn_restaurant.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFoodFragment extends Fragment {
    private static final String TAG = "OrderFoodFragment";

    private TableResponse table;
    private RecyclerView recyclerViewFoods;
    private ProgressBar progressBar;
    private Button btnConfirmOrder;
    private TextView tvTableInfo;
    private Spinner spinnerCategory;
    private Spinner spinnerOrderStatus;
    private OrderFoodAdapter adapter;

    private List<FoodResponse> foodList = new ArrayList<>();
    private List<FoodResponse> filteredFoodList = new ArrayList<>();
    private List<CategoryResponse> categoryList = new ArrayList<>();
    private Map<Long, Integer> selectedFoods = new HashMap<>();  // FoodId -> Quantity

    private Long currentCategoryId = null; // Để lưu danh mục đang được chọn
    private boolean showOnlyOrdered = false; // Để lưu trạng thái hiển thị

    public static OrderFoodFragment newInstance(TableResponse table) {
        OrderFoodFragment fragment = new OrderFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable("table", table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            table = (TableResponse) getArguments().getSerializable("table");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_food, container, false);

        // Khởi tạo các view
        recyclerViewFoods = view.findViewById(R.id.recyclerViewFoods);
        progressBar = view.findViewById(R.id.progressBar);
        btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);
        tvTableInfo = view.findViewById(R.id.tvTableInfo);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerOrderStatus = view.findViewById(R.id.spinnerOrderStatus);

        // Hiển thị thông tin bàn
        if (table != null) {
            tvTableInfo.setText("Đặt món cho bàn: " + table.getName());
        }

        // Thiết lập RecyclerView
        recyclerViewFoods.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new OrderFoodAdapter(filteredFoodList, selectedFoods, new OrderFoodAdapter.OnFoodQuantityChangeListener() {
            @Override
            public void onQuantityChanged(FoodResponse food, int quantity) {
                if (quantity > 0) {
                    selectedFoods.put(food.getId(), quantity);
                } else {
                    selectedFoods.remove(food.getId());
                }
                updateConfirmButtonState();

                // Cập nhật lại danh sách nếu đang lọc theo món đã đặt
                if (showOnlyOrdered) {
                    applyFilters();
                }
            }
        });
        recyclerViewFoods.setAdapter(adapter);

        // Thiết lập spinner trạng thái đặt món
        setupOrderStatusSpinner();

        // Xử lý sự kiện nút xác nhận đặt món
        btnConfirmOrder.setOnClickListener(v -> confirmOrder());
        updateConfirmButtonState();

        // Tải danh sách danh mục trước
        loadCategories();

        return view;
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Tất cả danh mục"); // Thêm lựa chọn "Tất cả"

        for (CategoryResponse category : categoryList) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Chọn "Tất cả danh mục"
                    currentCategoryId = null;
                } else {
                    // Chọn một danh mục cụ thể
                    currentCategoryId = categoryList.get(position - 1).getId();
                }
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentCategoryId = null;
                applyFilters();
            }
        });
    }

    private void setupOrderStatusSpinner() {
        String[] orderStatus = {"Tất cả món", "Các món đã đặt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                orderStatus
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderStatus.setAdapter(adapter);

        spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showOnlyOrdered = position == 1; // Vị trí 1 là "Các món đã đặt"
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showOnlyOrdered = false;
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        filteredFoodList.clear();

        for (FoodResponse food : foodList) {
            // Lọc theo danh mục
            boolean matchCategory = (currentCategoryId == null) || food.getCategory().getId().equals(currentCategoryId);

            // Lọc theo trạng thái đặt món
            boolean matchOrderStatus = !showOnlyOrdered || selectedFoods.containsKey(food.getId());

            if (matchCategory && matchOrderStatus) {
                filteredFoodList.add(food);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Đặt tiêu đề cho ActionBar
        activity.getSupportActionBar().setTitle("Quản lý đặt bàn");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getMenuApiService().getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    setupCategorySpinner();

                    // Sau khi tải danh mục xong, tiếp tục tải món ăn
                    loadFoods();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải danh mục món ăn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoods() {
        RetrofitClient.getMenuApiService().getFoodsByStatus("AVAILABLE").enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                if (response.isSuccessful() && response.body() != null) {
                    foodList.clear();
                    foodList.addAll(response.body());

                    // Áp dụng bộ lọc và cập nhật adapter
                    applyFilters();

                    if (table != null) {
                        loadCurrentBill();
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải danh sách món ăn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentBill() {
        if (table == null) return;

        progressBar.setVisibility(View.VISIBLE);
        // Kiểm tra xem bàn đã có bill chưa
        RetrofitClient.getRevenueApiService().getPendingBills().enqueue(new Callback<List<Bill>>() {
            @Override
            public void onResponse(Call<List<Bill>> call, Response<List<Bill>> response) {
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                if (response.isSuccessful() && response.body() != null) {
                    // Tìm bill của bàn hiện tại
                    for (Bill bill : response.body()) {
                        if (bill.getTableId().equals(table.getId())) {
                            // Tìm thấy bill, load các món ăn trong bill
                            loadBillItems(bill.getId());
                            return;
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải thông tin bill: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBillItems(Long billId) {
        RetrofitClient.getRevenueApiService().getBillItems(billId).enqueue(new Callback<List<DetailBill>>() {
            @Override
            public void onResponse(Call<List<DetailBill>> call, Response<List<DetailBill>> response) {
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    selectedFoods.clear(); // Xóa dữ liệu cũ

                    for (DetailBill item : response.body()) {
                        selectedFoods.put(item.getFoodId(), item.getQuantity());
                    }
                    // Tạo một bản sao của map để cập nhật vào adapter
                    Map<Long, Integer> updatedMap = new HashMap<>(selectedFoods);

                    // Sau khi cập nhật selectedFoods, cần thông báo cho adapter
                    if (adapter != null) {
                        adapter.updateQuantities(updatedMap);
                        updateConfirmButtonState();
                        // Áp dụng lại bộ lọc để cập nhật nếu đang chọn "Các món đã đặt"
                        applyFilters();
                    }
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<DetailBill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải chi tiết bill: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateConfirmButtonState() {
        btnConfirmOrder.setEnabled(!selectedFoods.isEmpty());
    }

    private void confirmOrder() {
        if (table == null) {
            Toast.makeText(getContext(), "Không có thông tin bàn", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFoods.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một món", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Tạo request
        List<BillItemDto> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : selectedFoods.entrySet()) {
            items.add(new BillItemDto(entry.getKey(), entry.getValue()));
        }

        TableBillRequest request = new TableBillRequest(table.getId(), items);

        // Gọi API để tạo hoặc cập nhật bill
        RetrofitClient.getRevenueApiService().createOrUpdateBill(request).enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật trạng thái bàn thành OCCUPIED nếu bàn chưa có khách
                    if (!table.getStatus().equals(TableStatus.OCCUPIED)) {
                        updateTableStatus(table.getId(), TableStatus.OCCUPIED);
                    }
                    Toast.makeText(getContext(), "Đặt món thành công", Toast.LENGTH_SHORT).show();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Đặt món thất bại: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTableStatus(Long tableId, String status) {
        RetrofitClient.getTableApiService().updateTableStatus(tableId, status).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                // Kiểm tra xem Fragment có còn gắn với Activity không
                if (!isAdded()) {
                    return; // Thoát khỏi callback nếu Fragment đã bị detach
                }
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Đặt món thành công", Toast.LENGTH_SHORT).show();
                    // Quay lại fragment trước đó
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<TableResponse> call, Throwable t) {
                Log.e(TAG, "Không thể cập nhật trạng thái bàn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}