package com.restaurant.Revenue_service.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BillItemDto {
	@NotNull(message = "id không được trống")
	private Long foodId;
	
	@Min(value = 1, message = "số lượng món ăn phải lớn hơn 0")
	private Integer quantity;

	public BillItemDto() {
	}

	public BillItemDto(Long foodId, Integer quantity) {
		this.foodId = foodId;
		this.quantity = quantity;
	}

	public Long getFoodId() {
		return foodId;
	}

	public void setFoodId(Long foodId) {
		this.foodId = foodId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
