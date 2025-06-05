package com.restaurant.Menu_service.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.Menu_service.Entity.Ingredient;

public class IngredientResponse {
	private Long id;
	private String name;
	private String unit;
	private Double purchasePrice;
	private String description;
	private Boolean isActive;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	// Constructor không tham số
	public IngredientResponse() {
	}

	// Constructor đầy đủ tham số
	public IngredientResponse(Long id, String name, String unit, Double purchasePrice, String description,
			Boolean isActive, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.purchasePrice = purchasePrice;
		this.description = description;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}

	// Phương thức static để chuyển đổi từ Entity sang DTO
	public static IngredientResponse fromEntity(Ingredient ingredient) {
		return new IngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getUnit(),
				ingredient.getPurchasePrice(), ingredient.getDescription(), ingredient.getIsActive(),
				ingredient.getCreatedAt());
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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