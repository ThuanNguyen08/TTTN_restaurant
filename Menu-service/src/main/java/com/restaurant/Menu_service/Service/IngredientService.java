package com.restaurant.Menu_service.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurant.Menu_service.DTO.IngredientRequest;
import com.restaurant.Menu_service.DTO.IngredientResponse;
import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Exception.ResourceAlreadyExistsException;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.IngredientRepository;
import com.restaurant.Menu_service.Repository.RecipeItemRepository;

@Service
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	private final RecipeItemRepository recipeItemRepository;

	public IngredientService(IngredientRepository ingredientRepository, RecipeItemRepository recipeItemRepository) {
		this.ingredientRepository = ingredientRepository;
		this.recipeItemRepository = recipeItemRepository;
	}

	public List<IngredientResponse> getAllIngredients() {
		return ingredientRepository.findByIsActiveTrue().stream().map(IngredientResponse::fromEntity).toList();
	}

	public IngredientResponse getIngredientById(Long id) {
		return ingredientRepository.findById(id).map(IngredientResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));
	}

	public List<IngredientResponse> getDisabledIngredients() {
		return ingredientRepository.findByIsActiveFalse().stream().map(IngredientResponse::fromEntity).toList();
	}

	public IngredientResponse createIngredient(IngredientRequest request) {
		if (ingredientRepository.existsByNameAndIsActiveTrue(request.getName())) {
			throw new ResourceAlreadyExistsException("Nguyên liệu với tên \"" + request.getName() + "\" đã tồn tại");
		}

		Ingredient ingredient = new Ingredient();
		ingredient.setName(request.getName());
		ingredient.setUnit(request.getUnit());
		ingredient.setPurchasePrice(request.getPurchasePrice());
		ingredient.setDescription(request.getDescription());


		Ingredient savedIngredient = ingredientRepository.save(ingredient);
		return IngredientResponse.fromEntity(savedIngredient);
	}

	public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
		Ingredient ingredient = ingredientRepository.findById(id).filter(Ingredient::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));

		Optional<Ingredient> existingIngredient = ingredientRepository.findByNameAndIsActiveTrue(request.getName());
		if (existingIngredient.isPresent() && !existingIngredient.get().getId().equals(id)) {
			throw new ResourceAlreadyExistsException("Nguyên liệu với tên \"" + request.getName() + "\" đã tồn tại");
		}

		ingredient.setName(request.getName());
		ingredient.setUnit(request.getUnit());
		ingredient.setPurchasePrice(request.getPurchasePrice());
		ingredient.setDescription(request.getDescription());

		Ingredient updatedIngredient = ingredientRepository.save(ingredient);
		return IngredientResponse.fromEntity(updatedIngredient);
	}

	public void disableIngredient(Long id) {
		Ingredient ingredient = ingredientRepository.findById(id).filter(Ingredient::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));

		ingredient.setIsActive(false);
		ingredientRepository.save(ingredient);
	}

//	public IngredientResponse updateIngredientStatus(Long id, Ingredient.Status status) {
//		Ingredient ingredient = ingredientRepository.findById(id).filter(Ingredient::getIsActive)
//				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));
//
//		ingredient.setStatus(status);
//		Ingredient updatedIngredient = ingredientRepository.save(ingredient);
//		return IngredientResponse.fromEntity(updatedIngredient);
//	}

	public IngredientResponse restoreIngredient(Long id) {
		Ingredient ingredient = ingredientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));

		// Chỉ cho phép khôi phục các nguyên liệu đã bị vô hiệu hóa
		if (ingredient.getIsActive()) {
			throw new ResourceAlreadyExistsException("Nguyên liệu này đã được kích hoạt");
		}

		ingredient.setIsActive(true);
		Ingredient restoredIngredient = ingredientRepository.save(ingredient);
		return IngredientResponse.fromEntity(restoredIngredient);
	}

	public void deleteIngredient(Long id) {
		Ingredient ingredient = ingredientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + id));

		// Kiểm tra xem nguyên liệu có được sử dụng trong công thức món ăn không
		if (!recipeItemRepository.findByIngredient(ingredient).isEmpty()) {
			throw new ResourceAlreadyExistsException(
					"Không thể xóa nguyên liệu này vì nó đang được sử dụng trong công thức món ăn");
		}

		ingredientRepository.delete(ingredient);
	}
}