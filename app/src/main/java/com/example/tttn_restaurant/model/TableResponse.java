package com.example.tttn_restaurant.model;


import java.io.Serializable;

public class TableResponse implements Serializable {
    private Long id;
    private String name;
    private String status;
    private String description;
    private String createdAt;

    public TableResponse() {
    }

    public TableResponse(Long id, String name, String status, String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}