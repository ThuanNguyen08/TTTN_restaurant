package com.example.tttn_restaurant.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.BillItemAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.Bill;
import com.example.tttn_restaurant.model.DetailBill;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.model.PayBillRequest;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.TableStatus;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {
    private static final String TAG = "PaymentFragment";

    private RecyclerView recyclerViewBillItems;
    private TextView tvTableName, tvTotalAmount, tvFinalAmount;
    private TextInputEditText etDiscountPercentage, etCustomerName, etCustomerPhone;
    private AutoCompleteTextView spinnerPaymentMethod;
    private Button btnConfirmPayment;
    private ProgressBar progressBar;
    private BillItemAdapter adapter;
    
    private TableResponse table;
    private Bill currentBill;
    private List<DetailBill> billItems = new ArrayList<>();
    private NumberFormat currencyFormat;

    private Map<Long, String> foodNames = new HashMap<>();//Lưu tên món ăn theo id
    
    // Danh sách phương thức thanh toán từ enum Bill.PaymentMethod
    private final String[] paymentMethods = {"CASH", "CREDIT_CARD", "BANK_TRANSFER"};

    public static PaymentFragment newInstance(TableResponse table) {
        PaymentFragment fragment = new PaymentFragment();
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
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        
        // Ánh xạ view
        recyclerViewBillItems = view.findViewById(R.id.recyclerViewBillItems);
        tvTableName = view.findViewById(R.id.tvTableName);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvFinalAmount = view.findViewById(R.id.tvFinalAmount);
        etDiscountPercentage = view.findViewById(R.id.etDiscountPercentage);
        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod);
        etCustomerName = view.findViewById(R.id.etCustomerName);
        etCustomerPhone = view.findViewById(R.id.etCustomerPhone);
        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Thiết lập RecyclerView
        recyclerViewBillItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BillItemAdapter(billItems, foodNames);
        recyclerViewBillItems.setAdapter(adapter);
        
        // Thiết lập thông tin bàn
        if (table != null) {
            tvTableName.setText("Thanh toán cho bàn: " + table.getName());
        }
        
        // Thiết lập spinner phương thức thanh toán
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                paymentMethods
        );
        spinnerPaymentMethod.setAdapter(methodAdapter);
        spinnerPaymentMethod.setText(paymentMethods[0], false);
        
        // Xử lý sự kiện khi giảm giá thay đổi
        etDiscountPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateFinalAmount();
            }
        });
        
        // Xử lý sự kiện nút thanh toán
        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
        
        // Tải dữ liệu hóa đơn
        if (table != null) {
            loadBillForTable();
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Hiển thị nút back và tiêu đề trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setTitle("Thanh toán");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadBillForTable() {
        progressBar.setVisibility(View.VISIBLE);
        loadFoodData();
        // Tìm bill đang chờ thanh toán (pending) cho bàn này
        RetrofitClient.getRevenueApiService().getPendingBills().enqueue(new Callback<List<Bill>>() {
            @Override
            public void onResponse(Call<List<Bill>> call, Response<List<Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean billFound = false;
                    
                    for (Bill bill : response.body()) {
                        if (bill.getTableId().equals(table.getId())) {
                            currentBill = bill;
                            billFound = true;
                            
                            // Tải chi tiết bill
                            loadBillDetails(bill.getId());
                            break;
                        }
                    }
                    
                    if (!billFound) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Không tìm thấy hóa đơn cho bàn này", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải hóa đơn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadBillDetails(Long billId) {
        RetrofitClient.getRevenueApiService().getBillItems(billId).enqueue(new Callback<List<DetailBill>>() {
            @Override
            public void onResponse(Call<List<DetailBill>> call, Response<List<DetailBill>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    billItems.clear();
                    billItems.addAll(response.body());
                    adapter.updateData(billItems);
                    
                    // Hiển thị tổng tiền
                    if (currentBill != null) {
                        tvTotalAmount.setText("Tổng tiền: " + currencyFormat.format(currentBill.getTotalPrice()));
                        updateFinalAmount();
                    }
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<DetailBill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Không thể tải chi tiết hóa đơn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
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
                Log.e(TAG, "Không thể tải danh sách món ăn: " + t.getMessage());
            }
        });
    }
    
    private void updateFinalAmount() {
        if (currentBill == null) return;
        
        String discountText = etDiscountPercentage.getText().toString().trim();
        BigDecimal discountPercentage;
        
        try {
            discountPercentage = discountText.isEmpty() ? 
                    BigDecimal.ZERO : new BigDecimal(discountText);
            
            // Kiểm tra giảm giá hợp lệ (0-100%)
            if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
                    discountPercentage.compareTo(new BigDecimal("100")) > 0) {
                etDiscountPercentage.setError("Giảm giá phải từ 0-100%");
                return;
            }

        } catch (NumberFormatException e) {
            etDiscountPercentage.setError("Giảm giá không hợp lệ");
            return;
        }
        
        // Tính giá cuối cùng sau khi trừ % giảm giá
        BigDecimal totalAmount = currentBill.getTotalPrice();
        BigDecimal discountAmount = totalAmount.multiply(discountPercentage).divide(new BigDecimal("100"));
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        
        tvFinalAmount.setText("Thanh toán: " + currencyFormat.format(finalAmount));
    }
    
    private void confirmPayment() {
        if (currentBill == null) {
            Toast.makeText(getContext(), "Không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Lấy dữ liệu từ form
        String discountText = etDiscountPercentage.getText().toString().trim();
        BigDecimal discountPercentage;
        
        try {
            discountPercentage = discountText.isEmpty() ? 
                    BigDecimal.ZERO : new BigDecimal(discountText);
                    
            // Kiểm tra giảm giá hợp lệ (0-100%)
            if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
                    discountPercentage.compareTo(new BigDecimal("100")) > 0) {
                etDiscountPercentage.setError("Giảm giá phải từ 0-100%");
                return;
            }
        } catch (NumberFormatException e) {
            etDiscountPercentage.setError("Giảm giá không hợp lệ");
            return;
        }
        
        String paymentMethod = spinnerPaymentMethod.getText().toString();
        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();

        if(!customerPhone.isEmpty() && !customerPhone.matches("^(0[0-9]{9})$")){
            etCustomerPhone.setError("Số điện thoại phải bắt đầu từ 0 và có 10 chữ số");
            return;
        }

        if(customerName.isEmpty() || customerName == null){
            etCustomerName.setError("Thiếu tên khách hàng");
            return;
        }
        
        // Tạo đối tượng request
        PayBillRequest request = new PayBillRequest(
                currentBill.getId(),
                discountPercentage,
                Bill.PaymentMethod.valueOf(paymentMethod),
                customerName,
                customerPhone
        );
        
        // Hiển thị dialog xác nhận
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc chắn muốn thanh toán hóa đơn này?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Gọi API thanh toán
                    processPayment(request);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void processPayment(PayBillRequest request) {
        progressBar.setVisibility(View.VISIBLE);
        btnConfirmPayment.setEnabled(false);
        
        RetrofitClient.getRevenueApiService().payBill(request).enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                progressBar.setVisibility(View.GONE);
                btnConfirmPayment.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    // Thanh toán thành công, cập nhật trạng thái bàn
                    updateTableStatus(table.getId(), TableStatus.AVAILABLE);
                    
                    // Hiển thị thông báo thành công
                    Toast.makeText(getContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    
                    // Quay lại danh sách bàn
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnConfirmPayment.setEnabled(true);
                
                Log.e(TAG, "Thanh toán thất bại: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateTableStatus(Long tableId, String status) {
        RetrofitClient.getTableApiService().updateTableStatus(tableId, status).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Không thể cập nhật trạng thái bàn");
                }
            }

            @Override
            public void onFailure(Call<TableResponse> call, Throwable t) {
                Log.e(TAG, "Không thể cập nhật trạng thái bàn: " + t.getMessage());
            }
        });
    }
}