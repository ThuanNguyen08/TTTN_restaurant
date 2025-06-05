package com.restaurant.Menu_service.DTO;

import com.restaurant.Menu_service.Entity.RecipeItem;

public class RecipeItemResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private Long ingredientId;
    private String ingredientName;
    private String ingredientUnit;
    private Double quantity;
    
    public RecipeItemResponse() {}
    
    public RecipeItemResponse(Long id, Long foodId, String foodName, Long ingredientId, String ingredientName,
            String ingredientUnit, Double quantity) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.ingredientUnit = ingredientUnit;
        this.quantity = quantity;
    }
    
    // Phương thức static để chuyển đổi từ Entity sang DTO
    public static RecipeItemResponse fromEntity(RecipeItem recipeItem) {
        return new RecipeItemResponse(
                recipeItem.getId(),
                recipeItem.getFood().getId(),
                recipeItem.getFood().getName(),
                recipeItem.getIngredient().getId(),
                recipeItem.getIngredient().getName(),
                recipeItem.getIngredient().getUnit(),
                recipeItem.getQuantity()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
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
}