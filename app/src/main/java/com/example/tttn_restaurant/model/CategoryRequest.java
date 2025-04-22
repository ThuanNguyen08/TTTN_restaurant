package com.example.tttn_restaurant.model;

public class CategoryRequest {
    private String name;

    public CategoryRequest() {
    }

    public CategoryRequest(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

}