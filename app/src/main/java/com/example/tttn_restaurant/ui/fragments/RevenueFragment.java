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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.RevenueReportDTO;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevenueFragment extends Fragment {

    private Spinner spinnerReportType;
    private EditText editStartDate;
    private EditText editEndDate;
    private Button btnGenerateReport;
    private CardView cardReportResult;
    private LinearLayout layoutDateRange1, layoutDateRange2;
    private TextView tvDateRange, tvTotalRevenue, tvTotalDiscounts, tvNetRevenue, tvTotalOrders, tvAverageOrderValue, edit_date;

    private RevenueReportDTO.DateRangeType selectedReportType = RevenueReportDTO.DateRangeType.DAY;
    private LocalDate startDate = null;
    private LocalDate endDate = null;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerReportType = view.findViewById(R.id.spinner_report_type);
        editStartDate = view.findViewById(R.id.edit_start_date);
        editEndDate = view.findViewById(R.id.edit_end_date);
        btnGenerateReport = view.findViewById(R.id.btn_generate_report);
        cardReportResult = view.findViewById(R.id.card_report_result);
        layoutDateRange1 = view.findViewById(R.id.layout_date_range1);
        layoutDateRange2 = view.findViewById(R.id.layout_date_range2);

        tvDateRange = view.findViewById(R.id.tv_date_range);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvTotalDiscounts = view.findViewById(R.id.tv_total_discounts);
        tvNetRevenue = view.findViewById(R.id.tv_net_revenue);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvAverageOrderValue = view.findViewById(R.id.tv_average_order_value);
        edit_date = view.findViewById(R.id.edit_date);

        // Cài đặt spinner loại báo cáo
        setupReportTypeSpinner();

        // Cài đặt date pickers
        setupDatePickers();

        // Xử lý sự kiện nút tạo báo cáo
        btnGenerateReport.setOnClickListener(v -> generateReport());

        // Hiển thị nút back và tiêu đề trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setTitle("Doanh thu");
    }

    private void setupReportTypeSpinner() {
        String[] reportTypes = new String[]{"Ngày", "Tuần", "Tháng", "Năm", "Tùy chỉnh"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                reportTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(adapter);

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedReportType = RevenueReportDTO.DateRangeType.DAY;
                        layoutDateRange1.setVisibility(View.VISIBLE);
                        layoutDateRange2.setVisibility(View.GONE);
                        break;
                    case 1:
                        selectedReportType = RevenueReportDTO.DateRangeType.WEEK;
                        layoutDateRange1.setVisibility(View.VISIBLE);
                        layoutDateRange2.setVisibility(View.GONE);
                        break;
                    case 2:
                        selectedReportType = RevenueReportDTO.DateRangeType.MONTH;
                        layoutDateRange1.setVisibility(View.VISIBLE);
                        layoutDateRange2.setVisibility(View.GONE);
                        break;
                    case 3:
                        selectedReportType = RevenueReportDTO.DateRangeType.YEAR;
                        layoutDateRange1.setVisibility(View.VISIBLE);
                        layoutDateRange2.setVisibility(View.GONE);
                        break;
                    case 4:
                        selectedReportType = RevenueReportDTO.DateRangeType.CUSTOM;
                        layoutDateRange1.setVisibility(View.VISIBLE);
                        layoutDateRange2.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupDatePickers() {
        editStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        editEndDate.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    if (isStartDate) {
                        startDate = selectedDate;
                        editStartDate.setText(startDate.format(dateFormatter));
                    } else {
                        endDate = selectedDate;
                        editEndDate.setText(endDate.format(dateFormatter));
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void generateReport() {

        // Gọi API
        String startDateStr = startDate != null ? startDate.format(apiDateFormatter) : null;
        String endDateStr = endDate != null ? endDate.format(apiDateFormatter) : null;

        RetrofitClient.getRevenueApiService().getRevenueReport(selectedReportType, startDateStr, endDateStr)
                .enqueue(new Callback<RevenueReportDTO>() {
                    @Override
                    public void onResponse(Call<RevenueReportDTO> call, Response<RevenueReportDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayReportData(response.body());
                        } else {
                            ErrorHandler.handleErrorResponse(requireContext(), response);
                        }
                    }

                    @Override
                    public void onFailure(Call<RevenueReportDTO> call, Throwable t) {
                        ErrorHandler.handleFailure(requireContext(), t);
                    }
                });
    }

    private void displayReportData(RevenueReportDTO report) {
        // Hiện kết quả
        cardReportResult.setVisibility(View.VISIBLE);

        // Định dạng khoảng thời gian
        String dateRangeText = formatDateRange(report.getReportType(), LocalDate.parse(report.getStartDate()), LocalDate.parse(report.getEndDate()));
        tvDateRange.setText(dateRangeText);

        // Hiển thị các giá trị
        tvTotalRevenue.setText(formatCurrency(report.getTotalRevenue()));
        tvTotalDiscounts.setText(formatCurrency(report.getTotalDiscounts()));
        tvNetRevenue.setText(formatCurrency(report.getNetRevenue()));
        tvTotalOrders.setText(String.valueOf(report.getTotalOrders()));
        tvAverageOrderValue.setText(formatCurrency(report.getAverageOrderValue()));
    }

    private String formatDateRange(RevenueReportDTO.DateRangeType type, LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return "Không có dữ liệu";
        }

        String formattedStart = start.format(dateFormatter);
        String formattedEnd = end.format(dateFormatter);

        switch (type) {
            case DAY:
                return formattedStart;
            case WEEK:
                return "Tuần từ " + formattedStart + " đến " + formattedEnd;
            case MONTH:
                return "Tháng " + start.getMonthValue() + "/" + start.getYear();
            case YEAR:
                return "Năm " + start.getYear();
            case CUSTOM:
                return "Từ " + formattedStart + " đến " + formattedEnd;
            default:
                return formattedStart + " - " + formattedEnd;
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 ₫";
        }
        return currencyFormatter.format(amount);
    }
}