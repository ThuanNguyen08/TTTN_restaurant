package com.restaurant.Revenue_service.DTO;

import java.math.BigDecimal;

import com.restaurant.Revenue_service.Entity.Bill.PaymentMethod;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PayBillRequest {
	@NotNull(message = "Bill ID không được để trống")
	private Long billId;

	@DecimalMin(value = "0.0", message = "Phần trăm giảm giá không được nhỏ hơn 0")
	@DecimalMax(value = "100.0", message = "Phần trăm giảm giá không được lớn hơn 100")
	private BigDecimal discountPercentage;

	@NotNull(message = "Phương thức thanh toán không được để trống")
	private PaymentMethod paymentMethod;

	@Size(max = 100, message = "Tên không vượt quá 100 ký tự")
	private String customerName;

	@Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
	private String customerPhone;

	public PayBillRequest() {
	}

	public PayBillRequest(Long billId, BigDecimal discountPercentage, PaymentMethod paymentMethod, String customerName,
			String customerPhone) {
		this.billId = billId;
		this.discountPercentage = discountPercentage;
		this.paymentMethod = paymentMethod;
		this.customerName = customerName;
		this.customerPhone = customerPhone;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
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

}
