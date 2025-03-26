package com.restaurant.Menu_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.Menu_service.Entity.Food;

public class FoodRequest {
    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 100, message = "Tên món không được vượt quá 100 ký tự")
    private String name;
    
    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải là số dương")
    private Double price;
    
    private MultipartFile image;
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
    
    private String description;
    
    private Food.Status status;

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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
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

    public Food.Status getStatus() {
        return status;
    }

    public void setStatus(Food.Status status) {
        this.status = status;
    }
}