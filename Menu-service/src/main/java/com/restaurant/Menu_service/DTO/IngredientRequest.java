package com.restaurant.Menu_service.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IngredientRequest {
	@NotBlank(message = "Tên nguyên liệu không được để trống")
	@Size(max = 100, message = "Tên nguyên liệu không được vượt quá 100 ký tự")
	private String name;

	@NotBlank(message = "Đơn vị tính không được để trống")
	@Size(max = 50, message = "Đơn vị tính không được vượt quá 50 ký tự")
	private String unit;

	@Min(value = 0, message = "Giá nhập phải lớn hơn hoặc bằng 0")
	private Double purchasePrice;

	private String description;

	public IngredientRequest() {
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

}