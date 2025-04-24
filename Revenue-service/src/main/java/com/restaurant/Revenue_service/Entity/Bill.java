package com.restaurant.Revenue_service.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bill")
public class Bill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "table_id")
	private Long tableId;

	@Column(name = "total_price", precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(name = "discount_amount", precision = 10, scale = 2)
	private BigDecimal discountAmount;

	@Column(name = "final_price", precision = 10, scale = 2)
	private BigDecimal finalPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method")
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private BillStatus status;

	@Column(name = "customer_name", length = 100)
	private String customerName;

	@Column(name = "customer_phone", length = 15)
	private String customerPhone;

	@Column(name = "note", columnDefinition = "TEXT")
	private String note;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	public enum PaymentMethod {
		CASH, CREDIT_CARD, BANK_TRANSFER
	}

	public enum BillStatus {
		PENDING, PAID, CANCELLED, REFUNDED
	}

	public Bill() {
	}

	public Bill(Long id, Long userId, Long tableId, BigDecimal totalPrice, BigDecimal discountAmount,
			BigDecimal finalPrice, PaymentMethod paymentMethod, BillStatus status, String customerName,
			String customerPhone, String note, LocalDateTime createdAt, LocalDateTime paidAt) {
		this.id = id;
		this.userId = userId;
		this.tableId = tableId;
		this.totalPrice = totalPrice;
		this.discountAmount = discountAmount;
		this.finalPrice = finalPrice;
		this.paymentMethod = paymentMethod;
		this.status = status;
		this.customerName = customerName;
		this.customerPhone = customerPhone;
		this.note = note;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BillStatus getStatus() {
		return status;
	}

	public void setStatus(BillStatus status) {
		this.status = status;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

}