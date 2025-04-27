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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Revenue_service.DTO.TableBillRequest;
import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Entity.DetailBill;
import com.restaurant.Revenue_service.Service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Bill", description = "Quản lý hóa đơn của nhà hàng")
public class BillController {

	private final PaymentService paymentService;

	public BillController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Operation(summary = "Tạo hoặc cập nhật hóa đơn", description = "Có thể tạo hóa đơn mới hoặc cập nhật hóa đơn hiện có cho một bàn cụ thể")
	@PostMapping
	public ResponseEntity<Bill> createOrUpdateBill(@Valid @RequestBody TableBillRequest request,
			@RequestAttribute("userId") Long userId, HttpServletRequest servletRequest) {
		Bill bill = paymentService.createOrUpdateTableBill(request, userId, servletRequest);
		return new ResponseEntity<>(bill, HttpStatus.CREATED);
	}

	@Operation(summary = "Lấy thông tin hóa đơn theo id", description = "Có thể xem thông tin chi tiết của một hóa đơn thông qua id")
	@GetMapping("/{id}")
	public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
		Bill bill = paymentService.getBillById(id);
		return ResponseEntity.ok(bill);
	}

	@Operation(summary = "Lấy danh sách món ăn trong hóa đơn", description = "Có thể xem danh sách các món ăn đã đặt trong một hóa đơn cụ thể")
	@GetMapping("/{id}/items")
	public ResponseEntity<List<DetailBill>> getBillItems(@PathVariable Long id) {
		List<DetailBill> items = paymentService.getBillItems(id);
		return ResponseEntity.ok(items);
	}

	@Operation(summary = "Lấy danh sách hóa đơn đang chờ thanh toán", description = "Có thể xem danh sách các hóa đơn đang ở trạng thái chờ thanh toán")
	@GetMapping("/pending")
	public ResponseEntity<List<Bill>> getPendingBills() {
		List<Bill> bills = paymentService.getPendingBills();
		return ResponseEntity.ok(bills);
	}

	@Operation(summary = "Lấy danh sách hóa đơn của người dùng hiện tại", description = "Có thể xem danh sách các hóa đơn đã được tạo bởi người dùng hiện tại")
	@GetMapping("/my")
	public ResponseEntity<List<Bill>> getMyBills(@RequestAttribute("userId") Long userId) {
		List<Bill> bills = paymentService.getBillsByUser(userId);
		return ResponseEntity.ok(bills);
	}
	
	@Operation(summary = "Lấy danh sách tất cả hóa đơn", description = "Có thể xem danh sách hóa đơn với phân trang và lọc theo trạng thái hoặc ngày")
	@GetMapping
	public ResponseEntity<Page<Bill>> getAllBills(Pageable pageable, @RequestParam(required = false) String status, @RequestParam(required = false) String date) {
	    Page<Bill> bills = paymentService.getAllBills(pageable, status, date);
	    return ResponseEntity.ok(bills);
	}
}