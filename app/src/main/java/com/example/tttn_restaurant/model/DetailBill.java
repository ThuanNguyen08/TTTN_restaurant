package com.example.tttn_restaurant.model;

import java.math.BigDecimal;

public class DetailBill {
    private Long id;
    private Long billId;
    private Long foodId;
    private Integer quantity;
    private BigDecimal price;

    public DetailBill() {
    }

    public DetailBill(Long id, Long billId, Long foodId, Integer quantity, BigDecimal price) {
        this.id = id;
        this.billId = billId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}