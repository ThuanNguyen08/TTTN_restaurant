package com.restaurant.Menu_service.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.Menu_service.DTO.RecipeItemRequest;
import com.restaurant.Menu_service.DTO.RecipeItemResponse;
import com.restaurant.Menu_service.Entity.Food;
import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.RecipeItem;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.FoodRepository;
import com.restaurant.Menu_service.Repository.IngredientRepository;
import com.restaurant.Menu_service.Repository.RecipeItemRepository;

@Service
public class RecipeItemService {

    private final RecipeItemRepository recipeItemRepository;
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeItemService(RecipeItemRepository recipeItemRepository, FoodRepository foodRepository,
            IngredientRepository ingredientRepository) {
        this.recipeItemRepository = recipeItemRepository;
        this.foodRepository = foodRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<RecipeItemResponse> getAllRecipeItems() {
        return recipeItemRepository.findAll().stream().map(RecipeItemResponse::fromEntity).toList();
    }

    public RecipeItemResponse getRecipeItemById(Long id) {
        return recipeItemRepository.findById(id).map(RecipeItemResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công thức với id: " + id));
    }

    public List<RecipeItemResponse> getRecipeItemsByFoodId(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + foodId));
        
        return recipeItemRepository.findByFood(food).stream().map(RecipeItemResponse::fromEntity).toList();
    }

    public List<RecipeItemResponse> getRecipeItemsByIngredientId(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));
        
        return recipeItemRepository.findByIngredient(ingredient).stream().map(RecipeItemResponse::fromEntity).toList();
    }

    @Transactional
    public RecipeItemResponse createRecipeItem(RecipeItemRequest request) {
        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + request.getFoodId()));
        
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId()).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + request.getIngredientId()));
        
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        RecipeItem recipeItem = new RecipeItem();
        recipeItem.setFood(food);
        recipeItem.setIngredient(ingredient);
        recipeItem.setQuantity(request.getQuantity());
        
        RecipeItem savedRecipeItem = recipeItemRepository.save(recipeItem);
        return RecipeItemResponse.fromEntity(savedRecipeItem);
    }

    @Transactional
    public RecipeItemResponse updateRecipeItem(Long id, RecipeItemRequest request) {
        RecipeItem recipeItem = recipeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công thức với id: " + id));
        
        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + request.getFoodId()));
        
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId()).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + request.getIngredientId()));
        
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        recipeItem.setFood(food);
        recipeItem.setIngredient(ingredient);
        recipeItem.setQuantity(request.getQuantity());
        
        RecipeItem updatedRecipeItem = recipeItemRepository.save(recipeItem);
        return RecipeItemResponse.fromEntity(updatedRecipeItem);
    }

    @Transactional
    public void deleteRecipeItem(Long id) {
        RecipeItem recipeItem = recipeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công thức với id: " + id));
        
        recipeItemRepository.delete(recipeItem);
    }
    
    @Transactional
    public void deleteAllRecipeItemsByFoodId(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + foodId));
        
        recipeItemRepository.deleteByFood(food);
    }
}