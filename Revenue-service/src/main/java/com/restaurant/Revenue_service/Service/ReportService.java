package com.restaurant.Revenue_service.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.Revenue_service.DTO.RevenueReportDTO;
import com.restaurant.Revenue_service.DTO.RevenueReportDTO.DateRangeType;
import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Entity.Bill.BillStatus;
import com.restaurant.Revenue_service.Repository.BillRepository;

@Service
public class ReportService {

    private final BillRepository billRepository;

    public ReportService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Transactional(readOnly = true)
    public RevenueReportDTO generateRevenueReport(DateRangeType type, LocalDate startDate, LocalDate endDate) {
        // Xác định khoảng thời gian báo cáo
        LocalDateTime[] dateRange = calculateDateRange(type, startDate, endDate);
        LocalDateTime start = dateRange[0];
        LocalDateTime end = dateRange[1];

        // Lấy danh sách hóa đơn đã thanh toán trong khoảng thời gian
        List<Bill> bills = billRepository.findByStatusAndPaidAtBetween(BillStatus.PAID, start, end);

        // Tính toán các giá trị tổng hợp
        BigDecimal totalRevenue = bills.stream()
                .map(Bill::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDiscounts = bills.stream()
                .map(Bill::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netRevenue = bills.stream()
                .map(Bill::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = bills.size();

        BigDecimal averageOrderValue = totalOrders > 0 
                ? netRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        // Tạo báo cáo doanh thu theo khoảng thời gian chi tiết
        Map<String, BigDecimal> revenueByPeriod = calculateRevenueByPeriod(bills, type, start, end);

        // Tạo và trả về đối tượng báo cáo
        RevenueReportDTO report = new RevenueReportDTO();
        report.setReportType(type);
        report.setStartDate(start.toLocalDate());
        report.setEndDate(end.toLocalDate());
        report.setTotalRevenue(totalRevenue);
        report.setTotalDiscounts(totalDiscounts);
        report.setNetRevenue(netRevenue);
        report.setTotalOrders(totalOrders);
        report.setAverageOrderValue(averageOrderValue);
        report.setRevenueByPeriod(revenueByPeriod);

        return report;
    }

    // Phương thức hỗ trợ tính toán khoảng thời gian báo cáo
    private LocalDateTime[] calculateDateRange(DateRangeType type, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start;
        LocalDateTime end;

        switch (type) {
            case DAY:
                // Báo cáo theo ngày - lấy ngày hiện tại nếu không có tham số
                LocalDate day = startDate != null ? startDate : LocalDate.now();
                start = day.atStartOfDay();
                end = day.atTime(LocalTime.MAX);
                break;
            case WEEK:
                // Báo cáo theo tuần - lấy tuần hiện tại nếu không có tham số
                LocalDate weekStart;
                if (startDate != null) {
                    weekStart = startDate.with(DayOfWeek.MONDAY);
                } else {
                    weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
                }
                start = weekStart.atStartOfDay();
                end = weekStart.plusDays(6).atTime(LocalTime.MAX);
                break;
            case MONTH:
                // Báo cáo theo tháng - lấy tháng hiện tại nếu không có tham số
                LocalDate monthStart;
                if (startDate != null) {
                    monthStart = startDate.withDayOfMonth(1);
                } else {
                    monthStart = LocalDate.now().withDayOfMonth(1);
                }
                start = monthStart.atStartOfDay();
                end = monthStart.plusMonths(1).minusDays(1).atTime(LocalTime.MAX);
                break;
            case YEAR:
                // Báo cáo theo năm - lấy năm hiện tại nếu không có tham số
                LocalDate yearStart;
                if (startDate != null) {
                    yearStart = startDate.withDayOfYear(1);
                } else {
                    yearStart = LocalDate.now().withDayOfYear(1);
                }
                start = yearStart.atStartOfDay();
                end = yearStart.plusYears(1).minusDays(1).atTime(LocalTime.MAX);
                break;
            case CUSTOM:
                // Báo cáo tùy chỉnh theo khoảng thời gian
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Phải cung cấp cả ngày bắt đầu và kết thúc cho báo cáo tùy chỉnh");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                break;
            default:
                throw new IllegalArgumentException("Loại báo cáo không được hỗ trợ");
        }

        return new LocalDateTime[] { start, end };
    }

    // Phương thức tính toán doanh thu theo từng khoảng thời gian chi tiết
    private Map<String, BigDecimal> calculateRevenueByPeriod(List<Bill> bills, DateRangeType type, 
            LocalDateTime start, LocalDateTime end) {
        Map<String, BigDecimal> revenueByPeriod = new HashMap<>();
        
        switch (type) {
            case DAY:
                // Doanh thu theo giờ trong ngày
                for (int hour = 0; hour < 24; hour++) {
                    String periodKey = String.format("%02d:00", hour);// 0 -> 00:00
                    revenueByPeriod.put(periodKey, BigDecimal.ZERO); // tạo các colection chứa thông tin giờ và doanh thu(để tạm 0)
                }
                
                for (Bill bill : bills) { // duyệt qua từng đơn để lấy đươc doanh thu, rồi cộng dồn vào colection hashmap
                    int hour = bill.getPaidAt().getHour();
                    String periodKey = String.format("%02d:00", hour);
                    
                    BigDecimal currentAmount = revenueByPeriod.get(periodKey);
                    revenueByPeriod.put(periodKey, currentAmount.add(bill.getFinalPrice()));
                }
                break;
                
            case WEEK:
                // Doanh thu theo ngày trong tuần
                for (int day = 0; day < 7; day++) {
                    LocalDate date = start.toLocalDate().plusDays(day); // lấy giá trị từng ngày trong tuần
                    String periodKey = date.getDayOfWeek().toString(); // lấy giá trị của từng ngày. vd: MONDAY, TUESDAY,...
                    revenueByPeriod.put(periodKey, BigDecimal.ZERO);
                }
                
                for (Bill bill : bills) {
                    String periodKey = bill.getPaidAt().getDayOfWeek().toString();
                    BigDecimal currentAmount = revenueByPeriod.get(periodKey);
                    revenueByPeriod.put(periodKey, currentAmount.add(bill.getFinalPrice()));
                }
                break;
                
            case MONTH:
                // Doanh thu theo ngày trong tháng
                YearMonth yearMonth = YearMonth.from(start);
                int daysInMonth = yearMonth.lengthOfMonth();
                
                for (int day = 1; day <= daysInMonth; day++) {
                    String periodKey = "Ngày " + day;
                    revenueByPeriod.put(periodKey, BigDecimal.ZERO);
                }
                
                for (Bill bill : bills) {
                    String periodKey = "Ngày " + bill.getPaidAt().getDayOfMonth();
                    BigDecimal currentAmount = revenueByPeriod.getOrDefault(periodKey, BigDecimal.ZERO);
                    revenueByPeriod.put(periodKey, currentAmount.add(bill.getFinalPrice()));
                }
                break;
                
            case YEAR:
                // Doanh thu theo tháng trong năm
                for (int month = 1; month <= 12; month++) {
                    String periodKey = "Tháng " + month;
                    revenueByPeriod.put(periodKey, BigDecimal.ZERO);
                }
                
                for (Bill bill : bills) {
                    String periodKey = "Tháng " + bill.getPaidAt().getMonthValue();
                    BigDecimal currentAmount = revenueByPeriod.get(periodKey);
                    revenueByPeriod.put(periodKey, currentAmount.add(bill.getFinalPrice()));
                }
                break;
                
            case CUSTOM:
                // Với báo cáo tùy chỉnh, chia doanh thu theo ngày
                LocalDate current = start.toLocalDate();
                while (!current.isAfter(end.toLocalDate())) {
                    String periodKey = current.toString();
                    revenueByPeriod.put(periodKey, BigDecimal.ZERO);
                    current = current.plusDays(1);
                }
                
                for (Bill bill : bills) {
                    String periodKey = bill.getPaidAt().toLocalDate().toString();
                    BigDecimal currentAmount = revenueByPeriod.getOrDefault(periodKey, BigDecimal.ZERO);
                    revenueByPeriod.put(periodKey, currentAmount.add(bill.getFinalPrice()));
                }
                break;
        }
        
        return revenueByPeriod;
    }
}