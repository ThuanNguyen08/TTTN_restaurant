package com.restaurant.Revenue_service.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RevenueReportDTO {
	private DateRangeType reportType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	private BigDecimal totalRevenue;
	private BigDecimal totalDiscounts;
	private BigDecimal netRevenue;//doanh thu ròng
	private int totalOrders;
	private BigDecimal averageOrderValue;// giá trị trung bình mỗi đơn
	private Map<String, BigDecimal> revenueByPeriod; // doanh thu chi tiết theo từng khoảng thời gian

	public enum DateRangeType {
		DAY, WEEK, MONTH, YEAR, CUSTOM
	}

	public RevenueReportDTO() {
	}

	public RevenueReportDTO(DateRangeType reportType, LocalDate startDate, LocalDate endDate, BigDecimal totalRevenue,
			BigDecimal totalDiscounts, BigDecimal netRevenue, int totalOrders, BigDecimal averageOrderValue,
			Map<String, BigDecimal> revenueByPeriod) {
		this.reportType = reportType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalRevenue = totalRevenue;
		this.totalDiscounts = totalDiscounts;
		this.netRevenue = netRevenue;
		this.totalOrders = totalOrders;
		this.averageOrderValue = averageOrderValue;
		this.revenueByPeriod = revenueByPeriod;
	}

	public DateRangeType getReportType() {
		return reportType;
	}

	public void setReportType(DateRangeType reportType) {
		this.reportType = reportType;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public BigDecimal getTotalDiscounts() {
		return totalDiscounts;
	}

	public void setTotalDiscounts(BigDecimal totalDiscounts) {
		this.totalDiscounts = totalDiscounts;
	}

	public BigDecimal getNetRevenue() {
		return netRevenue;
	}

	public void setNetRevenue(BigDecimal netRevenue) {
		this.netRevenue = netRevenue;
	}

	public int getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}

	public BigDecimal getAverageOrderValue() {
		return averageOrderValue;
	}

	public void setAverageOrderValue(BigDecimal averageOrderValue) {
		this.averageOrderValue = averageOrderValue;
	}

	public Map<String, BigDecimal> getRevenueByPeriod() {
		return revenueByPeriod;
	}

	public void setRevenueByPeriod(Map<String, BigDecimal> revenueByPeriod) {
		this.revenueByPeriod = revenueByPeriod;
	}

}
