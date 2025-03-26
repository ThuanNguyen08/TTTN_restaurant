package com.restaurant.Menu_service.DTO;

import java.time.LocalDateTime;

import com.restaurant.Menu_service.Entity.Category;

public class CategoryResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public static CategoryResponse fromEntity(Category category) {
    	CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setIsActive(category.getIsActive());
        response.setCreatedAt(category.getCreatedAt());
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