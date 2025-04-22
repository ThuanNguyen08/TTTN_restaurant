package com.example.tttn_restaurant.model;

import java.io.File;

public class FoodRequest {
    private String name;
    private Double price;
    private File image;
    private Long categoryId;
    private String description;
    private String status;

    public FoodRequest() {
    }

    public FoodRequest(String name, Double price, File image, Long categoryId, String description, String status) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.description = description;
        this.status = status;
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

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}