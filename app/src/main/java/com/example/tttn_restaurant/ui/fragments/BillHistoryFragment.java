package com.example.tttn_restaurant.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.adapter.BillHistoryAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.Bill;
import com.example.tttn_restaurant.model.PageResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private BillHistoryAdapter adapter;
    private ProgressBar progressBar;
    private List<Bill> billList = new ArrayList<>();
    private int currentPage = 0;        // Trang hiện tại đang hiển thị
    private boolean isLastPage = false; // Đánh dấu đã đến trang cuối cùng hay chưa
    private boolean isLoading = false;  // Đánh dấu đang tải dữ liệu hay không

    private TextView tvEmpty;
    private Spinner spinnerStatus;
    private Button btnPickDate;
    private ImageButton btnClearFilter;

    // Biến lưu trạng thái lọc
    private String filterStatus = null;
    private String filterDate = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_history, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewBillHistory);
        progressBar = view.findViewById(R.id.progressBar);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        btnPickDate = view.findViewById(R.id.btnPickDate);
        btnClearFilter = view.findViewById(R.id.btnClearFilter);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        setupSpinner();
        setupDatePicker();
        setupClearFilter();
        setupRecyclerView();
        loadBillHistory(currentPage);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        // Cập nhật lại ActionBar mỗi khi fragment được hiển thị lại
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý hóa đơn");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void setupSpinner() {
        // Tạo adapter cho spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item);
        adapter.add("Tất cả trạng thái");
        adapter.add("Đã thanh toán");
        adapter.add("Chờ thanh toán");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    filterStatus = null;
                } else if (position == 1) {
                    filterStatus = "PAID";
                } else if (position == 2) {
                    filterStatus = "PENDING";
                }

                // Tải lại dữ liệu khi thay đổi bộ lọc
                currentPage = 0;
                loadBillHistory(currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupDatePicker() {
        btnPickDate.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Hiển thị DatePicker
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Định dạng ngày đã chọn
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);

                        filterDate = formattedDate;
                        btnPickDate.setText("Ngày: " + formattedDate);

                        // Tải lại dữ liệu khi thay đổi bộ lọc
                        currentPage = 0;
                        loadBillHistory(currentPage);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void setupClearFilter() {
        btnClearFilter.setOnClickListener(v -> {
            // Xóa tất cả bộ lọc
            filterStatus = null;
            filterDate = null;

            // Reset giao diện
            spinnerStatus.setSelection(0);
            btnPickDate.setText("Chọn ngày");

            // Tải lại dữ liệu
            currentPage = 0;
            loadBillHistory(currentPage);
        });
    }

    private void setupRecyclerView() {
        adapter = new BillHistoryAdapter(billList, bill -> {
            // Xử lý khi click vào một hóa đơn
            showBillDetail(bill);
        });
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Thêm phân trang khi cuộn
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreBills();
                    }
                }
            }
        });
    }

    private void loadMoreBills() {
        loadBillHistory(currentPage + 1);
    }
    
    private void loadBillHistory(int page) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        
        RetrofitClient.getRevenueApiService().getAllBills(page, 10, "id,desc", filterStatus, filterDate).enqueue(new Callback<PageResponse<Bill>>() {
            @Override
            public void onResponse(Call<PageResponse<Bill>> call, Response<PageResponse<Bill>> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<Bill> pageResponse = response.body();
                    
                    if (page == 0) {
                        billList.clear();
                    }

                    billList.addAll(pageResponse.getContent());
                    adapter.notifyDataSetChanged();
                    
                    currentPage = pageResponse.getPageNumber();
                    isLastPage = pageResponse.isLast();

                    // Hiển thị thông báo nếu không có dữ liệu
                    if (billList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }
            
            @Override
            public void onFailure(Call<PageResponse<Bill>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(requireContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    

    
    private void showBillDetail(Bill bill) {
        // Chuyển đến fragment chi tiết hóa đơn
        BillDetailFragment fragment = BillDetailFragment.newInstance(bill.getId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}