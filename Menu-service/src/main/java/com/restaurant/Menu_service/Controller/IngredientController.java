package com.restaurant.Menu_service.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Menu_service.DTO.IngredientRequest;
import com.restaurant.Menu_service.DTO.IngredientResponse;
import com.restaurant.Menu_service.Service.IngredientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient", description = "Quản lý thông tin nguyên liệu của nhà hàng")
public class IngredientController {

	private IngredientService ingredientService;

	public IngredientController(IngredientService ingredientService) {
		this.ingredientService = ingredientService;
	}

	@Operation(summary = "Lấy tất cả nguyên liệu", description = "Có thể xem thông tin tất cả nguyên liệu đang hoạt động trong hệ thống")
	@GetMapping
	public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
		List<IngredientResponse> ingredients = ingredientService.getAllIngredients();
		return ResponseEntity.ok(ingredients);
	}

	@Operation(summary = "Lấy nguyên liệu theo id", description = "Có thể xem thông tin chi tiết của nguyên liệu bằng id")
	@GetMapping("/{id}")
	public ResponseEntity<IngredientResponse> getIngredientById(@PathVariable Long id) {
		IngredientResponse ingredient = ingredientService.getIngredientById(id);
		return ResponseEntity.ok(ingredient);
	}

	@Operation(summary = "Lấy danh sách nguyên liệu đã vô hiệu hóa", description = "Có thể xem danh sách các nguyên liệu đã bị vô hiệu hóa trong hệ thống")
	@GetMapping("/disable")
	public ResponseEntity<List<IngredientResponse>> getDisabledIngredients() {
		List<IngredientResponse> ingredients = ingredientService.getDisabledIngredients();
		return ResponseEntity.ok(ingredients);
	}

	@Operation(summary = "Tạo nguyên liệu mới", description = "Có thể tạo một nguyên liệu mới trong hệ thống")
	@PostMapping
	public ResponseEntity<IngredientResponse> createIngredient(@Valid @RequestBody IngredientRequest request) {
		IngredientResponse createdIngredient = ingredientService.createIngredient(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
	}

	@Operation(summary = "Cập nhật thông tin nguyên liệu", description = "Có thể cập nhật thông tin của nguyên liệu bằng id")
	@PutMapping("/{id}")
	public ResponseEntity<IngredientResponse> updateIngredient(@PathVariable Long id,
			@Valid @RequestBody IngredientRequest request) {
		IngredientResponse updatedIngredient = ingredientService.updateIngredient(id, request);
		return ResponseEntity.ok(updatedIngredient);
	}

	@Operation(summary = "Vô hiệu hóa nguyên liệu", description = "Có thể vô hiệu hóa một nguyên liệu trong hệ thống bằng id")
	@PutMapping("/{id}/disable")
	public ResponseEntity<Void> disableIngredient(@PathVariable Long id) {
		ingredientService.disableIngredient(id);
		return ResponseEntity.noContent().build();
	}


	@Operation(summary = "Khôi phục nguyên liệu", description = "Có thể khôi phục một nguyên liệu đã bị vô hiệu hóa trong hệ thống")
	@PutMapping("/{id}/restore")
	public ResponseEntity<IngredientResponse> restoreIngredient(@PathVariable Long id) {
		IngredientResponse restoredIngredient = ingredientService.restoreIngredient(id);
		return ResponseEntity.ok(restoredIngredient);
	}

	@Operation(summary = "Xóa nguyên liệu", description = "Có thể xóa hoàn toàn một nguyên liệu khỏi hệ thống (chỉ khi không được sử dụng trong công thức món ăn)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
		ingredientService.deleteIngredient(id);
		return ResponseEntity.noContent().build();
	}
}