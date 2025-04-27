package com.example.tttn_restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RevenueReportDTO {
    private DateRangeType reportType;
    private String startDate;
    private String endDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalDiscounts;
    private BigDecimal netRevenue;
    private int totalOrders;
    private BigDecimal averageOrderValue;
    private Map<String, BigDecimal> revenueByPeriod;

    public enum DateRangeType {
        DAY, WEEK, MONTH, YEAR, CUSTOM
    }

    public RevenueReportDTO() {
    }

    public RevenueReportDTO(DateRangeType reportType, String startDate, String endDate,
                          BigDecimal totalRevenue, BigDecimal totalDiscounts, BigDecimal netRevenue, 
                          int totalOrders, BigDecimal averageOrderValue, 
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

    // Getters và Setters
    public DateRangeType getReportType() {
        return reportType;
    }

    public void setReportType(DateRangeType reportType) {
        this.reportType = reportType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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