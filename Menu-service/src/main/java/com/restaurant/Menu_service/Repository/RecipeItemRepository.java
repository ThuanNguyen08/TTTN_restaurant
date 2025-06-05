package com.restaurant.Menu_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Menu_service.Entity.Food;
import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.RecipeItem;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    List<RecipeItem> findByFood(Food food);
    List<RecipeItem> findByIngredient(Ingredient ingredient);
    void deleteByFood(Food food);
}