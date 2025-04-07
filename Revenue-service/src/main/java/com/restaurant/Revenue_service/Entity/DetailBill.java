package com.restaurant.Revenue_service.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "detail_bill")
public class DetailBill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bill_id")
	private Long billId;

	@Column(name = "food_id")
	private Long foodId;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price;

	public DetailBill() {
	}

	public DetailBill(Long id, Long billId, Long foodId, Integer quantity, BigDecimal price) {
		this.id = id;
		this.billId = billId;
		this.foodId = foodId;
		this.quantity = quantity;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}