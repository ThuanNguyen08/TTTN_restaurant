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

import com.restaurant.Menu_service.DTO.RecipeItemRequest;
import com.restaurant.Menu_service.DTO.RecipeItemResponse;
import com.restaurant.Menu_service.Service.RecipeItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe-items")
@Tag(name = "Recipe Item", description = "Quản lý công thức món ăn và nguyên liệu cần thiết")
public class RecipeItemController {

	private RecipeItemService recipeItemService;

	public RecipeItemController(RecipeItemService recipeItemService) {
		this.recipeItemService = recipeItemService;
	}

	@Operation(summary = "Lấy tất cả công thức", description = "Có thể xem thông tin tất cả công thức món ăn trong hệ thống")
	@GetMapping
	public ResponseEntity<List<RecipeItemResponse>> getAllRecipeItems() {
		List<RecipeItemResponse> recipeItems = recipeItemService.getAllRecipeItems();
		return ResponseEntity.ok(recipeItems);
	}

	@Operation(summary = "Lấy công thức theo id", description = "Có thể xem thông tin chi tiết của một công thức bằng id")
	@GetMapping("/{id}")
	public ResponseEntity<RecipeItemResponse> getRecipeItemById(@PathVariable Long id) {
		RecipeItemResponse recipeItem = recipeItemService.getRecipeItemById(id);
		return ResponseEntity.ok(recipeItem);
	}

	@Operation(summary = "Lấy công thức theo món ăn", description = "Có thể xem danh sách công thức (nguyên liệu) của một món ăn cụ thể")
	@GetMapping("/food/{foodId}")
	public ResponseEntity<List<RecipeItemResponse>> getRecipeItemsByFoodId(@PathVariable Long foodId) {
		List<RecipeItemResponse> recipeItems = recipeItemService.getRecipeItemsByFoodId(foodId);
		return ResponseEntity.ok(recipeItems);
	}

	@Operation(summary = "Lấy công thức theo nguyên liệu", description = "Có thể xem danh sách công thức (món ăn) sử dụng một nguyên liệu cụ thể")
	@GetMapping("/ingredient/{ingredientId}")
	public ResponseEntity<List<RecipeItemResponse>> getRecipeItemsByIngredientId(@PathVariable Long ingredientId) {
		List<RecipeItemResponse> recipeItems = recipeItemService.getRecipeItemsByIngredientId(ingredientId);
		return ResponseEntity.ok(recipeItems);
	}

	@Operation(summary = "Tạo công thức mới", description = "Có thể tạo một công thức mới cho món ăn")
	@PostMapping
	public ResponseEntity<RecipeItemResponse> createRecipeItem(@Valid @RequestBody RecipeItemRequest request) {
		RecipeItemResponse createdRecipeItem = recipeItemService.createRecipeItem(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipeItem);
	}

	@Operation(summary = "Cập nhật công thức", description = "Có thể cập nhật thông tin công thức bằng id")
	@PutMapping("/{id}")
	public ResponseEntity<RecipeItemResponse> updateRecipeItem(@PathVariable Long id,
			@Valid @RequestBody RecipeItemRequest request) {
		RecipeItemResponse updatedRecipeItem = recipeItemService.updateRecipeItem(id, request);
		return ResponseEntity.ok(updatedRecipeItem);
	}

	@Operation(summary = "Xóa công thức", description = "Có thể xóa một công thức khỏi hệ thống")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRecipeItem(@PathVariable Long id) {
		recipeItemService.deleteRecipeItem(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Xóa tất cả công thức của món ăn", description = "Có thể xóa tất cả công thức của một món ăn cụ thể")
	@DeleteMapping("/food/{foodId}")
	public ResponseEntity<Void> deleteAllRecipeItemsByFoodId(@PathVariable Long foodId) {
		recipeItemService.deleteAllRecipeItemsByFoodId(foodId);
		return ResponseEntity.noContent().build();
	}
}