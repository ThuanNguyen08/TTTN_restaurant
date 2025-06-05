package com.restaurant.Menu_service.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.Menu_service.Entity.StockHistory;
import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;

public class StockHistoryResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String ingredientUnit;
    private Double quantity;
    private TransactionType type;
    private String note;
    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    public StockHistoryResponse() {}
    
    public StockHistoryResponse(Long id, Long ingredientId, String ingredientName, String ingredientUnit,
            Double quantity, TransactionType type, String note, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientUnit = ingredientUnit;
        this.quantity = quantity;
        this.type = type;
        this.note = note;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    // Phương thức static để chuyển đổi từ Entity sang DTO
    public static StockHistoryResponse fromEntity(StockHistory stockHistory) {
        return new StockHistoryResponse(
                stockHistory.getId(),
                stockHistory.getIngredient().getId(),
                stockHistory.getIngredient().getName(),
                stockHistory.getIngredient().getUnit(),
                stockHistory.getQuantity(),
                stockHistory.getType(),
                stockHistory.getNote(),
                stockHistory.getUserId(),
                stockHistory.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientUnit() {
        return ingredientUnit;
    }

    public void setIngredientUnit(String ingredientUnit) {
        this.ingredientUnit = ingredientUnit;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}