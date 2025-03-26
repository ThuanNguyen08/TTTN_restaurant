package com.restaurant.Menu_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Menu_service.Entity.Food;
import com.restaurant.Menu_service.Entity.Category;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByIsActiveTrue();
    List<Food> findByIsActiveTrueAndStatus(Food.Status status);
    List<Food> findByCategoryAndIsActiveTrue(Category category);
    boolean existsByNameAndIsActiveTrue(String name);
}