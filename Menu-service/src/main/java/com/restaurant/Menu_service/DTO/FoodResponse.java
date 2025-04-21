package com.restaurant.Menu_service.DTO;

import java.time.LocalDateTime;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.Menu_service.Entity.Food;

public class FoodResponse {
    private Long id;
    private String name;
    private Double price;
    private String imageBase64;
    private CategoryResponse category;
    private String description;
    private Food.Status status;
    private Boolean isActive;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    public static FoodResponse fromEntity(Food food) {
        FoodResponse response = new FoodResponse();
        response.setId(food.getId());
        response.setName(food.getName());
        response.setPrice(food.getPrice());
        
        // Chuyển đổi byte[] thành Base64 nếu có hình ảnh
        if (food.getImage() != null) {
            response.setImageBase64(Base64.getEncoder().encodeToString(food.getImage()));
        }
        
        response.setCategory(CategoryResponse.fromEntity(food.getCategory()));
        response.setDescription(food.getDescription());
        response.setStatus(food.getStatus());
        response.setIsActive(food.getIsActive());
        response.setCreatedAt(food.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Food.Status getStatus() {
        return status;
    }

    public void setStatus(Food.Status status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}