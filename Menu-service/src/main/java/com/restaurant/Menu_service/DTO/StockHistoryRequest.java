package com.restaurant.Menu_service.DTO;

import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockHistoryRequest {
    @NotNull(message = "ID nguyên liệu không được để trống")
    private Long ingredientId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Double quantity;
    
    @NotNull(message = "Loại giao dịch không được để trống")
    private TransactionType type;
    
    private String note;
    
    private Long userId;
    
    public StockHistoryRequest() {}

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}