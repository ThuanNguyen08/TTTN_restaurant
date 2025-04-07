package com.restaurant.Revenue_service.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.Revenue_service.DTO.BillItemDto;
import com.restaurant.Revenue_service.DTO.FoodDto;
import com.restaurant.Revenue_service.DTO.PayBillRequest;
import com.restaurant.Revenue_service.DTO.TableBillRequest;
import com.restaurant.Revenue_service.DTO.TableDto;
import com.restaurant.Revenue_service.DTO.TableDto.TableStatus;
import com.restaurant.Revenue_service.DTO.UserDto;
import com.restaurant.Revenue_service.Entity.Bill;
import com.restaurant.Revenue_service.Entity.Bill.BillStatus;
import com.restaurant.Revenue_service.Entity.DetailBill;
import com.restaurant.Revenue_service.Exception.ResourceNotFoundException;
import com.restaurant.Revenue_service.Repository.BillRepository;
import com.restaurant.Revenue_service.Repository.DetailBillRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {
	private final BillRepository billRepository;

	private final DetailBillRepository detailBillRepository;

	private final UserServiceClient userServiceClient;

	private final TableServiceClient tableServiceClient;

	private final MenuServiceClient menuServiceClient;

	public PaymentService(BillRepository billRepository, DetailBillRepository detailBillRepository,
			UserServiceClient userServiceClient, TableServiceClient tableServiceClient,
			MenuServiceClient menuServiceClient) {
		this.billRepository = billRepository;
		this.detailBillRepository = detailBillRepository;
		this.userServiceClient = userServiceClient;
		this.tableServiceClient = tableServiceClient;
		this.menuServiceClient = menuServiceClient;
	}

	@Transactional
	public Bill createOrUpdateTableBill(TableBillRequest request, Long userId, HttpServletRequest servletRequest) {

		TableDto table = tableServiceClient.getTableById(request.getTableId(), servletRequest);
		if (table == null) {
			throw new ResourceNotFoundException("Bàn không tồn tại: " + request.getTableId());
		}

		// Tìm hoặc tạo bill
		Bill existingBill = findPendingBillForTable(request.getTableId());

		if (existingBill == null) {
			existingBill = new Bill();
			existingBill.setTableId(table.getId());
			existingBill.setUserId(userId);
			existingBill.setStatus(BillStatus.PENDING);
			existingBill.setCreatedAt(LocalDateTime.now());
			existingBill.setTotalPrice(BigDecimal.ZERO);
			existingBill.setDiscountAmount(BigDecimal.ZERO);
			existingBill.setFinalPrice(BigDecimal.ZERO);

			// Lưu bill để có ID trước khi tạo chi tiết
			existingBill = billRepository.save(existingBill);
		}

		final Long billId = existingBill.getId();

		// Lấy danh sách tất cả các món trong request mới
		List<Long> requestFoodIds = request.getItems().stream().map(BillItemDto::getFoodId)
				.collect(Collectors.toList());

		// Lấy danh sách các chi tiết hóa đơn hiện có
		List<DetailBill> existingDetails = detailBillRepository.findByBillId(billId);

		// Xóa các chi tiết không có trong request mới
		existingDetails.stream().filter(detail -> !requestFoodIds.contains(detail.getFoodId()))
				.forEach(detail -> detailBillRepository.delete(detail));

		// Xử lý các món ăn
		List<DetailBill> detailBills = request.getItems().stream().map(item -> {
			// Lấy thông tin món ăn từ Menu Service
			FoodDto food = menuServiceClient.getFoodById(item.getFoodId());
			if (food == null) {
				throw new ResourceNotFoundException("Món ăn không tồn tại: " + item.getFoodId());
			}

			DetailBill detailBill = findOrCreateDetailBill(billId, item.getFoodId());

			detailBill.setBillId(billId);
			detailBill.setFoodId(food.getId());
			detailBill.setQuantity(item.getQuantity());
			detailBill.setPrice(food.getPrice());

			return detailBill;
		}).collect(Collectors.toList());

		detailBillRepository.saveAll(detailBills);

		// Tính tổng tiền
		BigDecimal totalPrice = calculateTotalPrice(detailBills);
		existingBill.setTotalPrice(totalPrice);
		existingBill.setFinalPrice(totalPrice); // Ban đầu final price = total price

		// Cập nhật trạng thái bàn
		if (table.getStatus() == TableStatus.AVAILABLE) {
			tableServiceClient.updateTableStatus(table.getId(), TableStatus.OCCUPIED, servletRequest);
		}

		return billRepository.save(existingBill);
	}

	@Transactional
	public Bill payBill(PayBillRequest request, Long userId, HttpServletRequest servletRequest) {
		if (request.getBillId() == null) {
			throw new IllegalArgumentException("Bill ID thiếu");
		}

		Bill bill = billRepository.findById(request.getBillId())
				.orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại"));

		if (bill.getStatus() != BillStatus.PENDING) {
			throw new IllegalStateException("Hóa đơn này đã được xử lý");
		}

		// lấy thông tin user
		UserDto user = userServiceClient.getUserById(userId, servletRequest);
		if (user == null) {
			throw new ResourceNotFoundException("User không tồn tại: " + userId);
		}

		// Tính toán giảm giá
		BigDecimal discountPercentage = request.getDiscountPercentage() != null ? request.getDiscountPercentage()
				: BigDecimal.ZERO;

		if (discountPercentage.compareTo(BigDecimal.ZERO) < 0
				|| discountPercentage.compareTo(new BigDecimal("100")) > 0) {
			throw new IllegalArgumentException("Phần trăm giảm giá phải từ 0 đến 100");
		}

		BigDecimal discountAmount = bill.getTotalPrice().multiply(discountPercentage.divide(new BigDecimal("100")));

		bill.setDiscountAmount(discountAmount);
		bill.setFinalPrice(bill.getTotalPrice().subtract(discountAmount));
		bill.setStatus(BillStatus.PAID);
		bill.setPaymentMethod(request.getPaymentMethod());
		bill.setPaidAt(LocalDateTime.now());
		bill.setCustomerName(request.getCustomerName());
		bill.setCustomerPhone(request.getCustomerPhone());
		bill.setNote(user.getFullName().toString() + " đã thực hiện thanh toán hóa đơn này.");

		// Cập nhật trạng thái bàn qua Table Service
		tableServiceClient.updateTableStatus(bill.getTableId(), TableStatus.AVAILABLE, servletRequest);

		return billRepository.save(bill);
	}

	@Transactional(readOnly = true)
	public Bill getBillById(Long billId) {
		return billRepository.findById(billId)
				.orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại: " + billId));
	}

	@Transactional(readOnly = true)
	public List<DetailBill> getBillItems(Long billId) {
		Bill bill = getBillById(billId);
		return detailBillRepository.findByBillId(bill.getId());
	}

	@Transactional(readOnly = true)
	public List<Bill> getPendingBills() {
		return billRepository.findByStatus(BillStatus.PENDING);
	}

	@Transactional(readOnly = true)
	public List<Bill> getBillsByUser(Long userId) {
		return billRepository.findByUserId(userId);
	}

	private Bill findPendingBillForTable(Long tableId) {
		return billRepository.findByStatus(BillStatus.PENDING).stream()
				.filter(bill -> bill.getTableId().equals(tableId)).findFirst().orElse(null);
	}

	private DetailBill findOrCreateDetailBill(Long billId, Long foodId) {
		if (billId == null) {
			// Nếu là bill mới, tạo DetailBill mới
			return new DetailBill();
		}

		// Tìm DetailBill hiện có theo billId và foodId
		List<DetailBill> existingItems = detailBillRepository.findByBillId(billId);
		Optional<DetailBill> existingItem = existingItems.stream().filter(item -> item.getFoodId().equals(foodId))
				.findFirst();

		return existingItem.orElse(new DetailBill());
	}

	private BigDecimal calculateTotalPrice(List<DetailBill> detailBills) {
		return detailBills.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	@Transactional(readOnly = true)
	public Page<Bill> getAllBills(Pageable pageable) {
	    return billRepository.findAll(pageable);
	}
	
	
}