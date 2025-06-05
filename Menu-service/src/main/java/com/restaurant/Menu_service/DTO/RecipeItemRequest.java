package com.restaurant.Menu_service.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RecipeItemRequest {
    @NotNull(message = "ID món ăn không được để trống")
    private Long foodId;
    
    @NotNull(message = "ID nguyên liệu không được để trống")
    private Long ingredientId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Double quantity;
    
    public RecipeItemRequest() {}

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

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
}