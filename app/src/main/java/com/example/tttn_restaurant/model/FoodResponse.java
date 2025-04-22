package com.example.tttn_restaurant.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FoodResponse {
    private Long id;
    private String name;
    private Double price;
    private String imageBase64;
    private CategoryResponse category;
    private String description;
    private String status;
    private Boolean isActive;
    private String createdAt;


    // Cached bitmap để không phải decode nhiều lần
    private transient Bitmap cachedBitmap;

    public FoodResponse() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
        // Reset cached bitmap khi set lại image
        this.cachedBitmap = null;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Phương thức tiện ích để chuyển đổi Base64 thành Bitmap
    public Bitmap getImageBitmap() {
        if (cachedBitmap == null && imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                cachedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedBitmap;
    }

    @Override
    public String toString() {
        return name;
    }
}