package com.restaurant.Revenue_service.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Revenue_service.DTO.PayBillRequest;
import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/pay")
	public ResponseEntity<Bill> payBill(@Valid @RequestBody PayBillRequest request,
			@RequestAttribute("userId") Long userId, HttpServletRequest servletRequest) {
		Bill paidBill = paymentService.payBill(request, userId, servletRequest);
		return ResponseEntity.ok(paidBill);
	}
}