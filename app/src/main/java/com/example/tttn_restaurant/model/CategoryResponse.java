package com.example.tttn_restaurant.model;

public class CategoryResponse {
    private Long id;
    private String name;
    private boolean isActive;
    private String createdAt;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, Boolean isActive, String createdAt) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.createdAt = createdAt;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}