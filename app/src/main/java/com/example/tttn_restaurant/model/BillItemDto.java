package com.example.tttn_restaurant.model;

public class BillItemDto {
    private Long foodId;
    private Integer quantity;

    public BillItemDto() {
    }

    public BillItemDto(Long foodId, Integer quantity) {
        this.foodId = foodId;
        this.quantity = quantity;
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
}