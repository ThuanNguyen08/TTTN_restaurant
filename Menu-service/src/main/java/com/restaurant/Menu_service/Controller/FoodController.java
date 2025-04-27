package com.restaurant.Menu_service.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Menu_service.DTO.FoodRequest;
import com.restaurant.Menu_service.DTO.FoodResponse;
import com.restaurant.Menu_service.Entity.Food;
import com.restaurant.Menu_service.Service.FoodService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "Food", description = "Quản lý thông tin món ăn của nhà hàng")
public class FoodController {

	private final FoodService foodService;

	public FoodController(FoodService foodService) {
		this.foodService = foodService;
	}

	@Operation(summary = "Lấy tất cả món ăn", description = "Có thể xem thông tin tất cả món ăn đang hoạt động trong hệ thống")
	@GetMapping
	public ResponseEntity<List<FoodResponse>> getAllFoods() {
		List<FoodResponse> foods = foodService.getAllFoods();
		return ResponseEntity.ok(foods);
	}

	@Operation(summary = "Lấy món ăn theo trạng thái", description = "Có thể xem danh sách món ăn theo trạng thái cụ thể (AVAILABLE, SOLD_OUT)")
	@GetMapping("/status")
	public ResponseEntity<List<FoodResponse>> getFoodsByStatus(@RequestParam Food.Status status) {
		List<FoodResponse> foods = foodService.getFoodsByStatus(status);
		return ResponseEntity.ok(foods);
	}

	@Operation(summary = "Lấy món ăn theo danh mục", description = "Có thể xem danh sách món ăn thuộc một danh mục cụ thể")
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<FoodResponse>> getFoodsByCategory(@PathVariable Long categoryId) {
		List<FoodResponse> foods = foodService.getFoodsByCategory(categoryId);
		return ResponseEntity.ok(foods);
	}

	@Operation(summary = "Lấy thông tin món ăn bằng id", description = "Có thể xem thông tin chi tiết của món ăn thông qua id")
	@GetMapping("/{id}")
	public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
		FoodResponse food = foodService.getFoodById(id);
		return ResponseEntity.ok(food);
	}
	
	@Operation(summary = "Lấy danh sách món ăn đã vô hiệu hóa", description = "Có thể xem danh sách các món ăn đã bị vô hiệu hóa trong hệ thống")
	@GetMapping("/disable")
	public ResponseEntity<List<FoodResponse>> getDisableFoods(){
		List<FoodResponse> foods = foodService.getFoodsByIsActiveFalse();
		return ResponseEntity.ok(foods);
	}

	@Operation(summary = "Tạo món ăn mới", description = "Có thể tạo một món ăn mới trong hệ thống, bao gồm cả hình ảnh")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FoodResponse> createFood(@Valid @ModelAttribute FoodRequest request) throws IOException {
		FoodResponse createdFood = foodService.createFood(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
	}

	@Operation(summary = "Cập nhật thông tin món ăn", description = "Có thể cập nhật thông tin của món ăn bao gồm cả hình ảnh bằng id")
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FoodResponse> updateFood(@PathVariable Long id, @Valid @ModelAttribute FoodRequest request)
			throws IOException {
		FoodResponse updatedFood = foodService.updateFood(id, request);
		return ResponseEntity.ok(updatedFood);
	}
	
	@Operation(summary = "Vô hiệu hóa món ăn", description = "Có thể vô hiệu hóa một món ăn trong hệ thống bằng id")
	@PutMapping("/{id}/disable")
	public ResponseEntity<Void> disableFood(@PathVariable Long id) {
		foodService.disableFood(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Cập nhật trạng thái món ăn", description = "Có thể cập nhật trạng thái món ăn (AVAILABLE, SOLD_OUT) trong hệ thống bằng id")
	@PutMapping("/{id}/status")
	public ResponseEntity<FoodResponse> updateFoodStatus(@PathVariable Long id, @RequestParam Food.Status status) {
		FoodResponse updatedFood = foodService.updateFoodStatus(id, status);
		return ResponseEntity.ok(updatedFood);
	}
	
	@Operation(summary = "Khôi phục món ăn", description = "Có thể khôi phục một món ăn đã bị vô hiệu hóa trong hệ thống")
	@PutMapping("/{id}/restore")
	public ResponseEntity<FoodResponse> restoreFood(@PathVariable Long id) {
	    FoodResponse restoredFood = foodService.restoreFood(id);
	    return ResponseEntity.ok(restoredFood);
	}
	
	@Operation(summary = "Xóa món ăn", description = "Có thể xóa hoàn toàn một món ăn khỏi hệ thống")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
	    foodService.deleteFood(id);
	    return ResponseEntity.noContent().build();
	}
}