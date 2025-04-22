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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

	private final FoodService foodService;

	public FoodController(FoodService foodService) {
		this.foodService = foodService;
	}

	@GetMapping
	public ResponseEntity<List<FoodResponse>> getAllFoods() {
		List<FoodResponse> foods = foodService.getAllFoods();
		return ResponseEntity.ok(foods);
	}

	@GetMapping("/status")
	public ResponseEntity<List<FoodResponse>> getFoodsByStatus(@RequestParam Food.Status status) {
		List<FoodResponse> foods = foodService.getFoodsByStatus(status);
		return ResponseEntity.ok(foods);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<FoodResponse>> getFoodsByCategory(@PathVariable Long categoryId) {
		List<FoodResponse> foods = foodService.getFoodsByCategory(categoryId);
		return ResponseEntity.ok(foods);
	}

	@GetMapping("/{id}")
	public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
		FoodResponse food = foodService.getFoodById(id);
		return ResponseEntity.ok(food);
	}
	
	@GetMapping("/disable")
	public ResponseEntity<List<FoodResponse>> getDisableFoods(){
		List<FoodResponse> foods = foodService.getFoodsByIsActiveFalse();
		return ResponseEntity.ok(foods);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FoodResponse> createFood(@Valid @ModelAttribute FoodRequest request) throws IOException {
		FoodResponse createdFood = foodService.createFood(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FoodResponse> updateFood(@PathVariable Long id, @Valid @ModelAttribute FoodRequest request)
			throws IOException {
		FoodResponse updatedFood = foodService.updateFood(id, request);
		return ResponseEntity.ok(updatedFood);
	}

	@PutMapping("/{id}/disable")
	public ResponseEntity<Void> disableFood(@PathVariable Long id) {
		foodService.disableFood(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<FoodResponse> updateFoodStatus(@PathVariable Long id, @RequestParam Food.Status status) {
		FoodResponse updatedFood = foodService.updateFoodStatus(id, status);
		return ResponseEntity.ok(updatedFood);
	}
	
	@PutMapping("/{id}/restore")
	public ResponseEntity<FoodResponse> restoreFood(@PathVariable Long id) {
	    FoodResponse restoredFood = foodService.restoreFood(id);
	    return ResponseEntity.ok(restoredFood);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
	    foodService.deleteFood(id);
	    return ResponseEntity.noContent().build();
	}
}