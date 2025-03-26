package com.restaurant.Menu_service.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.Menu_service.DTO.FoodRequest;
import com.restaurant.Menu_service.DTO.FoodResponse;
import com.restaurant.Menu_service.Entity.Category;
import com.restaurant.Menu_service.Entity.Food;
import com.restaurant.Menu_service.Exception.ResourceAlreadyExistsException;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.CategoryRepository;
import com.restaurant.Menu_service.Repository.FoodRepository;

@Service
public class FoodService {

	private final FoodRepository foodRepository;
	
	private final CategoryRepository categoryRepository;

	public FoodService(FoodRepository foodRepository, CategoryRepository categoryRepository) {
		this.foodRepository = foodRepository;
		this.categoryRepository = categoryRepository;
	}
	

	public List<FoodResponse> getAllFoods() {
		return foodRepository.findByIsActiveTrue().stream().map(FoodResponse::fromEntity).toList();
	}

	public List<FoodResponse> getFoodsByStatus(Food.Status status)	 {
		return foodRepository.findByIsActiveTrueAndStatus(status).stream().map(FoodResponse::fromEntity).toList();
	}

	public List<FoodResponse> getFoodsByCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId).filter(Category::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + categoryId));

		return foodRepository.findByCategoryAndIsActiveTrue(category).stream().map(FoodResponse::fromEntity).toList();
	}

	public FoodResponse getFoodById(Long id) {
		return foodRepository.findById(id).filter(Food::getIsActive).map(FoodResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));
	}

	public FoodResponse createFood(FoodRequest request) throws IOException {
		if (foodRepository.existsByNameAndIsActiveTrue(request.getName())) {
			throw new ResourceAlreadyExistsException("Món ăn với tên \"" + request.getName() + "\" đã tồn tại");
		}

		Category category = categoryRepository.findById(request.getCategoryId()).filter(Category::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Không tìm thấy danh mục với id: " + request.getCategoryId()));

		Food food = new Food();
		food.setName(request.getName());
		food.setPrice(request.getPrice());
		food.setCategory(category);
		food.setDescription(request.getDescription());

		if (request.getStatus() != null) {
			food.setStatus(request.getStatus());
		}

		// Xử lý hình ảnh nếu có
		if (request.getImage() != null && !request.getImage().isEmpty()) {
			food.setImage(request.getImage().getBytes());
		}

		Food savedFood = foodRepository.save(food);
		return FoodResponse.fromEntity(savedFood);
	}

	public FoodResponse updateFood(Long id, FoodRequest request) throws IOException {
		Food food = foodRepository.findById(id).filter(Food::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

		if (!food.getName().equals(request.getName())
				&& foodRepository.existsByNameAndIsActiveTrue(request.getName())) {
			throw new ResourceAlreadyExistsException("Món ăn với tên \"" + request.getName() + "\" đã tồn tại");
		}

		Category category = categoryRepository.findById(request.getCategoryId()).filter(Category::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Không tìm thấy danh mục với id: " + request.getCategoryId()));

		food.setName(request.getName());
		food.setPrice(request.getPrice());
		food.setCategory(category);
		food.setDescription(request.getDescription());

		if (request.getStatus() != null) {
			food.setStatus(request.getStatus());
		}

		// Xử lý hình ảnh nếu có
		if (request.getImage() != null && !request.getImage().isEmpty()) {
			food.setImage(request.getImage().getBytes());
		}

		Food updatedFood = foodRepository.save(food);
		return FoodResponse.fromEntity(updatedFood);
	}

	public void deleteFood(Long id) {
		Food food = foodRepository.findById(id).filter(Food::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

		food.setIsActive(false);
		foodRepository.save(food);
	}

	public FoodResponse updateFoodStatus(Long id, Food.Status status) {
		Food food = foodRepository.findById(id).filter(Food::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

		food.setStatus(status);
		Food updatedFood = foodRepository.save(food);
		return FoodResponse.fromEntity(updatedFood);
	}
	
	public FoodResponse restoreFood(Long id) {
	    Food food = foodRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

	    // Chỉ cho phép khôi phục các món ăn đã bị vô hiệu hóa
	    if (food.getIsActive()) {
	        throw new ResourceAlreadyExistsException("Món ăn này đã được kích hoạt");
	    }

	    food.setIsActive(true);
	    Food restoredFood = foodRepository.save(food);
	    return FoodResponse.fromEntity(restoredFood);
	}
}