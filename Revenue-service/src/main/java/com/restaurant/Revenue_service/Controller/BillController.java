package com.restaurant.Revenue_service.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Revenue_service.DTO.TableBillRequest;
import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Entity.DetailBill;
import com.restaurant.Revenue_service.Service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bills")
public class BillController {

	private final PaymentService paymentService;

	public BillController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<Bill> createOrUpdateBill(@Valid @RequestBody TableBillRequest request,
			@RequestAttribute("userId") Long userId, HttpServletRequest servletRequest) {
		Bill bill = paymentService.createOrUpdateTableBill(request, userId, servletRequest);
		return new ResponseEntity<>(bill, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
		Bill bill = paymentService.getBillById(id);
		return ResponseEntity.ok(bill);
	}

	@GetMapping("/{id}/items")
	public ResponseEntity<List<DetailBill>> getBillItems(@PathVariable Long id) {
		List<DetailBill> items = paymentService.getBillItems(id);
		return ResponseEntity.ok(items);
	}

	@GetMapping("/pending")
	public ResponseEntity<List<Bill>> getPendingBills() {
		List<Bill> bills = paymentService.getPendingBills();
		return ResponseEntity.ok(bills);
	}

	@GetMapping("/my")
	public ResponseEntity<List<Bill>> getMyBills(@RequestAttribute("userId") Long userId) {
		List<Bill> bills = paymentService.getBillsByUser(userId);
		return ResponseEntity.ok(bills);
	}
	
	@GetMapping
	public ResponseEntity<Page<Bill>> getAllBills(Pageable pageable) {
	    Page<Bill> bills = paymentService.getAllBills(pageable);
	    return ResponseEntity.ok(bills);
	}
}