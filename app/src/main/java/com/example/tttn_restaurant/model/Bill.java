package com.example.tttn_restaurant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private Long id;
    private Long userId;
    private Long tableId;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private PaymentMethod paymentMethod;
    private BillStatus status;
    private String customerName;
    private String customerPhone;
    private String note;
    private String createdAt;
    private String paidAt;

    public enum PaymentMethod {
        CASH, CREDIT_CARD, BANK_TRANSFER, NULL
    }

    public enum BillStatus {
        PENDING, PAID, CANCELLED, REFUNDED
    }

    public Bill() {
    }

    // Constructor với tất cả các trường
    public Bill(Long id, Long userId, Long tableId, BigDecimal totalPrice, BigDecimal discountAmount,
            BigDecimal finalPrice, PaymentMethod paymentMethod, BillStatus status, String customerName,
            String customerPhone, String note, String createdAt, String paidAt) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.note = note;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }
}