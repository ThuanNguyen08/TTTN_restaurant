package com.example.tttn_restaurant.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.tttn_restaurant.adapter.TableAdapter;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.TableRequest;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.TableStatus;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableFragment extends Fragment implements TableAdapter.OnTableActionListener {
    private static final String TAG = "TableFragment";

    private RecyclerView recyclerViewTables;
    private Spinner spinnerStatus;
    private FloatingActionButton fabAddTable;
    private TableAdapter tableAdapter;
    private List<TableResponse> tableList = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // Đặt tiêu đề cho ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Quản lý bàn ăn");

        // Ẩn nút back khi ở MenuFragment
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);

        // Khởi tạo views
        recyclerViewTables = view.findViewById(R.id.recyclerViewTables);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        fabAddTable = view.findViewById(R.id.fabAddTable);

        // Thiết lập RecyclerView
        recyclerViewTables.setLayoutManager(new GridLayoutManager(getContext(), 2));
        tableAdapter = new TableAdapter(getContext(), tableList, this);
        recyclerViewTables.setAdapter(tableAdapter);

        // Chỉ ADMIN và MANAGER có thể thêm món ăn
        String userRole = sessionManager.getUser().getRole();
        if ("ADMIN".equals(userRole) || "MANAGER".equals(userRole)) {
            fabAddTable.setVisibility(View.VISIBLE);
        } else {
            fabAddTable.setVisibility(View.GONE);
        }

        // Khởi tạo spinner trạng thái
        setupStatusSpinner();

        // Thiết lập sự kiện cho nút thêm bàn
        fabAddTable.setOnClickListener(v -> showAddEditTableDialog(null));

        // Tải danh sách bàn
        loadTables(null);
    }

    private void setupStatusSpinner() {
        // Danh sách các trạng thái
        String[] statuses = new String[]{
                "Tất cả",
                "Có sẵn",
                "Có khách",
                "Đã đặt",
                "Bảo trì"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = null;

                switch (position) {
                    case 0: // Tất cả
                        selectedStatus = null;
                        break;
                    case 1: // Có sẵn
                        selectedStatus = TableStatus.AVAILABLE;
                        break;
                    case 2: // Có khách
                        selectedStatus = TableStatus.OCCUPIED;
                        break;
                    case 3: // Đã đặt
                        selectedStatus = TableStatus.RESERVED;
                        break;
                    case 4: // Bảo trì
                        selectedStatus = TableStatus.MAINTENANCE;
                        break;
                }

                loadTables(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void loadTables(String status) {
        Call<List<TableResponse>> call;

        if (status == null) {
            call = RetrofitClient.getTableApiService().getAllTables();
        } else {
            call = RetrofitClient.getTableApiService().getTablesByStatus(status);
        }

        call.enqueue(new Callback<List<TableResponse>>() {
            @Override
            public void onResponse(Call<List<TableResponse>> call, Response<List<TableResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tableList = response.body();
                    tableAdapter.updateData(tableList);
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<TableResponse>> call, Throwable t) {
                Log.e(TAG, "Không thể tải danh sách bàn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditTableDialog(TableResponse table) {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Set title
        builder.setTitle(table == null ? "Thêm bàn mới" : "Chỉnh sửa bàn");

        // Inflating the layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_table, null);
        builder.setView(dialogView);

        // Ánh xạ các view trong dialog
        TextInputEditText etTableName = dialogView.findViewById(R.id.etTableName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Thiết lập spinner trạng thái
        String[] statuses = new String[]{
                TableStatus.AVAILABLE,
                TableStatus.OCCUPIED,
                TableStatus.RESERVED,
                TableStatus.MAINTENANCE
        };

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Nếu là chỉnh sửa, điền thông tin hiện có
        if (table != null) {
            etTableName.setText(table.getName());
            etDescription.setText(table.getDescription());

            // Set spinner selection
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(table.getStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

        // Thêm nút
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Validate input
            String tableName = etTableName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            if (tableName.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên bàn", Toast.LENGTH_SHORT).show();
                return;
            }

            TableRequest request = new TableRequest();
            request.setName(tableName);
            request.setDescription(description);
            request.setStatus(status);

            if (table == null) {
                // Thêm bàn mới
                createTable(request);
            } else {
                // Cập nhật bàn
                updateTable(table.getId(), request);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createTable(TableRequest request) {
        RetrofitClient.getTableApiService().createTable(request).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Thêm bàn thành công", Toast.LENGTH_SHORT).show();
                    // Tải lại danh sách bàn
                    loadTables(null);
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<TableResponse> call, Throwable t) {
                Log.e(TAG, "Không thể tạo bàn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTable(Long tableId, TableRequest request) {
        RetrofitClient.getTableApiService().updateTable(tableId, request).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Cập nhật bàn thành công", Toast.LENGTH_SHORT).show();
                    // Tải lại danh sách bàn
                    loadTables(null);
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<TableResponse> call, Throwable t) {
                Log.e(TAG, "Không thể cập nhật bàn: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTable(Long tableId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa bàn")
                .setMessage("Bạn có chắc chắn muốn xóa bàn này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    RetrofitClient.getTableApiService().deleteTable(tableId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Xóa bàn thành công", Toast.LENGTH_SHORT).show();
                                // Tải lại danh sách bàn
                                loadTables(null);
                            } else {
                                ErrorHandler.handleErrorResponse(requireContext(), response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Không thể xóa bàn: " + t.getMessage());
                            Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showTableDetailsDialog(TableResponse table) {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin bàn " + table.getName());

        // Tạo layout cho nội dung
        View contentView = getLayoutInflater().inflate(R.layout.dialog_table_details, null);
        builder.setView(contentView);

        // Ánh xạ các view
        TextView tvTableName = contentView.findViewById(R.id.tvTableName);
        TextView tvTableStatus = contentView.findViewById(R.id.tvTableStatus);
        TextView tvDescription = contentView.findViewById(R.id.tvDescription);
        TextView tvCreatedAt = contentView.findViewById(R.id.tvCreatedAt);

        // Hiển thị thông tin
        tvTableName.setText("Tên bàn: " + table.getName());
        tvTableStatus.setText("Trạng thái: " + getStatusText(table.getStatus()));
        tvDescription.setText("Mô tả: " + (table.getDescription() != null ? table.getDescription() : "Không có mô tả"));
        tvCreatedAt.setText("Ngày tạo: " + table.getCreatedAt());

        // Thêm các nút thao tác
        builder.setPositiveButton("Chỉnh sửa", (dialog, which) -> showAddEditTableDialog(table));
        builder.setNegativeButton("Xóa", (dialog, which) -> deleteTable(table.getId()));
        builder.setNeutralButton("Đóng", (dialog, which) -> dialog.dismiss());

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getStatusText(String status) {
        switch (status) {
            case TableStatus.AVAILABLE:
                return "Có sẵn";
            case TableStatus.OCCUPIED:
                return "Có khách";
            case TableStatus.RESERVED:
                return "Đã đặt";
            case TableStatus.MAINTENANCE:
                return "Bảo trì";
            default:
                return status;
        }
    }

    // Xử lý sự kiện khi click vào bàn
    @Override
    public void onTableClick(TableResponse table) {
        showTableDetailsDialog(table);
    }

    // Xử lý sự kiện khi click vào nút Order
    @Override
    public void onOrderClick(TableResponse table) {
        OrderFoodFragment orderFragment = OrderFoodFragment.newInstance(table);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .addToBackStack(null)
                .commit();
    }

    // Xử lý sự kiện khi click vào nút Thanh toán
    @Override
    public void onPaymentClick(TableResponse table) {
        PaymentFragment paymentFragment = PaymentFragment.newInstance(table);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateTableStatus(Long tableId, String status) {
        RetrofitClient.getTableApiService().updateTableStatus(tableId, status).enqueue(new Callback<TableResponse>() {
            @Override
            public void onResponse(Call<TableResponse> call, Response<TableResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
                    // Tải lại danh sách bàn
                    loadTables(null);
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