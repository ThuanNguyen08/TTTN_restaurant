package com.restaurant.Menu_service.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.Menu_service.Entity.Stock;

public class StockResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String ingredientUnit;
    private Double quantity;
    private Double minQuantity;
    private Boolean isLowStock;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    public StockResponse() {}
    
    public StockResponse(Long id, Long ingredientId, String ingredientName, String ingredientUnit, Double quantity,
            Double minQuantity, Boolean isLowStock, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientUnit = ingredientUnit;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.isLowStock = isLowStock;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
    
    // Phương thức static để chuyển đổi từ Entity sang DTO
    public static StockResponse fromEntity(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getIngredient().getId(),
                stock.getIngredient().getName(),
                stock.getIngredient().getUnit(),
                stock.getQuantity(),
                stock.getMinQuantity(),
                stock.isLowStock(),
                stock.getUpdatedAt(),
                stock.getCreatedAt()
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

    public Double getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Double minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}