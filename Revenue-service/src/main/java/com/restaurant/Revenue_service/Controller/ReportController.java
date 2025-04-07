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

@RestController
@RequestMapping("/api/reports")
public class ReportController {
	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/revenue")
	public ResponseEntity<RevenueReportDTO> getRevenueReport(@RequestParam("type") DateRangeType type,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		RevenueReportDTO report = reportService.generateRevenueReport(type, startDate, endDate);
		return ResponseEntity.ok(report);
	}
}
