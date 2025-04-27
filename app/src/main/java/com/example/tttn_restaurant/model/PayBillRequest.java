package com.example.tttn_restaurant.model;

import java.math.BigDecimal;

public class PayBillRequest {
    private Long billId;
    private BigDecimal discountPercentage;
    private Bill.PaymentMethod paymentMethod;
    private String customerName;
    private String customerPhone;

    public PayBillRequest() {
    }

    public PayBillRequest(Long billId, BigDecimal discountPercentage, Bill.PaymentMethod paymentMethod, String customerName, String customerPhone) {
        this.billId = billId;
        this.discountPercentage = discountPercentage;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Bill.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Bill.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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
}