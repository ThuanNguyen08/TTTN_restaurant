package com.example.tttn_restaurant.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.BillDetailAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.Bill;
import com.example.tttn_restaurant.model.DetailBill;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.view.MenuItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillDetailFragment extends Fragment {
    private static final String ARG_BILL_ID = "billId";
    
    private Long billId;
    private TextView tvBillId, tvCustomerName, tvCustomerPhone, tvStatus, tvPaymentMethod, tvNote;
    private TextView tvTableName, tvDateCreate, tvDatePaidAt, tvTotalAmount, tvDiscount, tvFinalAmount;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private BillDetailAdapter adapter;
    
    private List<DetailBill> billItems = new ArrayList<>();
    private Bill currentBill;
    private NumberFormat currencyFormat;

    private Map<Long, String> foodNames = new HashMap<>();
    
    public static BillDetailFragment newInstance(Long billId) {
        BillDetailFragment fragment = new BillDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BILL_ID, billId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            billId = getArguments().getLong(ARG_BILL_ID);
        }
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        setHasOptionsMenu(true);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);
        
        // Ánh xạ view
        tvBillId = view.findViewById(R.id.tvBillId);
        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvCustomerPhone = view.findViewById(R.id.tvCustomerPhone);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        tvTableName = view.findViewById(R.id.tvTableName);
        tvDateCreate = view.findViewById(R.id.tvDateCreate);
        tvDatePaidAt = view.findViewById(R.id.tvDatePaidAt);
        tvNote = view.findViewById(R.id.tvNote);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvFinalAmount = view.findViewById(R.id.tvFinalAmount);
        recyclerViewItems = view.findViewById(R.id.recyclerViewItems);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Thiết lập RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BillDetailAdapter(billItems, foodNames);
        recyclerViewItems.setAdapter(adapter);
        loadFoodData();

        // Tải dữ liệu
        loadBillDetail();
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Chi tiết hóa đơn");
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBillDetail() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getRevenueApiService().getBillById(billId).enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBill = response.body();
                    displayBillInfo();

                    // Tải chi tiết các món ăn
                    loadBillItems();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadBillItems() {
        RetrofitClient.getRevenueApiService().getBillItems(billId).enqueue(new Callback<List<DetailBill>>() {
            @Override
            public void onResponse(Call<List<DetailBill>> call, Response<List<DetailBill>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    billItems.clear();
                    billItems.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<List<DetailBill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayBillInfo() {
        tvBillId.setText("Hóa đơn #" + currentBill.getId());
        tvCustomerName.setText(currentBill.getCustomerName());
        tvCustomerPhone.setText(currentBill.getCustomerPhone());
        tvStatus.setText(getStatusText(currentBill.getStatus()));
        tvPaymentMethod.setText(getPaymentMethodText(currentBill.getPaymentMethod() != null ? currentBill.getPaymentMethod() : Bill.PaymentMethod.NULL));
        tvDateCreate.setText(currentBill.getCreatedAt());
        tvDatePaidAt.setText(currentBill.getPaidAt());
        tvNote.setText(currentBill.getNote());
        tvTotalAmount.setText(currencyFormat.format(currentBill.getTotalPrice()));
        tvDiscount.setText(currencyFormat.format(currentBill.getDiscountAmount()));
        tvFinalAmount.setText(currencyFormat.format(currentBill.getFinalPrice()));
        
        // Tải thông tin bàn
        loadTableInfo(currentBill.getTableId());

        // Set màu sắc tùy theo trạng thái
        int statusColor = getStatusColor(currentBill.getStatus());
        tvStatus.setTextColor(statusColor);
    }
    
    private void loadTableInfo(Long tableId) {
        RetrofitClient.getTableApiService().getTableById(tableId).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TableResponse table = response.body();
                    tvTableName.setText(table.getName());
                }
            }
            
            @Override
            public void onFailure(Call<TableResponse> call, Throwable t) {
                tvTableName.setText("Bàn " + tableId);
            }
        });
    }
    
    private String getStatusText(Bill.BillStatus status) {
        switch (status) {
            case PAID:
                return "Đã thanh toán";
            case PENDING:
                return "Chờ thanh toán";
            case CANCELLED:
                return "Đã hủy";
            case REFUNDED:
                return "Đã hoàn tiền";
            default:
                return "";
        }
    }
    
    private String getPaymentMethodText(Bill.PaymentMethod method) {
        switch (method) {
            case CASH:
                return "Tiền mặt";
            case CREDIT_CARD:
                return "Thẻ tín dụng";
            case BANK_TRANSFER:
                return "Chuyển khoản";
            default:
                return "";
        }
    }
    
    private int getStatusColor(Bill.BillStatus status) {
        switch (status) {
            case PAID:
                return ContextCompat.getColor(requireContext(), R.color.green);
            case PENDING:
                return ContextCompat.getColor(requireContext(), R.color.red);
            case CANCELLED:
                return ContextCompat.getColor(requireContext(), R.color.orange);
            case REFUNDED:
                return ContextCompat.getColor(requireContext(), R.color.purple_500);
            default:
                return ContextCompat.getColor(requireContext(), R.color.black);
        }
    }

    private void loadFoodData() {
        RetrofitClient.getMenuApiService().getAllFoods().enqueue(new Callback<List<FoodResponse>>() {
            @Override
            public void onResponse(Call<List<FoodResponse>> call, Response<List<FoodResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (FoodResponse food : response.body()) {
                        foodNames.put(food.getId(), food.getName());
                    }
                    if (!billItems.isEmpty()) {
                        adapter.updateFoodMap(foodNames);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FoodResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Không thể tải danh sách món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}