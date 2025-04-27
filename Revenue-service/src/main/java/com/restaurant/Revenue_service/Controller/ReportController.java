package com.restaurant.Revenue_service.Controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Revenue_service.DTO.RevenueReportDTO;
import com.restaurant.Revenue_service.DTO.RevenueReportDTO.DateRangeType;
import com.restaurant.Revenue_service.Service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Revenue", description = "Quản lý doanh thu của nhà hàng")
public class ReportController {
	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@Operation(summary = "Tạo báo cáo doanh thu", description = "Có thể tạo báo cáo doanh thu theo ngày, tuần, tháng hoặc khoảng thời gian tùy chọn")
	@GetMapping("/revenue")
	public ResponseEntity<RevenueReportDTO> getRevenueReport(@RequestParam("type") DateRangeType type,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		RevenueReportDTO report = reportService.generateRevenueReport(type, startDate, endDate);
		return ResponseEntity.ok(report);
	}
}
